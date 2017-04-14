package models;

/**
 * Created by jerry on 4/14/17.
 */
public enum Classifier {

    C50("C5.0", "C50"),
    NAIVE_BAYES("naiveBayes", "e1071"),
    RANDOM_FOREST("randomForest", "randomForest"),
    SVM("svm", "e1071");

    private String classifierName, libraryName;

    Classifier(final String classifierName, final String libraryName) {
        this.classifierName = classifierName;
        this.libraryName = libraryName;
    }

    public String getClassifierName() {
        return classifierName;
    }

    public String getLibraryName() {
        return libraryName;
    }

}
