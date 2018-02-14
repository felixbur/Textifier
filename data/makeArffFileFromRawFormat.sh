./runPreprocessor.sh $1>t1;
awk -f changeCategories.awk t1 > t2;
cat arffHeader.txt t2 > `basename $1 .txt`.arff;
#rm t1 t2;