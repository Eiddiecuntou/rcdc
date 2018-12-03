package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbShineMessageResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.callback.CbbTerminalCallback;
import com.ruijie.rcos.rcdc.terminal.module.impl.callback.AsyncRequestCallBack;
import com.ruijie.rcos.sk.commkit.base.message.base.BaseMessage;
import mockit.Injectable;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.fail;

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
     * 测试成功情况
     */
    @Test
    public void testSuccess() {
        try {
            String terminalId = "123";
            String action = "test";
            Object data = "";
            AsyncRequestCallBack callBack = new AsyncRequestCallBack(terminalId, callback);
            BaseMessage message = new BaseMessage(action, data);
            callBack.success(message);
        } catch (Exception e) {
            fail();
        }

        new Verifications() {
            {
                callback.success((CbbShineMessageResponse) any);
                times = 1;
            }
        };
    }

    /**
     * 测试超时情况
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
                callback.timeout(anyString);
                times = 1;
            }
        };
    }
}
