package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import static org.junit.Assert.fail;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.GatherLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.GatherLogStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.StateEnums;
import com.ruijie.rcos.sk.commkit.base.message.base.BaseMessage;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/8
 *
 * @author Jarman
 */
@RunWith(JMockit.class)
public class GatherLogRequestCallbackImplTest {


    @Injectable
    private GatherLogCacheManager gatherLogCacheManager;

    /**
     * 测试返回正常应答数据
     */
    @Test
    public void testSuccessForOk() {
        new Expectations() {
            {
                gatherLogCacheManager.updateState(anyString, (GatherLogStateEnums) any, anyString);
                result = null;
            }
        };

        new MockUp<GatherLogRequestCallbackImpl>() {
            private String terminalId;

            @Mock
            public void $init(String terminalId) {
                this.terminalId = terminalId;
            }
        };
        GatherLogRequestCallbackImpl requestCallback = new GatherLogRequestCallbackImpl("123");
        Deencapsulation.setField(requestCallback, "gatherLogCacheManager", gatherLogCacheManager);
        try {
            String action = "testAction";
            String data = getJsonData(StateEnums.SUCCESS);
            BaseMessage message = new BaseMessage(action, data);
            requestCallback.success(message);
        } catch (Exception e) {
            fail();
        }

        new Verifications() {
            {
                gatherLogCacheManager.updateState(anyString, (GatherLogStateEnums) any, anyString);
                times = 1;
                gatherLogCacheManager.updateState(anyString, GatherLogStateEnums.FAILURE);
                times = 0;

            }
        };
    }

    /**
     * 测试返回业务异常应答数据
     */
    @Test
    public void testSuccessForFailure() {
        new Expectations() {
            {
                gatherLogCacheManager.updateState(anyString, (GatherLogStateEnums) any);
                result = null;
            }
        };

        new MockUp<GatherLogRequestCallbackImpl>() {
            private String terminalId;

            @Mock
            public void $init(String terminalId) {
                this.terminalId = terminalId;
            }
        };
        GatherLogRequestCallbackImpl requestCallback = new GatherLogRequestCallbackImpl("123");
        Deencapsulation.setField(requestCallback, "gatherLogCacheManager", gatherLogCacheManager);
        try {
            String action = "testAction";
            String data = getJsonData(StateEnums.FAILURE);
            BaseMessage message = new BaseMessage(action, data);
            requestCallback.success(message);
        } catch (Exception e) {
            fail();
        }

        new Verifications() {
            {
                gatherLogCacheManager.updateState(anyString, (GatherLogStateEnums) any, anyString);
                times = 0;
                gatherLogCacheManager.updateState(anyString, GatherLogStateEnums.FAILURE);
                times = 1;

            }
        };
    }

    private String getJsonData(StateEnums state) {
        Map<String, Object> map = new HashMap<>();
        map.put("errorCode", state);
        map.put("msg", "log.zip");
        return JSON.toJSONString(map);
    }

    
    /**
     * 测试收集日志超时
     */
    @Test
    public void timeout() {
        new Expectations() {
            {
                gatherLogCacheManager.updateState(anyString, (GatherLogStateEnums) any);
                result = null;
            }
        };

        new MockUp<GatherLogRequestCallbackImpl>() {
            private String terminalId;

            @Mock
            public void $init(String terminalId) {
                this.terminalId = terminalId;
            }
        };
        GatherLogRequestCallbackImpl requestCallback = new GatherLogRequestCallbackImpl("123");
        Deencapsulation.setField(requestCallback, "gatherLogCacheManager", gatherLogCacheManager);

        try {
            requestCallback.timeout(null);
        } catch (Exception e) {
            fail();
        }

        new Verifications() {
            {
                gatherLogCacheManager.updateState(anyString, GatherLogStateEnums.FAILURE);
                times = 1;
            }
        };


    }
}
