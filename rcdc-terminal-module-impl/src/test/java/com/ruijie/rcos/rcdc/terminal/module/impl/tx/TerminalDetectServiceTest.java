package com.ruijie.rcos.rcdc.terminal.module.impl.tx;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.DetectStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalDetectionDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalBasicInfoEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalDetectResult;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.jpa.repository.JpaRepository;

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
            detectionDAO.save((TerminalDetectionEntity) any);
            basicInfoDAO.findTerminalBasicInfoEntitiesByTerminalId(anyString);
            basicInfoDAO.modifyDetectInfo(anyString, anyInt, (Date) any, anyInt);
        }};

        try {
            String terminalId = "123";
            TerminalDetectResult detectResult = new TerminalDetectResult();
            detectResult.setBandwidth(12.3);
            detectResult.setCanAccessInternet(1);
            detectResult.setIpConflict("ddd");
            detectResult.setNetworkDelay(233.2);
            detectResult.setPacketLossRate(233.2);
            detectService.updateBasicInfoAndDetect(terminalId, detectResult);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testSetOfflineTerminalToFailureState() {
        List<TerminalBasicInfoEntity> basicInfoList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            TerminalBasicInfoEntity entity = new TerminalBasicInfoEntity();
            basicInfoList.add(entity);
        }
        new Expectations() {{
            basicInfoDAO.findTerminalBasicInfoEntitiesByDetectState(DetectStateEnums.DOING);
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