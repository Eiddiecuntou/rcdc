package com.ruijie.rcos.rcdc.terminal.module.impl.quartz;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.google.common.base.Stopwatch;
import com.ruijie.rcos.base.aaa.module.def.api.BaseSystemLogMgmtAPI;
import com.ruijie.rcos.base.aaa.module.def.api.request.systemlog.BaseCreateSystemLogRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalDetectionDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.DetectStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalOperatorService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.i18n.LocaleI18nResolver;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.quartz.Quartz;
import com.ruijie.rcos.sk.base.quartz.QuartzTask;
import com.ruijie.rcos.sk.modulekit.api.isolation.GlobalUniqueBean;

/**
 * 
 * Description: 终端检测指令发送定时任务
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年5月10日
 * 
 * @author nt
 */
@GlobalUniqueBean("detectCommandSendQuartz")
@Quartz(cron = "0/12 * * * * ?", msgKey = BusinessKey.RCDC_TERMINAL_QUARTZ_SEND_DETECT_COMMAND)
public class TerminalDetectCommandSendQuartz implements QuartzTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalDetectCommandSendQuartz.class);

    @Autowired
    private TerminalDetectionDAO detectionDAO;

    @Autowired
    private TerminalOperatorService operatorService;

    @Autowired
    private BaseSystemLogMgmtAPI baseSystemLogMgmtAPI;


    @Override
    public void execute() throws Exception {
        LOGGER.info("开始发送终端检测指令...");
        List<TerminalDetectionEntity> detectList = detectionDAO.findByDetectState(DetectStateEnums.WAIT);
        if (CollectionUtils.isEmpty(detectList)) {
            LOGGER.info("没有处于等待状态的终端检测记录，不进行指令发送");
            return;
        }

        // 每次定时任务只发送一条检测指令，若成功则返回，失败则发送下一条
        for (TerminalDetectionEntity detectionEntity : detectList) {
            Stopwatch stopwatch = Stopwatch.createStarted();
            boolean isSuccess = sendDetectCommand(detectionEntity, stopwatch);
            if (isSuccess) {
                return;
            }
        }

    }

    private boolean sendDetectCommand(TerminalDetectionEntity detectionEntity, Stopwatch stopwatch) {
        try {
            operatorService.sendDetectRequest(detectionEntity);
            addSuccessSystemLog(detectionEntity, stopwatch);
            return true;
        } catch (BusinessException be) {
            LOGGER.error("发送终端检测指令失败", be);
            addFailSystemLog(detectionEntity, be.getI18nMessage(), stopwatch);
            return false;
        } catch (Exception ex) {
            LOGGER.error("发送终端检测指令失败", ex);
            addFailSystemLog(detectionEntity,
                    LocaleI18nResolver.resolve(BusinessKey.RCDC_TERMINAL_SEND_DETECT_COMMAND_FAIL, new String[] {}),
                    stopwatch);
            return false;
        }
    }

    private void addFailSystemLog(TerminalDetectionEntity detectionEntity, String failMsg, Stopwatch stopwatch) {
        String timeMillis = String.valueOf(stopwatch.elapsed(TimeUnit.MILLISECONDS));
        BaseCreateSystemLogRequest systemLogRequest =
                new BaseCreateSystemLogRequest(BusinessKey.RCDC_TERMINAL_DETECT_COMMAND_SEND_QUARTZ_FAIL_SYSTEM_LOG,
                        detectionEntity.getTerminalId(), failMsg, timeMillis);
        baseSystemLogMgmtAPI.createSystemLog(systemLogRequest);
    }

    private void addSuccessSystemLog(TerminalDetectionEntity detectionEntity, Stopwatch stopwatch) {
        String timeMillis = String.valueOf(stopwatch.elapsed(TimeUnit.MILLISECONDS));
        BaseCreateSystemLogRequest systemLogRequest =
                new BaseCreateSystemLogRequest(BusinessKey.RCDC_TERMINAL_DETECT_COMMAND_SEND_QUARTZ_SUCCESS_SYSTEM_LOG,
                        detectionEntity.getTerminalId(), timeMillis);
        baseSystemLogMgmtAPI.createSystemLog(systemLogRequest);
    }
}
