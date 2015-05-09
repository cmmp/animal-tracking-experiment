package br.fapesp.animaltracking;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import br.fapesp.myutils.MyUtils;
import edu.stanford.math.plex4.api.Plex4;
import edu.stanford.math.plex4.homology.barcodes.BarcodeCollection;
import edu.stanford.math.plex4.homology.barcodes.Interval;
import edu.stanford.math.plex4.homology.interfaces.AbstractPersistenceAlgorithm;
import edu.stanford.math.plex4.metric.impl.EuclideanMetricSpace;
import edu.stanford.math.plex4.metric.landmark.MaxMinLandmarkSelector;
import edu.stanford.math.plex4.streams.impl.LazyWitnessStream;

public class AnimalTrackingExperiment {
	public static FiltrationResult filtrationAnalysisFromDistanceMatrix(double[][] points, int ratio, double delta) {
		EuclideanMetricSpace ems = new EuclideanMetricSpace(points);

		double[][] euc = MyUtils.getEuclideanMatrix(points);
		int max_d = 2;
//		double max_dist = MyUtils.estimateMaxDist(points, 20, 2.0, null, 1234);
		double max_dist = MyUtils.getMatrixMax(euc);

		int nLandmarks = points.length / ratio;
		
		MaxMinLandmarkSelector maxmin = new MaxMinLandmarkSelector(ems, nLandmarks);
		LazyWitnessStream lt = new LazyWitnessStream(ems, maxmin, max_d, max_dist, 0, 1000);
		lt.finalizeStream();
		AbstractPersistenceAlgorithm persistence = Plex4.getModularSimplicialAlgorithm(max_d, 2);
		BarcodeCollection bc = persistence.computeIntervals(lt);
		
		int[] ndHoles = new int[max_d];
		int[] ndRelevantHoles = new int[max_d];
		double[] maxHoleLifeTime = new double[max_d];
		double[] averageHoleLifeTime = new double[max_d];
		double lf;
		int dim;
		ArrayList<Double> lifetimes = new ArrayList<Double>();
		
		for (int i = 0; i < max_d; i++) {
			dim = i;
			ndHoles[dim] = bc.getIntervalsAtDimension(dim).size();
			lifetimes.clear();
			
			if (ndHoles[dim] > 0) {
				List intervals = bc.getIntervalsAtDimension(dim);
				for (int j = 0; j < intervals.size(); j++) {
					Interval inter = (Interval) intervals.get(j);
					if (inter.isInfinite())
						averageHoleLifeTime[dim] += max_dist;
					else {
						lf = ((Double) inter.getEnd()) - ((Double) inter.getStart());
						lifetimes.add(lf);
						averageHoleLifeTime[dim] += lf;
						if (lf > maxHoleLifeTime[dim])
							maxHoleLifeTime[dim] = lf;
					}
				}
				for(Double life : lifetimes)
					if (life >= maxHoleLifeTime[dim] / 2.0)
						ndRelevantHoles[dim]++;
				
				averageHoleLifeTime[dim] /= ndHoles[dim];
			}
		}
		
		FiltrationResult fr = new FiltrationResult();
		fr.nDholes = ndHoles;
		fr.maxHoleLifeTime = maxHoleLifeTime;
		fr.averageHoleLifeTime = averageHoleLifeTime;
		fr.nDrelevantHoles = ndRelevantHoles;
		System.out.println(fr);
		return fr;
	}
	
    public static void main( String[] args ) throws IOException {
    	
    	String basePath = "/Users/cassio/Dropbox/animaltracking/";

//    	String[] paths = {"albatross", "gadwall", "pigeons", "vultures"};
    	String[] paths = {"pigeons", "vultures"};
    	
    	ArrayList atts = new ArrayList();
    	for (int i = 0; i < 2; i++)
    		atts.add(new Attribute("Nholes-" + i));
    	for (int i = 2, j = 0; i < 4; i++, j++)
    		atts.add(new Attribute("AverageHoleLife-" + j));
    	for (int i = 4, j = 0; i < 6; i++, j++)
    		atts.add(new Attribute("MaxHoleLifeTime-" + j));
    	for (int i = 6, j = 0; i < 8; i++, j++)
    		atts.add(new Attribute("NRelevantHoles-" + j));
    	
    	ArrayList classValues = new ArrayList();
    	for(int i = 0; i < paths.length; i++) classValues.add(paths[i]);
    	
    	Attribute clazz = new Attribute("class", classValues);
    	atts.add(clazz);
    	
    	Instances dataset = new Instances("Animal-tracking-experiment", atts, 25);
    	dataset.setClass(clazz);
        
        File baseFolder = new File(basePath);

        for (int i = 0; i < paths.length; i++) {
            String path = paths[i];
            File[] files = new File(basePath + path + "/animals/").listFiles();
            ArrayList<File> recFiles = new ArrayList<File>();
//            int k = 0;
            for(File f: files) {
            	if (f.getName().contains(".txt")) {
                    recFiles.add(f);
                    //k++;
            	}
            }
            
            // now load the files and run the topological analysis:
            for (int j = 0; j < recFiles.size(); j++) {
            	System.gc();
            	File f = recFiles.get(j);
            	System.out.println("Analyzing file " + f.getCanonicalPath());
            	double[][] points = MyUtils.readCSVdataSet(f.getCanonicalPath(), false, ' ');
            	
            	long time = System.currentTimeMillis();
            	FiltrationResult fr = filtrationAnalysisFromDistanceMatrix(points, 100, 0.001);
            	time = System.currentTimeMillis() - time;
            	System.out.println("Time taken: " + (time / 1000.) + "s");
            	
            	Instance inst = new DenseInstance(atts.size());
            	inst.setDataset(dataset);
            	inst.setValue(0, fr.nDholes[0]);
            	inst.setValue(1, fr.nDholes[1]);
            	inst.setValue(2, fr.averageHoleLifeTime[0]);
            	inst.setValue(3, fr.averageHoleLifeTime[1]);
            	inst.setValue(4, fr.maxHoleLifeTime[0]);
            	inst.setValue(5, fr.maxHoleLifeTime[1]);
            	inst.setValue(6, fr.nDrelevantHoles[0]);
            	inst.setValue(7, fr.nDrelevantHoles[1]);
            	
            	inst.setClassValue(path);
            	dataset.add(inst);
            }
        }
        
        ArffSaver saver = new ArffSaver();
    	saver.setInstances(dataset);
    	
    	try {
			saver.setFile(new File("animaltracking.arff"));
			saver.writeBatch();
			System.out.println("***** Output saved to animaltracking.arff... *****");
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
        
    }
}
