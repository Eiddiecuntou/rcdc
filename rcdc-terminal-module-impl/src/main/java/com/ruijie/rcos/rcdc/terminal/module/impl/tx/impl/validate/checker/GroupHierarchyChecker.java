package com.ruijie.rcos.rcdc.terminal.module.impl.tx.impl.validate.checker;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.TerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalGroupDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalGroupEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.vo.TreeNode;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Description: 分组层级校验器
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/7/11
 *
 * @author nt
 */
@Service
public class GroupHierarchyChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupHierarchyChecker.class);

    @Autowired
    private TerminalGroupDAO terminalGroupDAO;

    public void check(@Nullable UUID groupId, int addHierarchy) throws BusinessException {
        int groupHierarchy = getGroupHierarchy(groupId);
        LOGGER.info("分组层级为:[{}]", groupHierarchy);
        int totalHierarchy = groupHierarchy + addHierarchy;
        if (totalHierarchy > Constants.TERMINAL_GROUP_MAX_LEVEL) {
            throw new BusinessException(BusinessKey.RCDC_TERMINALGROUP_GROUP_LEVEL_EXCEED_LIMIT,
                    String.valueOf(Constants.TERMINAL_GROUP_MAX_LEVEL + 1));
        }
    }

    /**
     *  获取分组层级数（包含自身）
     *
     * @param groupId 分组id
     * @return 分组层级数
     */
    public int getSubHierarchy(UUID groupId) {
        Assert.notNull(groupId, "groupId can not be null");

        // 获取子分组层级数
        List<TerminalGroupEntity> subGroupList =
                terminalGroupDAO.findByTerminalTypeAndParentId(TerminalTypeEnums.VDI, groupId);
        List<TreeNode> childList = buildChildrenNode(subGroupList);
        TreeNode rootNode = new TreeNode(groupId, childList);
        int maxDepth = rootNode.maxDepth(rootNode);
        LOGGER.info("分组子分组层级为:[{}]", maxDepth);
        return maxDepth;
    }

    private List<TreeNode> buildChildrenNode(List<TerminalGroupEntity> subGroupList) {
        List<TreeNode> childList = new ArrayList<>(subGroupList.size());
        for (TerminalGroupEntity entity : subGroupList) {
            List<TerminalGroupEntity> subList =
                    terminalGroupDAO.findByTerminalTypeAndParentId(TerminalTypeEnums.VDI, entity.getId());
            childList.add(new TreeNode(entity.getId(), buildChildrenNode(subList)));
        }
        return childList;
    }

    public int getGroupHierarchy(@Nullable UUID groupId) throws BusinessException {
        if (groupId == null) {
            return 0;
        }

        if (Constants.DEFAULT_TERMINAL_GROUP_UUID.equals(groupId)) {
            // 父分组为默认分组
            return 1;
        }

        getAndCheckTerminalGroup(groupId);

        // 初始层级0
        int hierarchy = 0;
        // 父分组不是根分组，且不是默认分组，层级加1
        UUID parentId = groupId;
        for (int i = 0; i < Constants.TERMINAL_GROUP_MAX_LEVEL; i++) {
            hierarchy++;
            TerminalGroupEntity groupEntity = getAndCheckTerminalGroup(parentId);
            parentId = groupEntity.getParentId();
            if (parentId == null) {
                break;
            }
        }
        return hierarchy;
    }

    private TerminalGroupEntity getAndCheckTerminalGroup(UUID groupId) throws BusinessException {
        Optional<TerminalGroupEntity> entityOpt = terminalGroupDAO.findById(groupId);
        if (entityOpt.isPresent()) {
            return entityOpt.get();
        }

        throw new BusinessException(BusinessKey.RCDC_TERMINALGROUP_GROUP_NOT_EXIST, groupId.toString());
    }
}
