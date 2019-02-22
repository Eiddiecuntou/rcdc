package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.util.Assert;
import com.google.common.io.Files;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradePackageAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalSystemUpgradePackageInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalPlatformRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.PageSearchRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbCheckUploadingResultResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.UpgradeFileTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.SimpleCmdReturnValueResolver;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.QuerySystemUpgradePackageListService;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.shell.ShellCommandRunner;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * 
 * Description: 终端系统升级实现类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月19日
 * 
 * @author nt
 */
public class CbbTerminalSystemUpgradePackageAPIImpl implements CbbTerminalSystemUpgradePackageAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(CbbTerminalSystemUpgradePackageAPIImpl.class);

    private static final BeanCopier PACKAGE_BEAN_COPIER = BeanCopier.create(TerminalSystemUpgradePackageEntity.class,
            CbbTerminalSystemUpgradePackageInfoDTO.class, false);

    private static final Set<TerminalPlatformEnums> SYS_UPGRADE_PACKAGE_UPLOADING = new HashSet<>();

    private static final Object LOCK = new Object();

    @Autowired
    private TerminalSystemUpgradePackageDAO terminalSystemUpgradePackageDAO;

    @Autowired
    private TerminalSystemUpgradeDAO systemUpgradeDAO;

    @Autowired
    private TerminalSystemUpgradeService terminalSystemUpgradeService;

    @Autowired
    private TerminalSystemUpgradePackageService terminalSystemUpgradePackageService;

    @Autowired
    private QuerySystemUpgradePackageListService querySystemUpgradePackageListService;

    @Override
    public CbbCheckUploadingResultResponse isUpgradeFileUploading(CbbTerminalPlatformRequest request) {
        Assert.notNull(request, "request can not be null");

        CbbCheckUploadingResultResponse response = new CbbCheckUploadingResultResponse();
        response.setHasLoading(SYS_UPGRADE_PACKAGE_UPLOADING.contains(request.getPlatform()));
        return response;
    }

    @Override
    public DefaultResponse uploadUpgradeFile(CbbTerminalUpgradePackageUploadRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");
        final String fileName = request.getFileName();
        final String filePath = request.getFilePath();

        // 校验文件类型
        boolean isCorrectType = checkFileType(fileName);
        if (!isCorrectType) {
            LOGGER.debug("terminal system upgrade file type error, file name [{}] ", fileName);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_TYPE_ERROR);
        }

        TerminalUpgradeVersionFileInfo versionInfo = null;
        synchronized (LOCK) {
            versionInfo = getPackageVersionInfo(fileName, filePath);
            TerminalPlatformEnums packageType = versionInfo.getPackageType();
            if (SYS_UPGRADE_PACKAGE_UPLOADING.contains(packageType)) {
                throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_IS_UPLOADING);
            }
            SYS_UPGRADE_PACKAGE_UPLOADING.add(packageType);
        }

        try {
            // 根据升级包类型判断是否存在旧升级包，及是否存在旧升级包的正在进行中的升级任务，是则不允许替换升级包
            boolean hasRunningTask = isExistRunningTask(versionInfo.getPackageType());
            if (hasRunningTask) {
                LOGGER.debug("system upgrade task is running, can not upload file ", fileName);
                throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_IS_RUNNING);
            }

            // 将新升级文件移动到目录下
            final String packagePath = moveUpgradePackage(fileName, filePath, versionInfo.getPackageType());

            // 更新升级包信息入库
            versionInfo.setFilePath(packagePath);
            terminalSystemUpgradePackageService.saveTerminalUpgradePackage(versionInfo);

            // 替换升级文件,清除原升级包目录下旧文件
            FileOperateUtil.emptyDirectory(Constants.TERMINAL_UPGRADE_ISO_PATH_VDI, fileName);
        } finally {
            // 完成清除上传标志缓存内记录
            SYS_UPGRADE_PACKAGE_UPLOADING.remove(versionInfo.getPackageType());
        }

        return DefaultResponse.Builder.success();
    }

    private TerminalUpgradeVersionFileInfo getPackageVersionInfo(final String fileName, final String filePath)
            throws BusinessException {
        // 挂载升级包文件
        mountUpgradePackage(filePath);

        TerminalUpgradeVersionFileInfo versionInfo = null;
        try {
            // 读取校验文件内容
            versionInfo = checkVersionFile();
        } catch (Exception e) {
            LOGGER.error("check version file error", e);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_INCORRECT, e);
        } finally {
            // 取消挂载
            umountUpgradePackage();
        }

        versionInfo.setPackageName(fileName);
        return versionInfo;
    }

    /**
     * 检验是否存在正在进行的升级任务
     * 
     * @param packageType 升级包类型
     * @return
     */
    private boolean isExistRunningTask(TerminalPlatformEnums packageType) {
        TerminalSystemUpgradePackageEntity upgradePackage =
                terminalSystemUpgradePackageDAO.findFirstByPackageType(packageType);
        if (upgradePackage == null) {
            return false;
        }

        return terminalSystemUpgradeService.hasSystemUpgradeInProgress(upgradePackage.getId());
    }

    private String moveUpgradePackage(String fileName, String filePath, TerminalPlatformEnums packageType)
            throws BusinessException {
        File to = null;
        LOGGER.debug("升级包类型：{}", packageType.name());
        if (packageType != TerminalPlatformEnums.VDI) {
            LOGGER.debug("暂不支持的升级包类型：{}", packageType);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_PACKAGE_TYPE_UNSUPPORT,
                    packageType.name());
        }
        LOGGER.info("开始移动刷机包[{}]到路径[{}]", fileName, Constants.TERMINAL_UPGRADE_ISO_PATH_VDI);
        final String toPath = Constants.TERMINAL_UPGRADE_ISO_PATH_VDI + fileName;
        to = new File(toPath);
        File from = new File(filePath);

        try {
            Files.move(from, to);
        } catch (Exception e) {
            LOGGER.debug("move upgrade file to target directory fail, fileName : {}, packageType : {}", fileName,
                    packageType.name());
            throw new BusinessException(BusinessKey.RCDC_FILE_OPERATE_FAIL, e);
        }
        LOGGER.info("完成移动刷机包");

        return toPath;
    }

    private boolean checkFileType(String fileName) {
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        // 文件类型校验
        if (UpgradeFileTypeEnums.contains(fileType)) {
            LOGGER.debug("file type [{}] is correct", fileType);
            return true;
        }
        return false;
    }

    private void mountUpgradePackage(final String filePath) throws BusinessException {
        LOGGER.debug("mount package, path is [{}]", filePath);
        String mountCmd = String.format(Constants.SYSTEM_CMD_MOUNT_UPGRADE_ISO, filePath,
                Constants.TERMINAL_UPGRADE_ISO_MOUNT_PATH);

        LOGGER.info("mount package, cmd : {}", mountCmd);
        runShellCommand(mountCmd);
        LOGGER.info("mount package success");
    }

    private void umountUpgradePackage() throws BusinessException {
        LOGGER.debug("umount package, path is [{}]", Constants.TERMINAL_UPGRADE_ISO_MOUNT_PATH);
        String umountCmd =
                String.format(Constants.SYSTEM_CMD_UMOUNT_UPGRADE_ISO, Constants.TERMINAL_UPGRADE_ISO_MOUNT_PATH);

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

    private TerminalUpgradeVersionFileInfo checkVersionFile() throws BusinessException {
        // 获取升级文件信息
        TerminalUpgradeVersionFileInfo verInfo = getVersionInfo();
        if (verInfo.getPackageType() == null || StringUtils.isBlank(verInfo.getImgName())
                || StringUtils.isBlank(verInfo.getVersion())) {
            LOGGER.debug("version file info error: {}", verInfo.toString());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_INCORRECT);
        }

        return verInfo;
    }

    private TerminalUpgradeVersionFileInfo getVersionInfo() throws BusinessException {
        // 从文件中获取升级文件信息
        String filePath = Constants.TERMINAL_UPGRADE_ISO_MOUNT_PATH + Constants.TERMINAL_UPGRADE_ISO_VERSION_FILE_PATH;
        Properties prop = new Properties();
        try (InputStream in = new FileInputStream(filePath);) {
            prop.load(in);
        } catch (FileNotFoundException e) {
            LOGGER.debug("version file not found, file path[{}]", filePath);
            throw new BusinessException(BusinessKey.RCDC_FILE_NOT_EXIST, e);
        } catch (IOException e) {
            LOGGER.debug("version file read error, file path[{}]", filePath);
            throw new BusinessException(BusinessKey.RCDC_FILE_OPERATE_FAIL, e);
        }

        // 获取镜像名称
        String imgName = getImgName();
        TerminalUpgradeVersionFileInfo versionInfo = new TerminalUpgradeVersionFileInfo();
        versionInfo.setPackageType(TerminalPlatformEnums
                .valueOf(prop.getProperty(Constants.TERMINAL_UPGRADE_ISO_VERSION_FILE_KEY_PACKAGE_TYPE)));
        versionInfo.setVersion(prop.getProperty(Constants.TERMINAL_UPGRADE_ISO_VERSION_FILE_KEY_VERSION));
        versionInfo.setImgName(imgName);
        return versionInfo;
    }

    private String getImgName() throws BusinessException {
        String imgPath = Constants.TERMINAL_UPGRADE_ISO_MOUNT_PATH + Constants.TERMINAL_UPGRADE_ISO_IMG_FILE_PATH;
        File file = new File(imgPath);
        if (!file.isDirectory()) {
            LOGGER.debug("system upgrade file incorrect, img direction not exist, file path[{}]", imgPath);
            throw new BusinessException(BusinessKey.RCDC_FILE_NOT_EXIST);
        }
        String[] fileNameArr = file.list();
        if (fileNameArr == null || fileNameArr.length == 0) {
            LOGGER.debug("system upgrade file incorrect, img file not exist, file path[{}]", imgPath);
            throw new BusinessException(BusinessKey.RCDC_FILE_NOT_EXIST);
        }
        return fileNameArr[0];
    }

    @Override
    public DefaultPageResponse<CbbTerminalSystemUpgradePackageInfoDTO> listSystemUpgradePackage(
            PageSearchRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        final Page<TerminalSystemUpgradePackageEntity> page =
                querySystemUpgradePackageListService.pageQuery(request, TerminalSystemUpgradePackageEntity.class);
        List<TerminalSystemUpgradePackageEntity> packageList = page.getContent();
        CbbTerminalSystemUpgradePackageInfoDTO[] dtoArr =
                new CbbTerminalSystemUpgradePackageInfoDTO[packageList.size()];
        Stream.iterate(0, i -> i + 1).limit(packageList.size()).forEach(i -> {
            CbbTerminalSystemUpgradePackageInfoDTO dto = new CbbTerminalSystemUpgradePackageInfoDTO();
            final TerminalSystemUpgradePackageEntity packageEntity = packageList.get(i);
            PACKAGE_BEAN_COPIER.copy(packageEntity, dto, null);
            dto.setName(packageEntity.getPackageName());
            // 设置刷机包刷机状态
            completeUpgradingTaskInfo(dto, packageEntity);
            dtoArr[i] = dto;
        });

        return DefaultPageResponse.Builder.success(page.getSize(), (int) page.getTotalElements(), dtoArr);
    }

    private void completeUpgradingTaskInfo(CbbTerminalSystemUpgradePackageInfoDTO dto,
            final TerminalSystemUpgradePackageEntity packageEntity) {
        final UUID packageId = packageEntity.getId();
        boolean hasUpgradingTask = terminalSystemUpgradeService.hasSystemUpgradeInProgress(packageId);
        dto.setIsUpgrading(hasUpgradingTask);
        if (hasUpgradingTask) {
            final TerminalSystemUpgradeEntity upgradingTask = getUpgradingTask(packageId);
            dto.setUpgradeTaskId(upgradingTask.getId());
        }
    }

    private TerminalSystemUpgradeEntity getUpgradingTask(UUID packageId) {
        TerminalSystemUpgradeEntity exampleEntity = new TerminalSystemUpgradeEntity();
        exampleEntity.setUpgradePackageId(packageId);
        exampleEntity.setState(CbbSystemUpgradeTaskStateEnums.UPGRADING);
        final List<TerminalSystemUpgradeEntity> upgradingTaskList = systemUpgradeDAO.findAll(Example.of(exampleEntity));
        // 同一时间只存在一个正在刷机中的任务
        return upgradingTaskList.get(0);
    }

}
