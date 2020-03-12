package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalGroupMgmtAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.TerminalGroupDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.TerminalGroupTreeNodeDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.TerminalGroupNameDuplicationRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.group.CbbDeleteTerminalGroupRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.group.CbbEditTerminalGroupRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.group.CbbGetTerminalGroupCompleteTreeRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.group.CbbTerminalGroupRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.group.CbbGetTerminalGroupTreeResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.group.CbbObtainGroupNamePathResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.group.CbbTerminalGroupResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.group.CheckGroupNameDuplicationResponse;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalGroupEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalGroupService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.TerminalGroupHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalGroupServiceTx;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.DtoResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.IdRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;

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

    @Override
    public CbbGetTerminalGroupTreeResponse loadTerminalGroupCompleteTree(CbbGetTerminalGroupCompleteTreeRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        List<TerminalGroupEntity> groupList = terminalGroupService.findAll();
        if (CollectionUtils.isEmpty(groupList)) {
            return new CbbGetTerminalGroupTreeResponse(new TerminalGroupTreeNodeDTO[0]);
        }
        //过滤掉未分组
        if (request.getEnableFilterDefaultGroup()) {
            terminalGroupHandler.filterDefaultGroup(groupList);
        }
        TerminalGroupTreeNodeDTO[] dtoArr = terminalGroupHandler.assembleGroupTree(null, groupList, request.getFilterGroupId());
        return new CbbGetTerminalGroupTreeResponse(dtoArr);
    }

    @Override
    public CbbTerminalGroupResponse getByName(CbbTerminalGroupRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        List<TerminalGroupEntity> groupEntityList = terminalGroupService.getByName(request.getParentGroupId(), request.getGroupName());
        if (CollectionUtils.isEmpty(groupEntityList)) {
            return new CbbTerminalGroupResponse(null);
        }

        // 同级下分组名称唯一，因此列表只可能存在一个
        TerminalGroupDTO groupDTO = new TerminalGroupDTO();
        groupEntityList.get(0).converToDTO(groupDTO);
        return new CbbTerminalGroupResponse(groupDTO);
    }

    @Override
    public CbbTerminalGroupResponse loadById(IdRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        UUID id = request.getId();
        TerminalGroupEntity groupEntity = terminalGroupService.getTerminalGroup(id);
        TerminalGroupDTO groupDTO = new TerminalGroupDTO();
        groupEntity.converToDTO(groupDTO);
        if (groupEntity.getParentId() != null) {
            TerminalGroupEntity parentGroupEntity = terminalGroupService.getTerminalGroup(groupEntity.getParentId());
            groupDTO.setParentGroupName(parentGroupEntity.getName());
        }

        return new CbbTerminalGroupResponse(groupDTO);
    }

    @Override
    public DtoResponse<TerminalGroupDTO> createTerminalGroup(CbbTerminalGroupRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        TerminalGroupDTO saveGroup = new TerminalGroupDTO(null, request.getGroupName(), request.getParentGroupId());
        TerminalGroupEntity entity = terminalGroupService.saveTerminalGroup(saveGroup);
        saveGroup.setId(entity.getId());
        return DtoResponse.success(saveGroup);
    }

    @Override
    public DefaultResponse editTerminalGroup(CbbEditTerminalGroupRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        TerminalGroupDTO terminalGroupDTO = new TerminalGroupDTO(request.getId(), request.getGroupName(), request.getParentGroupId());
        terminalGroupService.modifyGroupById(terminalGroupDTO);
        return DefaultResponse.Builder.success();
    }

    @Override
    public DefaultResponse deleteTerminalGroup(CbbDeleteTerminalGroupRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        terminalGroupServiceTx.deleteGroup(request.getId(), request.getMoveGroupId());
        return DefaultResponse.Builder.success();
    }

    @Override
    public CbbObtainGroupNamePathResponse obtainGroupNamePathArr(IdRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        CbbObtainGroupNamePathResponse response = new CbbObtainGroupNamePathResponse();
        response.setGroupNameArr(terminalGroupService.getTerminalGroupNameArr(request.getId()));
        return response;
    }

    @Override
    public CheckGroupNameDuplicationResponse checkUseGroupNameDuplication(TerminalGroupNameDuplicationRequest request) throws BusinessException {
        Assert.notNull(request, "Param [TerminalGroupNameDuplicationRequest] must not be null");

        boolean isNameUnique;
        try {
            isNameUnique = terminalGroupService.checkGroupNameUnique(new TerminalGroupDTO(request.getId(),
                    request.getGroupName(), request.getParentId()));
        } catch (BusinessException e) {
            isNameUnique = false;
        }

        return new CheckGroupNameDuplicationResponse(!isNameUnique);
    }
}
