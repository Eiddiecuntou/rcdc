package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import java.io.File;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * 
 * Description: 刷机任务文件清理处理器
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月23日
 * 
 * @author nt
 */
@Service
public class SystemUpgradeFileClearHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemUpgradeFileClearHandler.class);

    @Autowired
    private TerminalSystemUpgradePackageService terminalSystemUpgradePackageService;

    /**
     * 刷机任务完成文件清理
     * 
     * @param systemUpgradeEntity 刷机任务实体
     * @throws BusinessException 业务异常
     */
    public void clear(TerminalSystemUpgradeEntity systemUpgradeEntity) throws BusinessException {
        Assert.notNull(systemUpgradeEntity, "systemUpgradeEntity can not be null");
        final UUID upgradePackageId = systemUpgradeEntity.getUpgradePackageId();
        Assert.notNull(upgradePackageId, "upgradePackageId can not be null");

        deleteStatusFile();
        deleteUpgradeImg(upgradePackageId);
    }

    /**
     * 清理终端刷机状态文件
     * 
     * @param upgradeTaskId 刷机任务id
     */
    private void deleteStatusFile() {
        deleteMacNameFile(Constants.TERMINAL_UPGRADE_START_SATTUS_FILE_PATH);
        deleteMacNameFile(Constants.TERMINAL_UPGRADE_END_SATTUS_FILE_PATH);
    }

    /**
     * 删除状态文件目录下对应以mac为名的状态文件
     * 
     * @param upgradeTerminal
     */
    private void deleteMacNameFile(String fileDir) {
        // 删除刷机状态文件夹内的状态文件
        File statusFileDir = new File(fileDir);
        if (!statusFileDir.isDirectory()) {
            LOGGER.error("刷机状态文件夹不存在，路径：{} ", fileDir);
            return;
        }
        final File[] fileArr = statusFileDir.listFiles();
        for (File statusFile : fileArr) {
            if (statusFile.isFile()) {
                statusFile.delete();
            }
        }
    }

    /**
     * 刷机完成，删除镜像文件
     * 
     * @param packageId 刷机文件
     * @throws BusinessException 业务异常
     */
    private void deleteUpgradeImg(UUID packageId) throws BusinessException {
        TerminalSystemUpgradePackageEntity packageEntity = terminalSystemUpgradePackageService.getSystemUpgradePackage(packageId);
        final String imgName = packageEntity.getImgName();
        String upgradeImgFileDir = Constants.ISO_IMG_MOUNT_PATH + imgName;
        FileOperateUtil.deleteFile(new File(upgradeImgFileDir));
    }
}
