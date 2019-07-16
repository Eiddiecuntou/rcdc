package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.checker;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
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
     * @param groupEntity 父分组实体对象
     * @param groupName 分组名称
     * @throws BusinessException 业务异常
     */
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
