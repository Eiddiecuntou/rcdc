package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineMessageResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCollectLogStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.MessageUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.response.TerminalLogName;
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
 * Create Time: 2019年1月23日
 * 
 * @author ls
 */
@RunWith(JMockit.class)
public class CollectLogResponseSPIImplTest {

    @Tested
    private CollectLogResponseSPIImpl spiImpl;

    @Injectable
    private CollectLogCacheManager collectLogCacheManager;

    /**
     * 测试dispatch,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testDispatchArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> spiImpl.dispatch(null), "CbbDispatcherRequest不能为空");
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        ThrowExceptionTester.throwIllegalArgumentException(() -> spiImpl.dispatch(request), "data不能为null");
        assertTrue(true);
    }

    /**
     * 测试dispatch,日志上传成功
     * 
     * @param utils mock MessageUtils
     */
    @Test
    public void testDispatchUpdateSuccess(@Mocked MessageUtils utils) {
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        request.setTerminalId("123");
        request.setData("data");

        CbbShineMessageResponse<TerminalLogName> response = new CbbShineMessageResponse<TerminalLogName>();
        response.setCode(0);
        response.setContent(new TerminalLogName());
        new Expectations() {
            {
                MessageUtils.parse(request.getData(), TerminalLogName.class);
                result = response;
            }
        };
        spiImpl.dispatch(request);

        new Verifications() {
            {
                collectLogCacheManager.updateState(request.getTerminalId(), CbbCollectLogStateEnums.DONE, response.getContent().getLogName());
                times = 1;
                collectLogCacheManager.updateState(request.getTerminalId(), CbbCollectLogStateEnums.FAULT);
                times = 0;
            }
        };
    }

    /**
     * 测试dispatch,日志收集失败
     * 
     * @param utils mock MessageUtils
     */
    @Test
    public void testDispatchCollectFail(@Mocked MessageUtils utils) {
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        request.setTerminalId("123");
        request.setData("data");

        CbbShineMessageResponse<TerminalLogName> response = new CbbShineMessageResponse<TerminalLogName>();
        response.setCode(1);
        response.setContent(new TerminalLogName());
        new Expectations() {
            {
                MessageUtils.parse(request.getData(), TerminalLogName.class);
                result = response;
            }
        };
        spiImpl.dispatch(request);

        new Verifications() {
            {
                collectLogCacheManager.updateState(request.getTerminalId(), CbbCollectLogStateEnums.DONE, response.getContent().getLogName());
                times = 0;
                collectLogCacheManager.updateState(request.getTerminalId(), CbbCollectLogStateEnums.FAULT);
                times = 1;
            }
        };
    }

}
