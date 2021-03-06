package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalUpgradePackageUploadDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.BtClientService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalOtaInitService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradePackageHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradePackageHandlerFactory;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.crypto.Md5Builder;
import com.ruijie.rcos.sk.base.filesystem.common.FileUtils;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/10/19
 *
 * @author nting
 */
@Service
public class TerminalOtaInitServiceImpl implements TerminalOtaInitService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalOtaInitServiceImpl.class);

    @Autowired
    private TerminalSystemUpgradePackageHandlerFactory handlerFactory;

    @Autowired
    private TerminalSystemUpgradePackageDAO terminalSystemUpgradePackageDAO;

    @Autowired
    private TerminalSystemUpgradeDAO terminalSystemUpgradeDAO;

    @Autowired
    private BtClientService btClientService;

    private static final String BT_SHARE_FILE_PATH = "/opt/share_file";

    @Override
    public void initAndroidOta() {
        // ?????????VDI Android???OTA???
        init(Constants.TERMINAL_UPGRADE_OTA, CbbTerminalTypeEnums.VDI_ANDROID);
    }

    private void init(String basePath, CbbTerminalTypeEnums type) {
        // ????????????OTA???????????????????????????????????????????????????ISO???????????????tar?????????????????????
        File packageDir = new File(basePath);
        if (packageDir.isDirectory()) {
            List<File> fileList = FileOperateUtil.listFile(basePath);
            if (!CollectionUtils.isEmpty(fileList)) {
                LOGGER.info("?????????ota?????????");
                initOtaFile(fileList.get(0), type);
                return;
            }
        }

        TerminalSystemUpgradePackageEntity upgradePackage = terminalSystemUpgradePackageDAO.findFirstByPackageType(type);
        if (upgradePackage == null) {
            LOGGER.info("???????????????{}??????????????????????????????", type.name());
            return;
        }

        if (needStartBtShare(upgradePackage)) {
            LOGGER.info("?????????bt??????");
            initBtServer(upgradePackage);
        }
    }

    private boolean needStartBtShare(TerminalSystemUpgradePackageEntity upgradePackage) {
        //TODO ?????????????????????????????????BT??????????????????????????????????????????
        if (StringUtils.isBlank(upgradePackage.getSeedPath())) {
            LOGGER.info("bt??????????????????????????????????????????");
            return false;
        }

        File shareFile = new File(BT_SHARE_FILE_PATH);
        if (!shareFile.isFile()) {
            LOGGER.info("bt??????????????????????????????????????????");
            return true;
        }

        try {
            String shareFileContent = FileUtils.readFileToString(shareFile, Charset.forName("UTF-8"));
            if (shareFileContent.contains(upgradePackage.getSeedPath())) {
                LOGGER.info("??????????????????????????????????????????");
                return false;
            }

            return true;
        } catch (IOException e) {
            LOGGER.error("??????bt????????????????????????");
            return true;
        }
    }

    private void initBtServer(TerminalSystemUpgradePackageEntity upgradePackage) {
        List<CbbSystemUpgradeTaskStateEnums> stateList =
                Arrays.asList(new CbbSystemUpgradeTaskStateEnums[]{CbbSystemUpgradeTaskStateEnums.UPGRADING});
        List<TerminalSystemUpgradeEntity> upgradingTaskList =
                terminalSystemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc(upgradePackage.getId(), stateList);
        if (!CollectionUtils.isEmpty(upgradingTaskList)) {
            try {
                btClientService.startBtShare(upgradePackage.getFilePath(), upgradePackage.getSeedPath());
            } catch (Exception e) {
                LOGGER.error("??????BT????????????", e);
            }
        }
    }

    private void initOtaFile(File packageFile, CbbTerminalTypeEnums type) {
        try {
            CbbTerminalUpgradePackageUploadDTO request = generateRequest(packageFile, type);
            TerminalSystemUpgradePackageHandler handler = handlerFactory.getHandler(type);
            handler.preUploadPackage();
            handler.uploadUpgradePackage(request);
            FileOperateUtil.deleteFile(packageFile);
            handler.postUploadPackage();
        } catch (Exception e) {
            LOGGER.error("??????OTA???????????????", e);
        }
    }

    private CbbTerminalUpgradePackageUploadDTO generateRequest(File file, CbbTerminalTypeEnums type) throws IOException {
        Assert.notNull(file, "file can not be null");
        String fileName = file.getName();
        String filePath = file.getPath();

        CbbTerminalUpgradePackageUploadDTO request = new CbbTerminalUpgradePackageUploadDTO();
        request.setFilePath(filePath);
        request.setFileName(fileName);
        request.setTerminalType(type);
        request.setFileMD5(StringUtils.bytes2Hex(Md5Builder.computeFileMd5(file)));
        return request;
    }
}
