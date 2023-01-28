package com.rachitsharma300.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

@Singleton
public class ExecService {
    private int NUM_OF_THREADS = 4;
    private ExecutorService es;

    public ExecService() {
        es = Executors.newFixedThreadPool(NUM_OF_THREADS);
    }

    /**
     * Submit a Runnable to be executed by an {@code ExecutorService}
     *
     * @param r runnable
     */
    public void submit(Runnable r) {
        this.es.submit(r);
    }
}
