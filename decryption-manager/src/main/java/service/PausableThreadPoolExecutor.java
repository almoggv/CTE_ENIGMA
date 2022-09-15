package main.java.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PausableThreadPoolExecutor extends ThreadPoolExecutor {

    private boolean isPaused = false;
    private Lock pauseLock = new ReentrantLock();
    private Condition unpaused = pauseLock.newCondition();

    public PausableThreadPoolExecutor(int corePoolSize, int maxPoolSize, long keepAlineTime, TimeUnit unit, BlockingQueue blockingQueue) {
        super(corePoolSize, maxPoolSize, keepAlineTime, unit, blockingQueue);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        pauseLock.lock();
        try {
            while (isPaused) {
                unpaused.await();
            }
        } catch (InterruptedException e) {
            t.interrupt();
        } finally {
            pauseLock.unlock();
        }
    }

    public void pause() {
        pauseLock.lock();
        try {
            isPaused = true;
        } finally {
            pauseLock.unlock();
        }
    }

    public void resume() {
        pauseLock.lock();
        try {
            isPaused = false;
            unpaused.signal();
        } finally {
            pauseLock.unlock();
        }
    }

}
