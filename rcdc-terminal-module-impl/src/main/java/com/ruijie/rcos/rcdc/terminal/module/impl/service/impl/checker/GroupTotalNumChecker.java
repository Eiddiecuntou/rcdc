package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.checker;

import java.util.UUID;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.TerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalGroupDAO;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import org.springframework.util.Assert;

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

    /**
     *  终端分组总数校验
     * @param terminalType 终端类型
     * @param addNum 添加终端数量
     * @throws BusinessException 业务异常
     */
    public void check(TerminalTypeEnums terminalType, long addNum) throws BusinessException {
        Assert.notNull(terminalType, "terminal type can not be null");

        long count = terminalGroupDAO.countByTerminalType(terminalType);
        if ((count + addNum) > Constants.TERMINAL_GROUP_MAX_GROUP_NUM) {
            throw new BusinessException(BusinessKey.RCDC_TERMINALGROUP_GROUP_NUM_EXCEED_LIMIT,
                    String.valueOf(Constants.TERMINAL_GROUP_MAX_GROUP_NUM));
        }
    }
}
