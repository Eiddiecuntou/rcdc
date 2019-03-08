package com.ruijie.rcos.rcdc.terminal.module.impl.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.IOException;
import org.junit.Test;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月1日
 * 
 * @author ls
 */
public class CmdExecuteUtilTest {

    /**
     * 测试executeCmd，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testExecuteCmdArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> CmdExecuteUtil.executeCmd(null), "cmd 不能为空");
        assertTrue(true);
    }
    
    /**
     * 测试executeCmd，出现异常
     */
    @Test
    public void testExecuteCmdHasException() {
        new MockUp<Runtime>() {
            @Mock
            public Process exec(String command) throws IOException {
                throw new IOException();
            }
        };
        try {
            CmdExecuteUtil.executeCmd("sdsd");
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_SYSTEM_CMD_EXECUTE_FAIL, e.getKey());
        }
    }
    
    /**
     * 测试executeCmd，返回值不等于0
     * @param exec mock对象
     * @throws InterruptedException 异常
     */
    @Test
    public void testExecuteCmdValueNotZero(@Mocked Process exec) throws InterruptedException {
        new MockUp<Runtime>() {
            @Mock
            public Process exec(String command) {
                return exec;
            }
        };
        
        new Expectations() {
            {
                exec.waitFor();
                result = -1;
            }
        };
        try {
            CmdExecuteUtil.executeCmd("sdsd");
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_SYSTEM_CMD_EXECUTE_FAIL, e.getKey());
        }
    }
    
    /**
     * 测试executeCmd，
     * @param exec mock对象
     * @throws InterruptedException 异常
     */
    @Test
    public void testExecuteCmd(@Mocked Process exec) throws InterruptedException {
        new MockUp<Runtime>() {
            @Mock
            public Process exec(String command) {
                return exec;
            }
        };
        
        new Expectations() {
            {
                exec.waitFor();
                result = 0;
            }
        };
        try {
            CmdExecuteUtil.executeCmd("sdsd");
        } catch (BusinessException e) {
            fail();
        }
    }
}
