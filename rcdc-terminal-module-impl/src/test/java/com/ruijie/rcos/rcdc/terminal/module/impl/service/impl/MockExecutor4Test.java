package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.sk.base.concurrent.ThreadExecutor;
import org.springframework.util.Assert;

import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/11/3
 *
 * @author nting
 */
public class MockExecutor4Test implements ThreadExecutor {


    @Override
    public ScheduledFuture<?> scheduleWithCron(Runnable runnable, String s) throws ParseException {
        // for test
        return null;
    }

    @Override
    public int getMaxThreadNum() {
        // for test
        return 0;
    }

    @Override
    public int getQueueSize() {
        // for test
        return 0;
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        // for test
        return null;
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        // for test
        return null;
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        // for test
        return null;
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        // for test
        return null;
    }

    @Override
    public void shutdown() {

    }

    @Override
    public List<Runnable> shutdownNow() {
        // for test
        return null;
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        // for test
        return null;
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        // for test
        return null;
    }

    @Override
    public Future<?> submit(Runnable task) {
        // for test
        return null;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        // for test
        return null;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        // for test
        return null;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        // for test
        return null;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        // for test
        return null;
    }

    @Override
    public void execute(Runnable command) {
        Assert.notNull(command, "command can not be null");
        command.run();
    }

    @Override
    public String getThreadPoolName() {
        // for test
        return "MockExecutor4Test";
    }
}
