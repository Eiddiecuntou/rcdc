package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.swing.tree.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.rco.module.impl.dao.TerminalGroupDAO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.TerminalGroupDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.TerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalGroupEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalGroupService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * Description: 终端分组service
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月20日
 *
 * @author nt
 */
@Service
public class TerminalGroupServiceImpl implements TerminalGroupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalGroupServiceImpl.class);

    @Autowired
    private TerminalGroupDAO terminalGroupDAO;

    @Override
    public void saveTerminalGroup(TerminalGroupDTO terminalGroup) throws BusinessException {
        Assert.notNull(terminalGroup, "terminal group can not be null");
        String groupName = terminalGroup.getGroupName();
        Assert.hasText(groupName, "terminal group name can not be null");

        UUID parentGroupId = terminalGroup.getParentGroupId();
        TerminalTypeEnums terminalType = terminalGroup.getTerminalType();
        checkDefault(parentGroupId);
        checkGroupNum(terminalType);
        checkSubGroupNum(terminalType, parentGroupId);
        checkNameUniqueThrowExceptionIfNot(terminalGroup);
        checkGroupLevel(parentGroupId);

        TerminalGroupEntity entity = buildTerminalGroupEntity(terminalGroup);
        LOGGER.info("create terminal group with name[{}] ,parent group id[{}]", groupName, parentGroupId);
        terminalGroupDAO.save(entity);
    }

    @Override
    public TerminalGroupEntity getTerminalGroup(UUID id) throws BusinessException {
        Assert.notNull(id, "terminal group id can not be null");

        Optional<TerminalGroupEntity> terminalGroupOpt = terminalGroupDAO.findById(id);
        if (!terminalGroupOpt.isPresent()) {
            LOGGER.error("terminal group not exist, group id[{}]", id);
            throw new BusinessException(BusinessKey.RCDC_RCO_TERMINALGROUP_GROUP_NOT_EXIST, id.toString());
        }
        return terminalGroupOpt.get();
    }

    @Override
    public List<TerminalGroupEntity> findByTerminalTypeAndParentId(TerminalTypeEnums terminalType, @Nullable UUID parentGroupId)
            throws BusinessException {
        Assert.notNull(terminalType, "terminal type can not be null");

        if (parentGroupId != null) {
            checkGroupExist(parentGroupId);
        }
        return terminalGroupDAO.findByTerminalTypeAndParentId(terminalType, parentGroupId);
    }


    @Override
    public boolean checkGroupNameUnique(TerminalGroupDTO terminalGroup) throws BusinessException {
        Assert.notNull(terminalGroup, "terminal group can not be null");

        String groupName = terminalGroup.getGroupName();
        Assert.notNull(groupName, "terminal group can not be null");
        UUID id = terminalGroup.getId();
        UUID parentGroupId = terminalGroup.getParentGroupId();
        List<TerminalGroupEntity> subList = findByTerminalTypeAndParentId(terminalGroup.getTerminalType(), parentGroupId);
        for (TerminalGroupEntity group : subList) {
            if (id != null && id.equals(group.getId())) {
                // 编辑排除自身
                continue;
            }
            if (groupName.equals(group.getName())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void modifyGroupById(TerminalGroupDTO terminalGroup) throws BusinessException {
        Assert.notNull(terminalGroup, "terminal group param can not be null");
        UUID id = terminalGroup.getId();
        UUID parentGroupId = terminalGroup.getParentGroupId();
        String groupName = terminalGroup.getGroupName();
        Assert.hasText(groupName, "terminal group name can not be blank");

        // 不可选取自己为父分组
        if (Objects.equals(id, parentGroupId)) {
            throw new BusinessException(BusinessKey.RCDC_RCO_TERMINALGROUP_GROUP_PARENT_CAN_NOT_SELECT_ITSELF);
        }
        // 校验分组是否存在
        TerminalGroupEntity groupEntity = checkGroupExist(id);
        // 变更分组需检验分组是否超过10级限制
        if (parentGroupId != null) {
            LOGGER.debug("parent group id is not null , check group id[{}] exist", parentGroupId);
            checkDefault(parentGroupId);
            checkGroupExist(parentGroupId);
            if (!parentGroupId.equals(groupEntity.getParentId())) {
                LOGGER.debug("parent group id is changed, check group level by id[{}] and parent id ", id, parentGroupId);
                checkGroupLevel(parentGroupId, terminalGroup.getId());
                checkSubGroupNum(groupEntity.getTerminalType(), parentGroupId);
            }
        }

        // 检验分组名称是否同级唯一
        checkNameUniqueThrowExceptionIfNot(terminalGroup);
        terminalGroupDAO.modifyGroupNameAndParent(id, groupName, parentGroupId, groupEntity.getVersion());
    }

    /**
     * 校验是否为默认的未分组
     *
     * @throws BusinessException 业务异常
     */
    private void checkDefault(UUID parentGroupId) throws BusinessException {
        if (Objects.equals(parentGroupId, Constants.DEFAULT_TERMINAL_GROUP_UUID)) {
            throw new BusinessException(BusinessKey.RCDC_RCO_TERMINALGROUP_GROUP_CAN_NOT_CREATE_IN_DEFAULT);
        }
    }

    /**
     * 检验分组数量是否超出限制
     *
     * @param terminalType 终端类型
     * @throws BusinessException 业务异常
     */
    private void checkGroupNum(TerminalTypeEnums terminalType) throws BusinessException {
        // 校验分组总数是否超出限制
        long count = terminalGroupDAO.countByTerminalType(terminalType);
        if (count >= Constants.TERMINAL_GROUP_MAX_GROUP_NUM) {
            throw new BusinessException(BusinessKey.RCDC_RCO_TERMINALGROUP_GROUP_NUM_EXCEED_LIMIT,
                    String.valueOf(Constants.TERMINAL_GROUP_MAX_GROUP_NUM));
        }
    }

    /**
     * 检验子分组数量是否超出限制
     *
     * @param terminalType 终端类型
     * @param parentGroupId 父分组id
     * @throws BusinessException 业务异常
     */
    private void checkSubGroupNum(TerminalTypeEnums terminalType, UUID parentGroupId) throws BusinessException {
        // 校验子分组数是否超出限制
        long subCount = terminalGroupDAO.countByTerminalTypeAndParentId(terminalType, parentGroupId);
        if (subCount >= Constants.TERMINAL_GROUP_MAX_SUB_GROUP_NUM) {
            throw new BusinessException(BusinessKey.RCDC_RCO_TERMINALGROUP_SUB_GROUP_NUM_EXCEED_LIMIT,
                    String.valueOf(Constants.TERMINAL_GROUP_MAX_SUB_GROUP_NUM));
        }
    }

    /**
     * 构建终端分组实体对象
     *
     * @param terminalGroup
     * @return
     */
    private TerminalGroupEntity buildTerminalGroupEntity(TerminalGroupDTO terminalGroup) {
        TerminalGroupEntity entity = new TerminalGroupEntity();
        entity.setName(terminalGroup.getGroupName());
        entity.setParentId(terminalGroup.getParentGroupId());
        entity.setTerminalType(terminalGroup.getTerminalType());
        entity.setCreateTime(new Date());
        return entity;
    }

    /**
     * 校验分组是否存在
     *
     * @param groupId 分组id
     * @return 终端分组对象
     * @throws BusinessException 业务异常
     */
    @Override
    public TerminalGroupEntity checkGroupExist(UUID groupId) throws BusinessException {
        Assert.notNull(groupId, "group id can not be null");

        Optional<TerminalGroupEntity> group = terminalGroupDAO.findById(groupId);
        if (!group.isPresent()) {
            LOGGER.error("terminal group not exist, group id[{}]", groupId);
            throw new BusinessException(BusinessKey.RCDC_RCO_TERMINALGROUP_GROUP_NOT_EXIST, groupId.toString());
        }

        return group.get();
    }

    @Override
    public String[] getTerminalGroupNameArr(UUID groupId) throws BusinessException {
        Assert.notNull(groupId, "groupId can not be null");

        TerminalGroupEntity entity = getTerminalGroup(groupId);
        List<String> groupNameList = new ArrayList<>();
        groupNameList.add(entity.getName());
        UUID parentId = entity.getParentId();
        while (parentId != null) {
            entity = getTerminalGroup(parentId);
            groupNameList.add(entity.getName());
            parentId = entity.getParentId();
        }
        String[] groupNameArr = new String[groupNameList.size()];
        Collections.reverse(groupNameList);
        groupNameList.toArray(groupNameArr);
        return groupNameArr;
    }

    /**
     * 检验分组名是否同级唯一
     *
     * @param terminalGroup 分组对象
     * @throws BusinessException 业务异常
     */
    private void checkNameUniqueThrowExceptionIfNot(TerminalGroupDTO terminalGroup) throws BusinessException {
        boolean enableUnique = checkGroupNameUnique(terminalGroup);
        if (!enableUnique) {
            LOGGER.error("terminal group name has exist, group name[{}], parent group id[{}]", terminalGroup.getGroupName(),
                    terminalGroup.getParentGroupId());
            throw new BusinessException(BusinessKey.RCDC_RCO_TERMINALGROUP_GROUP_NAME_DUPLICATE, terminalGroup.getGroupName());
        }
    }

    @Override
    public void checkGroupLevel(@Nullable UUID parentGroupId, UUID groupId) throws BusinessException {
        Assert.notNull(groupId, "groupId can not be null");

        int parentHierarchy = getGroupHierarchy(parentGroupId);
        if (parentHierarchy >= Constants.TERMINAL_GROUP_MAX_LEVEL) {
            throw new BusinessException(BusinessKey.RCDC_RCO_TERMINALGROUP_GROUP_LEVEL_EXCEED_LIMIT,
                    String.valueOf(Constants.TERMINAL_GROUP_MAX_LEVEL + 1));
        }
        int subHierarchy = getSubHierarchy(groupId);
        int totalHierarchy = parentHierarchy + subHierarchy;
        if (totalHierarchy > Constants.TERMINAL_GROUP_MAX_LEVEL) {
            // "总览"属于前端添加显示的根组，所以对于用户来说应该是后台实际限制的分组数+1，下同
            throw new BusinessException(BusinessKey.RCDC_RCO_TERMINALGROUP_GROUP_LEVEL_EXCEED_LIMIT,
                    String.valueOf(Constants.TERMINAL_GROUP_MAX_LEVEL + 1));
        }
    }

    @Override
    public int getSubHierarchy(UUID groupId) {
        Assert.notNull(groupId, "groupId can not be null");

        // 获取子分组层级数
        List<TerminalGroupEntity> subGroupList = terminalGroupDAO.findByTerminalTypeAndParentId(TerminalTypeEnums.VDI, groupId);
        List<TreeNode> childList = buildChildrenNode(subGroupList);
        TreeNode rootNode = new TreeNode(groupId, childList);
        return rootNode.maxDepth(rootNode);
    }

    private List<TreeNode> buildChildrenNode(List<TerminalGroupEntity> subGroupList) {
        List<TreeNode> childList = new ArrayList<>(subGroupList.size());
        for (TerminalGroupEntity entity : subGroupList) {
            List<TerminalGroupEntity> subList = terminalGroupDAO.findByTerminalTypeAndParentId(TerminalTypeEnums.VDI, entity.getId());
            childList.add(new TreeNode(entity.getId(), buildChildrenNode(subList)));
        }
        return childList;
    }

    /**
     * 检验分组级别是否超出限制
     *
     * @param parentGroupId 父级分组
     * @throws BusinessException 业务异常
     */
    private void checkGroupLevel(UUID parentGroupId) throws BusinessException {
        int hierarchy = getGroupHierarchy(parentGroupId);
        if (hierarchy >= Constants.TERMINAL_GROUP_MAX_LEVEL) {
            throw new BusinessException(BusinessKey.RCDC_RCO_TERMINALGROUP_GROUP_LEVEL_EXCEED_LIMIT,
                    String.valueOf(Constants.TERMINAL_GROUP_MAX_LEVEL + 1));
        }
    }

    @Override
    public int getGroupHierarchy(@Nullable UUID groupId) throws BusinessException {

        if (groupId == null) {
            return 0;
        }

        if (Constants.DEFAULT_TERMINAL_GROUP_UUID.equals(groupId)) {
            // 父分组为默认分组
            return 1;
        }

        checkGroupExist(groupId);

        // 初始层级0
        int hierarchy = 0;
        // 父分组不是根分组，且不是默认分组，层级加1
        UUID parentId = groupId;
        for (int i = 0; i < Constants.TERMINAL_GROUP_MAX_LEVEL; i++) {
            hierarchy++;
            TerminalGroupEntity groupEntity = getTerminalGroup(parentId);
            parentId = groupEntity.getParentId();
            if (parentId == null) {
                break;
            }
        }
        return hierarchy;
    }

    @Override
    public List<TerminalGroupEntity> findAllByTerminalType(TerminalTypeEnums terminalType) {
        Assert.notNull(terminalType, "terminal type can not be null");
        return terminalGroupDAO.findByTerminalType(terminalType);
    }

}
