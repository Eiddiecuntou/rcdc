package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbShineMessageResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.callback.CbbTerminalCallback;
import com.ruijie.rcos.sk.commkit.base.message.base.BaseMessage;
import mockit.Injectable;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/13
 *
 * @author Jarman
 */
@RunWith(JMockit.class)
public class AsyncRequestCallBackTest {

    @Injectable
    private CbbTerminalCallback callback;

    /**
     * 测试回调成功
     */
    @Test
    public void testSuccess() {
        try {
            String terminalId = "123";
            String action = "test";
            CbbShineMessageResponse data = new CbbShineMessageResponse();
            data.setCode(200);
            data.setContent("hello");

            AsyncRequestCallBack callBack = new AsyncRequestCallBack(terminalId, callback);
            BaseMessage message = new BaseMessage(action, JSON.toJSON(data));
            callBack.success(message);
        } catch (Exception e) {
            fail();
        }

        new Verifications() {
            {
                callback.success(anyString, (CbbShineMessageResponse) any);
                times = 1;
            }
        };
    }

    /**
     * 测试回调成功,参数BaseMessage为空
     */
    @Test
    public void testSuccessArgumentBaseMessageIsNull() {
        try {
            AsyncRequestCallBack callBack = new AsyncRequestCallBack("123", callback);
            callBack.success(null);
            fail();
        } catch (Exception e) {
            assertEquals("baseMessage参数不能为空", e.getMessage());
        }
    }
    
    /**
     * 测试回调成功,参数Action为空
     */
    @Test
    public void testSuccessArgumentActionIsBlank() {
        try {
            AsyncRequestCallBack callBack = new AsyncRequestCallBack("123", callback);
            BaseMessage baseMessage = new BaseMessage<CbbShineMessageResponse>("", null);
            callBack.success(baseMessage);
            fail();
        } catch (Exception e) {
            assertEquals("action不能为空", e.getMessage());
        }
    }
    
    /**
     * 测试回调成功,IllegalArgumentException
     */
    @Test
    public void testSuccessIllegalArgumentException() {
        try {
            AsyncRequestCallBack callBack = new AsyncRequestCallBack("123", callback);
            BaseMessage baseMessage = new BaseMessage<CbbShineMessageResponse>("action", null);
            callBack.success(baseMessage);
            fail();
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("执行syncRequest方法后shine返回的应答消息不能为空。data:"));
        }
    }
    
    /**
     * 测试回调超时
     */
    @Test
    public void testTimeout() {
        try {
            String terminalId = "123";
            String action = "test";
            Object data = "";
            AsyncRequestCallBack callBack = new AsyncRequestCallBack(terminalId, callback);
            callBack.timeout(null);
        } catch (Exception e) {
            fail();
        }

        new Verifications() {
            {
                callback.timeout("123");
                times = 1;
            }
        };
    }
}
