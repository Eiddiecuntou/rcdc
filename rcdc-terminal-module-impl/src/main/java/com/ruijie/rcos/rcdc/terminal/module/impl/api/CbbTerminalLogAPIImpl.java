package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.google.common.io.Files;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalLogAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalCollectLogStatusDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalLogFileInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCollectLogStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCache;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.quartz.TerminalCollectLogCleanQuartzTask;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalOperatorService;
import com.ruijie.rcos.sk.base.concurrent.ThreadExecutors;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.io.File;
import java.text.ParseException;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/8/20
 *
 * @author hs
 */
public class CbbTerminalLogAPIImpl implements CbbTerminalLogAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(CbbTerminalLogAPIImpl.class);

    @Autowired
    private TerminalOperatorService operatorService;

    @Autowired
    private CollectLogCacheManager collectLogCacheManager;

    @Autowired
    private TerminalCollectLogCleanQuartzTask terminalCollectLogCleanQuartzTask;

    @Override
    public void collectLog(String terminalId) throws BusinessException {
        Assert.hasText(terminalId, "terminalId不能为空");

        operatorService.collectLog(terminalId);
    }

    @Override
    public CbbTerminalCollectLogStatusDTO getCollectLog(String terminalId) throws BusinessException {
        Assert.hasText(terminalId, "terminalId can not be blank");

        CollectLogCache cache = getCollectLogCache(terminalId);
        CbbTerminalCollectLogStatusDTO response = new CbbTerminalCollectLogStatusDTO();
        response.setLogName(cache.getLogFileName());
        response.setState(cache.getState());
        return response;
    }

    @Override
    public CbbTerminalLogFileInfoDTO getTerminalLogFileInfo(String logFileName) throws BusinessException {
        Assert.hasText(logFileName, "logFileName can not be blank");

        String logFilePath = Constants.STORE_TERMINAL_LOG_PATH + logFileName;
        checkFileExist(logFilePath);
        String logFileNameWithoutExtension = Files.getNameWithoutExtension(logFileName);
        String suffix = getFileSuffix(logFileName);
        CbbTerminalLogFileInfoDTO response = new CbbTerminalLogFileInfoDTO();
        response.setLogFilePath(logFilePath);
        response.setLogFileName(logFileNameWithoutExtension);
        response.setSuffix(suffix);

        return response;
    }

    /**
     * 获取日志缓存
     *
     * @param terminalId 终端id
     * @return 日志收集缓存
     * @throws BusinessException 业务异常
     */
    private CollectLogCache getCollectLogCache(String terminalId) throws BusinessException {
        CollectLogCache cache = collectLogCacheManager.getCache(terminalId);
        if (cache == null) {
            // 日志不存在，构造失败状态信息
            cache = new CollectLogCache(CbbCollectLogStateEnums.FAULT);
        }
        return cache;
    }

    private void checkFileExist(String logFilePath) throws BusinessException {
        File logFile = new File(logFilePath);
        if (logFile.isFile()) {
            return;
        }
        throw new BusinessException(BusinessKey.RCDC_TERMINAL_COLLECT_LOG_NOT_EXIST);
    }

    private String getFileSuffix(String fileName) {
        String suffix = "";
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf > -1) {
            suffix = fileName.substring(lastIndexOf + 1);
        }
        return suffix;
    }

    @Override
    public void startDefaultCleanCollectLogTask() throws BusinessException {
        // 每日凌晨2点触发
        String cronExpression = "0 0 2 * * ? *";
        try {
            LOGGER.info("启动缺省清理终端采集日志任务，cronExpression：{}", cronExpression);
            ThreadExecutors.scheduleWithCron(TerminalCollectLogCleanQuartzTask.class.getSimpleName(),
                    terminalCollectLogCleanQuartzTask, cronExpression);
        } catch (Exception e) {
            LOGGER.error("启动缺省清理终端采集日志任务" + TerminalCollectLogCleanQuartzTask.class.getName() + "失败", e);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_START_DEFAULT_CLEAN_COLLECT_LOG_FAIL, e);
        }
    }
}
