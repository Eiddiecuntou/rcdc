package com.ruijie.rcos.rcdc.terminal.module.impl.quartz;

import static org.junit.Assert.assertEquals;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.base.aaa.module.def.api.BaseSystemLogMgmtAPI;
import com.ruijie.rcos.base.aaa.module.def.api.request.systemlog.BaseCreateSystemLogRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalDetectionDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.DetectStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalOperatorService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.i18n.LocaleI18nResolver;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.*;
import org.springframework.util.Assert;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/9/20
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class TerminalDetectCommandSendQuartzTest {

    @Tested
    private TerminalDetectCommandSendQuartzTask quartz;

    @Injectable
    private TerminalDetectionDAO detectionDAO;

    @Injectable
    private TerminalOperatorService operatorService;

    @Injectable
    private BaseSystemLogMgmtAPI baseSystemLogMgmtAPI;

    /**
     * 测试运行发送检测命令 - 无需要发送的记录
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testRunWhileNoDetectRecord() throws BusinessException {
        new Expectations() {
            {
                detectionDAO.findFirstByDetectStateOrderByCreateTime(DetectStateEnums.WAIT);
                result = null;
            }
        };

        quartz.run();

        new Verifications() {
            {
                detectionDAO.findFirstByDetectStateOrderByCreateTime(DetectStateEnums.WAIT);
                times = 1;

                operatorService.sendDetectRequest((TerminalDetectionEntity) any);
                times = 0;
            }
        };
    }

    /**
     * 测试运行发送检测命令 - 发送检测命令异常BusinessException
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testRunWhileSendDetectCommandHasBusinessException() throws BusinessException {

        TerminalDetectionEntity entity = new TerminalDetectionEntity();
        entity.setTerminalId("111");

        new Expectations() {
            {
                detectionDAO.findFirstByDetectStateOrderByCreateTime(DetectStateEnums.WAIT);
                returns(entity, null);

                operatorService.sendDetectRequest(entity);
                result = new BusinessException("123");
            }
        };

        new MockUp<LocaleI18nResolver>() {
            @Mock
            public String resolve(String key, String... args) {
                Assert.hasText(key, "key is not empty");
                Assert.notNull(args, "args is not null");
                StringBuilder sb = new StringBuilder();
                sb.append(key);
                if (ArrayUtils.isEmpty(args)) {
                    return sb.toString();
                }

                for (String str : args) {
                    sb.append("," + str);
                }
                return sb.toString();
            }
        };

        quartz.run();

        new Verifications() {
            {
                detectionDAO.findFirstByDetectStateOrderByCreateTime(DetectStateEnums.WAIT);
                times = 2;

                operatorService.sendDetectRequest((TerminalDetectionEntity) any);
                times = 1;

                BaseCreateSystemLogRequest logRequest;
                baseSystemLogMgmtAPI.createSystemLog(logRequest = withCapture());
                times = 1;

                assertEquals(BusinessKey.RCDC_TERMINAL_DETECT_COMMAND_SEND_QUARTZ_FAIL_SYSTEM_LOG, logRequest.getKey());
                assertEquals(entity.getTerminalId(), logRequest.getArgArr()[0]);
                assertEquals("123", logRequest.getArgArr()[1]);
            }
        };
    }

    /**
     * 测试运行发送检测命令 - 发送检测命令异常Exception
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testRunWhileSendDetectCommandHasException() throws BusinessException {
        TerminalDetectionEntity entity = new TerminalDetectionEntity();
        entity.setTerminalId("111");

        new Expectations() {
            {
                detectionDAO.findFirstByDetectStateOrderByCreateTime(DetectStateEnums.WAIT);
                returns(entity, null);

                operatorService.sendDetectRequest(entity);
                result = new Exception("123");
            }
        };

        new MockUp<LocaleI18nResolver>() {
            @Mock
            public String resolve(String key, String... args) {
                Assert.hasText(key, "key is not empty");
                Assert.notNull(args, "args is not null");
                StringBuilder sb = new StringBuilder();
                sb.append(key);
                if (ArrayUtils.isEmpty(args)) {
                    return sb.toString();
                }

                for (String str : args) {
                    sb.append("," + str);
                }
                return sb.toString();
            }
        };

        quartz.run();

        new Verifications() {
            {
                detectionDAO.findFirstByDetectStateOrderByCreateTime(DetectStateEnums.WAIT);
                times = 2;

                operatorService.sendDetectRequest((TerminalDetectionEntity) any);
                times = 1;

                BaseCreateSystemLogRequest logRequest;
                baseSystemLogMgmtAPI.createSystemLog(logRequest = withCapture());
                times = 1;

                assertEquals(BusinessKey.RCDC_TERMINAL_DETECT_COMMAND_SEND_QUARTZ_FAIL_SYSTEM_LOG, logRequest.getKey());
                assertEquals(entity.getTerminalId(), logRequest.getArgArr()[0]);
                assertEquals(BusinessKey.RCDC_TERMINAL_SEND_DETECT_COMMAND_FAIL, logRequest.getArgArr()[1]);
            }
        };
    }

    /**
     * 测试运行发送检测命令 - 发送检测命令异常Exception
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testRun() throws BusinessException {
        TerminalDetectionEntity entity = new TerminalDetectionEntity();
        entity.setTerminalId("111");

        new Expectations() {
            {
                detectionDAO.findFirstByDetectStateOrderByCreateTime(DetectStateEnums.WAIT);
                result = entity;

                operatorService.sendDetectRequest(entity);
            }
        };

        new MockUp<LocaleI18nResolver>() {
            @Mock
            public String resolve(String key, String... args) {
                Assert.hasText(key, "key is not empty");
                Assert.notNull(args, "args is not null");
                StringBuilder sb = new StringBuilder();
                sb.append(key);
                if (ArrayUtils.isEmpty(args)) {
                    return sb.toString();
                }

                for (String str : args) {
                    sb.append("," + str);
                }
                return sb.toString();
            }
        };

        quartz.run();

        new Verifications() {
            {
                detectionDAO.findFirstByDetectStateOrderByCreateTime(DetectStateEnums.WAIT);
                times = 1;

                operatorService.sendDetectRequest((TerminalDetectionEntity) any);
                times = 1;

                BaseCreateSystemLogRequest logRequest;
                baseSystemLogMgmtAPI.createSystemLog(logRequest = withCapture());
                times = 1;

                assertEquals(BusinessKey.RCDC_TERMINAL_DETECT_COMMAND_SEND_QUARTZ_SUCCESS_SYSTEM_LOG, logRequest.getKey());
                assertEquals(entity.getTerminalId(), logRequest.getArgArr()[0]);
            }
        };
    }

}
