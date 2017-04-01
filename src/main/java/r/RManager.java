package r;

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

    public void trainDataFromFile(final String filePath) {
        mRengine.eval("marc1_c99 <- read.csv('" + filePath + "')");
        mRengine.eval("marc1_c99_ids <- cbind(marc1_c99[,0:4])");
        mRengine.eval("marc1_c99_train <- cbind(marc1_c99[,5:12])");
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
