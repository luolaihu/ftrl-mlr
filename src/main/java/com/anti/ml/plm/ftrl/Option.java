package com.anti.ml.plm.ftrl;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

/**
 * Created by luolaihu on 7/3/17.
 */
public class Option {
    public String model_path, init_m_path;
    public double u_mean, u_stdev, w_mean, w_stdev;
    public double u_alpha, u_beta, u_l1, u_l2;
    public double w_alpha, w_beta, w_l1, w_l2;
    public int threads_num, piece_num;
    public Boolean u_bias, w_bias, b_init;

    public Option() {
        u_bias = true;
        w_bias = true;
        piece_num = 4;
        u_mean = 0.0;
        u_stdev = 0.1;
        w_mean = 0.0;
        w_stdev = 0.1;
        u_alpha = 0.05;
        u_beta = 1.0;
        u_l1 = 0.1;
        u_l2 = 5.0;
        w_alpha = 0.05;
        w_beta = 1.0;
        w_l1 = 0.1;
        w_l2 = 5.0;
        threads_num = 1;
        b_init = false;
    }

    public void parseOption(String[] args) throws Exception {
        int argc = args.length;
        if (0 == argc) {
//            throw new Exception("invalid command\n");
            System.out.println("user default params ");
        }
        for (int i = 0; i < argc; ++i) {
            if (args[i].compareToIgnoreCase("-m") == 0) {
                if (i == argc - 1)
                    throw new Exception("invalid command\n");
                model_path = args[++i];
            } else if (args[i].compareToIgnoreCase("-u_bias") == 0) {
                if (i == argc - 1)
                    throw new Exception("invalid command\n");
                u_bias = (0 == Ints.tryParse(args[++i])) ? false : true;
            } else if (args[i].compareToIgnoreCase("-w_bias") == 0) {
                if (i == argc - 1)
                    throw new Exception("invalid command\n");
                w_bias = (0 == Ints.tryParse(args[++i])) ? false : true;
            } else if (args[i].compareToIgnoreCase("-piece_num") == 0) {
                if (i == argc - 1)
                    throw new Exception("invalid command\n");
                piece_num = Ints.tryParse(args[++i]);
            } else if (args[i].compareToIgnoreCase("-u_stdev") == 0) {
                if (i == argc - 1)
                    throw new Exception("invalid command\n");
                u_stdev = Doubles.tryParse(args[++i]);
            } else if (args[i].compareToIgnoreCase("-w_stdev") == 0) {
                if (i == argc - 1)
                    throw new Exception("invalid command\n");
                w_stdev = Doubles.tryParse(args[++i]);
            } else if (args[i].compareToIgnoreCase("-w_alpha") == 0) {
                if (i == argc - 1)
                    throw new Exception("invalid command\n");
                w_alpha = Doubles.tryParse(args[++i]);
            } else if (args[i].compareToIgnoreCase("-w_beta") == 0) {
                if (i == argc - 1)
                    throw new Exception("invalid command\n");
                w_beta = Doubles.tryParse(args[++i]);
            } else if (args[i].compareToIgnoreCase("-w_l1") == 0) {
                if (i == argc - 1)
                    throw new Exception("invalid command\n");
                w_l1 = Doubles.tryParse(args[++i]);
            } else if (args[i].compareToIgnoreCase("-w_l2") == 0) {
                if (i == argc - 1)
                    throw new Exception("invalid command\n");
                w_l2 = Doubles.tryParse(args[++i]);
            } else if (args[i].compareToIgnoreCase("-u_alpha") == 0) {
                if (i == argc - 1)
                    throw new Exception("invalid command\n");
                u_alpha = Doubles.tryParse(args[++i]);
            } else if (args[i].compareToIgnoreCase("-u_beta") == 0) {
                if (i == argc - 1)
                    throw new Exception("invalid command\n");
                u_beta = Doubles.tryParse(args[++i]);
            } else if (args[i].compareToIgnoreCase("-u_l1") == 0) {
                if (i == argc - 1)
                    throw new Exception("invalid command\n");
                u_l1 = Doubles.tryParse(args[++i]);
            } else if (args[i].compareToIgnoreCase("-u_l2") == 0) {
                if (i == argc - 1)
                    throw new Exception("invalid command\n");
                u_l2 = Doubles.tryParse(args[++i]);
            } else if (args[i].compareToIgnoreCase("-core") == 0) {
                if (i == argc - 1)
                    throw new Exception("invalid command\n");
                threads_num = Ints.tryParse(args[++i]);
            } else if (args[i].compareToIgnoreCase("-im") == 0) {
                if (i == argc - 1)
                    throw new Exception("invalid command\n");
                init_m_path = args[++i];
                b_init = true; //if im field exits , that means b_init = true !
            } else {
                throw new Exception("invalid command\n");
            }
        }
    }
}
