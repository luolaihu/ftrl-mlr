package com.anti.ml.plm.ftrl;

import com.anti.ml.plm.utils.MathUtil;
import com.google.common.primitives.Doubles;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by luolaihu on 7/3/17.
 */
//每一个特征维度的模型单元
public class FtrlModelUnit {
    public ArrayList<Double> u;
    public ArrayList<Double> u_n;
    public ArrayList<Double> u_z;
    public ArrayList<Double> w;
    public ArrayList<Double> w_n;
    public ArrayList<Double> w_z;
    public Lock mtx = new ReentrantLock();

    public FtrlModelUnit(int piece_num, double u_mean, double u_stdev, double w_mean, double w_stdev) {
        u = new ArrayList<>(piece_num);
        u_n = new ArrayList<>(piece_num);
        u_z = new ArrayList<>(piece_num);
        for (int f = 0; f < piece_num; ++f) {
            u.add(f, MathUtil.gaussian(u_mean, u_stdev));
            u_n.add(f, 0.0);
            u_z.add(f, 0.0);
        }
        w = new ArrayList<>(piece_num);
        w_n = new ArrayList<>(piece_num);
        w_z = new ArrayList<>(piece_num);
        for (int f = 0; f < piece_num; ++f) {
            w.add(f, MathUtil.gaussian(w_mean, w_stdev));
            w_n.add(f, 0.0);
            w_z.add(f, 0.0);
        }
    }

    public FtrlModelUnit(int piece_num, String[] modelLineSeg) {
        u = new ArrayList<>(piece_num);
        u_n = new ArrayList<>(piece_num);
        u_z = new ArrayList<>(piece_num);
        w = new ArrayList<>(piece_num);
        w_n = new ArrayList<>(piece_num);
        w_z = new ArrayList<>(piece_num);
        for (int f = 0; f < piece_num; ++f) {
            u.add(f, Doubles.tryParse(modelLineSeg[1 + f]));
            w.add(f, Doubles.tryParse(modelLineSeg[piece_num + 1 + f]));
            u_n.add(f, Doubles.tryParse(modelLineSeg[2 * piece_num + 1 + f]));
            w_n.add(f, Doubles.tryParse(modelLineSeg[3 * piece_num + 1 + f]));
            u_z.add(f, Doubles.tryParse(modelLineSeg[4 * piece_num + 1 + f]));
            w_z.add(f, Doubles.tryParse(modelLineSeg[5 * piece_num + 1 + f]));
        }
    }

    void reinit_u(double u_mean, double u_stdev) {
        int size = u.size();
        for (int f = 0; f < size; ++f) {
            u.set(f, MathUtil.gaussian(u_mean, u_stdev));
        }
    }

    void reinit_w(double w_mean, double w_stdev) {
        int size = w.size();
        for (int f = 0; f < size; ++f) {
            w.set(f, MathUtil.gaussian(w_mean, w_stdev));
        }
    }

    public String outputModel() {
        // todo format output
        StringBuffer sb = new StringBuffer();
        sb.append(""+u.size());
        for (int i = 0; i < u.size(); i++){
            sb.append(" " + u.get(i));
        }
        for (int i = 0; i < u_n.size(); i++){
            sb.append(" " + u_n.get(i));
        }
        for (int i = 0; i < u_z.size(); i++){
            sb.append(" " + u_z.get(i));
        }
        for (int i = 0; i < w.size(); i++){
            sb.append(" " + w.get(i));
        }
        for (int i = 0; i < w_n.size(); i++){
            sb.append(" " + w_n.get(i));
        }
        for (int i = 0; i < w_z.size(); i++){
            sb.append(" " + w_z.get(i));
        }
        return sb.toString();
    }
}
