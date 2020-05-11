package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.checker;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.ruijie.rcos.rcdc.terminal.module.def.PublicBusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalGroupDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalGroupEntity;
import com.ruijie.rcos.sk.base.exception.BusinessException;

/**
 * Description: 分组名称重复校验器
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/7/11
 *
 * @author nt
 */
@Service
public class GroupNameDuplicationChecker {

    @Autowired
    private TerminalGroupDAO terminalGroupDAO;

    /**
     *  分组重名校验
     *
     * @param moveGroupEntity 待移动分组实体对象
     * @param deleteGroupEntity 待删除分组实体对象
     * @param groupName 分组名称
     * @throws BusinessException 业务异常
     */
    public void check(TerminalGroupEntity deleteGroupEntity, TerminalGroupEntity moveGroupEntity, String groupName) throws BusinessException {
        Assert.notNull(deleteGroupEntity, "deleteGroupEntity can not be null");
        Assert.notNull(moveGroupEntity, "moveGroupEntity can not be null");
        Assert.hasText(groupName, "groupName can not be blank");

        List<TerminalGroupEntity> findGroupList = terminalGroupDAO
                .findByParentIdAndName(moveGroupEntity.getId(), groupName);
        //排除待删除的分组本身
        findGroupList = findGroupList.stream().filter(item -> item.getId() != deleteGroupEntity.getId()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(findGroupList)) {
            return;
        }

        throw new BusinessException(PublicBusinessKey.RCDC_DELETE_TERMINAL_GROUP_SUB_GROUP_HAS_DUPLICATION_WITH_MOVE_GROUP);
    }
}
