package com.ruijie.rcos.rcdc.terminal.module.impl.tx.impl.validate.checker;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.TerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalGroupDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalGroupEntity;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.UUID;

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

    public long getSubGroupNum(TerminalTypeEnums terminalType, UUID id) {
        return terminalGroupDAO.countByTerminalTypeAndParentId(terminalType, id);
    }
}
