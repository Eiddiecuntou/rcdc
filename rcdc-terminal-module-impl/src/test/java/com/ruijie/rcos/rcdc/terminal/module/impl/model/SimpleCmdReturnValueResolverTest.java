package com.ruijie.rcos.rcdc.terminal.module.impl.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.Tested;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月5日
 * 
 * @author ls
 */
public class SimpleCmdReturnValueResolverTest {

    @Tested
    private SimpleCmdReturnValueResolver resolver;

    /**
     * 测试resolve，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testResolveArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> resolver.resolve("", 1, "sds"), "command can not be empty");
        ThrowExceptionTester.throwIllegalArgumentException(() -> resolver.resolve("dv", null, "sds"), "exitValue can not be empty");
        assertTrue(true);
    }

    /**
     * 测试resolve，exitValue不为0
     */
    @Test
    public void testResolveExitValueNotZero() {
        try {
            resolver.resolve("dv", 1, "sds");
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_SYSTEM_CMD_EXECUTE_FAIL, e.getKey());
        }
    }

    /**
     * 测试resolve，
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testResolve() throws BusinessException {
        assertEquals("sds", resolver.resolve("dv", 0, "sds"));
    }

}
