package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.ruijie.rcos.rcdc.codec.adapter.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.MessageUtils;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月24日
 * 
 * @author ls
 */
@RunWith(JMockit.class)
public class GetI18nLangHandlerSPIImplTest {

    @Tested
    private GetI18nLangHandlerSPIImpl spiImpl;

    @Injectable
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    /**
     * 测试dispatch,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testDispatchArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> spiImpl.dispatch(null), "CbbDispatcherRequest不能为空");
        assertTrue(true);
    }

    /**
     * 测试dispatch,
     * 
     * @param utils mock MessageUtils
     */
    @Test
    public void testDispatch(@Mocked MessageUtils utils) {
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        CbbResponseShineMessage responseMessage = new CbbResponseShineMessage<>();
        new Expectations() {
            {
                MessageUtils.buildResponseMessage(request, any);
                result = responseMessage;
            }
        };
        spiImpl.dispatch(request);
        new Verifications() {
            {
                MessageUtils.buildResponseMessage(request, any);
                times = 1;
                messageHandlerAPI.response(responseMessage);
                times = 1;
            }
        };
    }
}
