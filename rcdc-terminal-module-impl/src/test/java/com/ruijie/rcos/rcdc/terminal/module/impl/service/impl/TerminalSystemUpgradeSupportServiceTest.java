package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

import org.junit.Before;
import org.junit.Test;
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
     * @param nfsServiceUtil mock对象
     * @throws BusinessException 异常
     */
    @Test
    public void testCloseSystemUpgradeServiceAllIsNotNull(@Injectable ScheduledFuture<?> upgradeTaskFuture,
            @Injectable ScheduledFuture<?> supportServiceFuture, @Mocked NfsServiceUtil nfsServiceUtil) throws BusinessException {

        Deencapsulation.setField(TerminalSystemUpgradeSupportService.class, "UPGRADE_TASK_FUTURE", upgradeTaskFuture);
        service.closeSystemUpgradeService();

        new Verifications() {
            {
                NfsServiceUtil.shutDownService();
                times = 1;
                upgradeTaskFuture.cancel(true);
                times = 1;
            }
        };
    }

    /**
     * 测试closeSystemUpgradeService,upgradeTaskFuture和supportServiceFuture都为空
     * 
     * @param nfsServiceUtil mock对象
     * @throws BusinessException 异常
     */
    @Test
    public void testCloseSystemUpgradeService(@Mocked NfsServiceUtil nfsServiceUtil) throws BusinessException {
        Deencapsulation.setField(TerminalSystemUpgradeSupportService.class, "UPGRADE_TASK_FUTURE", null);
        service.closeSystemUpgradeService();
        new Verifications() {
            {
                NfsServiceUtil.shutDownService();
                times = 1;
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
     * 测试openSystemUpgradeService，复制文件失败
     * 
     * @param nfsServiceUtil mock对象
     * @param upgradeTaskFuture mock对象
     * @param supportServiceFuture mock对象
     * @throws BusinessException 异常
     */
    @Test
    public void testIsTerminalOnlineCopyFileFailed(@Mocked NfsServiceUtil nfsServiceUtil, @Injectable ScheduledFuture<?> upgradeTaskFuture,
            @Injectable ScheduledFuture<?> supportServiceFuture) throws BusinessException {

        Deencapsulation.setField(TerminalSystemUpgradeSupportService.class, "UPGRADE_TASK_FUTURE", upgradeTaskFuture);
        TerminalSystemUpgradePackageEntity packageEntity = new TerminalSystemUpgradePackageEntity();
        new MockUp<File>() {
            @Mock
            public boolean exists() {
                return true;
            }
        };
        new Expectations() {
            {
                runner.execute((SimpleCmdReturnValueResolver) any);
                result = new Delegate<ShellCommandRunner>() {
                    int i = 0;

                    String execute(ReturnValueResolver<String> arg0) throws BusinessException {
                        if (i == 1) {
                            i++;
                            throw new BusinessException("key");
                        }
                        i++;
                        return "ss";
                    }
                };
            }
        };
        service.openSystemUpgradeService(packageEntity);

        new Verifications() {
            {
                runner.execute((SimpleCmdReturnValueResolver) any);
                times = 3;
            }
        };
    }

    /**
     * 测试openSystemUpgradeService，
     * 
     * @param nfsServiceUtil mock对象
     * @throws BusinessException 异常
     * @throws InterruptedException ex
     */
    @Test
    public void testIsTerminalOnline(@Mocked NfsServiceUtil nfsServiceUtil) throws BusinessException, InterruptedException {

        Deencapsulation.setField(TerminalSystemUpgradeSupportService.class, "UPGRADE_TASK_FUTURE", null);
        TerminalSystemUpgradePackageEntity packageEntity = new TerminalSystemUpgradePackageEntity();
        new MockUp<File>() {
            @Mock
            public boolean exists() {
                return false;
            }

            @Mock
            public boolean mkdir() {
                return true;
            }
        };

        service.openSystemUpgradeService(packageEntity);

        Thread.sleep(1000);

        new Verifications() {
            {
                runner.execute((SimpleCmdReturnValueResolver) any);
                times = 3;
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
