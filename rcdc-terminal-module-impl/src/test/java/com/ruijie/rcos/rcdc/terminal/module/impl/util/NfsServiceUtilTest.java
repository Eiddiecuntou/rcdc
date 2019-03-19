package com.ruijie.rcos.rcdc.terminal.module.impl.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.SimpleCmdReturnValueResolver;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.shell.ShellCommandRunner;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月4日
 * 
 * @author ls
 */
public class NfsServiceUtilTest {

    @Mocked
    private ShellCommandRunner runner;

    /**
     * 测试startService，启动失败
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testStartServiceFail() throws BusinessException {

        new Expectations() {
            {
                runner.execute((SimpleCmdReturnValueResolver) any);
                result = new BusinessException("key");
            }
        };
        try {
            NfsServiceUtil.startService();
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_SYSTEM_CMD_EXECUTE_FAIL, e.getKey());
        }
    }

    /**
     * 测试startService，启动成功
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testStartServiceSuccess() throws BusinessException {
        try {
            NfsServiceUtil.startService();
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * 测试shutDownService，关闭失败
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testShutDownServiceFail() throws BusinessException {

        new Expectations() {
            {
                runner.execute((SimpleCmdReturnValueResolver) any);
                result = new BusinessException("key");
            }
        };
        NfsServiceUtil.shutDownService();
        new Verifications() {
            {
                runner.execute((SimpleCmdReturnValueResolver) any);
                times = 1;
            }
        };
    }

    /**
     * 测试shutDownService，关闭成功
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testShutDownServiceSuccess() throws BusinessException {
        try {
            NfsServiceUtil.shutDownService();
        } catch (Exception e) {
            fail();
        }
    }

}
