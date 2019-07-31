package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import com.ruijie.rcos.rcdc.terminal.module.def.api.response.group.CbbObtainGroupNamePathResponse;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalGroupEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalGroupService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalGroupServiceTx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import com.google.common.base.Objects;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalGroupMgmtAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.TerminalGroupDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.TerminalGroupTreeNodeDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.TerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.group.CbbCheckGroupNameDuplicationRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.group.CbbCreateTerminalGroupRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.group.CbbDeleteTerminalGroupRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.group.CbbEditTerminalGroupRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.group.CbbGetTerminalGroupCompleteTreeRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.group.CbbTerminalGroupIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.group.CbbCheckGroupNameDuplicationResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.group.CbbGetTerminalGroupTreeResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.group.CbbTerminalGroupResponse;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.modulekit.api.tx.DtxBusizContext;

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
    private TerminalGroupServiceTx terminalGroupServiceTx;

    @Override
    public CbbGetTerminalGroupTreeResponse loadTerminalGroupCompleteTree(CbbGetTerminalGroupCompleteTreeRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        List<TerminalGroupEntity> groupList = terminalGroupService.findAllByTerminalType(request.getTerminalType());
        if (CollectionUtils.isEmpty(groupList)) {
            return new CbbGetTerminalGroupTreeResponse(new TerminalGroupTreeNodeDTO[0]);
        }
        //过滤掉未分组
        if (request.getEnableFilterDefaultGroup()) {
            filterDefaultGroup(groupList);
        }
        TerminalGroupTreeNodeDTO[] dtoArr = assembleGroupTree(null, groupList, request.getFilterGroupId());
        return new CbbGetTerminalGroupTreeResponse(dtoArr);
    }
    
    /**
     * 过滤掉未分组
     * @param groupList 分组列表
     */
    private void filterDefaultGroup(List<TerminalGroupEntity> groupList) {
        Iterator<TerminalGroupEntity> iterator = groupList.iterator();
        for (; iterator.hasNext();) {
            TerminalGroupEntity group = iterator.next();
            if (Objects.equal(Constants.DEFAULT_TERMINAL_GROUP_UUID, group.getId())) {
                iterator.remove();
                return;
            }
        }
    }

    /**
     * 组装树形结构
     * 
     * @param parentId 父级节点
     * @param groupList 分组列表
     * @param filterGroupId 分组列表
     * @return 树形结构的分组列表
     */
    private TerminalGroupTreeNodeDTO[] assembleGroupTree(UUID parentId, List<TerminalGroupEntity> groupList, UUID filterGroupId) {
        if (CollectionUtils.isEmpty(groupList)) {
            return new TerminalGroupTreeNodeDTO[0];
        }
        
        List<TerminalGroupEntity> subList = new ArrayList<>();
        Iterator<TerminalGroupEntity> iterator = groupList.iterator();
        TerminalGroupEntity defaultGroup = null;
        for (; iterator.hasNext();) {
            TerminalGroupEntity group = iterator.next();
            //过滤的分组跳过
            if (Objects.equal(group.getId(), filterGroupId)) {
                continue;
            }
            
            if (Objects.equal(parentId, group.getParentId())) {
                //将默认分组放到列表最后
                if (Objects.equal(group.getId(), Constants.DEFAULT_TERMINAL_GROUP_UUID)) {
                    defaultGroup = group;
                    continue;
                }
                subList.add(group);
                iterator.remove();
            }
        }
        if (defaultGroup != null) {
            subList.add(defaultGroup);
        }

        TerminalGroupTreeNodeDTO[] dtoArr = convertToNodeDTO(subList);
        for (TerminalGroupTreeNodeDTO dto : dtoArr) {
            dto.setChildren(assembleGroupTree(dto.getId(), groupList, filterGroupId));
        }

        return dtoArr;
    }

    /**
     * 将分组对象转换为dto数组输出
     * 
     * @param subList 分组列表
     * @return dto数组
     */
    private TerminalGroupTreeNodeDTO[] convertToNodeDTO(List<TerminalGroupEntity> subList) {
        int size = subList.size();
        TerminalGroupTreeNodeDTO[] dtoArr = new TerminalGroupTreeNodeDTO[size];
        Stream.iterate(0, i -> i + 1).limit(size).forEach(i -> {
            TerminalGroupEntity entity = subList.get(i);
            TerminalGroupTreeNodeDTO dto = new TerminalGroupTreeNodeDTO();
            entity.converToDTO(dto);
            dtoArr[i] = dto;
        });
        return dtoArr;
    }

    @Override
    public CbbCheckGroupNameDuplicationResponse checkNameDuplication(CbbCheckGroupNameDuplicationRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        TerminalGroupDTO terminalGroup = new TerminalGroupDTO(request.getId(), request.getGroupName(), request.getParentGroupId());
        boolean enableUnique = terminalGroupService.checkGroupNameUnique(terminalGroup);
        return new CbbCheckGroupNameDuplicationResponse(!enableUnique);
    }

    @Override
    public CbbTerminalGroupResponse loadById(CbbTerminalGroupIdRequest request) throws BusinessException {
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
    public DefaultResponse createTerminalGroup(CbbCreateTerminalGroupRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        TerminalGroupDTO saveGroup = new TerminalGroupDTO(null, request.getGroupName(), request.getParentGroupId());
        terminalGroupService.saveTerminalGroup(saveGroup);

        // idv新建分组
        if (TerminalTypeEnums.IDV == request.getTerminalType()) {
            // 暂不支持idv
            LOGGER.warn("do not support create idv terminal group, group name [{}], parent group id[{}]",
                    request.getGroupName(), request.getParentGroupId());
        }

        return DefaultResponse.Builder.success();
    }

    @Override
    public void rollbackCreateTerminalGroup(DtxBusizContext context) {
        // 暂不支持idv, 仅在创建idv分组会需要补偿

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
    public CbbObtainGroupNamePathResponse obtainGroupNamePathArr(CbbTerminalGroupIdRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        CbbObtainGroupNamePathResponse response = new CbbObtainGroupNamePathResponse();
        response.setGroupNameArr(terminalGroupService.getTerminalGroupNameArr(request.getId()));
        return response;
    }
}