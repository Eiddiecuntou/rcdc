package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.checker;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.TerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalGroupDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalGroupEntity;
import com.ruijie.rcos.sk.base.exception.BusinessException;

/**
 * Description: 分组子分组数量校验器
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/7/11
 *
 * @author nt
 */
public class GroupSubNumChecker {

    @Autowired
    private TerminalGroupDAO terminalGroupDAO;

    /**
     *  终端子分组数量校验
     *
     * @param groupEntity 父分组实体对象
     * @param addSubGroupNum 添加的子分组数量
     * @throws BusinessException 业务异常
     */
    public void check(TerminalGroupEntity groupEntity, long addSubGroupNum) throws BusinessException {
        Assert.notNull(groupEntity, "groupEntity can not be null");

        TerminalTypeEnums terminalType = groupEntity.getTerminalType();
        long totalSubGroupNum =
                getSubGroupNum(terminalType, groupEntity.getId()) + addSubGroupNum;
        if (totalSubGroupNum > Constants.TERMINAL_GROUP_MAX_SUB_GROUP_NUM) {
            throw new BusinessException(BusinessKey.RCDC_TERMINALGROUP_SUB_GROUP_NUM_EXCEED_LIMIT,
                    String.valueOf(Constants.TERMINAL_GROUP_MAX_SUB_GROUP_NUM));
        }
    }

    /**
     *  获取子分组数量
     *
     * @param terminalType 终端类型
     * @param parentId 父分组ID
     * @return 子分组数量
     */
    public long getSubGroupNum(TerminalTypeEnums terminalType, @Nullable UUID parentId) {
        Assert.notNull(terminalType, "terminal type can not be null");

        return terminalGroupDAO.countByTerminalTypeAndParentId(terminalType, parentId);
    }
}
