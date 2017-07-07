package com.anti.ml.plm;

import com.anti.ml.plm.ftrl.FtrlTrain;
import com.anti.ml.plm.ftrl.Option;
import com.anti.ml.plm.job.PCScheduler;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;

/**
 * Created by luolaihu on 7/4/17.
 */
public class Train {
    public static void main(String[] argv){

        Option opt = new Option();
        try {
            opt.parseOption(argv);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String modelFile = "/home/luolaihu/model";
        FtrlTrain ftrlTrain = new FtrlTrain(opt);
        ftrlTrain.loadModel(modelFile);
        PCScheduler.run(ftrlTrain, 2);
        ftrlTrain.outputModel(modelFile);

    }
}
