package com.anti.ml.plm.ftrl;

import com.anti.ml.plm.utils.MathUtil;
import com.google.common.collect.Lists;
import javafx.util.Pair;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by luolaihu on 7/3/17.
 */
public class FtrlModel {
    public FtrlModelUnit muBias;
    public ConcurrentHashMap<String, FtrlModelUnit> muMap;

    public int piece_num;
    public double u_stdev;
    public double u_mean;
    public double w_stdev;
    public double w_mean;

    Lock mtx = new ReentrantLock();
    Lock mtx_bias = new ReentrantLock();

    public FtrlModel(int piece_num) {
        this.piece_num = piece_num;
        u_mean = 0.0;
        u_stdev = 0.0;
        w_mean = 0.0;
        w_stdev = 0.0;
        muBias = null;
        muMap = new ConcurrentHashMap<>();

    }

    public FtrlModel(int piece_num, double _u_mean, double _u_stdev, double _w_mean, double _w_stdev) {
        this.piece_num = piece_num;
        u_mean = _u_mean;
        u_stdev = _u_stdev;
        w_mean = _w_mean;
        w_stdev = _w_stdev;
        muBias = null;
        muMap = new ConcurrentHashMap<>();
    }

    public FtrlModelUnit getOrInitModelUnit(String index) {
        mtx.lock();
        try {
            FtrlModelUnit mu = muMap.get(index);
            if (mu == null) {
                FtrlModelUnit pMU = new FtrlModelUnit(piece_num, u_mean, u_stdev, w_mean, w_stdev);
                muMap.put(index, pMU);
                return pMU;
            } else {
                return mu;
            }
        } finally {
            mtx.unlock();
        }
    }

    public FtrlModelUnit getOrInitModelUnitBias() {
        mtx_bias.lock();
        try {
            if (null == muBias) {
                muBias = new FtrlModelUnit(piece_num, 0, 0, 0, 0);
            }
            return muBias;
        } finally {
            mtx_bias.unlock();
        }
    }

    public double get_uTx(List<Pair<String, Double>> x, FtrlModelUnit muBias, Map<String, FtrlModelUnit> muMap, int f) {
        double result = 0;
        result += muBias.u.get(f);
        for (Pair<String, Double> ix : x) {
            if (muMap.get(ix.getKey()) != null) {
                result += muMap.get(ix.getKey()).u.get(f) * ix.getValue();
            }
        }
        return result;
    }

    public double get_wTx(List<Pair<String, Double>> x, FtrlModelUnit muBias, Map<String, FtrlModelUnit> muMap, int f) {
        double result = 0;
        result += muBias.w.get(f);
        for (Pair<String, Double> ix : x) {
            if (muMap.get(ix.getKey()) != null) {
                result += muMap.get(ix.getKey()).w.get(f) * ix.getValue();
            }
        }
        return result;
    }

    public double getScore(List<Pair<String, Double>> x, FtrlModelUnit muBias, Map<String, FtrlModelUnit> muMap) {
        ArrayList<Double> uTx = new ArrayList<>(piece_num);
        double max_uTx = Double.MIN_VALUE;

        for (int f = 0; f < piece_num; ++f) {
            uTx.add(f, get_uTx(x, muBias, muMap, f));
            if (uTx.get(f) > max_uTx) max_uTx = uTx.get(f);
        }
        double numerator = 0.0;
        double denominator = 0.0;
        for (int f = 0; f < piece_num; ++f) {
            uTx.set(f, Math.exp(uTx.get(f) - max_uTx));
            double wTx = get_wTx(x, muBias, muMap, f);
            double s_wx = MathUtil.sigmoid(wTx);
            numerator += uTx.get(f) * s_wx;
            denominator += uTx.get(f);
        }
        return numerator / denominator;
    }

    public void outputModel(String modelFile) {
        // todo format output
        try {
            File file = new File(modelFile);
            Files.deleteIfExists(file.toPath());
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(modelFile));
            outputStreamWriter.write(muBias.outputModel() + "\r\n");
            for (Map.Entry<String, FtrlModelUnit> entry: muMap.entrySet()){
                outputStreamWriter.write(entry.getKey() + " " + entry.getValue().outputModel() + "\r\n");
            }
            outputStreamWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean loadModel(String modelFile) {
        // todo parse format input
        File file = new File(modelFile);
        if (!file.exists()){
            System.out.println("Input Model not Exists!");
            return true;
        }
        String line = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(modelFile)));
            line = bufferedReader.readLine();
            muBias = new FtrlModelUnit(piece_num, line.split(" "));
            while ((line = bufferedReader.readLine()) != null){
                String index = line.substring(0, line.indexOf(" "));
                String[] items = line.substring(line.indexOf(" ") + 1, line.length()).split(" ");
                muMap.put(index, new FtrlModelUnit(piece_num, items));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
