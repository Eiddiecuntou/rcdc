package com.ruijie.rcos.rcdc.terminal.module.impl.init;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Example;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalDetectionDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.DetectStateEnums;
import mockit.Expectations;
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
     * 测试safeInit,checkingList为空
     */
    @Test
    public void testSafeInitCheckingListIsEmpty() {
        new Expectations() {
            {
                detectionDAO.findAll((Example<TerminalDetectionEntity>) any);
                result = Collections.emptyList();
            }
        };
        init.safeInit();
        new Verifications() {
            {
                detectionDAO.save((TerminalDetectionEntity) any);
                times = 0;
            }
        };
    }

    /**
     * 测试safeInit,checkingList不为空
     */
    @Test
    public void testSafeInitCheckingListIsNotEmpty() {
        List<TerminalDetectionEntity> checkingList = new ArrayList<>();
        checkingList.add(new TerminalDetectionEntity());
        checkingList.add(new TerminalDetectionEntity());
        new Expectations() {
            {
                detectionDAO.findAll((Example<TerminalDetectionEntity>) any);
                result = checkingList;
            }
        };
        init.safeInit();
        new Verifications() {
            {
                detectionDAO.save((TerminalDetectionEntity) any);
                times = 2;
            }
        };
    }

}
