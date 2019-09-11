package com.ruijie.rcos.rcdc.terminal.module.impl.quartz;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.ruijie.rcos.sk.base.quartz.QuartzTaskContext;
import org.junit.Test;
import org.junit.runner.RunWith;
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
 * Create Time: 2019年1月24日
 * 
 * @author ls
 */
@RunWith(JMockit.class)
public class TerminalDetectTimeoutQuartzTaskTest {

    @Tested
    private TerminalDetectTimeoutQuartzTask quartz;

    @Injectable
    private TerminalDetectionDAO detectionDAO;

    @Injectable
    private QuartzTaskContext quartzTaskContext;

    /**
     * 测试execute,timeoutDetectList为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testExecuteTimeoutDetectListIsEmpty() throws Exception {
        new Expectations() {
            {
                detectionDAO.findByDetectStateAndDetectTimeBefore(DetectStateEnums.CHECKING, (Date) any);
                result = Collections.emptyList();
            }
        };
        quartz.execute(quartzTaskContext);
        new Verifications() {
            {
                detectionDAO.save((TerminalDetectionEntity) any);
                times = 0;
            }
        };
    }

    /**
     * 测试execute,
     * 
     * @throws Exception 异常
     */
    @Test
    public void testExecute() throws Exception {
        List<TerminalDetectionEntity> timeoutDetectList = new ArrayList<>();
        timeoutDetectList.add(new TerminalDetectionEntity());
        new Expectations() {
            {
                detectionDAO.findByDetectStateAndDetectTimeBefore(DetectStateEnums.CHECKING, (Date) any);
                result = timeoutDetectList;
            }
        };
        quartz.execute(quartzTaskContext);
        new Verifications() {
            {
                TerminalDetectionEntity entity1;
                detectionDAO.save(entity1 = withCapture());
                times = 1;
                assertEquals(DetectStateEnums.ERROR, entity1.getDetectState());
            }
        };
    }
}
