package com.ruijie.rcos.rcdc.terminal.module.impl.tx;

import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalDetectionDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.StateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalDetectResponse;
import mockit.Injectable;
import mockit.Tested;
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
public class TerminalDetectServiceTest {

    @Tested
    private TerminalDetectService detectService;

    @Injectable
    private TerminalBasicInfoDAO basicInfoDAO;

    @Injectable
    private TerminalDetectionDAO detectionDAO;

    @Test
    public void testUpdateBasicInfoAndDetect() {
        String terminalId = "123";

        try {
            TerminalDetectResponse detectResult = new TerminalDetectResponse();
            TerminalDetectResponse.DetectResult result = new TerminalDetectResponse.DetectResult();
            result.setBandwidth(12.3);
            result.setCanAccessInternet(1);
            result.setIpConflict("ddd");
            result.setNetworkDelay(233.2);
            result.setPacketLossRate(233.2);
            detectResult.setErrorCode(StateEnums.SUCCESS);
            detectResult.setResult(result);

            detectService.updateTerminalDetect(terminalId, detectResult);
        } catch (Exception e) {
            fail();
        }
    }


}
