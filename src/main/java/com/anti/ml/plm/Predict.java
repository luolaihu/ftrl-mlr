package com.anti.ml.plm;

import com.anti.ml.plm.ftrl.FtrlPredict;
import com.anti.ml.plm.ftrl.Option;
import com.anti.ml.plm.job.PCScheduler;

/**
 * Created by luolaihu on 7/4/17.
 */
public class Predict {
    public static void main(String[] argv){

        Option opt = new Option();
        try {
            opt.parseOption(argv);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String modelFile = "/home/luolaihu/model";
        String outputFile = "/home/luolaihu/output";
        FtrlPredict ftrlPredict = new FtrlPredict(4, modelFile, outputFile);
        PCScheduler.run(ftrlPredict, 2);

    }
}
