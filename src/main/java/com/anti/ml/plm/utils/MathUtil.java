package com.anti.ml.plm.utils;

import java.util.Random;

/**
 * Created by luolaihu on 7/3/17.
 */
public class MathUtil {
    static Random random = new Random(7);
    final static double kPrecision = 0.0000000001;

    public static int sgn(double x) {
        if (x > kPrecision)
            return 1;
        else
            return -1;
    }

    public static double uniform() {
        return random.nextDouble();
    }

    public static double gaussian() {
        double u, v, x, y, Q;
        do {
            do {
                u = uniform();
            } while (u == 0.0);

            v = 1.7156 * (uniform() - 0.5);
            x = u - 0.449871;
            y = Math.abs(v) + 0.386595;
            Q = x * x + y * (0.19600 * y - 0.25472 * x);
        } while (Q >= 0.27597 && (Q > 0.27846 || v * v > -4.0 * u * u * Math.log(u)));
        return v / u;
    }

    public static double gaussian(double mean, double stdev) {
        if (0.0 == stdev) {
            return mean;
        } else {
            return mean + stdev * gaussian();
        }
    }

    public static double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }
}
