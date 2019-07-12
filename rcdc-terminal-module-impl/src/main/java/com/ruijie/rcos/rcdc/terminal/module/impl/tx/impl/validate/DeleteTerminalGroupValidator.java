package com.ruijie.rcos.rcdc.terminal.module.impl.tx.impl.validate;

import com.google.common.base.Objects;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.TerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalGroupDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalGroupEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalGroupService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.impl.validate.checker.GroupHierarchyChecker;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;

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
        TerminalTypeEnums terminalType = groupEntity.getTerminalType();
        long totalSubGroupNum =
                getSubGroupNum(terminalType, groupEntity.getId()) + getSubGroupNum(terminalType, moveGroupId);
        if (totalSubGroupNum > Constants.TERMINAL_GROUP_MAX_SUB_GROUP_NUM) {
            throw new BusinessException(BusinessKey.RCDC_TERMINALGROUP_SUB_GROUP_NUM_EXCEED_LIMIT,
                    String.valueOf(Constants.TERMINAL_GROUP_MAX_SUB_GROUP_NUM));
        }
    }

    private long getSubGroupNum(TerminalTypeEnums terminalType, UUID id) {
        return terminalGroupDAO.countByTerminalTypeAndParentId(terminalType, id);
    }

    private void checkSubGroupNameDuplication(TerminalGroupEntity groupEntity, UUID moveGroupId) throws BusinessException {
        // 移动分组为未分组时则将删除子分组，无需校验子分组数量
        if (Objects.equal(moveGroupId, Constants.DEFAULT_TERMINAL_GROUP_UUID)) {
            return;
        }

        TerminalTypeEnums terminalType = groupEntity.getTerminalType();
        List<TerminalGroupEntity> subGroupList =
                terminalGroupDAO.findByTerminalTypeAndParentId(terminalType, groupEntity.getId());
        if (CollectionUtils.isEmpty(subGroupList)) {
            return;
        }

        for (TerminalGroupEntity subGroup : subGroupList) {
            List<TerminalGroupEntity> findGroupList = terminalGroupDAO
                    .findByTerminalTypeAndParentIdAndName(terminalType, moveGroupId, subGroup.getName());
            if (CollectionUtils.isEmpty(findGroupList)) {
                continue;
            }
            throw new BusinessException(
                    BusinessKey.RCDC_DELETE_TERMINAL_GROUP_SUB_GROUP_HAS_DUPLICATION_WITH_MOVE_GROUP);
        }

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
