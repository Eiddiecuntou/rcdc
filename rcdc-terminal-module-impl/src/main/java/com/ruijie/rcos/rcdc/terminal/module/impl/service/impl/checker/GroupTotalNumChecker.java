package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.checker;

import java.util.UUID;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.TerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalGroupDAO;
import com.ruijie.rcos.sk.base.exception.BusinessException;

/**
 * Description: 分组总数校验器
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/7/11
 *
 * @author nt
 */
@Service
public class GroupTotalNumChecker {

    @Autowired
    private TerminalGroupDAO terminalGroupDAO;

    public void check(TerminalTypeEnums terminalType, long addNum) throws BusinessException {
        long count = terminalGroupDAO.countByTerminalType(terminalType);
        if ((count + addNum) > Constants.TERMINAL_GROUP_MAX_GROUP_NUM) {
            throw new BusinessException(BusinessKey.RCDC_TERMINALGROUP_GROUP_NUM_EXCEED_LIMIT,
                    String.valueOf(Constants.TERMINAL_GROUP_MAX_GROUP_NUM));
        }
    }
}
