package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.CollectLogStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.StateEnums;
import com.ruijie.rcos.sk.commkit.base.message.base.BaseMessage;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.fail;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/8
 *
 * @author Jarman
 */
@RunWith(JMockit.class)
public class CollectLogRequestCallbackImplTest {


    @Injectable
    private CollectLogCacheManager collectLogCacheManager;

    /**
     * 测试返回正常应答数据
     */
    @Test
    public void testSuccessForOk() {
        new Expectations() {{
            collectLogCacheManager.updateState(anyString, (CollectLogStateEnums) any, anyString);
            result = null;
        }};

        new MockUp<CollectLogRequestCallbackImpl>() {
            private String terminalId;

            @Mock
            public void $init(String terminalId) {
                this.terminalId = terminalId;
            }
        };
        CollectLogRequestCallbackImpl requestCallback = new CollectLogRequestCallbackImpl(collectLogCacheManager, "123");
        Deencapsulation.setField(requestCallback, "collectLogCacheManager", collectLogCacheManager);
        try {
            String action = "testAction";
            String data = getJsonData(StateEnums.SUCCESS);
            BaseMessage message = new BaseMessage(action, data);
            requestCallback.success(message);
        } catch (Exception e) {
            fail();
        }

        new Verifications() {{
            collectLogCacheManager.updateState(anyString, (CollectLogStateEnums) any, anyString);
            times = 1;
            collectLogCacheManager.updateState(anyString, CollectLogStateEnums.FAILURE);
            times = 0;

        }};
    }

    /**
     * 测试返回业务异常应答数据
     */
    @Test
    public void testSuccessForFailure() {
        new Expectations() {{
            collectLogCacheManager.updateState(anyString, (CollectLogStateEnums) any);
            result = null;
        }};

        new MockUp<CollectLogRequestCallbackImpl>() {
            private String terminalId;

            @Mock
            public void $init(String terminalId) {
                this.terminalId = terminalId;
            }
        };
        CollectLogRequestCallbackImpl requestCallback = new CollectLogRequestCallbackImpl(collectLogCacheManager, "123");
        Deencapsulation.setField(requestCallback, "collectLogCacheManager", collectLogCacheManager);
        try {
            String action = "testAction";
            String data = getJsonData(StateEnums.FAILURE);
            BaseMessage message = new BaseMessage(action, data);
            requestCallback.success(message);
        } catch (Exception e) {
            fail();
        }

        new Verifications() {{
            collectLogCacheManager.updateState(anyString, (CollectLogStateEnums) any, anyString);
            times = 0;
            collectLogCacheManager.updateState(anyString, CollectLogStateEnums.FAILURE);
            times = 1;

        }};
    }

    private String getJsonData(StateEnums state) {
        Map<String, Object> map = new HashMap<>();
        map.put("errorCode", state);
        map.put("msg", "log.zip");
        return JSON.toJSONString(map);
    }


    @Test
    public void timeout() {
        new Expectations() {{
            collectLogCacheManager.updateState(anyString, (CollectLogStateEnums) any);
            result = null;
        }};

        new MockUp<CollectLogRequestCallbackImpl>() {
            private String terminalId;

            @Mock
            public void $init(String terminalId) {
                this.terminalId = terminalId;
            }
        };
        CollectLogRequestCallbackImpl requestCallback = new CollectLogRequestCallbackImpl(collectLogCacheManager, "123");
        Deencapsulation.setField(requestCallback, "collectLogCacheManager", collectLogCacheManager);

        try {
            requestCallback.timeout(null);
        } catch (Exception e) {
            fail();
        }

        new Verifications() {{
            collectLogCacheManager.updateState(anyString, CollectLogStateEnums.FAILURE);
            times = 1;
        }};


    }
}