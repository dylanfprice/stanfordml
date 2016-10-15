#!/bin/sh

#classifier="weka.classifiers.bayes.NaiveBayesMultinomial"
classifier="weka.classifiers.functions.SMO"

java -cp ~/Downloads/weka-3-8-0/weka.jar $classifier -t $1 -T spam_test.arff > "$1_results.txt"
