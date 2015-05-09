require(phyclust)
explabels = c(rep(0,10), rep(1,7), rep(2,10), rep(3,10)) + 1
print(explabels)
predlabels =  c(rep(1,7), rep(2,3), rep(1,4), rep(3,3), rep(0,10), rep(1,3), rep(2,7)) + 1
print(predlabels)
RRand(explabels, predlabels)

#   Rand adjRand  Eindex 
# 0.6491  0.0000  1.2097 