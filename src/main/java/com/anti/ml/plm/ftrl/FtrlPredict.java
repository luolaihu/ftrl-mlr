package com.anti.ml.plm.ftrl;

import com.anti.ml.plm.job.PCTask;
import com.anti.ml.plm.utils.Sample;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by luolaihu on 7/3/17.
 */
public class FtrlPredict implements PCTask {
    FtrlModel pModel;
    OutputStreamWriter outputStreamWriter;
    Lock mtx = new ReentrantLock();


    @Override
    public void runTask(List<String> samples) {
        mtx.lock();
        try {
            for (String strSample : samples) {
                Sample sample = new Sample(strSample);
                double score = pModel.getScore(sample.getX(), pModel.muBias, pModel.muMap);
                outputStreamWriter.write(sample.getY() + " " + score + "\r\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mtx.unlock();
        }
    }

    public FtrlPredict(int piece_num, String modelFile, String outputFile)
    {
        pModel = new FtrlModel(piece_num);
        if(!pModel.loadModel(modelFile))
        {
            System.out.println("load model error!");
        }
        System.out.println(pModel.muBias.outputModel());
        try {
            outputStreamWriter = new OutputStreamWriter(new FileOutputStream(outputFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
