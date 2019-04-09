package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.MessageUtils;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

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
