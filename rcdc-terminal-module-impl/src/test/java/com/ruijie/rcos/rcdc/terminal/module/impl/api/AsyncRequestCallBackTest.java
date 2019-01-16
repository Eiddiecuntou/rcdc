package com.ruijie.rcos.rcdc.terminal.module.impl.api;

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
                callback.success((CbbShineMessageResponse) any);
                times = 1;
            }
        };
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
