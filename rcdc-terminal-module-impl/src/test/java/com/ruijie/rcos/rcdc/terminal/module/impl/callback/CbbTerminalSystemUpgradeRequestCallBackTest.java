package com.ruijie.rcos.rcdc.terminal.module.impl.callback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.alibaba.fastjson.JSONObject;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbShineMessageResponse;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.SystemUpgradeTask;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.SystemUpgradeTaskManager;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月23日
 * 
 * @author ls
 */
@RunWith(JMockit.class)
public class CbbTerminalSystemUpgradeRequestCallBackTest {

    @Tested
    private CbbTerminalSystemUpgradeRequestCallBack callBack;
    
    @Injectable
    private SystemUpgradeTaskManager manager;
    
    /**
     * 测试success,参数为空
     * @throws Exception 异常
     */
    @Test
    public void testSuccessArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> callBack.success("", new CbbShineMessageResponse()),
                "terminalId 不能为空");
        ThrowExceptionTester.throwIllegalArgumentException(() -> callBack.success("ss", null),
                "TerminalSystemUpgradeRequest 不能为空");
        assertTrue(true);
    }
    
    /**
     * 测试success,升级成功
     */
    @Test
    public void testSuccess() {
        CbbShineMessageResponse msg = new CbbShineMessageResponse();
        msg.setCode(0);
        JSONObject content = new JSONObject();
        content.put("code", 0);
        msg.setContent(content);
        callBack.success("123", msg);
        
        new Verifications() {
            {
                manager.getTaskByTerminalId("123");
                times = 0;
            }
        };
    }
    
    /**
     * 测试success,升级失败,task为null
     */
    @Test
    public void testSuccessUpgradeFailTaskIsNull() {
        CbbShineMessageResponse msg = new CbbShineMessageResponse();
        msg.setCode(0);
        JSONObject content = new JSONObject();
        content.put("code", -1);
        msg.setContent(content);
        SystemUpgradeTask task = null;
        new Expectations() {
            {
                manager.getTaskByTerminalId("123");
                result = task;
            }
        };
        callBack.success("123", msg);
        assertNull(task);
        new Verifications() {
            {
                manager.getTaskByTerminalId("123");
                times = 1;
            }
        };
    }
    
    /**
     * 测试success,升级失败,task不为null
     */
    @Test
    public void testSuccessUpgradeFailTaskIsNotNull() {
        CbbShineMessageResponse msg = new CbbShineMessageResponse();
        msg.setCode(0);
        JSONObject content = new JSONObject();
        content.put("code", -1);
        msg.setContent(content);
        SystemUpgradeTask task = new SystemUpgradeTask();
        new Expectations() {
            {
                manager.getTaskByTerminalId("123");
                result = task;
            }
        };
        callBack.success("123", msg);
        assertEquals(CbbSystemUpgradeStateEnums.FAIL, task.getState());
        new Verifications() {
            {
                manager.getTaskByTerminalId("123");
                times = 1;
            }
        };
    }

    /**
     * 测试timeout,参数为空
     * @throws Exception 异常
     */
    @Test
    public void testTimeoutArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> callBack.timeout(""), "terminalId 不能为空");
        assertTrue(true);
    }
    
    /**
     * 测试timeout,task为null
     */
    @Test
    public void testTimeoutTaskIsNull() {
        SystemUpgradeTask task = null;
        new Expectations() {
            {
                manager.getTaskByTerminalId("123");
                result = task;
            }
        };
        callBack.timeout("123");
        assertNull(task);
        new Verifications() {
            {
                manager.getTaskByTerminalId("123");
                times = 1;
            }
        };
    }
    
    /**
     * 测试timeout,task不为null
     */
    @Test
    public void testTimeoutTaskIsNotNull() {
        SystemUpgradeTask task = new SystemUpgradeTask();
        new Expectations() {
            {
                manager.getTaskByTerminalId("123");
                result = task;
            }
        };
        callBack.timeout("123");
        assertEquals(CbbSystemUpgradeStateEnums.FAIL, task.getState());
        new Verifications() {
            {
                manager.getTaskByTerminalId("123");
                times = 1;
            }
        };
    }
}
