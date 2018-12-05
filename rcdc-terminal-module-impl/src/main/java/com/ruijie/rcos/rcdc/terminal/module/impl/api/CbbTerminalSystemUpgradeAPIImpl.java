package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradeAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalSystemUpgradePackageInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.TerminalSystemUpgradeTaskDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbAddTerminalSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbBatchAddTerminalSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbRemoveTerminalSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalSystemUpgradePackageListRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbBaseListResponse;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.SystemUpgradeTask;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.SystemUpgradeTaskManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.CbbTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.UpgradeFileTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalSystemUpgradeMsg;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.CmdExecuteUtil;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.NfsServiceUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.util.Assert;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.webmvc.api.request.ChunkUploadFile;

/**
 * 
 * Description: 终端系统升级实现类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月19日
 * 
 * @author nt
 */
public class CbbTerminalSystemUpgradeAPIImpl implements CbbTerminalSystemUpgradeAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(CbbTerminalSystemUpgradeAPIImpl.class);

    /**
     * 系统镜像挂载指令
     */
    private static final String SYSTEM_CMD_MOUNT_UPGRADE_ISO = "mount %s %s";

    /**
     * 系统镜像解除挂载指令
     */
    private static final String SYSTEM_CMD_UMOUNT_UPGRADE_ISO = "umount %s %s";

    private static final BeanCopier PACKAGE_BEAN_COPIER = BeanCopier.create(TerminalSystemUpgradePackageEntity.class,
            CbbTerminalSystemUpgradePackageInfoDTO.class, false);

    private static final BeanCopier TASK_BEAN_COPIER =
            BeanCopier.create(SystemUpgradeTask.class, TerminalSystemUpgradeTaskDTO.class, false);

    @Autowired
    private TerminalSystemUpgradePackageDAO terminalSystemUpgradePackageDAO;

    @Autowired
    private TerminalSystemUpgradeService terminalSystemUpgradeService;

    @Autowired
    private SystemUpgradeTaskManager systemUpgradeTaskManager;

    @Autowired
    private TerminalBasicInfoDAO basicInfoDAO;


    @Override
    public DefaultResponse uploadUpgradeFile(CbbTerminalUpgradePackageUploadRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");
        ChunkUploadFile file = request.getFile();
        final String fileName = file.getFileName();
        final String filePath = file.getFilePath();
        final String fileMD5 = file.getFileMD5();

        // 判断是否有正在升级中的任务
        if (systemUpgradeTaskManager.countUpgradingNum() > 0) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_IS_RUNNING);
        }

        // 校验文件类型
        boolean isCorrectType = checkFileType(fileName);
        if (!isCorrectType) {
            LOGGER.debug("terminal system upgrade file type error, file name [{}] ", fileName);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_TYPE_ERROR);
        }

        // 验证文件是否完整
        boolean isComplete = isComplete(fileMD5);
        if (!isComplete) {
            LOGGER.debug("terminal system upgrade file complete check error, file md5 [{}] ", fileMD5);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_COMPLETE_CHECK_FAIL);
        }

        // 挂载升级文件，读取版本信息内容，校验正确
        mountUpgradePackage(filePath);

        // 读取校验文件内容
        TerminalUpgradeVersionFileInfo versionInfo = checkVersionFile();

        // 取消挂载
        umountUpgradePackage(filePath);

        // 将新升级文件移动到目录下
        moveUpgradePackage(fileName, filePath, versionInfo.getPackageType());

        // 更新升级包信息入库
        // versionInfo.setPackageName(fileName);
        terminalSystemUpgradeService.modifyTerminalUpgradePackageVersion(versionInfo);

        // 替换升级文件,清除原升级包目录下旧文件，
        FileOperateUtil.deleteFile(Constants.TERMINAL_UPGRADE_ISO_PATH_VDI, fileName);

        return DefaultResponse.Builder.success();
    }

    private void moveUpgradePackage(String fileName, String filePath, CbbTerminalTypeEnums packageType)
            throws BusinessException {
        File out = null;
        if (packageType == CbbTerminalTypeEnums.VDI) {
            out = new File(Constants.TERMINAL_UPGRADE_ISO_PATH_VDI + fileName);
        } else if (packageType == CbbTerminalTypeEnums.IDV) {
            out = new File(Constants.TERMINAL_UPGRADE_ISO_PATH_IDV + fileName);
        } else {
            out = new File(Constants.TERMINAL_UPGRADE_ISO_PATH_OTA + fileName);
        }
        File in = new File(filePath);
        try {
            FileCopyUtils.copy(in, out);
        } catch (IOException e) {
            LOGGER.debug("copy upgrade file to target directory fail, fileName : {}, packageType : {}", fileName,
                    packageType.getName());
            throw new BusinessException(BusinessKey.RCDC_FILE_OPERATE_FAIL, e);
        }
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

    /**
     * 验证包是否完整
     * 
     * @param md5
     * @return
     */
    private boolean isComplete(String md5) {
        // TODO
        return true;
    }

    private void mountUpgradePackage(final String filePath) throws BusinessException {
        LOGGER.debug("mount package");
        CmdExecuteUtil.executeCmd(
                String.format(SYSTEM_CMD_MOUNT_UPGRADE_ISO, filePath, Constants.TERMINAL_UPGRADE_ISO_MOUNT_PATH));
    }

    private void umountUpgradePackage(final String filePath) throws BusinessException {
        LOGGER.debug("umount package");
        CmdExecuteUtil.executeCmd(
                String.format(SYSTEM_CMD_UMOUNT_UPGRADE_ISO, filePath, Constants.TERMINAL_UPGRADE_ISO_MOUNT_PATH));
    }


    private TerminalUpgradeVersionFileInfo checkVersionFile() throws BusinessException {
        // 获取升级文件信息
        TerminalUpgradeVersionFileInfo verInfo = getVersionInfo();
        if (verInfo.getPackageType() == null || StringUtils.isBlank(verInfo.getPackageName())
                || StringUtils.isBlank(verInfo.getInternalVersion())
                || StringUtils.isBlank(verInfo.getExternalVersion())) {
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

        TerminalUpgradeVersionFileInfo versionInfo = new TerminalUpgradeVersionFileInfo();
        versionInfo.setPackageType(CbbTerminalTypeEnums
                .valueOf(prop.getProperty(Constants.TERMINAL_UPGRADE_ISO_VERSION_FILE_KEY_PACKAGE_TYPE)));
        versionInfo
                .setInternalVersion(prop.getProperty(Constants.TERMINAL_UPGRADE_ISO_VERSION_FILE_KEY_INTERNAL_VERSION));
        versionInfo
                .setExternalVersion(prop.getProperty(Constants.TERMINAL_UPGRADE_ISO_VERSION_FILE_KEY_EXTERNAL_VERSION));

        return versionInfo;
    }

    @Override
    public CbbBaseListResponse<CbbTerminalSystemUpgradePackageInfoDTO> listSystemUpgradePackage(
            CbbTerminalSystemUpgradePackageListRequest request) throws BusinessException {
        Assert.notNull(request, "SystemUpgradePackageListRequest can not be null");
        List<TerminalSystemUpgradePackageEntity> packages =
                terminalSystemUpgradePackageDAO.findByPackageType(request.getTerminalType());
        if (CollectionUtils.isEmpty(packages)) {
            LOGGER.debug("query terminal system upgrade package info with terminalType[{}] is empty",
                    request.getTerminalType());
            return new CbbBaseListResponse<>();
        }
        CbbTerminalSystemUpgradePackageInfoDTO[] dtoArr = new CbbTerminalSystemUpgradePackageInfoDTO[packages.size()];
        Stream.iterate(0, i -> i + 1).limit(packages.size()).forEach(i -> {
            CbbTerminalSystemUpgradePackageInfoDTO dto = new CbbTerminalSystemUpgradePackageInfoDTO();
            PACKAGE_BEAN_COPIER.copy(packages.get(i), dto, null);
            dtoArr[i] = dto;
        });

        return new CbbBaseListResponse<>(dtoArr);
    }

    @Override
    public DefaultResponse batchAddSystemUpgradeTask(CbbBatchAddTerminalSystemUpgradeTaskRequest request)
            throws BusinessException {

        Assert.notNull(request, "BatchAddSystemUpgradeTaskRequest can not be null");

        TerminalSystemUpgradePackageEntity upgradePackage =
                terminalSystemUpgradePackageDAO.findFirstByPackageType(request.getTerminalType());
        if (upgradePackage == null) {
            LOGGER.debug("query terminal system upgrade package by terminal type [{}] is null",
                    request.getTerminalType());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_NOT_EXIST);
        }

        List<String> terminalIdList = new ArrayList<>(Arrays.asList(request.getTerminalIdArr()));
        // 判断添加数量是否超过限制
        if (systemUpgradeTaskManager.checkMaxAddNum(terminalIdList.size())) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_NUM_EXCEED_LIMIT);
        }
        List<CbbTerminalEntity> terminals = basicInfoDAO.findByTerminalIdIn(terminalIdList);
        if (CollectionUtils.isEmpty(terminals) || terminals.size() < terminalIdList.size()) {
            LOGGER.debug("terminal can not found by terminal ids {}", terminalIdList.toString());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL);
        }

        for (CbbTerminalEntity terminal : terminals) {
            addTask(upgradePackage, terminal);
        }

        return DefaultResponse.Builder.success();
    }

    @Override
    public DefaultResponse addSystemUpgradeTask(CbbAddTerminalSystemUpgradeTaskRequest request)
            throws BusinessException {
        Assert.notNull(request, "AddSystemUpgradeTaskRequest can not be null");

        TerminalSystemUpgradePackageEntity upgradePackage =
                terminalSystemUpgradePackageDAO.findFirstByPackageType(request.getTerminalType());
        if (upgradePackage == null) {
            LOGGER.debug("terminal type is [{}], terminal system upgrade package not found", request.getTerminalType());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_NOT_EXIST);
        }

        CbbTerminalEntity terminal = basicInfoDAO.findFirstByTerminalId(request.getTerminalId());
        if (terminal == null) {
            LOGGER.debug("terminal id is [{}], terminal not found", request.getTerminalId());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL);
        }

        addTask(upgradePackage, terminal);

        return DefaultResponse.Builder.success();
    }

    private void addTask(TerminalSystemUpgradePackageEntity upgradePackage, CbbTerminalEntity terminal)
            throws BusinessException {
        // 非在线状态的终端不进行刷机
        if (!CbbTerminalStateEnums.ONLINE.equals(terminal.getState())) {
            LOGGER.info("terminal offline, can not upgrade system");
            return;
        }

        // 运行虚机的终端不进行刷机
        if (isRunningVirtualMachine()) {
            LOGGER.info("terminal is running virtual machine, can not upgrade system");
            return;
        }

        // 添加进任务队列中
        SystemUpgradeTask upgradeTask =
                systemUpgradeTaskManager.addTask(terminal.getTerminalId(), terminal.getTerminalType());

        // 开启NFS服务
        NfsServiceUtil.startService();

        if (upgradeTask != null && CbbSystemUpgradeStateEnums.DOING == upgradeTask.getState()
                && !upgradeTask.getIsSend()) {
            LOGGER.debug("send terminal system upgrade message ...");
            // 下发系统刷机指令
            TerminalSystemUpgradeMsg upgradeMsg =
                    new TerminalSystemUpgradeMsg(upgradePackage.getName(), upgradePackage.getStorePath(),
                            upgradePackage.getInternalVersion(), upgradePackage.getExternalVersion());
            try {
                terminalSystemUpgradeService.systemUpgrade(terminal.getId().toString(), upgradeMsg);
                upgradeTask.setIsSend(true);
                LOGGER.debug("send terminal system upgrade message success");
            } catch (Exception e) {
                LOGGER.info("system upgrade message send failed, remove it from cache");
                // 系统刷机指令发送失败，将任务重置为等待中
                systemUpgradeTaskManager.modifyTaskState(terminal.getTerminalId(), CbbSystemUpgradeStateEnums.WAIT);
            }

        }
    }

    private boolean isRunningVirtualMachine() {
        // TODO FIXME 终端是否在运行虚机
        return false;
    }

    @Override
    public DefaultResponse removeTerminalSystemUpgradeTask(CbbRemoveTerminalSystemUpgradeTaskRequest request)
            throws BusinessException {
        Assert.notNull(request, "RemoveSystemUpgradeTaskRequest can not be null");

        SystemUpgradeTask task = systemUpgradeTaskManager.getTaskByTerminalId(request.getTerminalId());
        if (task == null) {
            LOGGER.debug("query terminal upgrade task by terminal id[{}] is null", request.getTerminalId());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_NOT_EXIST);
        }

        if (CbbSystemUpgradeStateEnums.DOING == task.getState()) {
            LOGGER.debug("terminal upgrade task state is doing, terminal id[{}] ", request.getTerminalId());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_IS_RUNNING);
        }
        systemUpgradeTaskManager.removeTaskByTerminalId(request.getTerminalId());

        return DefaultResponse.Builder.success();

    }

    @Override
    public CbbBaseListResponse<TerminalSystemUpgradeTaskDTO> listTerminalSystemUpgradeTask() throws BusinessException {
        // 获取所以升级任务
        List<SystemUpgradeTask> tasks = systemUpgradeTaskManager.getAllTasks();
        if (CollectionUtils.isEmpty(tasks)) {
            return new CbbBaseListResponse<>();
        }

        // 对列表进行排序
        Collections.sort(tasks, new Comparator<SystemUpgradeTask>() {
            @Override
            public int compare(SystemUpgradeTask o1, SystemUpgradeTask o2) {
                if (o1.getState() == o2.getState()) {
                    if (o1.getStartTime() < o2.getStartTime()) {
                        return 1;
                    }
                } else {
                    if (o1.getState() == CbbSystemUpgradeStateEnums.DOING) {
                        return 1;
                    }
                }
                return -1;
            }
        });

        TerminalSystemUpgradeTaskDTO[] dtoArr = new TerminalSystemUpgradeTaskDTO[tasks.size()];
        // 将数据转换成dto输出
        Stream.iterate(0, i -> i + 1).limit(tasks.size()).forEach(i -> {
            TerminalSystemUpgradeTaskDTO dto = new TerminalSystemUpgradeTaskDTO();
            TASK_BEAN_COPIER.copy(tasks.get(i), dto, null);
            dtoArr[i] = dto;
        });

        return new CbbBaseListResponse<>(dtoArr);
    }

}
