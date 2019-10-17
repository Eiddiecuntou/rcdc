package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruijie.rcos.base.sysmanage.module.def.api.NetworkAPI;
import com.ruijie.rcos.base.sysmanage.module.def.api.request.network.BaseDetailNetworkRequest;
import com.ruijie.rcos.base.sysmanage.module.def.api.response.network.BaseDetailNetworkInfoResponse;
import com.ruijie.rcos.linux.library.Bt;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbMakeBtSeedRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.SeedFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.TerminalOtaUpgradeScheduleService;
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
public class AndroidVDISystemUpgradeHandler extends AbstractTerminalSystemUpgradeHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AndroidVDISystemUpgradeHandler.class);

    private static final String UPGRADE_MODE = "upgradeMode";

    private static final String SEED_PATH = "seed_path";

    private static final String ZIP_SUFFIX = "zip";

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
        // 替换升级文件,清除原升级包目录下旧文件
        FileOperateUtil.emptyDirectory(Constants.TERMINAL_UPGRADE_OTA_PACKAGE, upgradeInfo.getPackageName());

        if ( UPGRADE_TASK_FUTURE == null) {
            //开启检查终端状态定时任务
            OTA_UPGRADE_SCHEDULED_THREAD_POOL.scheduleAtFixedRate(terminalOtaUpgradeScheduleService, 0, PERIOD_SECOND, TimeUnit.SECONDS);
        }

    }

    @Override
    public TerminalUpgradeVersionFileInfo getPackageInfo(String fileName, String filePath) throws BusinessException {
        Assert.hasText(filePath, "filePath can not be blank");
        String storePackageName = UUID.randomUUID() + ZIP_SUFFIX;
        String packagePath = Constants.TERMINAL_UPGRADE_OTA_PACKAGE + storePackageName;
        //解压zip文件
        Properties prop = unZipPackage(filePath);
        File newFile = new File(packagePath);
        File oldFile = new File(Constants.TERMINAL_UPGRADE_OTA_PACKAGE_ZIP);
        //解压后的zip文件重命名
        oldFile.renameTo(newFile);
        TerminalUpgradeVersionFileInfo upgradeInfo = new TerminalUpgradeVersionFileInfo();
        upgradeInfo.setVersion(prop.getProperty(Constants.TERMINAL_UPGRADE_OTA_VERSION_FILE_KEY_PACKAGE_VERSION));
        upgradeInfo.setFileMD5(prop.getProperty(Constants.TERMINAL_UPGRADE_OTA_VERSION_FILE_KEY_PACKAGE_MD5));
        upgradeInfo.setPackageType(TerminalTypeEnums.VDI_ANDROID);
        upgradeInfo.setPackageName(storePackageName);
        upgradeInfo.setFilePath(packagePath);
        SeedFileInfo seedFileInfo = makeBtSeed(filePath);
        upgradeInfo.setSeedLink(seedFileInfo.getSeedFilePath());
        upgradeInfo.setSeedMD5(seedFileInfo.getSeedFileMD5());
        return upgradeInfo;
    }

    private Properties unZipPackage(String filePath) throws BusinessException {
        Assert.hasText(filePath, "filePath can not be blank");
        File zipFile = new File(filePath);
        String unZipFilePath = Constants.TERMINAL_UPGRADE_OTA_PACKAGE;
        createFilePath(unZipFilePath);
        File unZipFile = new File(unZipFilePath);
        String versionFilePath = getVersionFilePath();
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
        }

        return prop;

    }

    private SeedFileInfo makeBtSeed(String filePath) throws BusinessException {
        Assert.notNull(filePath, "filePath can not be null");
        String seedSavePath = Constants.TERMINAL_UPGRADE_OTA_SEED_FILE;
        createFilePath(seedSavePath);
        CbbMakeBtSeedRequest request = new CbbMakeBtSeedRequest(getLocalIP(), filePath, seedSavePath);
        String result = Bt.btMakeSeed_block(JSON.toJSONString(request));
        SystemResultCheckUtil.checkResult(result);
        String seedPath = JSONObject.parseObject(result).getString(SEED_PATH);
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
