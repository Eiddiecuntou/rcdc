package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import java.io.File;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
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

    @Autowired
    private TerminalSystemUpgradeTerminalDAO terminalSystemUpgradeTerminalDAO;

    /**
     * 刷机任务完成文件清理
     * 
     * @param upgradeTaskId 刷机任务id
     * @param upgradePackageId 刷机包id
     * @throws BusinessException 业务异常
     */
    public void clear(UUID upgradeTaskId, UUID upgradePackageId) throws BusinessException {
        Assert.notNull(upgradeTaskId, "upgradeTaskId can not be null");
        Assert.notNull(upgradePackageId, "upgradePackageId can not be null");
        
        deleteStatusFile(upgradeTaskId);
        deleteUpgradeImg(upgradePackageId);
    }

    /**
     * 清理终端刷机状态文件
     * 
     * @param upgradeTaskId 刷机任务id
     */
    private void deleteStatusFile(UUID upgradeTaskId) {
        final List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList =
                terminalSystemUpgradeTerminalDAO.findBySysUpgradeId(upgradeTaskId);
        if (CollectionUtils.isEmpty(upgradeTerminalList)) {
            LOGGER.debug("刷机任务无刷机终端");
            return;
        }

        for (TerminalSystemUpgradeTerminalEntity upgradeTerminal : upgradeTerminalList) {
            deleteMacNameFile(upgradeTerminal.getTerminalId());
        }
    }

    /**
     * 删除状态文件目录下对应以mac为名的状态文件
     * 
     * @param upgradeTerminal
     */
    private void deleteMacNameFile(String terminalId) {
        // 删除开始刷机，刷机完成状态的mac名称的文件
        deleteStatusFile(Constants.TERMINAL_UPGRADE_START_SATTUS_FILE_PATH, terminalId);
        deleteStatusFile(Constants.TERMINAL_UPGRADE_END_SATTUS_FILE_PATH, terminalId);
    }

    private void deleteStatusFile(String fileDir, String terminalId) {
        final String filePath = fileDir + terminalId;
        File statusFile = new File(filePath);
        if (statusFile.exists()) {
            statusFile.delete();
        }
    }

    /**
     * 刷机完成，删除镜像文件
     * 
     * @param packageId 刷机文件
     * @throws BusinessException 业务异常
     */
    private void deleteUpgradeImg(UUID packageId) throws BusinessException {
        TerminalSystemUpgradePackageEntity packageEntity =
                terminalSystemUpgradePackageService.getSystemUpgradePackage(packageId);
        final String imgName = packageEntity.getImgName();
        String upgradeImgFileDir = Constants.ISO_IMG_MOUNT_PATH + imgName;
        FileOperateUtil.deleteFile(new File(upgradeImgFileDir));
    }
}
