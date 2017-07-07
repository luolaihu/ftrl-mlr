package com.anti.ml.plm.job;

import com.anti.ml.plm.utils.Cache;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by luolaihu on 7/3/17.
 */
public class PCScheduler {

    public static void run(PCTask task, int t_num) {
        PCTask pcTask = task;
        int threadNum = t_num;

        ExecutorService productExecutor = Executors.newFixedThreadPool(1);
        productExecutor.execute(new Runnable() {
            @Override
            public void run() {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String line;
                int num = 0;
                try {
                    while ((line = br.readLine()) != null) {
                        try {
                            Cache.samples.put(line);
                            num++;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        productExecutor.shutdown();

        List<Future<Boolean>> futures = new ArrayList<>();
        ExecutorService workExecutor = Executors.newFixedThreadPool(t_num);
        for (int i = 0; i < threadNum; i++) {
            Future<Boolean> future = workExecutor.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    while (true) {
                        try {
                            String strSample = Cache.samples.poll(1, TimeUnit.MINUTES);
                            if (strSample == null){
                                break;
                            }
                            List<String> samples = new ArrayList<>();
                            samples.add(strSample);
                            pcTask.runTask(samples);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    return true;
                }
            });
            futures.add(future);
        }
        workExecutor.shutdown();
        for(Future<Boolean> future: futures){
            try {
                Boolean result = future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Finish!");
    }
}
