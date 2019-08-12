package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.TerminalGroupDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalGroupDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalGroupEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalGroupService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.checker.GroupHierarchyChecker;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.checker.GroupSubNumChecker;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.checker.GroupTotalNumChecker;
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

    @Autowired
    private GroupHierarchyChecker groupHierarchyChecker;

    @Autowired
    private GroupSubNumChecker groupSubNumChecker;

    @Autowired
    private GroupTotalNumChecker groupTotalNumChecker;

    @Override
    public void saveTerminalGroup(TerminalGroupDTO terminalGroup) throws BusinessException {
        Assert.notNull(terminalGroup, "terminal group can not be null");
        String groupName = terminalGroup.getGroupName();
        Assert.hasText(groupName, "terminal group name can not be null");

        UUID parentGroupId = terminalGroup.getParentGroupId();
        checkDefault(parentGroupId);
        checkGroupNum();
        checkSubGroupNum(parentGroupId);
        checkNameUniqueThrowExceptionIfNot(terminalGroup);
        checkGroupLevel(parentGroupId, 1);

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
            throw new BusinessException(BusinessKey.RCDC_TERMINALGROUP_GROUP_NOT_EXIST, id.toString());
        }
        return terminalGroupOpt.get();
    }

    @Override
    public boolean checkGroupNameUnique(TerminalGroupDTO terminalGroup) throws BusinessException {
        Assert.notNull(terminalGroup, "terminal group can not be null");
        Assert.hasText(terminalGroup.getGroupName(), "terminal group name can not be blank");

        String groupName = terminalGroup.getGroupName();
        UUID groupId = terminalGroup.getId();
        UUID parentGroupId = terminalGroup.getParentGroupId();
        List<TerminalGroupEntity> subList =
                terminalGroupDAO.findByParentId(parentGroupId);
        if (CollectionUtils.isEmpty(subList)) {
            return true;
        }

        for (TerminalGroupEntity group : subList) {
            if (groupId != null && groupId.equals(group.getId())) {
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
            throw new BusinessException(BusinessKey.RCDC_TERMINALGROUP_GROUP_PARENT_CAN_NOT_SELECT_ITSELF);
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
                checkGroupLevel(parentGroupId, groupHierarchyChecker.getSubHierarchy(terminalGroup.getId()));
                checkSubGroupNum(parentGroupId);
            }
        }

        // 检验分组名称是否同级唯一
        checkNameUniqueThrowExceptionIfNot(terminalGroup);
        modifyGroupNameAndParent(id, groupName, parentGroupId);
    }

    /**
     *  更新分组名及父分组
     * @param groupId 分组id
     * @param groupName 分组名
     * @param parentGroupId 父分组id
     * @throws BusinessException 业务异常
     */
    private void modifyGroupNameAndParent(UUID groupId, String groupName, UUID parentGroupId) throws BusinessException {
        TerminalGroupEntity groupEntity = checkGroupExist(groupId);
        groupEntity.setParentId(parentGroupId);
        groupEntity.setName(groupName);
        terminalGroupDAO.save(groupEntity);
    }

    /**
     * 校验是否为默认的未分组
     *
     * @throws BusinessException 业务异常
     */
    private void checkDefault(UUID parentGroupId) throws BusinessException {
        if (Objects.equals(parentGroupId, Constants.DEFAULT_TERMINAL_GROUP_UUID)) {
            throw new BusinessException(BusinessKey.RCDC_TERMINALGROUP_GROUP_CAN_NOT_CREATE_IN_DEFAULT);
        }
    }

    /**
     * 检验分组数量是否超出限制
     *
     * @throws BusinessException 业务异常
     */
    private void checkGroupNum() throws BusinessException {
        // 校验分组总数是否超出限制
        groupTotalNumChecker.check(1);
    }

    /**
     * 检验子分组数量是否超出限制
     *
     * @param parentGroupId 父分组id
     * @throws BusinessException 业务异常
     */
    private void checkSubGroupNum(UUID parentGroupId) throws BusinessException {
        groupSubNumChecker.check(obtainGroupEntity(parentGroupId), 1);
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
            throw new BusinessException(BusinessKey.RCDC_TERMINALGROUP_GROUP_NOT_EXIST, groupId.toString());
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
            throw new BusinessException(BusinessKey.RCDC_TERMINALGROUP_GROUP_NAME_DUPLICATE, terminalGroup.getGroupName());
        }
    }

    /**
     * 检验分组级别是否超出限制
     *
     * @param parentGroupId 父级分组
     * @throws BusinessException 业务异常
     */
    private void checkGroupLevel(UUID parentGroupId, int addHerarchy) throws BusinessException {
        groupHierarchyChecker.check(parentGroupId, addHerarchy);
    }

    private TerminalGroupEntity obtainGroupEntity(UUID parentGroupId)
            throws BusinessException {
        if (parentGroupId == null) {
            TerminalGroupEntity groupEntity = new TerminalGroupEntity();
            return groupEntity;
        }

        return checkGroupExist(parentGroupId);
    }

    @Override
    public List<TerminalGroupEntity> findAll() {
        return terminalGroupDAO.findAll();
    }

}
