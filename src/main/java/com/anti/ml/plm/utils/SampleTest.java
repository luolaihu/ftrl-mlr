package com.anti.ml.plm.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by luolaihu on 7/3/17.
 */
public class SampleTest {
    public static void main(String[] argv) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line;
        try {
            while ((line = br.readLine()) != null) {
                Sample sample = new Sample(line);
                System.out.println(sample.getY());
                System.out.println(sample.getX().get(0).getKey());
                System.out.println(sample.getX().get(0).getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
