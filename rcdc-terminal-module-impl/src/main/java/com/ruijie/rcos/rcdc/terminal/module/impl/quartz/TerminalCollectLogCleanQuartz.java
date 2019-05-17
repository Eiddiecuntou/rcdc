package com.ruijie.rcos.rcdc.terminal.module.impl.quartz;

import java.io.File;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.ruijie.rcos.sk.base.i18n.LocaleI18nResolver;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Stopwatch;
import com.ruijie.rcos.base.aaa.module.def.api.BaseSystemLogMgmtAPI;
import com.ruijie.rcos.base.aaa.module.def.api.request.systemlog.BaseCreateSystemLogRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.sk.base.filesystem.SkyengineFile;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.quartz.Quartz;
import com.ruijie.rcos.sk.base.quartz.QuartzTask;
import com.ruijie.rcos.sk.modulekit.api.isolation.GlobalUniqueBean;


/**
 *
 * Description: 终端收集日志文件清理定时任务
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年5月5日
 *
 * @author nt
 */
@GlobalUniqueBean("terminalCollectLogCleanQuartz")
@Quartz(cron = "0 0 2 * * ? *", msgKey = BusinessKey.RCDC_TERMINAL_QUARTZ_CLEAN_TERMINAL_COLLECT_LOG)
public class TerminalCollectLogCleanQuartz implements QuartzTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalCollectLogCleanQuartz.class);

    public static final long TERMINAL_LOG_FILE_EXPIRE_TIME = 24 * 60 * 60 * 1000;

    @Autowired
    private BaseSystemLogMgmtAPI baseSystemLogMgmtAPI;

    @Override
    public void execute() throws Exception {
        deleteTerminalLogFile();
    }

    private void deleteTerminalLogFile() {
        LOGGER.info("开始清理终端收集日志文件定时任务...");

        Stopwatch stopwatch = Stopwatch.createStarted();
        File fileDir = new File(Constants.STORE_TERMINAL_LOG_PATH);
        if (!fileDir.isDirectory()) {
            LOGGER.error("终端收集日志文件目录不存在");
            addFailSystemLog(stopwatch);
            return;
        }

        File[] fileArr = fileDir.listFiles();
        if (ArrayUtils.isEmpty(fileArr)) {
            LOGGER.info("无终端收集日志文件");
            return;
        }

        int deleteCount = 0;
        for (File file : fileArr) {
            boolean hasDelete = cleanFile(file);
            if (hasDelete) {
                deleteCount++;
            }
        }

        // 记录系统日志
        String timeMillis = String.valueOf(stopwatch.elapsed(TimeUnit.MILLISECONDS));
        addSuccessSystemLog(deleteCount, timeMillis);
        LOGGER.info("完成清理终端收集日志文件定时任务, 共删除[{}]个日志文件, 耗时[{}]毫秒", deleteCount, timeMillis);
    }

    private void addFailSystemLog(Stopwatch stopwatch) {
        BaseCreateSystemLogRequest request =
                new BaseCreateSystemLogRequest(
                        BusinessKey.RCDC_TERMINAL_QUARTZ_CLEAN_TERMINAL_COLLECT_LOG_FAIL_SYSTEM_LOG, LocaleI18nResolver
                                .resolve(BusinessKey.RCDC_TERMINAL_COLLECT_LOG_DIRECTORY_NOT_EXIST, new String[] {}),
                        String.valueOf(stopwatch.elapsed(TimeUnit.MILLISECONDS)));
        baseSystemLogMgmtAPI.createSystemLog(request);
    }

    private void addSuccessSystemLog(int deleteCount, String timeMillis) {
        BaseCreateSystemLogRequest request = new BaseCreateSystemLogRequest(
                BusinessKey.RCDC_TERMINAL_QUARTZ_CLEAN_TERMINAL_COLLECT_LOG_SUCCESS_SYSTEM_LOG,
                String.valueOf(deleteCount), timeMillis);
        baseSystemLogMgmtAPI.createSystemLog(request);
    }

    private boolean cleanFile(File file) {
        long diffTime = new Date().getTime() - file.lastModified();
        if (diffTime < TERMINAL_LOG_FILE_EXPIRE_TIME) {
            LOGGER.debug("日志文件[{}]保存时间未超过期限，不处理", file.getName());
            return false;
        }

        SkyengineFile skyengineFile = new SkyengineFile(file);
        return skyengineFile.delete(false);
    }

}
