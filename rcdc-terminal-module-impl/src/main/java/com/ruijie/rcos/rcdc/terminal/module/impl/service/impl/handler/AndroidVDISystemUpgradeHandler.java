package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruijie.rcos.base.sysmanage.module.def.api.NetworkAPI;
import com.ruijie.rcos.base.sysmanage.module.def.api.request.network.BaseDetailNetworkRequest;
import com.ruijie.rcos.base.sysmanage.module.def.api.response.network.BaseDetailNetworkInfoResponse;
import com.ruijie.rcos.linux.library.Bt;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.MakeBtSeedRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.StartBtShareRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.SeedFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.TerminalOtaUpgradeScheduleService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalSystemUpgradeServiceTx;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.SystemResultCheckUtil;
import com.ruijie.rcos.sk.base.api.util.ZipUtil;
import com.ruijie.rcos.sk.base.concurrent.ThreadExecutor;
import com.ruijie.rcos.sk.base.concurrent.ThreadExecutors;
import com.ruijie.rcos.sk.base.crypto.Md5Builder;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.io.*;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/10
 *
 * @author hs
 */
public class AndroidVDISystemUpgradeHandler implements TerminalSystemUpgradeHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AndroidVDISystemUpgradeHandler.class);

    private static final String UPGRADE_MODE = "upgradeMode";

    private static final String SEED_PATH = "seed_path";

    private static final String OTA_SUFFIX = "zip";

    private static final int PERIOD_SECOND = 15;

    private static ScheduledFuture<?> UPGRADE_TASK_FUTURE = null;

    private static ThreadExecutor OTA_UPGRADE_SCHEDULED_THREAD_POOL =
            ThreadExecutors.newBuilder("OTA_UPGRADE_SCHEDULED_THREAD").maxThreadNum(1).queueSize(1).build();

    @Autowired
    private TerminalSystemUpgradePackageService terminalSystemUpgradePackageService;

    @Autowired
    private NetworkAPI networkAPI;

    @Autowired
    private TerminalOtaUpgradeScheduleService terminalOtaUpgradeScheduleService;

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
        terminalSystemUpgradeServiceTx.addOtaUpgradeTask(packageEntity);
        if ( UPGRADE_TASK_FUTURE == null) {
            //开启检查终端状态定时任务
            OTA_UPGRADE_SCHEDULED_THREAD_POOL.scheduleAtFixedRate(terminalOtaUpgradeScheduleService, 0, PERIOD_SECOND, TimeUnit.SECONDS);
        }

    }

    private TerminalUpgradeVersionFileInfo getPackageInfo(String fileName, String filePath) throws BusinessException {
        Assert.hasText(fileName, "fileName can not be blank");
        Assert.hasText(filePath, "filePath can not be blank");
        String storePackageName = UUID.randomUUID() + OTA_SUFFIX;
        String packagePath = Constants.TERMINAL_UPGRADE_OTA_PACKAGE + storePackageName;
        //解压zip文件
        Properties prop = unZipPackage(filePath);
        File newFile = new File(packagePath);
        File oldFile = new File(Constants.TERMINAL_UPGRADE_OTA_PACKAGE_ZIP);
        if (!oldFile.exists()) {
            LOGGER.error("ota upgrade package not exist");
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OTA_UPGRADE_PACKAGE_NOT_EXIST);
        }
        //解压后的zip文件重命名
        oldFile.renameTo(newFile);
        String fileMD5 = prop.getProperty(Constants.TERMINAL_UPGRADE_OTA_VERSION_FILE_KEY_PACKAGE_MD5);
        String paltType = prop.getProperty(Constants.TERMINAL_UPGRADE_OTA_VERSION_FILE_KEY_PACKAGE_PLAT);
        //校验OTA包
        checkOtaUpgradePackage(paltType, fileMD5, packagePath);
        TerminalUpgradeVersionFileInfo upgradeInfo = new TerminalUpgradeVersionFileInfo();
        upgradeInfo.setVersion(prop.getProperty(Constants.TERMINAL_UPGRADE_OTA_VERSION_FILE_KEY_PACKAGE_VERSION));
        upgradeInfo.setFileMD5(fileMD5);
        upgradeInfo.setPackageType(CbbTerminalTypeEnums.VDI_ANDROID);
        upgradeInfo.setPackageName(fileName);
        upgradeInfo.setFilePath(packagePath);
        //制作Bt种子
        SeedFileInfo seedFileInfo = makeBtSeed(packagePath);
        //开启Bt服务
        startBtShare(seedFileInfo.getSeedFilePath(), Constants.TERMINAL_UPGRADE_OTA_PACKAGE);
        upgradeInfo.setSeedLink(seedFileInfo.getSeedFilePath());
        upgradeInfo.setSeedMD5(seedFileInfo.getSeedFileMD5());
        // 替换升级文件,清除原升级包目录下旧文件
        FileOperateUtil.emptyDirectory(Constants.TERMINAL_UPGRADE_OTA_PACKAGE, storePackageName);
        return upgradeInfo;
    }

    private void checkOtaUpgradePackage(String platType, String fileMD5, String packagePath) throws BusinessException {
        Assert.notNull(platType, "platType can not be null");
        Assert.notNull(fileMD5, "fileMD5 can not be null");
        Assert.notNull(packagePath, "packagePath can not be null");
        String packageMD5 = generateFileMD5(packagePath);
        File packageFile = new File(packagePath);
        if (!fileMD5.equals(packageMD5) || !platType.equals(Constants.TERMINAL_UPGRADE_OTA_PLATFORM_TYPE)) {
            FileOperateUtil.deleteFile(packageFile);
            LOGGER.error("terminal ota upgrade package has error");
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OTA_UPGRADE_PACKAGE_HAS_ERROR);
        }

    }

    private Properties unZipPackage(String filePath) throws BusinessException {
        Assert.hasText(filePath, "filePath can not be blank");
        File zipFile = new File(filePath);
        String unZipFilePath = Constants.TERMINAL_UPGRADE_OTA_PACKAGE;
        createFilePath(unZipFilePath);
        File unZipFile = new File(unZipFilePath);
        String versionFilePath = getVersionFilePath();
        File versionFile = new File(versionFilePath);
        Properties prop = new Properties();
        try {
            ZipUtil.unzipFile(zipFile, unZipFile);
            InputStream inputStream = new FileInputStream(versionFilePath);
            prop.load(inputStream);
        } catch (FileNotFoundException e) {
            LOGGER.debug("version file not found, file path[{}]", filePath);
            throw new BusinessException(BusinessKey.RCDC_FILE_NOT_EXIST, e);
        } catch (IOException e) {
            LOGGER.debug("version file read error, file path[{}]", filePath);
            throw new BusinessException(BusinessKey.RCDC_FILE_OPERATE_FAIL, e);
        } finally {
            //删除version文件
            FileOperateUtil.deleteFile(versionFile);
        }

        return prop;

    }

    private SeedFileInfo makeBtSeed(String filePath) throws BusinessException {
        Assert.notNull(filePath, "filePath can not be null");
        String seedSavePath = Constants.TERMINAL_UPGRADE_OTA_SEED_FILE;
        createFilePath(seedSavePath);
        MakeBtSeedRequest request = new MakeBtSeedRequest(getLocalIP(), filePath, seedSavePath);
        String result = Bt.btMakeSeed_block(JSON.toJSONString(request));
        SystemResultCheckUtil.checkResult(result);
        String seedPath = JSONObject.parseObject(result).getString(SEED_PATH);
        File seedFile = new File(seedPath);
        FileOperateUtil.emptyDirectory(Constants.TERMINAL_UPGRADE_OTA_SEED_FILE, seedFile.getName());
        String seedMD5 = generateFileMD5(seedPath);
        SeedFileInfo seedFileInfo = new SeedFileInfo(seedPath, seedMD5);
        return seedFileInfo;
    }

    private void startBtShare(String seedPath, String filePath) throws BusinessException {
        StartBtShareRequest btShareRequest = new StartBtShareRequest(seedPath, filePath);
        String result = Bt.btShareStart(JSON.toJSONString(btShareRequest));
        SystemResultCheckUtil.checkResult(result);
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
