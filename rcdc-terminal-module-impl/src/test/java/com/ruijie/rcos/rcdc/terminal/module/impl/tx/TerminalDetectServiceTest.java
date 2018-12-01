package com.ruijie.rcos.rcdc.terminal.module.impl.tx;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbDetectStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalDetectionDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.CbbTerminalEntity;
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
        new Expectations() {{
            basicInfoDAO.findTerminalBasicInfoEntitiesByTerminalId(anyString);
            basicInfoDAO.modifyDetectInfo(anyString, anyInt, (Date) any, anyInt);
        }};

        try {
            String terminalId = "123";
            TerminalDetectResponse detectResult = new TerminalDetectResponse();
            TerminalDetectResponse.DetectResult result = new TerminalDetectResponse.DetectResult();
            result.setBandwidth(12.3);
            result.setCanAccessInternet(1);
            result.setIpConflict("ddd");
            result.setNetworkDelay(233.2);
            result.setPacketLossRate(233.2);
            detectResult.setErrorCode(StateEnums.SUCCESS);
            detectResult.setResult(result);

            detectService.updateBasicInfoAndDetect(terminalId, detectResult);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testUpdateBasicInfo() {
        new Expectations() {{
            basicInfoDAO.findTerminalBasicInfoEntitiesByTerminalId(anyString);
            basicInfoDAO.modifyDetectInfo(anyString, anyInt, (Date) any, anyInt);
        }};

        try {
            String terminalId = "123";
            TerminalDetectResponse detectResult = new TerminalDetectResponse();
            TerminalDetectResponse.DetectResult result = new TerminalDetectResponse.DetectResult();
            result.setBandwidth(12.3);
            result.setCanAccessInternet(1);
            detectResult.setErrorCode(StateEnums.FAILURE);
            detectResult.setResult(result);
            detectService.updateBasicInfoAndDetect(terminalId, detectResult);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testSetOfflineTerminalToFailureState() {
        List<CbbTerminalEntity> basicInfoList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            CbbTerminalEntity entity = new CbbTerminalEntity();
            basicInfoList.add(entity);
        }
        new Expectations() {{
            basicInfoDAO.findTerminalBasicInfoEntitiesByDetectState(CbbDetectStateEnums.DOING);
            result = basicInfoList;
        }};

        try {
            detectService.setOfflineTerminalToFailureState();
        } catch (Exception e) {
            fail();
        }

        new Verifications() {{
            basicInfoDAO.modifyDetectInfo(anyString, anyInt, (Date) any, anyInt);
            times = 3;
        }};
    }

}