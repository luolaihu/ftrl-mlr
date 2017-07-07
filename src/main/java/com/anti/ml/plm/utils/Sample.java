package com.anti.ml.plm.utils;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luolaihu on 7/3/17.
 */
public class Sample {
    int y;
    List<Pair<String, Double>> x;

    public Sample() {
    }

    public Sample(String line) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(line), "Sample line is Null or Empty");
        y = 0;
        //parse svm
        line = line.trim();
        int label = Ints.tryParse(line.substring(0, line.indexOf(" ")));
        y = label > 0 ? 1 : -1;
        String[] items = line.substring(line.indexOf(" ")).trim().split(" ");
        x = new ArrayList<Pair<String, Double>>(items.length);
        for (String item : items) {
            String[] kv = item.split(":");
            String key = kv[0];
            if (Strings.isNullOrEmpty(key)){
                continue;
            }
            double value = Doubles.tryParse(kv[1]);
            x.add(new Pair<String, Double>(key, value));
        }
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public List<Pair<String, Double>> getX() {
        return x;
    }

    public void setX(List<Pair<String, Double>> x) {
        this.x = x;
    }
}
