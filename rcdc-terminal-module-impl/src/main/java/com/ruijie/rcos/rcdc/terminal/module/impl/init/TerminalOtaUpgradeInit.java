package com.ruijie.rcos.rcdc.terminal.module.impl.init;

import com.alibaba.fastjson.JSONObject;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.BtService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.TerminalSystemUpgradeHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.TerminalSystemUpgradeHandlerFactory;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.bootstrap.SafetySingletonInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/16
 *
 * @author hs
 */
@Service
public class TerminalOtaUpgradeInit implements SafetySingletonInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalOtaUpgradeInit.class);

    @Autowired
    private TerminalSystemUpgradeHandlerFactory handlerFactory;

    @Autowired
    private TerminalSystemUpgradePackageDAO termianlSystemUpgradePackageDAO;

    @Autowired
    private TerminalSystemUpgradeDAO terminalSystemUpgradeDAO;

    @Autowired
    private BtService btService;

    @Override
    public void safeInit() {
        String basePath = Constants.TERMINAL_UPGRADE_OTA;

        //bt服务初始化
        btServiceInit();

        try {
            List<File> fileList = FileOperateUtil.listFile(basePath);
            if (CollectionUtils.isEmpty(fileList)) {
                LOGGER.debug("出厂路径下OTA升级包不存在");
                return;
            }
            File file = fileList.get(0);
            CbbTerminalUpgradePackageUploadRequest request = generateRequest(file);
            TerminalSystemUpgradeHandler handler = handlerFactory.getHandler(CbbTerminalTypeEnums.VDI_ANDROID);
            handler.uploadUpgradePackage(request);
            FileOperateUtil.deleteFile(file);
        } catch (BusinessException e) {
            LOGGER.error("获取OTA包信息失败", e);
        }

    }

    private void btServiceInit() {
        TerminalSystemUpgradePackageEntity upgradePackage = termianlSystemUpgradePackageDAO
                .findFirstByPackageType(CbbTerminalTypeEnums.VDI_ANDROID);
        if (upgradePackage == null) {
            return;
        }
        List<CbbSystemUpgradeTaskStateEnums> stateList = Arrays
                .asList(new CbbSystemUpgradeTaskStateEnums[] {CbbSystemUpgradeTaskStateEnums.UPGRADING});
        List<TerminalSystemUpgradeEntity> upgradingTaskList = terminalSystemUpgradeDAO
                .findByUpgradePackageIdAndStateInOrderByCreateTimeAsc(upgradePackage.getId(), stateList);
        if (!CollectionUtils.isEmpty(upgradingTaskList)) {
            try {
                btService.startBtShare(upgradePackage.getSeedPath());
            } catch (BusinessException e) {
                LOGGER.error("开始BT服务失败", e);
            }

        }
    }

    private CbbTerminalUpgradePackageUploadRequest generateRequest(File file) {
        Assert.notNull(file, "file can not be null");
        String fileName = file.getName();
        String filePath = file.getPath();
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("upgradeMode", CbbSystemUpgradeModeEnums.AUTO);
        request.setFilePath(filePath);
        request.setFileName(fileName);
        request.setCustomData(jsonObject);
        return request;
    }
}
