package com.anti.ml.plm.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by luolaihu on 7/3/17.
 */
public class Cache {
    public static int QUEUESIZE = 100000;
    public static BlockingQueue<String> samples = new ArrayBlockingQueue<String>(QUEUESIZE);
}
