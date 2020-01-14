package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradeAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbSystemUpgradeTaskDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbSystemUpgradeTaskTerminalDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbUpgradeableTerminalListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.MatchEqual;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.TerminalGroupDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.*;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbAddSystemUpgradeTaskResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbGetTaskUpgradeTerminalResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbGetTerminalUpgradeTaskResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalNameResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.*;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.*;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.QuerySystemUpgradeListService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.QuerySystemUpgradeTerminalGroupListService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.QuerySystemUpgradeTerminalListService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.QueryUpgradeableTerminalListService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradeHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradeHandlerFactory;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalSystemUpgradeServiceTx;
import com.ruijie.rcos.sk.base.concurrent.ThreadExecutors;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.IdRequest;

/**
 * Description: 终端系统升级实现类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月19日
 *
 * @author nt
 */
public class CbbTerminalSystemUpgradeAPIImpl implements CbbTerminalSystemUpgradeAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(CbbTerminalSystemUpgradeAPIImpl.class);

    private static final BeanCopier TASK_BEAN_COPIER = BeanCopier.create(TerminalSystemUpgradeEntity.class, CbbSystemUpgradeTaskDTO.class, false);

    private static final BeanCopier TASK_TERMINAL_BEAN_COPIER =
            BeanCopier.create(TerminalSystemUpgradeTerminalEntity.class, CbbSystemUpgradeTaskTerminalDTO.class, false);

    private static final String PARAM_FIELD_PACKAGE_ID = "packageId";

    private static final String ENTITY_FILED_PLATFORM = "platform";

    private static final String ENTITY_FILED_TERMINAL_OS_TYPE = "terminalOsType";

    private static ExecutorService SINGLE_THREAD_EXECUTOR =
            ThreadExecutors.newBuilder("singleSystemUpgradeThreadPool").maxThreadNum(1).queueSize(10).build();

    private static final ExecutorService SEND_SYSTEM_UPGRADE_MSG_THREAD_POOL =
            ThreadExecutors.newBuilder("sendSystemUpgradeMsgThreadPool").maxThreadNum(10).queueSize(1).build();

    @Autowired
    private TerminalSystemUpgradePackageDAO terminalSystemUpgradePackageDAO;

    @Autowired
    private TerminalSystemUpgradeService terminalSystemUpgradeService;

    @Autowired
    private TerminalSystemUpgradeServiceTx terminalSystemUpgradeServiceTx;

    @Autowired
    private TerminalBasicInfoDAO basicInfoDAO;

    @Autowired
    private QuerySystemUpgradeListService querySystemUpgradeListService;

    @Autowired
    private QuerySystemUpgradeTerminalListService querySystemUpgradeTerminalListService;

    @Autowired
    private QuerySystemUpgradeTerminalGroupListService querySystemUpgradeTerminalGroupListService;

    @Autowired
    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;

    @Autowired
    private UpgradeTerminalLockManager lockManager;

    @Autowired
    private QueryUpgradeableTerminalListService upgradeableTerminalListService;

    @Autowired
    private TerminalSystemPackageUploadingService terminalSystemPackageUploadingService;

    @Autowired
    private TerminalSystemUpgradeHandlerFactory systemUpgradeHandlerFactory;

    @Autowired
    private TerminalSystemUpgradePackageService systemUpgradePackageService;

    @Autowired
    private TerminalGroupService terminalGroupService;

    @Override
    public CbbAddSystemUpgradeTaskResponse addSystemUpgradeTask(CbbAddSystemUpgradeTaskRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        UUID packageId = request.getPackageId();
        TerminalSystemUpgradePackageEntity upgradePackage = getUpgradePackageEntity(packageId);

        // 判断刷机包是否允许开启升级任务
        checkAllowCreateTask(upgradePackage);

        UUID upgradeTaskId = terminalSystemUpgradeServiceTx.addSystemUpgradeTask(upgradePackage, request);

        // 添加升级任务成功后的处理
        TerminalSystemUpgradeHandler handler = systemUpgradeHandlerFactory.getHandler(upgradePackage.getPackageType());
        handler.afterAddSystemUpgrade(upgradePackage);

        Object upgradeMsg = handler.getSystemUpgradeMsg(upgradePackage, upgradeTaskId, request.getUpgradeMode());
        // 向在线终端发送升级消息
        SINGLE_THREAD_EXECUTOR.execute(() -> sendSystemUpgradeMsg(request.getTerminalIdArr(), upgradeMsg));

        CbbAddSystemUpgradeTaskResponse response = new CbbAddSystemUpgradeTaskResponse();
        response.setUpgradeTaskId(upgradeTaskId);
        response.setImgName(upgradePackage.getPackageName());
        return response;
    }

    private void sendSystemUpgradeMsg(String[] terminalIdArr, Object upgradeMsg) {
        if (ArrayUtils.isEmpty(terminalIdArr)) {
            return;
        }

        for (String terminalId : terminalIdArr) {
            SEND_SYSTEM_UPGRADE_MSG_THREAD_POOL.execute(() -> {
                try {
                    terminalSystemUpgradeService.systemUpgrade(terminalId, upgradeMsg);
                } catch (BusinessException e) {
                    LOGGER.error("向终端[" + terminalId + "]发送系统升级消息失败", e);
                }
            });
        }
    }

    private void checkAllowCreateTask(TerminalSystemUpgradePackageEntity upgradePackage) throws BusinessException {

        // 判断刷机包是否正在上传中
        UUID packageId = upgradePackage.getId();
        boolean hasLoading = terminalSystemPackageUploadingService.isUpgradeFileUploading(upgradePackage.getPackageType());
        if (hasLoading) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_IS_UPLOADING);
        }

        // 判断是否已存在进行中的刷机任务
        final boolean hasUpgradingTask = terminalSystemUpgradeService.hasSystemUpgradeInProgress(packageId);
        if (hasUpgradingTask) {
            LOGGER.error("terminal system upgrade package has upgrading task, package id is: {}", packageId);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_IS_RUNNING);
        }

        // 判断升级文件是否存在
        String filePath = upgradePackage.getFilePath();
        boolean hasExist = Files.exists(new File(filePath).toPath());
        if (!hasExist) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_FILE_NOT_EXIST, upgradePackage.getPackageName());
        }
    }

    @Override
    public CbbTerminalNameResponse addSystemUpgradeTerminal(CbbUpgradeTerminalRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        LOGGER.info("开始追加刷机终端：{} ", request.getTerminalId());
        TerminalEntity terminal = basicInfoDAO.findTerminalEntityByTerminalId(request.getTerminalId());
        if (terminal == null) {
            LOGGER.error("terminal id is [{}], terminal not found", request.getTerminalId());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL);
        }

        final TerminalSystemUpgradeEntity upgradeEntity = terminalSystemUpgradeService.getSystemUpgradeTask(request.getUpgradeTaskId());
        checkUpgradeTaskState(upgradeEntity);
        addUpgradeTerminal(upgradeEntity, terminal);

        CbbTerminalNameResponse response = new CbbTerminalNameResponse();
        response.setTerminalName(terminal.getTerminalName());
        return response;
    }

    @Override
    public DefaultResponse editSystemUpgradeTerminalGroup(CbbUpgradeTerminalGroupRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        LOGGER.info("编辑刷机终端分组：{} ", JSON.toJSONString(request.getTerminalGroupIdArr()));

        final TerminalSystemUpgradeEntity upgradeEntity = terminalSystemUpgradeService.getSystemUpgradeTask(request.getUpgradeTaskId());
        checkUpgradeTaskState(upgradeEntity);

        checkGroupExist(request.getTerminalGroupIdArr());
        terminalSystemUpgradeServiceTx.editUpgradeGroup(upgradeEntity, request.getTerminalGroupIdArr());

        return DefaultResponse.Builder.success();
    }

    private void checkGroupExist(UUID[] terminalGroupIdArr) throws BusinessException {
        if (ArrayUtils.isEmpty(terminalGroupIdArr)) {
            return;
        }

        for (UUID groupId : terminalGroupIdArr) {
            terminalGroupService.checkGroupExist(groupId);
        }
    }

    private void checkUpgradeTaskState(TerminalSystemUpgradeEntity upgradeTaskEntity) throws BusinessException {
        if (upgradeTaskEntity.getState() != CbbSystemUpgradeTaskStateEnums.UPGRADING) {
            LOGGER.error("system upgrade task is finish, id is : {}", upgradeTaskEntity.getId());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_HAS_CLOSED);
        }
    }

    private void addUpgradeTerminal(TerminalSystemUpgradeEntity upgradeEntity, TerminalEntity terminalEntity) throws BusinessException {

        // 判断是否重复添加
        TerminalSystemUpgradeTerminalEntity upgradeTerminal =
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeEntity.getId(), terminalEntity.getTerminalId());
        if (upgradeTerminal != null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TERMINAL_EXIST, terminalEntity.getTerminalName());
        }

        upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminal.setSysUpgradeId(upgradeEntity.getId());
        upgradeTerminal.setTerminalId(terminalEntity.getTerminalId());
        upgradeTerminal.setState(CbbSystemUpgradeStateEnums.WAIT);
        upgradeTerminal.setCreateTime(new Date());
        systemUpgradeTerminalDAO.save(upgradeTerminal);
    }

    @Override
    public DefaultPageResponse<CbbSystemUpgradeTaskDTO> listSystemUpgradeTask(PageSearchRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        Page<TerminalSystemUpgradeEntity> upgradeTaskPage = querySystemUpgradeListService.pageQuery(request, TerminalSystemUpgradeEntity.class);

        // 将数据转换成dto输出
        final int numberOfElements = upgradeTaskPage.getNumberOfElements();
        final List<TerminalSystemUpgradeEntity> taskList = upgradeTaskPage.getContent();
        CbbSystemUpgradeTaskDTO[] dtoArr = new CbbSystemUpgradeTaskDTO[numberOfElements];
        Stream.iterate(0, i -> i + 1).limit(numberOfElements).forEach(i -> {
            CbbSystemUpgradeTaskDTO dto = new CbbSystemUpgradeTaskDTO();
            final TerminalSystemUpgradeEntity upgradeEntity = taskList.get(i);
            TASK_BEAN_COPIER.copy(upgradeEntity, dto, null);
            dto.setUpgradeTaskState(upgradeEntity.getState());
            fillUpgradeTaskDTO(dto);
            dtoArr[i] = dto;
        });

        return DefaultPageResponse.Builder.success(upgradeTaskPage.getSize(), (int) upgradeTaskPage.getTotalElements(), dtoArr);
    }

    private void fillUpgradeTaskDTO(CbbSystemUpgradeTaskDTO dto) {
        dto.setSuccessNum(systemUpgradeTerminalDAO.countBySysUpgradeIdAndState(dto.getId(), //
                CbbSystemUpgradeStateEnums.SUCCESS));
    }

    @Override
    public CbbGetTerminalUpgradeTaskResponse getTerminalUpgradeTaskById(IdRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        final TerminalSystemUpgradeEntity upgradeTaskEntity = terminalSystemUpgradeService.getSystemUpgradeTask(request.getId());
        CbbSystemUpgradeTaskDTO upgradeTaskDTO = new CbbSystemUpgradeTaskDTO();
        TASK_BEAN_COPIER.copy(upgradeTaskEntity, upgradeTaskDTO, null);
        upgradeTaskDTO.setUpgradeTaskState(upgradeTaskEntity.getState());
        return new CbbGetTerminalUpgradeTaskResponse(upgradeTaskDTO);
    }

    @Override
    public DefaultPageResponse<CbbSystemUpgradeTaskTerminalDTO> listSystemUpgradeTaskTerminal(PageSearchRequest request) {
        Assert.notNull(request, "request can not be null");

        Page<TerminalSystemUpgradeTerminalEntity> upgradeTaskTerminalPage =
                querySystemUpgradeTerminalListService.pageQuery(request, TerminalSystemUpgradeTerminalEntity.class);

        // 将列表转换为dto输出
        final int numberOfElements = upgradeTaskTerminalPage.getNumberOfElements();
        final List<TerminalSystemUpgradeTerminalEntity> taskList = upgradeTaskTerminalPage.getContent();
        CbbSystemUpgradeTaskTerminalDTO[] dtoArr = new CbbSystemUpgradeTaskTerminalDTO[numberOfElements];
        Stream.iterate(0, i -> i + 1).limit(numberOfElements).forEach(i -> {
            final TerminalSystemUpgradeTerminalEntity entity = taskList.get(i);
            CbbSystemUpgradeTaskTerminalDTO dto = buildUpgradeTerminalDTO(entity);
            dto.setTerminalUpgradeState(entity.getState());
            completeTerminalInfo(dto, entity.getTerminalId());
            dtoArr[i] = dto;
        });

        return DefaultPageResponse.Builder.success(upgradeTaskTerminalPage.getSize(), (int) upgradeTaskTerminalPage.getTotalElements(), dtoArr);
    }

    @Override
    public DefaultPageResponse<TerminalGroupDTO> listSystemUpgradeTaskTerminalGroup(PageSearchRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        Page<TerminalSystemUpgradeTerminalGroupEntity> upgradeTaskTerminalPage =
                querySystemUpgradeTerminalGroupListService.pageQuery(request, TerminalSystemUpgradeTerminalGroupEntity.class);

        // 将列表转换为dto输出
        final List<TerminalSystemUpgradeTerminalGroupEntity> upgradeGroupList = upgradeTaskTerminalPage.getContent();
        List<TerminalGroupDTO> terminalGroupList = Lists.newArrayList();

        for (TerminalSystemUpgradeTerminalGroupEntity upgradeGroup : upgradeGroupList) {
            TerminalGroupEntity terminalGroup = terminalGroupService.getTerminalGroup(upgradeGroup.getTerminalGroupId());

            TerminalGroupDTO groupDTO = new TerminalGroupDTO();
            groupDTO.setId(terminalGroup.getId());
            groupDTO.setGroupName(terminalGroup.getName());
            groupDTO.setParentGroupId(terminalGroup.getParentId());
            terminalGroupList.add(groupDTO);
        }

        return DefaultPageResponse.Builder.success(upgradeTaskTerminalPage.getSize(), (int) upgradeTaskTerminalPage.getTotalElements(),
                terminalGroupList.toArray(new TerminalGroupDTO[terminalGroupList.size()]));
    }

    /**
     * 补充终端信息
     *
     * @param dto 刷机终端dto
     * @param terminalId 终端id
     */
    private void completeTerminalInfo(CbbSystemUpgradeTaskTerminalDTO dto, String terminalId) {
        TerminalEntity terminalEntity = basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
        dto.setTerminalName(terminalEntity.getTerminalName());
        dto.setIp(terminalEntity.getIp());
        dto.setMac(terminalEntity.getMacAddr());
    }

    @Override
    public DefaultResponse closeSystemUpgradeTask(IdRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        terminalSystemUpgradeServiceTx.closeSystemUpgradeTask(request.getId());
        doAfterCloseUpgradeTask(request.getId());
        return DefaultResponse.Builder.success();
    }

    private void doAfterCloseUpgradeTask(UUID upgradeTaskId) throws BusinessException {
        TerminalSystemUpgradeEntity systemUpgradeTask = terminalSystemUpgradeService.getSystemUpgradeTask(upgradeTaskId);
        TerminalSystemUpgradePackageEntity upgradePackage =
                systemUpgradePackageService.getSystemUpgradePackage(systemUpgradeTask.getUpgradePackageId());
        systemUpgradeHandlerFactory.getHandler(systemUpgradeTask.getPackageType()).afterCloseSystemUpgrade(upgradePackage);
    }

    @Override
    public DefaultPageResponse<CbbUpgradeableTerminalListDTO> listUpgradeableTerminal(CbbUpgradeableTerminalPageSearchRequest request)
            throws BusinessException {
        Assert.notNull(request, "request can not be null");

        setMatchEqualArr(request);
        Page<ViewUpgradeableTerminalEntity> upgradeableTerminalPage =
                upgradeableTerminalListService.pageQuery(request, ViewUpgradeableTerminalEntity.class);

        // 将列表转换为dto输出
        final int numberOfElements = upgradeableTerminalPage.getNumberOfElements();
        final List<ViewUpgradeableTerminalEntity> taskList = upgradeableTerminalPage.getContent();
        CbbUpgradeableTerminalListDTO[] dtoArr = new CbbUpgradeableTerminalListDTO[numberOfElements];
        Stream.iterate(0, i -> i + 1).limit(numberOfElements).forEach(i -> {
            CbbUpgradeableTerminalListDTO dto = new CbbUpgradeableTerminalListDTO();
            fillTerminalListDTO(dto, taskList.get(i));
            dtoArr[i] = dto;
        });

        return DefaultPageResponse.Builder.success(upgradeableTerminalPage.getSize(), (int) upgradeableTerminalPage.getTotalElements(), dtoArr);
    }

    /**
     * 设置查询参数
     *
     * @param request 请求参数
     * @throws BusinessException 业务异常
     */
    private void setMatchEqualArr(CbbUpgradeableTerminalPageSearchRequest request) throws BusinessException {
        if (ArrayUtils.isEmpty(request.getMatchEqualArr())) {
            return;
        }

        List<MatchEqual> matchEqualList = new ArrayList<>(Arrays.asList(request.getMatchEqualArr()));
        convertPackageId(matchEqualList);
        request.setMatchEqualArr(matchEqualList.toArray(new MatchEqual[matchEqualList.size()]));
    }

    /**
     * 获取request参数中的packageId，解析平台类型，过滤终端
     *
     * @param matchEqualList 匹配参数列表
     * @throws BusinessException 业务异常
     */
    private void convertPackageId(List<MatchEqual> matchEqualList) throws BusinessException {
        List<MatchEqual> convertMEList = Lists.newArrayList();
        for (Iterator<MatchEqual> iterator = matchEqualList.iterator(); iterator.hasNext();) {
            MatchEqual matchEqual = iterator.next();
            if (PARAM_FIELD_PACKAGE_ID.equals(matchEqual.getName())) {
                UUID packageId = (UUID) matchEqual.getValueArr()[0];
                TerminalSystemUpgradePackageEntity packageEntity = getUpgradePackageEntity(packageId);
                CbbTerminalTypeEnums packageType = packageEntity.getPackageType();
                MatchEqual platformME = new MatchEqual(ENTITY_FILED_PLATFORM,
                        new CbbTerminalPlatformEnums[] {CbbTerminalPlatformEnums.convert(packageType.getPlatform())});
                MatchEqual osTypeME = new MatchEqual(ENTITY_FILED_TERMINAL_OS_TYPE, new String[] {packageType.getOsType()});
                convertMEList.add(platformME);
                convertMEList.add(osTypeME);

                iterator.remove();
                break;
            }
        }

        matchEqualList.addAll(convertMEList);
    }

    private void fillTerminalListDTO(CbbUpgradeableTerminalListDTO dto, ViewUpgradeableTerminalEntity viewEntity) {
        dto.setId(viewEntity.getTerminalId());
        dto.setTerminalName(viewEntity.getTerminalName());
        dto.setIp(viewEntity.getIp());
        dto.setMac(viewEntity.getMacAddr());
        dto.setProductType(viewEntity.getProductType());
        dto.setTerminalState(viewEntity.getState());
        dto.setLastUpgradeTime(viewEntity.getLastUpgradeTime());
    }


    @Override
    public CbbGetTaskUpgradeTerminalResponse getUpgradeTerminalByTaskId(CbbGetTaskUpgradeTerminalRequest request) {
        Assert.notNull(request, "request can not be null");

        final UUID upgradeTaskId = request.getId();
        final CbbSystemUpgradeStateEnums upgradeTerminalState = request.getTerminalState();
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = null;
        if (upgradeTerminalState == null) {
            upgradeTerminalList = systemUpgradeTerminalDAO.findBySysUpgradeId(upgradeTaskId);
        } else {
            upgradeTerminalList = systemUpgradeTerminalDAO.findBySysUpgradeIdAndState(upgradeTaskId, upgradeTerminalState);
        }
        if (CollectionUtils.isEmpty(upgradeTerminalList)) {
            return new CbbGetTaskUpgradeTerminalResponse(Lists.newArrayList());
        }

        List<CbbSystemUpgradeTaskTerminalDTO> upgradeTerminalDTOList = new ArrayList<>();
        upgradeTerminalList.forEach(entity -> {
            upgradeTerminalDTOList.add(buildUpgradeTerminalDTO(entity));
        });

        return new CbbGetTaskUpgradeTerminalResponse(upgradeTerminalDTOList);
    }

    private CbbSystemUpgradeTaskTerminalDTO buildUpgradeTerminalDTO(TerminalSystemUpgradeTerminalEntity entity) {
        CbbSystemUpgradeTaskTerminalDTO dto = new CbbSystemUpgradeTaskTerminalDTO();
        TASK_TERMINAL_BEAN_COPIER.copy(entity, dto, null);
        dto.setTerminalUpgradeState(entity.getState());
        return dto;
    }

    @Override
    public CbbTerminalNameResponse cancelUpgradeTerminal(CbbUpgradeTerminalRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        final UUID upgradeTaskId = request.getUpgradeTaskId();
        final String terminalId = request.getTerminalId();

        try {
            lockManager.getAndCreateLock(terminalId).lock();
            checkAndCancelUpgradeTerminal(terminalId, upgradeTaskId);
        } finally {
            lockManager.getAndCreateLock(terminalId).unlock();
        }

        CbbTerminalNameResponse response = new CbbTerminalNameResponse();
        response.setTerminalName(basicInfoDAO.getTerminalNameByTerminalId(terminalId));
        return response;
    }

    private void checkAndCancelUpgradeTerminal(String terminalId, UUID upgradeTaskId) throws BusinessException {
        final TerminalSystemUpgradeTerminalEntity upgradeTerminal = checkUpgradeTerminalExist(upgradeTaskId, terminalId);
        if (upgradeTerminal.getState() != CbbSystemUpgradeStateEnums.WAIT) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TERMINAL_STATE_NOT_ALLOW_CANCEL, terminalId);
        }

        upgradeTerminal.setState(CbbSystemUpgradeStateEnums.UNDO);
        terminalSystemUpgradeServiceTx.modifySystemUpgradeTerminalState(upgradeTerminal);
    }


    @Override
    public CbbTerminalNameResponse retryUpgradeTerminal(CbbUpgradeTerminalRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        final UUID upgradeTaskId = request.getUpgradeTaskId();
        final String terminalId = request.getTerminalId();
        final TerminalSystemUpgradeTerminalEntity upgradeTerminal = checkUpgradeTerminalExist(upgradeTaskId, terminalId);

        checkRetryIsAllowed(upgradeTerminal);

        doUpgradeRetry(upgradeTerminal);

        CbbTerminalNameResponse response = new CbbTerminalNameResponse();
        response.setTerminalName(basicInfoDAO.getTerminalNameByTerminalId(terminalId));
        return response;
    }

    private void checkRetryIsAllowed(final TerminalSystemUpgradeTerminalEntity upgradeTerminal) throws BusinessException {
        final CbbSystemUpgradeStateEnums state = upgradeTerminal.getState();
        if (state != CbbSystemUpgradeStateEnums.FAIL && state != CbbSystemUpgradeStateEnums.UNDO) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TERMINAL_STATE_NOT_ALLOW_RETRY, upgradeTerminal.getTerminalId());
        }
    }

    private void doUpgradeRetry(TerminalSystemUpgradeTerminalEntity upgradeTerminal) throws BusinessException {
        // 判断刷机是否已开始，若开始则直接设置为升级中状态,否则加入到等待中队列进行重新刷机
        final String terminalId = upgradeTerminal.getTerminalId();
        CbbSystemUpgradeStateEnums state = CbbSystemUpgradeStateEnums.WAIT;
        if (checkTerminalStartUpgrade(terminalId)) {
            state = CbbSystemUpgradeStateEnums.UPGRADING;
        }

        upgradeTerminal.setState(state);
        terminalSystemUpgradeServiceTx.modifySystemUpgradeTerminalState(upgradeTerminal);
    }

    private boolean checkTerminalStartUpgrade(String terminalId) {
        TerminalEntity terminalEntity = basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
        Assert.notNull(terminalEntity, "terminalId can not be null");

        CbbTerminalTypeEnums terminalType = CbbTerminalTypeEnums.convert(terminalEntity.getPlatform().name(), terminalEntity.getTerminalOsType());
        if (terminalType == CbbTerminalTypeEnums.VDI_LINUX) {
            String startFilePath = Constants.PXE_SAMBA_LINUX_VDI_UPGRADE_BEGIN_FILE_PATH + terminalId;
            File startFile = new File(startFilePath);
            return startFile.isFile();
        }

        return false;
    }

    private TerminalSystemUpgradeTerminalEntity checkUpgradeTerminalExist(final UUID upgradeTaskId, final String terminalId)
            throws BusinessException {
        final TerminalSystemUpgradeTerminalEntity upgradeTerminal =
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeTaskId, terminalId);
        if (upgradeTerminal == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TERMINAL_NOT_EXIST);
        }
        return upgradeTerminal;
    }

    private TerminalSystemUpgradePackageEntity getUpgradePackageEntity(UUID packageId) throws BusinessException {
        Optional<TerminalSystemUpgradePackageEntity> upgradePackageOpt = terminalSystemUpgradePackageDAO.findById(packageId);
        if (!upgradePackageOpt.isPresent() || upgradePackageOpt.get().getIsDelete()) {
            LOGGER.error("terminal system upgrade package not found, package id is: {}", packageId);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_NOT_EXIST);
        }
        return upgradePackageOpt.get();
    }

}
