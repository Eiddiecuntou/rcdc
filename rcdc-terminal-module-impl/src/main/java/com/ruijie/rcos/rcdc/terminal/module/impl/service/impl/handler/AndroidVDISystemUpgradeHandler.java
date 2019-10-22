package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import com.alibaba.fastjson.JSONObject;
import com.ruijie.rcos.base.sysmanage.module.def.api.NetworkAPI;
import com.ruijie.rcos.base.sysmanage.module.def.api.request.network.BaseDetailNetworkRequest;
import com.ruijie.rcos.base.sysmanage.module.def.api.response.network.BaseDetailNetworkInfoResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.SeedFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalSystemUpgradeServiceTx;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.api.util.ZipUtil;
import com.ruijie.rcos.sk.base.crypto.Md5Builder;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.shell.ShellCommandRunner;
import com.ruijie.rcos.sk.base.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.*;
import java.nio.file.Files;
import java.util.Properties;
import java.util.UUID;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/10
 *
 * @author hs
 */
@Service
public class AndroidVDISystemUpgradeHandler implements TerminalSystemUpgradeHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AndroidVDISystemUpgradeHandler.class);

    private static final String UPGRADE_MODE = "upgradeMode";

    private static final String OTA_SUFFIX = ".zip";

    private static final String INIT_COMMAND = "python %s %s %s %s";

    private static final String INIT_PYTHON_SCRIPT_PATH = "/data/web/rcdc/shell/ota_bt.py";

    @Autowired
    private TerminalSystemUpgradePackageService terminalSystemUpgradePackageService;

    @Autowired
    private NetworkAPI networkAPI;

    @Autowired
    private TerminalSystemUpgradePackageDAO terminalSystemUpgradePackageDAO;

    @Autowired
    private TerminalSystemUpgradeServiceTx terminalSystemUpgradeServiceTx;

    @Override
    public void uploadUpgradePackage(CbbTerminalUpgradePackageUploadRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");
        String fileName = request.getFileName();
        String filePath = request.getFilePath();
        JSONObject jsonObject = request.getCustomData();
        CbbSystemUpgradeModeEnums upgradeMode = jsonObject.getObject(UPGRADE_MODE, CbbSystemUpgradeModeEnums.class);
        TerminalUpgradeVersionFileInfo upgradeInfo = getPackageInfo(fileName, filePath);
        upgradeInfo.setUpgradeMode(upgradeMode);
        terminalSystemUpgradePackageService.saveTerminalUpgradePackage(upgradeInfo);
        TerminalSystemUpgradePackageEntity packageEntity = terminalSystemUpgradePackageDAO.findFirstByPackageType(CbbTerminalTypeEnums.VDI_ANDROID);
        terminalSystemUpgradeServiceTx.startOtaUpgradeTask(packageEntity);

    }

    private TerminalUpgradeVersionFileInfo getPackageInfo(String fileName, String filePath) throws BusinessException {
        Assert.hasText(fileName, "fileName can not be blank");
        Assert.hasText(filePath, "filePath can not be blank");
        String storePackageName = UUID.randomUUID() + OTA_SUFFIX;
        String unZipFilePath = Constants.TERMINAL_UPGRADE_OTA_PACKAGE;
        String packagePath = unZipFilePath + storePackageName;
        //解压zip文件
        Properties prop = unZipPackage(filePath, unZipFilePath);
        String fileMD5 = prop.getProperty(Constants.TERMINAL_UPGRADE_OTA_VERSION_FILE_KEY_PACKAGE_MD5);
        String platType = prop.getProperty(Constants.TERMINAL_UPGRADE_OTA_VERSION_FILE_KEY_PACKAGE_PLAT);
        String version = prop.getProperty(Constants.TERMINAL_UPGRADE_OTA_VERSION_FILE_KEY_PACKAGE_VERSION);
        SeedFileInfo seedFileInfo = null;
        File newFile = new File(packagePath);
        try {
            //校验OTA包
            checkOtaUpgradePackage(platType, fileMD5, packagePath);
            //制作Bt种子
            seedFileInfo = makeBtSeed(packagePath);
        } catch (BusinessException e) {
            //有异常，删除新上传的OTA包
            FileOperateUtil.deleteFile(newFile);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OTA_UPGRADE_PACKAGE_UPLOAD_FAIL, e);
        }

        // 替换升级文件,清除原升级包目录下旧文件
        FileOperateUtil.emptyDirectory(Constants.TERMINAL_UPGRADE_OTA_PACKAGE, storePackageName);
        TerminalUpgradeVersionFileInfo upgradeInfo = new TerminalUpgradeVersionFileInfo();
        upgradeInfo.setVersion(version);
        upgradeInfo.setFileMD5(fileMD5);
        upgradeInfo.setPackageType(CbbTerminalTypeEnums.VDI_ANDROID);
        upgradeInfo.setPackageName(fileName);
        upgradeInfo.setFilePath(packagePath);
        upgradeInfo.setSeedLink(seedFileInfo.getSeedFilePath());
        upgradeInfo.setSeedMD5(seedFileInfo.getSeedFileMD5());
        return upgradeInfo;
    }

    private void checkOtaUpgradePackage(String platType, String fileMD5, String packagePath) throws BusinessException {
        Assert.notNull(platType, "platType can not be null");
        Assert.notNull(fileMD5, "fileMD5 can not be null");
        Assert.notNull(packagePath, "packagePath can not be null");
        File oldFile = new File(Constants.TERMINAL_UPGRADE_OTA_PACKAGE_ZIP);
        File newFile = new File(packagePath);
        if (!oldFile.exists()) {
            LOGGER.error("ota upgrade package not exist");
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OTA_UPGRADE_PACKAGE_NOT_EXIST);
        }
        try {
            Files.move(oldFile.toPath(), newFile.toPath());
        } catch (Exception e) {
            LOGGER.debug("move upgrade file to target directory fail");
            FileOperateUtil.deleteFile(oldFile);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OTA_UPGRADE_PACKAGE_MOVE_FAIL, e);
        }

        String packageMD5 = generateFileMD5(packagePath);
        if (!fileMD5.equals(packageMD5) || !platType.equals(Constants.TERMINAL_UPGRADE_OTA_PLATFORM_TYPE)) {
            LOGGER.error("terminal ota upgrade package has error, fileMD5[{}], platType[{}]", fileMD5, platType);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OTA_UPGRADE_PACKAGE_HAS_ERROR);
        }

    }

    private Properties unZipPackage(String zipfilePath, String unZipFilePath) throws BusinessException {
        Assert.hasText(zipfilePath, "filePath can not be blank");
        Assert.hasText(unZipFilePath, "unZipFilePath can not be blank");
        File zipFile = new File(zipfilePath);
        createFilePath(unZipFilePath);
        File unZipFile = new File(unZipFilePath);
        Properties prop = new Properties();
        try {
            ZipUtil.unzipFile(zipFile, unZipFile);
            InputStream inputStream = new FileInputStream(getVersionFilePath());
            prop.load(inputStream);
        } catch (FileNotFoundException e) {
            LOGGER.debug("version file not found, file path[{}]", zipFile);
            throw new BusinessException(BusinessKey.RCDC_FILE_NOT_EXIST, e);
        } catch (IOException e) {
            LOGGER.debug("version file read error, file path[{}]", zipFile);
            throw new BusinessException(BusinessKey.RCDC_FILE_OPERATE_FAIL, e);
        }
        return prop;

    }

    private SeedFileInfo makeBtSeed(String filePath) throws BusinessException {
        Assert.notNull(filePath, "filePath can not be null");
        String seedSavePath = Constants.TERMINAL_UPGRADE_OTA_SEED_FILE;
        createFilePath(seedSavePath);
        ShellCommandRunner runner = new ShellCommandRunner();
        String seedPath = null;
        String shellCmd = String.format(INIT_COMMAND, INIT_PYTHON_SCRIPT_PATH, filePath, seedSavePath, getLocalIP());
        LOGGER.info("excecute shell cmd : {}", shellCmd);
        runner.setCommand(shellCmd);
        try {
            seedPath = runner.execute();
            LOGGER.debug("seed path is :{}", seedPath);
        } catch (BusinessException e) {
            LOGGER.error("make seed file error", e);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OTA_UPGRADE_MAKE_SEED_FILE_FAIL, e);
        }
        File seedFile = new File(seedPath);
        FileOperateUtil.emptyDirectory(Constants.TERMINAL_UPGRADE_OTA_SEED_FILE, seedFile.getName());
        String seedMD5 = generateFileMD5(seedPath);
        SeedFileInfo seedFileInfo = new SeedFileInfo(seedPath, seedMD5);
        return seedFileInfo;
    }

    /**
     * 计算MD5值
     */
    private String generateFileMD5(String filePath) throws  BusinessException {

        File seedFile = new File(filePath);
        String seedMD5 = null;
        try {
            seedMD5 = StringUtils.bytes2Hex(Md5Builder.computeFileMd5(seedFile));
        } catch (IOException e) {
            LOGGER.error("compute seed file md5 fail, seed file path[{}]", filePath);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OTA_UPGRADE_COMPUTE_SEED_FILE_MD5_FAIL, e);
        }
        return seedMD5;

    }

    /**
     * 获取ip
     *
     * @return ip
     */
    private String getLocalIP() throws BusinessException {
        BaseDetailNetworkRequest request = new BaseDetailNetworkRequest();
        BaseDetailNetworkInfoResponse response = networkAPI.detailNetwork(request);
        return response.getNetworkDTO().getIp();
    }

    private void createFilePath(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdir();
            file.setReadable(true, false);
            file.setExecutable(true, false);
        }

    }

    private String getVersionFilePath() {
        return Constants.TERMINAL_UPGRADE_OTA_PACKAGE_VERSION;
    }

}
