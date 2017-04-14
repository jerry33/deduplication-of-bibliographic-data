package r;

import models.Classifier;
import models.ConfusionMatrix;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/**
 * Created by jerry on 3/31/17.
 */
public class RManager {

    private static RManager INSTANCE;
    private REXP mRexp;
    private Rengine mRengine;

    private RManager() {}

    public static RManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RManager();
            String args[] = new String[]{"--no-save"};
            INSTANCE.mRengine = new Rengine(args, false, new TextConsole());
            System.out.println("Rengine created, waiting for R");
            if (!INSTANCE.mRengine.waitForR()) {
                throw new RuntimeException("Cannot load R");
            }
        }
        return INSTANCE;
    }

    public void trainDataFromFile(final String filePath, final int startOfIds, final int endOfIds,
                                  final int startOfData, final int endOfData) {
        mRengine.eval("marc1_c99 <- read.csv('" + filePath + "')");
        mRengine.eval("marc1_c99_ids <- cbind(marc1_c99[," + startOfIds + ":" + endOfIds + "])");
        mRengine.eval("marc1_c99_train <- cbind(marc1_c99[," + startOfData + ":" + endOfData + "])");
    }

    public void classifyData(final String filePath, final int startOfIds, final int endOfIds,
                             final int startOfData, final int endOfData) {
        mRengine.eval("marc1_c99_test <- read.csv('" + filePath + "')");
        mRengine.eval("marc1_c99_test_ids <- cbind(marc1_c99_test[," + startOfIds + ":" + endOfIds + "])");
        mRengine.eval("marc1_c99_test <- cbind(marc1_c99_test[," + startOfData + ":" + endOfData + "])");
        mRengine.eval("library(C50)");
        mRengine.eval("c5_c99 <- C5.0(marc1_c99_train[,-8], marc1_c99_train[,8])");
        mRengine.eval("p1_c99 <- predict(c5_c99, marc1_c99_test)");
        mRengine.eval("marc1_c99_test <- cbind(marc1_c99_test_ids, marc1_c99_test)");
        mRengine.eval("marc1_c99_test <- cbind(marc1_c99_test, p1_c99)");
        mRengine.eval("write.csv(marc1_c99_test, '" + filePath + "', row.names = FALSE)");
    }

    public void shuffleData() {
        mRengine.eval("comp_vectors_not_shuffled <- read.csv(\"/Users/jerry/Desktop/git/deduplication-of-bibliographic-data/assets/prod/comp_vectors_all_train2.csv\")");
        mRengine.eval("comp_vectors_shuffled <- comp_vectors_not_shuffled[sample(nrow(comp_vectors_not_shuffled)),]");
        mRengine.eval("write.csv(comp_vectors_shuffled, \"/Users/jerry/Desktop/git/deduplication-of-bibliographic-data/assets/prod/comp_vectors_all_train2_shuffled.csv\", row.names = FALSE)");
    }

    public void trainAndClassifyData2(final Classifier classifier) {
        mRexp = mRengine.eval("marc1 <- read.csv(\"/Users/jerry/Desktop/git/deduplication-of-bibliographic-data/assets/prod/comp_vectors_all_train2_shuffled.csv\")");
        mRexp = mRengine.eval("marc1_c99_train <- marc1[1:61546,]");
        mRexp = mRengine.eval("marc1_c99_test <- marc1[61547:71546,]");
        mRexp = mRengine.eval("marc1_c99_train_ids <- cbind(marc1_c99_train[,0:6])");
        mRexp = mRengine.eval("marc1_c99_train <- cbind(marc1_c99_train[,7:14])");
        mRexp = mRengine.eval("marc1_c99_test_ids <- cbind(marc1_c99_test[,0:6])");
        mRexp = mRengine.eval("marc1_c99_test <- cbind(marc1_c99_test[,7:14])");
//        x = mRengine.eval("install.packages(\"C50\")");
        mRexp = mRengine.eval("library(" + classifier.getLibraryName() + ")");
        mRexp = mRengine.eval("classifier <- " + classifier.getClassifierName() + "(compOverall ~., data = marc1_c99_train)");
        mRexp = mRengine.eval("p1_c99 <- predict(classifier, marc1_c99_test)");
        mRexp = mRengine.eval("library(caret)");
        mRexp = mRengine.eval("confusionMatrix(p1_c99, marc1_c99_test[,8], positive = levels(marc1_c99_test$compOverall)[2])");
        final ConfusionMatrix confusionMatrix = ConfusionMatrix.createFromRexp(mRexp);
        System.out.println(confusionMatrix);
    }

    public REXP trainAndClassifyData() {
        mRexp = mRengine.eval("marc1_c99 <- read.csv(\"/Users/jerry/Desktop/idea/marc_comp_vectors3_with_c99.csv\")");
//        x = rengine.eval("myData$compPersonalName");
//        x = rengine.eval("myData");
        mRexp = mRengine.eval("marc1_c99 <- marc1_c99[sample(nrow(marc1_c99)),]");
        mRexp = mRengine.eval("marc1_c99_train <- marc1_c99[1:2000,]");
        mRexp = mRengine.eval("marc1_c99_test <- marc1_c99[2001:5000,]");
        mRexp = mRengine.eval("column_ids_c99 <- marc1_c99_test[,1]");
        mRexp = mRengine.eval("marc1_c99_train <- cbind(marc1_c99_train[,2:9])");
        mRexp = mRengine.eval("marc1_c99_test <- cbind(marc1_c99_test[,2:9])");
//        x = mRengine.eval("install.packages(\"C50\")");
        mRexp = mRengine.eval("library(C50)");
        mRexp = mRengine.eval("c5_c99 <- C5.0(marc1_c99_train[,-8], marc1_c99_train[,8])");
        mRexp = mRengine.eval("p1_c99 <- predict(c5_c99, marc1_c99_test)");
        mRexp = mRengine.eval("marc1_c99_test <- cbind(marc1_c99_test[,1:8], p1_c99)");
        mRexp = mRengine.eval("marc1_c99_test <- cbind(marc1_c99_test[,1:9], column_ids_c99)");
        mRexp = mRengine.eval("write.csv(marc1_c99_test, \"/Users/jerry/Desktop/idea/jri_test.csv\", row.names = FALSE)");
        mRexp = mRengine.eval("marc1_c99_test");
        System.out.println(mRexp);
        System.out.println(mRexp.asList().at(0).asDoubleArray()[2]);
        System.out.println(mRexp.asList().at(0).asDoubleArray()[3]);
        return mRexp;
    }

}
