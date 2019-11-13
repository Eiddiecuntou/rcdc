package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.SimpleCmdReturnValueResolver;
import com.ruijie.rcos.rcdc.terminal.module.impl.quartz.handler.SystemUpgradeQuartzHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.NfsServiceUtil;
import com.ruijie.rcos.sk.base.concurrent.ThreadExecutor;
import com.ruijie.rcos.sk.base.concurrent.ThreadExecutors;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.shell.ShellCommandRunner;

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

        // 关闭nfs目录服务
        NfsServiceUtil.shutDownService();

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
            // 复制镜像文件到nfs刷机路径
            prepareUpgradeImg(packageEntity);

            // 开启nfs目录服务
            NfsServiceUtil.startService();
        } catch (Exception e) {
            LOGGER.error("开启刷机相关服务失败。", e);
        }

        // 开始定时任务
        beginScheduleTask();
    }

    private void prepareUpgradeImg(TerminalSystemUpgradePackageEntity packageEntity) throws BusinessException {
        LOGGER.info("开始准备刷机镜像文件");
        final String imgName = packageEntity.getImgName();
        String upgradeImgFileDir = Constants.ISO_IMG_MOUNT_PATH;

        // 挂载镜像
        final String filePath = packageEntity.getFilePath();
        final String mountPath = Constants.TERMINAL_UPGRADE_ISO_PATH_VDI + Constants.MOUNT_RELATE_DIR;
        File mountFile = new File(mountPath);
        if (!mountFile.exists()) {
            mountFile.mkdir();
        }
        LOGGER.info("挂载刷机镜像文件：{}", mountPath);
        mountUpgradePackage(filePath, mountPath);

        final String imgFilePath = mountPath + Constants.TERMINAL_UPGRADE_ISO_IMG_FILE_PATH + imgName;
        try {
            // 复制镜像文件到nfs服务目录下镜像包内
            String cpCmd = "cp -r " + imgFilePath + " " + upgradeImgFileDir;
            LOGGER.info("复制文件到指定目录，cmd: {}", cpCmd);
            runShellCommand(cpCmd);
            LOGGER.info("复制文件到指定目录成功，cmd: {}", cpCmd);
        } catch (Exception e) {
            LOGGER.error("复制文件失败", e);
            throw new BusinessException(BusinessKey.RCDC_FILE_OPERATE_FAIL, e);
        } finally {
            LOGGER.info("卸载刷机镜像文件：{}", mountPath);
            umountUpgradePackage(mountPath);
        }
        LOGGER.info("完成准备刷机镜像文件");
    }

    private void mountUpgradePackage(final String filePath, final String mountPath) throws BusinessException {
        LOGGER.debug("mount package, path is [{}]", filePath);
        String mountCmd = String.format(Constants.SYSTEM_CMD_MOUNT_UPGRADE_ISO, filePath, mountPath);

        LOGGER.info("mount package, cmd : {}", mountCmd);
        runShellCommand(mountCmd);
        LOGGER.info("mount package success");
    }

    private void umountUpgradePackage(final String mountPath) throws BusinessException {
        LOGGER.debug("umount package, path is [{}]", mountPath);
        String umountCmd = String.format(Constants.SYSTEM_CMD_UMOUNT_UPGRADE_ISO, mountPath);

        LOGGER.info("umount package, cmd : {}", umountCmd);
        runShellCommand(umountCmd);
        LOGGER.info("umount package success");
    }

    private void runShellCommand(String cmd) throws BusinessException {
        ShellCommandRunner runner = new ShellCommandRunner();
        runner.setCommand(cmd);
        try {
            String outStr = runner.execute(new SimpleCmdReturnValueResolver());
            LOGGER.debug("out String is :{}", outStr);
        } catch (BusinessException e) {
            LOGGER.error("shell command execute error", e);
            throw new BusinessException(BusinessKey.RCDC_SYSTEM_CMD_EXECUTE_FAIL, e);
        }
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
