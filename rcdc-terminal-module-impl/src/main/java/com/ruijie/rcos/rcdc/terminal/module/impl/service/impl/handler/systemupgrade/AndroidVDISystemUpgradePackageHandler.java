package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import com.ruijie.rcos.base.aaa.module.def.api.AuditLogAPI;
import com.ruijie.rcos.base.sysmanage.module.def.dto.SeedFileInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradeAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbAddSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.UpgradeableTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.ViewUpgradeableTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.BtClientService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/10
 *
 * @author hs
 */
@Service
public class AndroidVDISystemUpgradePackageHandler extends AbstractSystemUpgradePackageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AndroidVDISystemUpgradePackageHandler.class);

    private static final String OTA_SUFFIX = ".zip";

    private static final CbbSystemUpgradeModeEnums DEFAULT_UPGRADE_MODE = CbbSystemUpgradeModeEnums.AUTO;

    @Autowired
    private TerminalSystemUpgradePackageService terminalSystemUpgradePackageService;

    @Autowired
    private AndroidVDISystemUpgradePackageHelper systemUpgradePackageHelper;

    @Autowired
    private BtClientService btClientService;

    @Autowired
    private UpgradeableTerminalDAO upgradeableTerminalDAO;

    @Autowired
    private CbbTerminalSystemUpgradeAPI cbbTerminalSystemUpgradeAPI;

    @Autowired
    private TerminalSystemUpgradePackageDAO terminalSystemUpgradePackageDAO;

    @Autowired
    private AuditLogAPI logAPI;

    @Override
    protected TerminalSystemUpgradePackageService getSystemUpgradePackageService() {
        return terminalSystemUpgradePackageService;
    }

    @Override
    protected TerminalUpgradeVersionFileInfo getPackageInfo(String fileName, String filePath) throws BusinessException {
        Assert.hasText(fileName, "fileName can not be blank");
        Assert.hasText(filePath, "filePath can not be blank");
        String savePackageName = UUID.randomUUID() + OTA_SUFFIX;
        try {
            // 解压zip文件
            String packagePath = systemUpgradePackageHelper.unZipPackage(filePath, savePackageName);
            // 校验version信息
            TerminalUpgradeVersionFileInfo upgradeInfo =
                    systemUpgradePackageHelper.checkVersionInfo(packagePath, Constants.TERMINAL_UPGRADE_OTA_PACKAGE_VERSION);
            // 制作Bt种子
            SeedFileInfoDTO seedFileInfo = btClientService.makeBtSeed(packagePath, Constants.TERMINAL_UPGRADE_OTA_SEED_FILE);

            upgradeInfo.setPackageType(CbbTerminalTypeEnums.VDI_ANDROID);
            upgradeInfo.setPackageName(fileName);
            upgradeInfo.setFilePath(packagePath);
            upgradeInfo.setSeedLink(seedFileInfo.getSeedFilePath());
            upgradeInfo.setSeedMD5(seedFileInfo.getSeedFileMD5());
            upgradeInfo.setFileSaveDir(Constants.TERMINAL_UPGRADE_OTA_PACKAGE);
            upgradeInfo.setRealFileName(savePackageName);
            upgradeInfo.setUpgradeMode(DEFAULT_UPGRADE_MODE);
            return upgradeInfo;
        } catch (Exception e) {
            FileOperateUtil.deleteFileByPath(Constants.TERMINAL_UPGRADE_OTA_PACKAGE + savePackageName);
            throw e;
        } finally {
            FileOperateUtil.deleteFileByPath(Constants.TERMINAL_UPGRADE_OTA_PACKAGE_VERSION);
            FileOperateUtil.deleteFileByPath(Constants.TERMINAL_UPGRADE_OTA_PACKAGE_ZIP);
        }
    }

    @Override
    protected void postUploadPackage(TerminalUpgradeVersionFileInfo upgradeInfo) {
        Assert.notNull(upgradeInfo, "upgradeInfo cannot be null!");

        LOGGER.info("开始执行Android VDI终端升级包上传后处理");
        // 查询所有android终端ID
        List<ViewUpgradeableTerminalEntity> terminalEntityList =
                upgradeableTerminalDAO.findAllByPlatformEqualsAndTerminalOsTypeEquals(CbbTerminalPlatformEnums.VDI,
                        CbbTerminalTypeEnums.VDI_ANDROID.getOsType());
        if (CollectionUtils.isEmpty(terminalEntityList)) {
            LOGGER.info("未找到可升级的Android VDI终端");
            return;
        }
        String[] terminalIdArr = terminalEntityList.stream()
                .map(ViewUpgradeableTerminalEntity::getTerminalId).collect(Collectors.toList()).toArray(new String[]{});
        LOGGER.debug("需要升级的终端ID: [{}]", Arrays.toString(terminalIdArr));

        // 获取升级包信息
        TerminalSystemUpgradePackageEntity upgradePackage =
                terminalSystemUpgradePackageDAO.findFirstByPackageType(CbbTerminalTypeEnums.VDI_ANDROID);
        LOGGER.debug("升级包信息: [{}]", upgradeInfo.toString());

        // 添加升级任务
        CbbAddSystemUpgradeTaskRequest request = new CbbAddSystemUpgradeTaskRequest();
        request.setTerminalIdArr(terminalIdArr);
        request.setPackageId(upgradePackage.getId());
        try {
            cbbTerminalSystemUpgradeAPI.addSystemUpgradeTask(request);
            LOGGER.info("自动添加Android VDI升级任务成功");
            logAPI.recordLog(BusinessKey.RCDC_TERMINAL_CREATE_UPGRADE_TASK_SUCCESS_LOG, upgradePackage.getPackageName());
        } catch (BusinessException e) {
            LOGGER.error("自动添加Android VDI升级任务失败");
            logAPI.recordLog(BusinessKey.RCDC_TERMINAL_CREATE_UPGRADE_TASK_FAIL_LOG,
                    upgradePackage.getPackageName(), e.getI18nMessage());
        }
    }
}
