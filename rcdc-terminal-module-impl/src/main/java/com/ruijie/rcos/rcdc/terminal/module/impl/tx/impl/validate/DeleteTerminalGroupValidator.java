package com.ruijie.rcos.rcdc.terminal.module.impl.tx.impl.validate;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.google.common.base.Objects;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalGroupDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalGroupEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalGroupService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.checker.GroupHierarchyChecker;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.checker.GroupNameDuplicationChecker;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.checker.GroupSubNumChecker;
import com.ruijie.rcos.sk.base.exception.BusinessException;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/7/9
 *
 * @author nt
 */
@Service
public class DeleteTerminalGroupValidator {

    @Autowired
    private TerminalGroupService terminalGroupService;

    @Autowired
    private TerminalGroupDAO terminalGroupDAO;

    @Autowired
    private GroupHierarchyChecker groupHierarchyChecker;

    @Autowired
    private GroupSubNumChecker groupSubNumChecker;

    @Autowired
    private GroupNameDuplicationChecker groupNameDuplicationChecker;

    /**
     *  删除终端分组校验
     *
     * @param groupId 删除分组id
     * @param moveGroupId 移动分组id
     * @throws BusinessException 业务异常
     */
    public void validate (UUID groupId, @Nullable UUID moveGroupId) throws BusinessException {
        Assert.notNull(groupId, "delete group id can not be null");

        TerminalGroupEntity groupEntity = checkDeleteGroupExist(groupId);
        checkMoveGroupExist(moveGroupId);
        checkDeleteGroupIsDefault(groupId);
        checkAllowMoveTo(groupEntity, moveGroupId);
    }

    /**
     *  校验是否允许移动到指定分组
     *
     * @param groupEntity 删除分组
     * @param moveGroupId 移动分组id
     */
    private void checkAllowMoveTo(TerminalGroupEntity groupEntity, UUID moveGroupId) throws BusinessException {
        if (Objects.equal(moveGroupId, Constants.DEFAULT_TERMINAL_GROUP_UUID)) {
            // 移动的分组是默认分组，不需校验
            return;
        }

        if (moveGroupId == null) {
            // 移动的分组是总览，需校验子分组名称是否与
        }

        // 校验分组层级
        checkHierarchy(groupEntity, moveGroupId);

        // 校验分组子分组数
        checkSubGroupNum(groupEntity, moveGroupId);

        checkSubGroupNameDuplication(groupEntity, moveGroupId);
    }

    private void checkHierarchy(TerminalGroupEntity groupEntity, UUID moveGroupId) throws BusinessException {
        // 移动到总览下，无需校验层级； 移动分组为未分组时则将删除子分组，亦无需校验
        if (moveGroupId == null || moveGroupId.equals(Constants.DEFAULT_TERMINAL_GROUP_UUID)) {
            return;
        }

        // 获取的子分组层级包含自身，需减去
        groupHierarchyChecker.check(moveGroupId, groupHierarchyChecker.getSubHierarchy(groupEntity.getId()) - 1);
    }

    private void checkSubGroupNum(TerminalGroupEntity groupEntity, UUID moveGroupId) throws BusinessException {
        // 移动分组为未分组时则将删除子分组，无需校验子分组数量
        if (Objects.equal(moveGroupId, Constants.DEFAULT_TERMINAL_GROUP_UUID)) {
            return;
        }

        groupSubNumChecker.check(groupEntity,
                groupSubNumChecker.getSubGroupNum(moveGroupId));
    }

    private void checkSubGroupNameDuplication(TerminalGroupEntity groupEntity, UUID moveGroupId) throws BusinessException {
        // 移动分组为未分组时则将删除子分组，无需校验子分组数量
        if (Objects.equal(moveGroupId, Constants.DEFAULT_TERMINAL_GROUP_UUID)) {
            return;
        }

        List<TerminalGroupEntity> subGroupList =
                terminalGroupDAO.findByParentId(groupEntity.getId());
        if (CollectionUtils.isEmpty(subGroupList)) {
            return;
        }

        TerminalGroupEntity moveGroupEntity = obtainMoveGroupEntity(moveGroupId);
        for (TerminalGroupEntity subGroup : subGroupList) {
            groupNameDuplicationChecker.check(moveGroupEntity, subGroup.getName());
        }

    }

    private TerminalGroupEntity obtainMoveGroupEntity(UUID moveGroupId)
            throws BusinessException {
        if (moveGroupId == null) {
            TerminalGroupEntity groupEntity = new TerminalGroupEntity();
            return groupEntity;
        }

        return terminalGroupService.checkGroupExist(moveGroupId);
    }

    /**
     *  校验删除分组是否默认分组
     *
     * @param groupId 删除的分组id
     * @throws BusinessException 业务异常
     */
    private void checkDeleteGroupIsDefault(UUID groupId) throws BusinessException {
        if (Objects.equal(groupId, Constants.DEFAULT_TERMINAL_GROUP_UUID)) {
            throw new BusinessException(BusinessKey.RCDC_TERMINALGROUP_GROUP_CAN_NOT_DELETE_DEFAULT);
        }
    }

    /**
     *  校验移动的分组是否存在
     *
     * @param moveGroupId 移动到的分组id
     * @throws BusinessException 业务异常
     */
    private void checkMoveGroupExist(UUID moveGroupId) throws BusinessException {
        if (moveGroupId == null) {
            // 移动分组为总览
            return;
        }

        terminalGroupService.checkGroupExist(moveGroupId);
    }

    /**
     *  校验删除分组是否存在
     *
     * @param groupId 删除分组id
     * @throws BusinessException 业务异常
     */
    private TerminalGroupEntity checkDeleteGroupExist(UUID groupId) throws BusinessException {
        return terminalGroupService.checkGroupExist(groupId);
    }


}
