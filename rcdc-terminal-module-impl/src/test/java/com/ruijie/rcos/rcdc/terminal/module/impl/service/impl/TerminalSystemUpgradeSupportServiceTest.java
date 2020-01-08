package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.SimpleCmdReturnValueResolver;
import com.ruijie.rcos.rcdc.terminal.module.impl.quartz.handler.SystemUpgradeQuartzHandler;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.shell.ShellCommandRunner;
import com.ruijie.rcos.sk.base.shell.ShellCommandRunner.ReturnValueResolver;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;

import mockit.*;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月28日
 * 
 * @author ls
 */
@RunWith(SkyEngineRunner.class)
public class TerminalSystemUpgradeSupportServiceTest {

    @Tested
    private TerminalSystemUpgradeSupportService service;

    @Injectable
    private SystemUpgradeQuartzHandler systemUpgradeQuartzHandler;

    @Injectable
    private TerminalSystemUpgradeDAO systemUpgradeDAO;

    @Mocked
    private ShellCommandRunner runner;

    /**
     * 初始化
     */
    @Before
    public void before() {
        ExecutorService executorService = new MockedExecutorService();
        Deencapsulation.setField(TerminalSystemUpgradeSupportService.class, "CLOSE_SERVICE_THREAD_POOL", executorService);
    }

    /**
     * 测试closeSystemUpgradeService,upgradeTaskFuture和supportServiceFuture都不为空
     * 
     * @param upgradeTaskFuture mock对象
     * @param supportServiceFuture mock对象
     * @throws BusinessException 异常
     */
    @Test
    public void testCloseSystemUpgradeServiceAllIsNotNull(@Injectable ScheduledFuture<?> upgradeTaskFuture,
            @Injectable ScheduledFuture<?> supportServiceFuture) throws BusinessException {

        Deencapsulation.setField(TerminalSystemUpgradeSupportService.class, "UPGRADE_TASK_FUTURE", upgradeTaskFuture);
        service.closeSystemUpgradeService();

        new Verifications() {
            {
                upgradeTaskFuture.cancel(true);
                times = 1;
            }
        };
    }

    /**
     * 测试closeSystemUpgradeService,upgradeTaskFuture和supportServiceFuture都为空
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testCloseSystemUpgradeService(@Mocked ScheduledFuture UPGRADE_TASK_FUTURE) throws BusinessException {
        Deencapsulation.setField(TerminalSystemUpgradeSupportService.class, "UPGRADE_TASK_FUTURE", null);
        service.closeSystemUpgradeService();
        new Verifications() {
            {
                UPGRADE_TASK_FUTURE.cancel(true);
                times = 0;
            }
        };
    }

    /**
     * 测试openSystemUpgradeService，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testIsTerminalOnlineArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> service.openSystemUpgradeService(null), "packageEntity can not be null");
        assertTrue(true);
    }

    /**
     * 测试openSystemUpgradeService，
     * 
     * @throws BusinessException 异常
     * @throws InterruptedException ex
     */
    @Test
    public void testOpenSystemUpgradeService() throws BusinessException, InterruptedException, IOException {

        Deencapsulation.setField(TerminalSystemUpgradeSupportService.class, "UPGRADE_TASK_FUTURE", null);
        TerminalSystemUpgradePackageEntity packageEntity = new TerminalSystemUpgradePackageEntity();
        packageEntity.setFilePath("/a/b.iso");

        new MockUp<Files>() {
            @Mock
            public Path copy(Path source, Path target, CopyOption... options) {
                // test
                return null;
            }
        };

        new MockUp<File>() {

            @Mock
            public boolean isFile() {
                return true;
            }

            @Mock
            public boolean isDirectory() {
                return true;
            }

            @Mock
            public boolean mkdirs() {
                return true;
            }
        };


        service.openSystemUpgradeService(packageEntity);

        Thread.sleep(1000);

        new Verifications() {
            {
                Files.copy((Path) any, (Path) any, (CopyOption) any);
                times = 1;
            }
        };
    }

    /**
     * 
     * Description: Function Description
     * Copyright: Copyright (c) 2019
     * Company: Ruijie Co., Ltd.
     * Create Time: 2019年3月7日
     * 
     * @author ls
     */
    private class MockedExecutorService implements ExecutorService {

        @Override
        public void execute(Runnable command) {
            Assert.notNull(command, "command can not be null");
            command.run();
        }

        @Override
        public void shutdown() {

        }

        @Override
        public List<Runnable> shutdownNow() {
            //
            return null;
        }

        @Override
        public boolean isShutdown() {
            //
            return false;
        }

        @Override
        public boolean isTerminated() {
            //
            return false;
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            //
            return false;
        }

        @Override
        public <T> Future<T> submit(Callable<T> task) {
            //
            return null;
        }

        @Override
        public <T> Future<T> submit(Runnable task, T result) {
            //
            return null;
        }

        @Override
        public Future<?> submit(Runnable task) {
            //
            return null;
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
            //
            return null;
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
            //
            return null;
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
            //
            return null;
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
                throws InterruptedException, ExecutionException, TimeoutException {
            //
            return null;
        }

    }
}
