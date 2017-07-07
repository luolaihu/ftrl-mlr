package com.anti.ml.plm.ftrl;

import com.anti.ml.plm.job.PCTask;
import com.anti.ml.plm.utils.MathUtil;
import com.anti.ml.plm.utils.Sample;
import javafx.util.Pair;

import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by luolaihu on 7/3/17.
 */
public class FtrlTrain implements PCTask {

    public FtrlModel pModel;
    public double u_alpha, u_beta, u_l1, u_l2;
    public double w_alpha, w_beta, w_l1, w_l2;
    public Boolean u_bias;
    public Boolean w_bias;

    public FtrlTrain(Option opt) {
        u_alpha = opt.u_alpha;
        u_beta = opt.u_beta;
        u_l1 = opt.u_l1;
        u_l2 = opt.u_l2;
        w_alpha = opt.w_alpha;
        w_beta = opt.w_beta;
        w_l1 = opt.w_l1;
        w_l2 = opt.w_l2;
        u_bias = opt.u_bias;
        w_bias = opt.w_bias;
        pModel = new FtrlModel(opt.piece_num, opt.u_mean, opt.u_stdev, opt.w_mean, opt.w_stdev);
    }

    public boolean loadModel(String modelFile) {
        // todo
        pModel.loadModel(modelFile);
        return true;
    }

    public void outputModel(String modelFile) {
        pModel.outputModel(modelFile);
    }

    @Override
    public void runTask(List<String> samples) {
        try {
            for (String strSample : samples) {
                Sample sample = new Sample(strSample);
                train(sample.getY(), sample.getX());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //输入一个样本，更新参数
    private void train(int y, List<Pair<String, Double>> x) {
        FtrlModelUnit thetaBias = pModel.getOrInitModelUnitBias();
        int xLen = x.size();
        Map<String, FtrlModelUnit> theta = new HashMap<>();
        for (int i = 0; i < xLen; ++i) {
            String index = x.get(i).getKey();
            theta.put(index, pModel.getOrInitModelUnit(index));
        }
        ArrayList<Double> uTx = new ArrayList<>(pModel.piece_num);
        ArrayList<Double> wTx = new ArrayList<>(pModel.piece_num);
        double max_uTx = Double.MIN_VALUE;

        for (int f = 0; f < pModel.piece_num; ++f) {
            uTx.add(f, pModel.get_uTx(x, thetaBias, theta, f));
            if (uTx.get(f) > max_uTx) max_uTx = uTx.get(f);
            wTx.add(f, pModel.get_wTx(x, thetaBias, theta, f));
        }
        double denominator1 = 0.0;
        double denominator2 = 0.0;
        for (int f = 0; f < pModel.piece_num; ++f) {
            uTx.set(f, Math.exp(uTx.get(f) - max_uTx));
            wTx.set(f, MathUtil.sigmoid(y * wTx.get(f)));
            denominator1 += uTx.get(f);
            denominator2 += uTx.get(f) * wTx.get(f);
        }

        //update u_n, u_z
        for (int i = 0; i <= xLen; ++i) {
            FtrlModelUnit mu = i < xLen ? theta.get(x.get(i).getKey()) : thetaBias;
            double xi = i < xLen ? x.get(i).getValue() : 1.0;
            if (i < xLen || u_bias) {
                for (int f = 0; f < pModel.piece_num; ++f) {
                    mu.mtx.lock();
                    try {
                        double u_gif = xi * uTx.get(f) * (1.0 / denominator1 - wTx.get(f) / denominator2);
                        double u_sif = 1 / u_alpha * (Math.sqrt(mu.u_n.get(f) + u_gif * u_gif) - Math.sqrt(mu.u_n.get(f)));
                        mu.u_z.set(f, mu.u_z.get(f) + (u_gif - u_sif * mu.u.get(f)));
                        mu.u_n.set(f, mu.u_n.get(f) + (u_gif * u_gif));
                    } finally {
                        mu.mtx.unlock();
                    }
                }
            }
        }
        //update w_n, w_z
        for (int i = 0; i <= xLen; ++i) {
            FtrlModelUnit mu = i < xLen ? theta.get(x.get(i).getKey()) : thetaBias;
            double xi = i < xLen ? x.get(i).getValue() : 1.0;
            if (i < xLen || w_bias) {
                for (int f = 0; f < pModel.piece_num; ++f) {
                    mu.mtx.lock();
                    try {
                        double w_gif = -y * xi * uTx.get(f) * wTx.get(f) * (1.0 - wTx.get(f)) / denominator2;
                        double w_sif = 1 / w_alpha * (Math.sqrt(mu.w_n.get(f) + w_gif * w_gif) - Math.sqrt(mu.w_n.get(f)));
                        mu.w_z.set(f, mu.w_z.get(f) + (w_gif - w_sif * mu.u.get(f)));
                        mu.w_n.set(f, mu.w_n.get(f) + (w_gif * w_gif));
                    } finally {
                        mu.mtx.unlock();
                    }
                }
            }
        }
        //update u via FTRL
        for (int i = 0; i <= xLen; ++i) {
            FtrlModelUnit mu = i < xLen ? theta.get(x.get(i).getKey()) : thetaBias;
            if (i < xLen || u_bias) {
                for (int f = 0; f < pModel.piece_num; ++f) {
                    mu.mtx.lock();
                    try {
                        if (Math.abs(mu.u_z.get(f)) <= u_l1) {
                            mu.u.set(f, 0.0);
                        } else {
                            mu.u.set(f, (-1) *
                                    (1 / (u_l2 + (u_beta + Math.sqrt(mu.u_n.get(f))) / u_alpha)) *
                                    (mu.u_z.get(f) - MathUtil.sgn(mu.u_z.get(f)) * u_l1)
                            );
                        }
                    } finally {
                        mu.mtx.unlock();
                    }
                }
            }
        }
        //update w via FTRL
        for (int i = 0; i <= xLen; ++i) {
            FtrlModelUnit mu = i < xLen ? theta.get(x.get(i).getKey()) : thetaBias;
            if (i < xLen || w_bias) {
                for (int f = 0; f < pModel.piece_num; ++f) {
                    mu.mtx.lock();
                    try {
                        if (Math.abs(mu.w_z.get(f)) <= w_l1) {
                            mu.w.set(f, 0.0);
                        } else {
                            mu.w.set(f, (-1) *
                                    (1 / (w_l2 + (w_beta + Math.sqrt(mu.w_n.get(f))) / w_alpha)) *
                                    (mu.w_z.get(f) - MathUtil.sgn(mu.w_z.get(f)) * w_l1)
                            );
                        }
                    } finally {
                        mu.mtx.unlock();
                    }

                }
            }
        }
    }
}
