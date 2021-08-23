package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradeAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.*;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbFlashModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.*;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbAddSystemUpgradeTaskResultDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.UpgradeableTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.*;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeArchType;
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
import com.ruijie.rcos.sk.pagekit.api.PageQueryRequest;
import com.ruijie.rcos.sk.pagekit.api.PageQueryResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

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

    private static final String ENTITY_FILED_TERMINAL_SUPPORT_CPU_ARCH_TYPE = "cpuArch";

    private static final String ENTITY_FILED_TERMINAL_SUPPORT_CPU = "upgradeCpuType";

    private static ExecutorService SINGLE_THREAD_EXECUTOR =
            ThreadExecutors.newBuilder("singleSystemUpgradeThreadPool").maxThreadNum(5).queueSize(10).build();

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

    @Autowired
    private UpgradeableTerminalDAO upgradeableTerminalDAO;

    @Override
    public CbbAddSystemUpgradeTaskResultDTO addSystemUpgradeTask(CbbAddSystemUpgradeTaskDTO request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        if (request.getUpgradeMode() == null) {
            request.setUpgradeMode(CbbSystemUpgradeModeEnums.AUTO);
        }

        if (request.getFlashModeEnums() == null) {
            // 兼容旧实现，默认使用快速刷机
            request.setFlashModeEnums(CbbFlashModeEnums.FAST);
        }

        UUID packageId = request.getPackageId();
        TerminalSystemUpgradePackageEntity upgradePackage = getUpgradePackageEntity(packageId);
        // 判断刷机包是否允许开启升级任务
        checkAllowCreateTask(upgradePackage);

        UUID upgradeTaskId = terminalSystemUpgradeServiceTx.addSystemUpgradeTask(upgradePackage, request);
        // 添加升级任务成功后的处理
        TerminalSystemUpgradeHandler handler = systemUpgradeHandlerFactory.getHandler(upgradePackage.getTerminalTypeArchType());
        handler.afterAddSystemUpgrade(upgradePackage);

        Object upgradeMsg = handler.getSystemUpgradeMsg(upgradePackage, upgradeTaskId);
        // 向在线终端发送升级消息
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = systemUpgradeTerminalDAO.findBySysUpgradeId(upgradeTaskId);
        String[] upgradeTerminalIdArr = upgradeTerminalList.stream().map(upgradeTerminal -> upgradeTerminal.getTerminalId()).toArray(String[]::new);
        SINGLE_THREAD_EXECUTOR.execute(() -> sendSystemUpgradeMsg(upgradeTerminalIdArr, upgradeMsg));

        CbbAddSystemUpgradeTaskResultDTO response = new CbbAddSystemUpgradeTaskResultDTO();
        response.setUpgradeTaskId(upgradeTaskId);
        response.setImgName(upgradePackage.getPackageName());
        return response;
    }

    private void checkAllowCreateTask(TerminalSystemUpgradePackageEntity upgradePackage) throws BusinessException {

        UUID packageId = upgradePackage.getId();

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
    public String addSystemUpgradeTerminal(CbbUpgradeTerminalDTO request) throws BusinessException {
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

        // 向在线终端发送升级消息
        sendUpgradeMsg(request, upgradeEntity);

        return terminal.getTerminalName();
    }

    @Override
    public void editSystemUpgradeTerminalGroup(CbbUpgradeTerminalGroupDTO request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        LOGGER.info("编辑刷机终端分组：{} ", JSON.toJSONString(request.getTerminalGroupIdArr()));

        final TerminalSystemUpgradeEntity upgradeEntity = terminalSystemUpgradeService.getSystemUpgradeTask(request.getUpgradeTaskId());
        checkUpgradeTaskState(upgradeEntity);

        checkGroupExist(request.getTerminalGroupIdArr());
        terminalSystemUpgradeServiceTx.editUpgradeGroup(upgradeEntity, request.getTerminalGroupIdArr());

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
    public DefaultPageResponse<CbbSystemUpgradeTaskDTO> pageQuerySystemUpgradeTask(PageSearchRequest request)
            throws BusinessException {
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
    public CbbSystemUpgradeTaskDTO findTerminalUpgradeTaskById(UUID taskId) throws BusinessException {
        Assert.notNull(taskId, "taskId can not be null");

        final TerminalSystemUpgradeEntity upgradeTaskEntity = terminalSystemUpgradeService.getSystemUpgradeTask(taskId);
        CbbSystemUpgradeTaskDTO upgradeTaskDTO = new CbbSystemUpgradeTaskDTO();
        TASK_BEAN_COPIER.copy(upgradeTaskEntity, upgradeTaskDTO, null);
        upgradeTaskDTO.setUpgradeTaskState(upgradeTaskEntity.getState());
        return upgradeTaskDTO;
    }

    @Override
    public DefaultPageResponse<CbbSystemUpgradeTaskTerminalDTO> pageQuerySystemUpgradeTaskTerminal(PageSearchRequest request)
            throws BusinessException {
        Assert.notNull(request, "request can not be null");

        Page<TerminalSystemUpgradeTerminalEntity> upgradeTaskTerminalPage =
                querySystemUpgradeTerminalListService.pageQuery(request, TerminalSystemUpgradeTerminalEntity.class);

        // 将列表转换为dto输出
        final List<TerminalSystemUpgradeTerminalEntity> taskList = upgradeTaskTerminalPage.getContent();
        final List<CbbSystemUpgradeTaskTerminalDTO> respList = Lists.newArrayList();
        for (TerminalSystemUpgradeTerminalEntity entity : taskList) {
            CbbSystemUpgradeTaskTerminalDTO dto = buildUpgradeTerminalDTO(entity);
            dto.setTerminalUpgradeState(entity.getState());
            completeTerminalInfo(dto, entity.getTerminalId());
            respList.add(dto);
        }
        final CbbSystemUpgradeTaskTerminalDTO[] respArr = respList.toArray(new CbbSystemUpgradeTaskTerminalDTO[0]);

        return DefaultPageResponse.Builder.success(upgradeTaskTerminalPage.getSize(), (int) upgradeTaskTerminalPage.getTotalElements(), respArr);
    }

    @Override
    public DefaultPageResponse<CbbTerminalGroupDetailDTO> pageQuerySystemUpgradeTaskTerminalGroup(PageSearchRequest request)
            throws BusinessException {
        Assert.notNull(request, "request can not be null");

        Page<TerminalSystemUpgradeTerminalGroupEntity> upgradeTaskTerminalPage =
                querySystemUpgradeTerminalGroupListService.pageQuery(request, TerminalSystemUpgradeTerminalGroupEntity.class);

        // 将列表转换为dto输出
        final List<TerminalSystemUpgradeTerminalGroupEntity> upgradeGroupList = upgradeTaskTerminalPage.getContent();
        List<CbbTerminalGroupDetailDTO> terminalGroupList = Lists.newArrayList();

        for (TerminalSystemUpgradeTerminalGroupEntity upgradeGroup : upgradeGroupList) {
            TerminalGroupEntity terminalGroup = terminalGroupService.getTerminalGroup(upgradeGroup.getTerminalGroupId());

            CbbTerminalGroupDetailDTO groupDTO = new CbbTerminalGroupDetailDTO();
            groupDTO.setId(terminalGroup.getId());
            groupDTO.setGroupName(terminalGroup.getName());
            groupDTO.setParentGroupId(terminalGroup.getParentId());
            terminalGroupList.add(groupDTO);
        }

        return DefaultPageResponse.Builder.success(upgradeTaskTerminalPage.getSize(), (int) upgradeTaskTerminalPage.getTotalElements(),
                terminalGroupList.toArray(new CbbTerminalGroupDetailDTO[terminalGroupList.size()]));
    }

    /**
     * 补充终端信息
     *
     * @param dto 刷机终端dto
     * @param terminalId 终端id
     * @throws BusinessException 业务异常
     */
    private void completeTerminalInfo(CbbSystemUpgradeTaskTerminalDTO dto, String terminalId) throws BusinessException {
        TerminalEntity terminalEntity = basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
        dto.setTerminalName(terminalEntity.getTerminalName());
        dto.setIp(terminalEntity.getIp());
        dto.setMac(terminalEntity.getMacAddr());
        dto.setNetworkMode(terminalEntity.getNetworkAccessMode());
        dto.setNetworkInfoArr(terminalEntity.getNetworkInfoArr());
        dto.setPlatform(terminalEntity.getPlatform());
    }

    @Override
    public void closeSystemUpgradeTask(UUID taskId) throws BusinessException {
        Assert.notNull(taskId, "taskId can not be null");

        terminalSystemUpgradeServiceTx.closeSystemUpgradeTask(taskId);
        doAfterCloseUpgradeTask(taskId);
    }

    private void doAfterCloseUpgradeTask(UUID upgradeTaskId) throws BusinessException {
        TerminalSystemUpgradeEntity systemUpgradeTask = terminalSystemUpgradeService.getSystemUpgradeTask(upgradeTaskId);
        TerminalSystemUpgradePackageEntity upgradePackage =
                systemUpgradePackageService.getSystemUpgradePackage(systemUpgradeTask.getUpgradePackageId());
        systemUpgradeHandlerFactory.getHandler(upgradePackage.getTerminalTypeArchType()).afterCloseSystemUpgrade(upgradePackage, systemUpgradeTask);
    }

    @Override
    public DefaultPageResponse<CbbUpgradeableTerminalListDTO> pageQueryUpgradeableTerminal(CbbUpgradeableTerminalPageSearchRequest request)
            throws BusinessException {
        Assert.notNull(request, "request can not be null");

        // 升级包可升级终端过滤条件转换
        if (ArrayUtils.isNotEmpty(request.getMatchEqualArr())) {
            convertPackageId(request);
        }

        Page<ViewUpgradeableTerminalEntity> upgradeableTerminalPage =
                upgradeableTerminalListService.pageQuery(request, ViewUpgradeableTerminalEntity.class);

        // 将列表转换为dto输出
        final List<ViewUpgradeableTerminalEntity> taskList = upgradeableTerminalPage.getContent();
        final List<CbbUpgradeableTerminalListDTO> respList = Lists.newArrayList();

        for (ViewUpgradeableTerminalEntity viewUpgradeableTerminalEntity : taskList) {
            CbbUpgradeableTerminalListDTO dto = new CbbUpgradeableTerminalListDTO();
            fillTerminalListDTO(dto, viewUpgradeableTerminalEntity);
            respList.add(dto);
        }

        CbbUpgradeableTerminalListDTO[] respArr = respList.toArray(new CbbUpgradeableTerminalListDTO[0]);

        return DefaultPageResponse.Builder.success(upgradeableTerminalPage.getSize(), (int) upgradeableTerminalPage.getTotalElements(), respArr);
    }

    @Override
    public PageQueryResponse<CbbUpgradeableTerminalListDTO> pageQuery(PageQueryRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        PageQueryResponse<ViewUpgradeableTerminalEntity> pageQueryResponse = upgradeableTerminalDAO.pageQuery(request);

        ViewUpgradeableTerminalEntity[] upgradeableTerminalEntitieArr = pageQueryResponse.getItemArr();
        List<CbbUpgradeableTerminalListDTO> respList = Lists.newArrayList();
        for (ViewUpgradeableTerminalEntity entity : upgradeableTerminalEntitieArr) {
            CbbUpgradeableTerminalListDTO dto = new CbbUpgradeableTerminalListDTO();
            fillTerminalListDTO(dto, entity);
            respList.add(dto);
        }

        PageQueryResponse<CbbUpgradeableTerminalListDTO> queryResponse = new PageQueryResponse<>();
        queryResponse.setItemArr(respList.toArray(new CbbUpgradeableTerminalListDTO[respList.size()]));
        queryResponse.setTotal(pageQueryResponse.getTotal());

        return queryResponse;
    }

    /**
     * 获取request参数中的packageId，解析平台类型，cpu型号，过滤终端
     *
     * @param request 请求参数
     * @throws BusinessException 业务异常
     */
    private void convertPackageId(CbbUpgradeableTerminalPageSearchRequest request) throws BusinessException {

        // TODO 添加cpu型号过滤
        List<MatchEqual> matchEqualList = Lists.newArrayList(request.getMatchEqualArr());
        List<MatchEqual> convertMEList = Lists.newArrayList();
        for (Iterator<MatchEqual> iterator = matchEqualList.iterator(); iterator.hasNext();) {
            MatchEqual matchEqual = iterator.next();
            if (PARAM_FIELD_PACKAGE_ID.equals(matchEqual.getName())) {
                UUID packageId = (UUID) matchEqual.getValueArr()[0];
                TerminalSystemUpgradePackageEntity packageEntity = getUpgradePackageEntity(packageId);

                // 设置精确查找参数， 平台类型、操作系统、cpu架构以及支持的cpu类型
                CbbTerminalTypeEnums packageType = packageEntity.getPackageType();
                MatchEqual platformME = new MatchEqual(ENTITY_FILED_PLATFORM,
                        obtainTerminalPlatform(packageType));
                MatchEqual osTypeME = new MatchEqual(ENTITY_FILED_TERMINAL_OS_TYPE,
                        new String[] {packageType.getOsType()});
                MatchEqual archTypeME = new MatchEqual(ENTITY_FILED_TERMINAL_SUPPORT_CPU_ARCH_TYPE,
                        new String[] {packageEntity.getCpuArch().name()});

                convertMEList.add(platformME);
                convertMEList.add(osTypeME);
                convertMEList.add(archTypeME);

                // 非支持所有类型CPU需要添加cpu匹配参数
                if (packageEntity.getSupportCpu() != null && !packageEntity.getSupportCpu().equals("ALL")) {
                    MatchEqual supportCpuME = new MatchEqual(ENTITY_FILED_TERMINAL_SUPPORT_CPU,
                            getSupportCpuValueArr(packageEntity.getSupportCpu()));
                    convertMEList.add(supportCpuME);
                }

                iterator.remove();
                break;
            }
        }

        matchEqualList.addAll(convertMEList);
        request.setMatchEqualArr(matchEqualList.stream().toArray(MatchEqual[]::new));
        LOGGER.info("request 匹配条件：{}", JSON.toJSONString(request));
    }

    private String[] getSupportCpuValueArr(String supportCpuType) {
        return supportCpuType.split(",");
    }

    CbbTerminalPlatformEnums[] obtainTerminalPlatform(CbbTerminalTypeEnums packageType) {
        List<CbbTerminalPlatformEnums> terminalPlatformList = Lists.newArrayList();
        terminalPlatformList.add(CbbTerminalPlatformEnums.convert(packageType.getPlatform()));
        if (CbbTerminalPlatformEnums.convert(packageType.getPlatform()) == CbbTerminalPlatformEnums.IDV) {
            LOGGER.info("IDV平台类型刷机包过滤终端条件增加VOI终端平台类型");
            terminalPlatformList.add(CbbTerminalPlatformEnums.VOI);
        }

        return terminalPlatformList.toArray(new CbbTerminalPlatformEnums[terminalPlatformList.size()]);
    }

    private void fillTerminalListDTO(CbbUpgradeableTerminalListDTO dto, ViewUpgradeableTerminalEntity viewEntity) throws BusinessException {
        TerminalEntity terminalEntity = basicInfoDAO.findTerminalEntityByTerminalId(viewEntity.getTerminalId());
        dto.setId(viewEntity.getTerminalId());
        dto.setTerminalName(viewEntity.getTerminalName());
        dto.setIp(viewEntity.getIp());
        dto.setMac(viewEntity.getMacAddr());
        dto.setProductType(viewEntity.getProductType());
        dto.setTerminalState(viewEntity.getState());
        dto.setLastUpgradeTime(viewEntity.getLastUpgradeTime());
        dto.setNetworkMode(terminalEntity.getNetworkAccessMode());
        dto.setNetworkInfoArr(terminalEntity.getNetworkInfoArr());
    }


    @Override
    public List<CbbSystemUpgradeTaskTerminalDTO> listUpgradeTerminalByTaskId(CbbGetTaskUpgradeTerminalDTO request) {
        Assert.notNull(request, "request can not be null");

        final UUID upgradeTaskId = request.getTaskId();
        final CbbSystemUpgradeStateEnums upgradeTerminalState = request.getTerminalState();
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = null;
        if (upgradeTerminalState == null) {
            upgradeTerminalList = systemUpgradeTerminalDAO.findBySysUpgradeId(upgradeTaskId);
        } else {
            upgradeTerminalList = systemUpgradeTerminalDAO.findBySysUpgradeIdAndState(upgradeTaskId, upgradeTerminalState);
        }
        if (CollectionUtils.isEmpty(upgradeTerminalList)) {
            return Lists.newArrayList();
        }

        List<CbbSystemUpgradeTaskTerminalDTO> upgradeTerminalDTOList = new ArrayList<>();
        upgradeTerminalList.forEach(entity -> {
            upgradeTerminalDTOList.add(buildUpgradeTerminalDTO(entity));
        });

        return upgradeTerminalDTOList;
    }

    private CbbSystemUpgradeTaskTerminalDTO buildUpgradeTerminalDTO(TerminalSystemUpgradeTerminalEntity entity) {
        CbbSystemUpgradeTaskTerminalDTO dto = new CbbSystemUpgradeTaskTerminalDTO();
        TASK_TERMINAL_BEAN_COPIER.copy(entity, dto, null);
        dto.setTerminalUpgradeState(entity.getState());
        return dto;
    }

    @Override
    public String cancelUpgradeTerminal(CbbUpgradeTerminalDTO request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        final UUID upgradeTaskId = request.getUpgradeTaskId();
        final String terminalId = request.getTerminalId();

        try {
            lockManager.getAndCreateLock(terminalId).lock();
            checkAndCancelUpgradeTerminal(terminalId, upgradeTaskId);
        } finally {
            lockManager.getAndCreateLock(terminalId).unlock();
        }

        return basicInfoDAO.getTerminalNameByTerminalId(terminalId);
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
    public String retryUpgradeTerminal(CbbUpgradeTerminalDTO request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        final UUID upgradeTaskId = request.getUpgradeTaskId();
        final String terminalId = request.getTerminalId();
        final TerminalSystemUpgradeTerminalEntity upgradeTerminal = checkUpgradeTerminalExist(upgradeTaskId, terminalId);

        checkRetryIsAllowed(upgradeTerminal);

        doUpgradeRetry(upgradeTerminal);

        final TerminalSystemUpgradeEntity upgradeEntity = terminalSystemUpgradeService.getSystemUpgradeTask(request.getUpgradeTaskId());
        sendUpgradeMsg(request, upgradeEntity);

        return basicInfoDAO.getTerminalNameByTerminalId(terminalId);
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

    private void sendUpgradeMsg(CbbUpgradeTerminalDTO request, TerminalSystemUpgradeEntity upgradeEntity) throws BusinessException {
        TerminalSystemUpgradePackageEntity upgradePackage = systemUpgradePackageService.getSystemUpgradePackage(upgradeEntity.getUpgradePackageId());
        TerminalSystemUpgradeHandler handler = systemUpgradeHandlerFactory.getHandler(upgradePackage.getTerminalTypeArchType());
        Object upgradeMsg = handler.getSystemUpgradeMsg(upgradePackage, upgradeEntity.getId());
        SINGLE_THREAD_EXECUTOR.execute(() -> sendSystemUpgradeMsg(new String[] {request.getTerminalId()}, upgradeMsg));
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
}
