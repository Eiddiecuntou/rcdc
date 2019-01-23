package com.ruijie.rcos.rcdc.terminal.module.impl.init;

import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalDetectionDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.DetectStateEnums;
import mockit.Injectable;
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
public class TerminalDetectInitTest {

    @Tested
    private TerminalDetectInit init;
    
    @Injectable
    private TerminalDetectionDAO detectionDAO;
    
    /**
     * 测试safeInit
     */
    @Test
    public void testSafeInit() {
        init.safeInit();
        new Verifications() {
            {
                detectionDAO.modifyDetectionCheckingToFail(DetectStateEnums.CHECKING, DetectStateEnums.ERROR);
                times = 1;
            }
        };
    }

}
