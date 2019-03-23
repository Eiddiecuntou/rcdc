package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradeAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradePackageAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbSystemUpgradeTaskDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbSystemUpgradeTaskTerminalDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.MatchEqual;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.TerminalListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbAddSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbAddTerminalSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbCancelUpgradeTerminalRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbCloseSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbGetTaskUpgradeTerminalRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbGetUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbRetryUpgradeTerminalRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalPlatformRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.PageSearchRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbAddSystemUpgradeTaskResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbCheckUploadingResultResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbGetTaskUpgradeTerminalResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbGetTerminalUpgradeTaskResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalNameResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.ViewUpgradeableTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.QuerySystemUpgradeListService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.QuerySystemUpgradeTerminalListService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.QueryUpgradeableTerminalListService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.TerminalSystemUpgradeSupportService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalSystemUpgradeServiceTx;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
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
public class CbbTerminalSystemUpgradeAPIImpl implements CbbTerminalSystemUpgradeAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(CbbTerminalSystemUpgradeAPIImpl.class);

    private static final BeanCopier TASK_BEAN_COPIER =
            BeanCopier.create(TerminalSystemUpgradeEntity.class, CbbSystemUpgradeTaskDTO.class, false);

    private static final BeanCopier TASK_TERMINAL_BEAN_COPIER =
            BeanCopier.create(TerminalSystemUpgradeTerminalEntity.class, CbbSystemUpgradeTaskTerminalDTO.class, false);
    
    private static final String TERMINAL_STATE_FIELD_NAME = "state";
    
    @Autowired
    private TerminalSystemUpgradePackageDAO terminalSystemUpgradePackageDAO;

    @Autowired
    private TerminalSystemUpgradeService terminalSystemUpgradeService;

    @Autowired
    private TerminalSystemUpgradeSupportService terminalSystemUpgradeSupportService;

    @Autowired
    private TerminalSystemUpgradeServiceTx terminalSystemUpgradeServiceTx;

    @Autowired
    private TerminalBasicInfoDAO basicInfoDAO;

    @Autowired
    private QuerySystemUpgradeListService querySystemUpgradeListService;

    @Autowired
    private QuerySystemUpgradeTerminalListService querySystemUpgradeTerminalListService;

    @Autowired
    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;

    @Autowired
    private CbbTerminalSystemUpgradePackageAPI systemUpgradePackageAPI;

    @Autowired
    private QueryUpgradeableTerminalListService queryUpgradeableTerminalListDAO;

    @Override
    public CbbAddSystemUpgradeTaskResponse addSystemUpgradeTask(CbbAddSystemUpgradeTaskRequest request)
            throws BusinessException {
        Assert.notNull(request, "request can not be null");


        UUID packageId = request.getPackageId();
        Optional<TerminalSystemUpgradePackageEntity> upgradePackageOpt =
                terminalSystemUpgradePackageDAO.findById(packageId);
        if (!upgradePackageOpt.isPresent()) {
            LOGGER.error("terminal system upgrade package not found, package id is: {}", packageId);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_NOT_EXIST);
        }

        // 判断刷机包是否正在上传中
        final TerminalSystemUpgradePackageEntity upgradePackage = upgradePackageOpt.get();
        isUpgradePackageUploading(upgradePackage.getPackageType());

        // 判断是否已存在进行中的刷机任务
        final boolean hasUpgradingTask = terminalSystemUpgradeService.hasSystemUpgradeInProgress(packageId);
        if (hasUpgradingTask) {
            LOGGER.error("terminal system upgrade package has upgrading task, package id is: {}", packageId);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_IS_RUNNING);
        }

        UUID upgradeTaskId =
                terminalSystemUpgradeServiceTx.addSystemUpgradeTask(upgradePackage, request.getTerminalIdArr());

        // 开启刷机相关服务
        terminalSystemUpgradeSupportService.openSystemUpgradeService(upgradePackage);

        CbbAddSystemUpgradeTaskResponse response = new CbbAddSystemUpgradeTaskResponse();
        response.setUpgradeTaskId(upgradeTaskId);
        response.setImgName(upgradePackage.getPackageName());
        return response;
    }

    /**
     * 判断刷机包是否正在上传中
     * 
     * @param platform 终端平台类型
     * @throws BusinessException 业务异常
     */
    private void isUpgradePackageUploading(TerminalPlatformEnums platform) throws BusinessException {
        CbbTerminalPlatformRequest platformReq = new CbbTerminalPlatformRequest();
        platformReq.setPlatform(platform);
        final CbbCheckUploadingResultResponse response = systemUpgradePackageAPI.isUpgradeFileUploading(platformReq);
        if (response.isHasLoading()) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_IS_UPLOADING);
        }
    }

    @Override
    public CbbTerminalNameResponse addSystemUpgradeTerminal(CbbAddTerminalSystemUpgradeTaskRequest request)
            throws BusinessException {
        Assert.notNull(request, "request can not be null");

        LOGGER.info("开始追加刷机终端：{} ", request.getTerminalId());
        TerminalEntity terminal = basicInfoDAO.findTerminalEntityByTerminalId(request.getTerminalId());
        if (terminal == null) {
            LOGGER.error("terminal id is [{}], terminal not found", request.getTerminalId());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL);
        }

        final TerminalSystemUpgradeEntity upgradeEntity =
                terminalSystemUpgradeService.getSystemUpgradeTask(request.getUpgradeTaskId());
        checkUpgradeTaskState(upgradeEntity);
        addUpgradeTerminal(upgradeEntity, terminal);

        CbbTerminalNameResponse response = new CbbTerminalNameResponse();
        response.setTerminalName(terminal.getTerminalName());
        return response;
    }

    private void checkUpgradeTaskState(TerminalSystemUpgradeEntity upgradeTaskEntity) throws BusinessException {
        if (upgradeTaskEntity.getState() != CbbSystemUpgradeTaskStateEnums.UPGRADING) {
            LOGGER.error("system upgrade task is finish, id is : {}", upgradeTaskEntity.getId());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_HAS_CLOSED);
        }
    }

    private void addUpgradeTerminal(TerminalSystemUpgradeEntity upgradeEntity, TerminalEntity terminalEntity)
            throws BusinessException {

        // 判断是否重复添加
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = systemUpgradeTerminalDAO
                .findFirstBySysUpgradeIdAndTerminalId(upgradeEntity.getId(), terminalEntity.getTerminalId());
        if (upgradeTerminal != null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TERMINAL_EXIST,
                    terminalEntity.getTerminalName());
        }

        upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminal.setSysUpgradeId(upgradeEntity.getId());
        upgradeTerminal.setTerminalId(terminalEntity.getTerminalId());
        upgradeTerminal.setState(CbbSystemUpgradeStateEnums.WAIT);
        upgradeTerminal.setCreateTime(new Date());
        systemUpgradeTerminalDAO.save(upgradeTerminal);
    }

    @Override
    public DefaultPageResponse<CbbSystemUpgradeTaskDTO> listSystemUpgradeTask(PageSearchRequest request)
            throws BusinessException {
        Assert.notNull(request, "request can not be null");

        Page<TerminalSystemUpgradeEntity> upgradeTaskPage =
                querySystemUpgradeListService.pageQuery(request, TerminalSystemUpgradeEntity.class);

        // 将数据转换成dto输出
        final int numberOfElements = upgradeTaskPage.getNumberOfElements();
        final List<TerminalSystemUpgradeEntity> taskList = upgradeTaskPage.getContent();
        CbbSystemUpgradeTaskDTO[] dtoArr = new CbbSystemUpgradeTaskDTO[numberOfElements];
        Stream.iterate(0, i -> i + 1).limit(numberOfElements).forEach(i -> {
            CbbSystemUpgradeTaskDTO dto = new CbbSystemUpgradeTaskDTO();
            final TerminalSystemUpgradeEntity upgradeEntity = taskList.get(i);
            TASK_BEAN_COPIER.copy(upgradeEntity, dto, null);
            dto.setUpgradeTaskState(upgradeEntity.getState());
            dtoArr[i] = dto;
        });

        return DefaultPageResponse.Builder.success(upgradeTaskPage.getSize(), (int) upgradeTaskPage.getTotalElements(),
                dtoArr);
    }
    
    @Override
    public CbbGetTerminalUpgradeTaskResponse getTerminalUpgradeTaskById(CbbGetUpgradeTaskRequest request)
            throws BusinessException {
        Assert.notNull(request, "request can not be null");

        final TerminalSystemUpgradeEntity upgradeTaskEntity =
                terminalSystemUpgradeService.getSystemUpgradeTask(request.getUpgradeTaskId());
        CbbSystemUpgradeTaskDTO upgradeTaskDTO = new CbbSystemUpgradeTaskDTO();
        TASK_BEAN_COPIER.copy(upgradeTaskEntity, upgradeTaskDTO, null);
        upgradeTaskDTO.setUpgradeTaskState(upgradeTaskEntity.getState());
        return new CbbGetTerminalUpgradeTaskResponse(upgradeTaskDTO);
    }

    @Override
    public DefaultPageResponse<CbbSystemUpgradeTaskTerminalDTO> listSystemUpgradeTaskTerminal(
            PageSearchRequest request) {
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

        return DefaultPageResponse.Builder.success(upgradeTaskTerminalPage.getSize(),
                (int) upgradeTaskTerminalPage.getTotalElements(), dtoArr);
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
    public DefaultResponse closeSystemUpgradeTask(CbbCloseSystemUpgradeTaskRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        terminalSystemUpgradeServiceTx.closeSystemUpgradeTask(request.getUpgradeTaskId());
        return DefaultResponse.Builder.success();
    }

    @Override
    public DefaultPageResponse<TerminalListDTO> listUpgradeableTerminal(PageSearchRequest request) {
        Assert.notNull(request, "request can not be null");

        // TODO request参数中的packageId可获取刷机包，解析平台类型，过滤终端
        setMatchEqualArr(request);
        Page<ViewUpgradeableTerminalEntity> terminalListPage =
                queryUpgradeableTerminalListDAO.pageQuery(request, ViewUpgradeableTerminalEntity.class);
        // 将列表转换为dto输出
        final int numberOfElements = terminalListPage.getNumberOfElements();
        final List<ViewUpgradeableTerminalEntity> taskList = terminalListPage.getContent();
        TerminalListDTO[] dtoArr = new TerminalListDTO[numberOfElements];
        Stream.iterate(0, i -> i + 1).limit(numberOfElements).forEach(i -> {
            TerminalListDTO dto = new TerminalListDTO();
            fillTerminalListDTO(dto, taskList.get(i));
            dtoArr[i] = dto;
        });

        return DefaultPageResponse.Builder.success(terminalListPage.getSize(),
                (int) terminalListPage.getTotalElements(), dtoArr);
    }

    private void setMatchEqualArr(PageSearchRequest request) {
        // 设置查询终端状态为在线
        MatchEqual stateMe = new MatchEqual();
        stateMe.setName(TERMINAL_STATE_FIELD_NAME);
        stateMe.setValueArr(new CbbTerminalStateEnums[] {CbbTerminalStateEnums.ONLINE});
        MatchEqual[] matchEqualArr = new MatchEqual[] {stateMe};
        request.setMatchEqualArr(matchEqualArr);
    }

    private void fillTerminalListDTO(TerminalListDTO dto, ViewUpgradeableTerminalEntity viewEntity) {
        dto.setId(viewEntity.getTerminalId());
        dto.setTerminalName(viewEntity.getTerminalName());
        dto.setIp(viewEntity.getIp());
        dto.setMac(viewEntity.getMacAddr());
        dto.setProductType(viewEntity.getProductType());
        dto.setTerminalState(viewEntity.getState());
    }

    @Override
    public CbbGetTaskUpgradeTerminalResponse getUpgradeTerminalByTaskId(CbbGetTaskUpgradeTerminalRequest request) {
        Assert.notNull(request, "request can not be null");
        
        final UUID upgradeTaskId = request.getUpgradeTaskId();
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
    public CbbTerminalNameResponse cancelUpgradeTerminal(CbbCancelUpgradeTerminalRequest request)
            throws BusinessException {
        Assert.notNull(request, "request can not be null");

        final UUID upgradeTaskId = request.getUpgradeTaskId();
        final String terminalId = request.getTerminalId();
        final TerminalSystemUpgradeTerminalEntity upgradeTerminal =
                checkUpgradeTerminalExist(upgradeTaskId, terminalId);
        
        if (upgradeTerminal.getState() != CbbSystemUpgradeStateEnums.WAIT) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TERMINAL_STATE_NOT_ALLOW_CANCEL, terminalId);
        }
        
        upgradeTerminal.setState(CbbSystemUpgradeStateEnums.UNDO);
        terminalSystemUpgradeServiceTx.modifySystemUpgradeTerminalState(upgradeTerminal);

        CbbTerminalNameResponse response = new CbbTerminalNameResponse();
        response.setTerminalName(basicInfoDAO.getTerminalNameByTerminalId(terminalId));
        return response;
    }


    @Override
    public CbbTerminalNameResponse retryUpgradeTerminal(CbbRetryUpgradeTerminalRequest request)
            throws BusinessException {
        Assert.notNull(request, "request can not be null");
        
        final UUID upgradeTaskId = request.getUpgradeTaskId();
        final String terminalId = request.getTerminalId();
        final TerminalSystemUpgradeTerminalEntity upgradeTerminal =
                checkUpgradeTerminalExist(upgradeTaskId, terminalId);
        
        if (upgradeTerminal.getState() != CbbSystemUpgradeStateEnums.FAIL) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TERMINAL_STATE_NOT_ALLOW_RETRY, terminalId);
        }
        
        doUpgradeRetry(upgradeTerminal);
        
        CbbTerminalNameResponse response = new CbbTerminalNameResponse();
        response.setTerminalName(basicInfoDAO.getTerminalNameByTerminalId(terminalId));
        return response;
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
        String startFilePath = Constants.TERMINAL_UPGRADE_START_SATTUS_FILE_PATH + terminalId;
        File startFile = new File(startFilePath);
        
        return startFile.isFile();
    }

    private TerminalSystemUpgradeTerminalEntity checkUpgradeTerminalExist(final UUID upgradeTaskId,
            final String terminalId) throws BusinessException {
        final TerminalSystemUpgradeTerminalEntity upgradeTerminal =
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeTaskId, terminalId);
        if (upgradeTerminal == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TERMINAL_NOT_EXIST);
        }
        return upgradeTerminal;
    }
    
}
