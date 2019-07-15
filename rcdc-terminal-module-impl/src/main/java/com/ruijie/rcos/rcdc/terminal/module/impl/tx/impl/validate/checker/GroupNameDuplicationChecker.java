package com.ruijie.rcos.rcdc.terminal.module.impl.tx.impl.validate.checker;

import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalGroupDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalGroupEntity;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;

/**
 * Description: 分组名称重复校验器
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/7/11
 *
 * @author nt
 */
public class GroupNameDuplicationChecker {

    @Autowired
    private TerminalGroupDAO terminalGroupDAO;

    public void check(TerminalGroupEntity groupEntity, String groupName) throws BusinessException {
        Assert.notNull(groupEntity, "groupEntity can not be null");
        Assert.notNull(groupEntity.getTerminalType(), "group type can not be null");
        Assert.hasText(groupName, "groupName can not be blank");

        List<TerminalGroupEntity> findGroupList = terminalGroupDAO
                .findByTerminalTypeAndParentIdAndName(groupEntity.getTerminalType(), groupEntity.getId(), groupName);
        if (CollectionUtils.isEmpty(findGroupList)) {
            return;
        }

        throw new BusinessException(BusinessKey.RCDC_DELETE_TERMINAL_GROUP_SUB_GROUP_HAS_DUPLICATION_WITH_MOVE_GROUP);
    }
}
