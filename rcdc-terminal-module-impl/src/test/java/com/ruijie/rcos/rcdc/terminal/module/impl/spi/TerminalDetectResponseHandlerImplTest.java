package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.StateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalDetectResponse;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalDetectService;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/13
 *
 * @author Jarman
 */
@RunWith(JMockit.class)
public class TerminalDetectResponseHandlerImplTest {

    @Tested
    private TerminalDetectResponseHandlerImpl responseHandler;

    @Injectable
    private TerminalDetectService detectService;

    @Test
    public void dispatch() {
        try {
            CbbDispatcherRequest request = new CbbDispatcherRequest();
            request.setRequestId("1234");
            request.setTerminalId("12345");
            request.setDispatcherKey("test");
            request.setData(generateJson());
            responseHandler.dispatch(request);
        } catch (Exception e) {
            fail();
        }

        new Verifications() {{
            detectService.updateTerminalDetect(anyString, (TerminalDetectResponse) any);
            times = 1;
        }};
    }

    private String generateJson() {
        TerminalDetectResponse response = new TerminalDetectResponse();
        response.setErrorCode(StateEnums.SUCCESS);
        TerminalDetectResponse.DetectResult detectResult = new TerminalDetectResponse.DetectResult();
        detectResult.setBandwidth(1234.2);
        detectResult.setCanAccessInternet(1);
        detectResult.setIpConflict("222");
        detectResult.setNetworkDelay(23.22);
        detectResult.setPacketLossRate(22.2);
        response.setResult(detectResult);
        return JSON.toJSONString(detectResult);
    }
}