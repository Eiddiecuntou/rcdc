package com.ruijie.rcos.rcdc.terminal.module.impl.tx;

import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalDetectionDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalDetectResult;
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

    /**
     * 测试检测信息
     */
    @Test
    public void testUpdateBasicInfoAndDetect() {
        String terminalId = "123";

        try {
            TerminalDetectResult result = new TerminalDetectResult();
            result.setBandwidth(12.3);
            result.setAccessInternet(1);
            result.setIpConflict(1);
            result.setIpConflictMac("123");
            result.setDelay(233);
            result.setPacketLossRate(233.2);

            detectService.updateTerminalDetect(terminalId, result);
        } catch (Exception e) {
            fail();
        }
    }


}
