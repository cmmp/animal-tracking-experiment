#!/bin/bash
# Cassio M. M. Pereira
# Fri Aug  1 15:21:36 BRT 2014

CP=/Users/cassio/tools/weka-3-7-11/weka.jar

INPUT=animaltracking.arff
#FILTERED=$INPUT
#FILTERED_ATTSEL=$INPUT
#FILTERED=animaltracking_filtered.arff
#FILTERED_ATTSEL=animaltracking_filtered_attsel.arff

# attribute evaluation:
#java -cp $CP weka.attributeSelection.PrincipalComponents -i $INPUT 
#java -cp $CP weka.attributeSelection.CfsSubsetEval -P 4 -E 4 -s "weka.attributeSelection.BestFirst" -i $INPUT -Z 


#java -cp $CP weka.filters.unsupervised.instance.Randomize -S 1234 -i $INPUT -o $FILTERED

# java -cp $CP weka.filters.unsupervised.attribute.Normalize -c last -i $INPUT -o $FILTERED
#java -cp $CP weka.filters.unsupervised.attribute.Remove -R "1,6,4,2,5,7" -i $FILTERED -o $FILTERED_ATTSEL
#java -cp $CP weka.filters.unsupervised.attribute.Remove -R "4,6,8,2" -i $FILTERED -o $FILTERED_ATTSEL
# java -cp $CP weka.attributeSelection.InfoGainAttributeEval -i $FILTERED -c last -x 10 -n 1234

java -cp $CP weka.clusterers.SimpleKMeans -t $INPUT -c last -N 2 -init 1 -I 500 -S 1234 
#java -cp $CP weka.clusterers.SimpleKMeans -t $FILTERED -c last -N 2 -init 1 -I 500 -S 1234 
#java -cp $CP weka.clusterers.SimpleKMeans -t $FILTERED_ATTSEL -c last -N 2 -init 1 -I 500 -S 1234 
