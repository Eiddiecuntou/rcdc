package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.quartz.handler.SystemUpgradeQuartzHandler;
import com.ruijie.rcos.sk.base.concurrent.ThreadExecutor;
import com.ruijie.rcos.sk.base.concurrent.ThreadExecutors;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * 
 * Description: 刷机任务支持服务
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月20日
 * 
 * @author nt
 */
@Service
public class TerminalSystemUpgradeSupportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalSystemUpgradeSupportService.class);

    private static final String PXE_SAMBA_LINUX_VDI_ISO_PATH = "/opt/samba/pxeuser/linux_vdi/";

    private static final int QUARTZ_PERIOD_SECOND = 15;

    private static ScheduledFuture<?> UPGRADE_TASK_FUTURE = null;

    private static ThreadExecutor SYSTEM_UPGRADE_SCHEDULED_THREAD_POOL =
            ThreadExecutors.newBuilder("SYSTEM_UPGRADE_SCHEDULED_THREAD").maxThreadNum(2).queueSize(1).build();

    private static ExecutorService CLOSE_SERVICE_THREAD_POOL =
            ThreadExecutors.newBuilder("CLOSE_SERVICE_THREAD").maxThreadNum(1).queueSize(1).build();

    @Autowired
    private SystemUpgradeQuartzHandler systemUpgradeQuartzHandler;

    /**
     * 关闭刷机相关支持服务
     * 
     * @throws BusinessException 业务异常
     */
    public void closeSystemUpgradeService() throws BusinessException {

        // 停止定时任务
        cancelScheduleTask();
    }

    /**
     * 停止刷机任务处理定时任务
     */
    private void cancelScheduleTask() {
        LOGGER.info("关闭定时任务");
        if (UPGRADE_TASK_FUTURE != null) {
            UPGRADE_TASK_FUTURE.cancel(true);
            UPGRADE_TASK_FUTURE = null;
        }
    }

    /**
     * 开启刷机相关服务
     * 
     * @param packageEntity 刷机包对象
     */
    public void openSystemUpgradeService(TerminalSystemUpgradePackageEntity packageEntity) {
        Assert.notNull(packageEntity, "packageEntity can not be null");

        CLOSE_SERVICE_THREAD_POOL.execute(() -> openSupportService(packageEntity));
    }

    /**
     * 开启刷机相关支持服务
     * 
     * @param packageEntity 刷机包对象
     */
    private void openSupportService(TerminalSystemUpgradePackageEntity packageEntity) {
        try {
            // 复制镜像文件到samba刷机路径
            prepareUpgradeImg(packageEntity);
        } catch (Exception e) {
            LOGGER.error("开启刷机相关服务失败。", e);
        }

        // 开始定时任务
        beginScheduleTask();
    }

    private void prepareUpgradeImg(TerminalSystemUpgradePackageEntity packageEntity) throws BusinessException {
        LOGGER.info("开始准备刷机镜像文件");

        // 镜像文件
        final String filePath = packageEntity.getFilePath();
        File isoImgFile = checkIsoFileExist(filePath);
        LOGGER.info("复制刷机镜像文件到samba刷机目录：{}", PXE_SAMBA_LINUX_VDI_ISO_PATH);

        // samba刷机路径
        File destPath = obtainPxeLinuxVdiIsoPath(packageEntity.getPackageName());

        try {
            Files.move(isoImgFile.toPath(), destPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOGGER.error("复制升级文件至刷机目录失败", e);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OPEN_SYSTEM_UPGRADE_TASK_ERROR_FOR_COPY_PACKAGE, e);
        }

        LOGGER.info("完成准备刷机镜像文件");
    }

    private File obtainPxeLinuxVdiIsoPath(String packageName) {
        File destPath = new File(PXE_SAMBA_LINUX_VDI_ISO_PATH + packageName);
        if (destPath.getParentFile().isDirectory()) {
            return destPath;
        }

        destPath.getParentFile().mkdirs();
        return destPath;
    }

    private File checkIsoFileExist(String filePath) throws BusinessException {
        File isoImgFile = new File(filePath);
        if (isoImgFile.isFile()) {
            return isoImgFile;
        }

        throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_NOT_EXIST);
    }


    /**
     * 确认开启刷机任务处理定时任务
     */
    private void beginScheduleTask() {
        if (UPGRADE_TASK_FUTURE == null) {
            LOGGER.info("开启定时任务");
            UPGRADE_TASK_FUTURE =
                    SYSTEM_UPGRADE_SCHEDULED_THREAD_POOL.scheduleAtFixedRate(systemUpgradeQuartzHandler, 0, QUARTZ_PERIOD_SECOND, TimeUnit.SECONDS);
        }
    }

}
