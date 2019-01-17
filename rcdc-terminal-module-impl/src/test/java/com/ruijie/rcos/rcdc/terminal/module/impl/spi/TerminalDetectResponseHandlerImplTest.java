package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.StateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalDetectResponse;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalDetectService;
import mockit.Injectable;
import mockit.Tested;
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
public class TerminalDetectResponseHandlerImplTest {

    @Tested
    private TerminalDetectResponseHandlerSPIImpl responseHandler;

    @Injectable
    private TerminalDetectService detectService;

    /**
     * 测试分发
     */
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

        new Verifications() {
            {
                detectService.updateTerminalDetect(anyString, (TerminalDetectResponse) any);
                times = 1;
            }
        };
    }

    private String generateJson() {
        TerminalDetectResponse response = new TerminalDetectResponse();
        response.setErrorCode(StateEnums.SUCCESS);
        TerminalDetectResponse.DetectResult detectResult = new TerminalDetectResponse.DetectResult();
        detectResult.setBandwidth(1234.2);
        detectResult.setAccessInternet(1);
        detectResult.setIpConflict(1);
        detectResult.setIpConflictMac("222");
        detectResult.setDelay(23);
        detectResult.setPacketLossRate(22.2);
        response.setResult(detectResult);
        return JSON.toJSONString(detectResult);
    }
}
