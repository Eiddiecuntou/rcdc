package com.ruijie.rcos.rcdc.terminal.module.impl.tx;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbDetectStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalDetectionDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.StateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalDetectResponse;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
            result.setAccessInternet(1);
            result.setIpConflict(1);
            result.setIpConflictMac("123");
            result.setDelay(233);
            result.setPacketLossRate(233.2);
            detectResult.setErrorCode(StateEnums.SUCCESS);
            detectResult.setResult(result);

            detectService.updateTerminalDetect(terminalId, detectResult);
        } catch (Exception e) {
            fail();
        }
    }


}
