package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalGroupMgmtAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.*;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.TerminalStatisticsItem;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalGroupOperNotifySPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbTerminalGroupOperNotifyRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.ViewTerminalStatDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.TerminalStatisticsDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalGroupEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalGroupService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.TerminalGroupHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalGroupServiceTx;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.util.HibernateUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 终端组管理API接口实现.
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年10月30日
 * 
 * @author chenzj
 */
public class CbbTerminalGroupMgmtAPIImpl implements CbbTerminalGroupMgmtAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(CbbTerminalGroupMgmtAPIImpl.class);

    @Autowired
    private TerminalGroupService terminalGroupService;

    @Autowired
    private TerminalGroupHandler terminalGroupHandler;

    @Autowired
    private TerminalGroupServiceTx terminalGroupServiceTx;

    @Autowired
    private CbbTerminalGroupOperNotifySPI cbbTerminalGroupOperNotifySPI;

    @Autowired
    private ViewTerminalStatDAO viewTerminalStatDAO;

    @Override
    public List<CbbTerminalGroupDetailDTO> listTerminalGroup() {

        List<TerminalGroupEntity> groupList = terminalGroupService.findAll();
        List<CbbTerminalGroupDetailDTO> dtoList = new ArrayList<>();
        for (TerminalGroupEntity entity : groupList) {
            CbbTerminalGroupDetailDTO dto = new CbbTerminalGroupDetailDTO();
            dto.setId(entity.getId());
            dto.setGroupName(entity.getName());
            dto.setParentGroupId(entity.getParentId());
            if (Constants.DEFAULT_TERMINAL_GROUP_UUID.equals(dto.getId())) {
                dto.setEnableDefault(true);
            } else {
                dto.setEnableDefault(false);
            }
            dtoList.add(dto);
        }

        return dtoList;
    }

    @Override
    public CbbTerminalGroupTreeNodeDTO[] loadTerminalGroupCompleteTree(CbbGetTerminalGroupCompleteTreeDTO request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        List<TerminalGroupEntity> groupList = terminalGroupService.findAll();
        if (CollectionUtils.isEmpty(groupList)) {
            return new CbbTerminalGroupTreeNodeDTO[0];
        }
        //过滤掉未分组
        if (request.getEnableFilterDefaultGroup()) {
            terminalGroupHandler.filterDefaultGroup(groupList);
        }
        CbbTerminalGroupTreeNodeDTO[] dtoArr = terminalGroupHandler.assembleGroupTree(null, groupList, request.getFilterGroupId());
        return dtoArr;
    }

    @Override
    public CbbTerminalGroupDetailDTO getByName(CbbTerminalGroupDTO request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        List<TerminalGroupEntity> groupEntityList = terminalGroupService.getByName(request.getParentGroupId(), request.getGroupName());
        if (CollectionUtils.isEmpty(groupEntityList)) {
            //groupEntityList为空，返回null
            return null;
        }

        // 同级下分组名称唯一，因此列表只可能存在一个
        CbbTerminalGroupDetailDTO groupDTO = new CbbTerminalGroupDetailDTO();
        groupEntityList.get(0).converToDTO(groupDTO);
        return groupDTO;
    }

    @Override
    public CbbTerminalGroupDetailDTO loadById(UUID groupId) throws BusinessException {
        Assert.notNull(groupId, "groupId can not be null");

        TerminalGroupEntity groupEntity = terminalGroupService.getTerminalGroup(groupId);
        CbbTerminalGroupDetailDTO groupDTO = new CbbTerminalGroupDetailDTO();
        groupEntity.converToDTO(groupDTO);
        if (groupEntity.getParentId() != null) {
            TerminalGroupEntity parentGroupEntity = terminalGroupService.getTerminalGroup(groupEntity.getParentId());
            groupDTO.setParentGroupName(parentGroupEntity.getName());
        }

        return groupDTO;
    }

    @Override
    public CbbTerminalGroupDetailDTO createTerminalGroup(CbbTerminalGroupDTO request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        CbbTerminalGroupDetailDTO saveGroup = new CbbTerminalGroupDetailDTO(null, request.getGroupName(), request.getParentGroupId());
        TerminalGroupEntity entity = terminalGroupService.saveTerminalGroup(saveGroup);
        saveGroup.setId(entity.getId());
        return saveGroup;
    }

    @Override
    public void editTerminalGroup(CbbEditTerminalGroupDTO request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        CbbTerminalGroupDetailDTO terminalGroupDTO = new CbbTerminalGroupDetailDTO(request.getId()
                , request.getGroupName(), request.getParentGroupId());
        terminalGroupService.modifyGroupById(terminalGroupDTO);
    }

    @Override
    public void deleteTerminalGroup(CbbDeleteTerminalGroupDTO request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        terminalGroupServiceTx.deleteGroup(request.getId(), request.getMoveGroupId());
        CbbTerminalGroupOperNotifyRequest cbbTerminalGroupOperNotifyRequest = new CbbTerminalGroupOperNotifyRequest();
        cbbTerminalGroupOperNotifyRequest.setId(request.getId());
        cbbTerminalGroupOperNotifyRequest.setMoveGroupId(request.getMoveGroupId());
        cbbTerminalGroupOperNotifySPI.notifyTerminalGroupChange(cbbTerminalGroupOperNotifyRequest);
    }

    @Override
    public String[] obtainGroupNamePathArr(UUID groupId) throws BusinessException {
        Assert.notNull(groupId, "request can not be null");

        return terminalGroupService.getTerminalGroupNameArr(groupId);
    }

    @Override
    public boolean checkUseGroupNameDuplication(CbbTerminalGroupNameDuplicationDTO request) {
        Assert.notNull(request, "Param [CbbTerminalGroupNameDuplicationDTO] must not be null");

        boolean isNameUnique;
        try {
            isNameUnique = terminalGroupService.checkGroupNameUnique(new CbbTerminalGroupDetailDTO(request.getId(),
                    request.getGroupName(), request.getParentId()));
        } catch (BusinessException e) {
            isNameUnique = false;
        }

        return !isNameUnique;
    }

    @Override
    public CbbTerminalStatisticsDTO statisticsTerminal(UUID[] groupIdArr) {
        Assert.notNull(groupIdArr, "groupIdArr");
        CbbTerminalStatisticsDTO response = new CbbTerminalStatisticsDTO();
        //统计各类型终端在线情况
        TerminalStatisticsItem itemVDI = buildTerminalStatisticsItem(CbbTerminalPlatformEnums.VDI, groupIdArr);
        response.setVdi(itemVDI);
        TerminalStatisticsItem itemIDV = buildTerminalStatisticsItem(CbbTerminalPlatformEnums.IDV, groupIdArr);
        response.setIdv(itemIDV);
        TerminalStatisticsItem itemAPP = buildTerminalStatisticsItem(CbbTerminalPlatformEnums.APP, groupIdArr);
        response.setApp(itemAPP);

        TerminalStatisticsItem itemVOI = buildTerminalStatisticsItem(CbbTerminalPlatformEnums.VOI, groupIdArr);
        response.setVoi(itemVOI);
        //统计所有终端在线情况
        return response;
    }

    private TerminalStatisticsItem buildTerminalStatisticsItem(CbbTerminalPlatformEnums terminalPlatform, UUID[] groupIdArr) {
        List<TerminalStatisticsDTO> resultList;
        Long neverLoginCount = 0L;
        if (ArrayUtils.isEmpty(groupIdArr)) {
            resultList = viewTerminalStatDAO.statisticsByTerminalState(terminalPlatform);
        } else {
            List<UUID> terminalGroupIdList = HibernateUtil
                    .handleQueryIncludeList(Arrays.asList(groupIdArr));
            resultList = viewTerminalStatDAO.statisticsByTerminalStateAndGroupId(terminalPlatform,
                    terminalGroupIdList);
        }
        if (CollectionUtils.isEmpty(resultList)) {
            LOGGER.debug("没有终端类型为[{}]的数据", terminalPlatform);
            return new TerminalStatisticsItem();
        }
        TerminalStatisticsItem item = buildTerminalStatisticsItem(resultList, neverLoginCount);
        return item;
    }

    private TerminalStatisticsItem buildTerminalStatisticsItem(List<TerminalStatisticsDTO> list, Long neverLoginCount) {
        AtomicInteger online = new AtomicInteger();
        AtomicInteger offline = new AtomicInteger();
        AtomicInteger other = new AtomicInteger();
        list.forEach((item -> {
            CbbTerminalStateEnums state = CbbTerminalStateEnums.valueOf(item.getState());
            int count = item.getCount().intValue();
            switch (state) {
                case UPGRADING:
                case ONLINE:
                    online.addAndGet(count);
                    break;
                case OFFLINE:
                    offline.addAndGet(count);
                    break;
                default:
                    other.addAndGet(count);
                    break;
            }
        }));
        TerminalStatisticsItem item = new TerminalStatisticsItem();
        item.setOnline(online.get());
        item.setOffline(offline.get());
        item.setNeverLogin(neverLoginCount.intValue());
        item.setTotal(online.get() + offline.get() + other.get());
        return item;
    }
}
