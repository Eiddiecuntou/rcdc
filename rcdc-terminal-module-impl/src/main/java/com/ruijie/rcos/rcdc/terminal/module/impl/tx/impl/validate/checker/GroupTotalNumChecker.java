package com.ruijie.rcos.rcdc.terminal.module.impl.tx.impl.validate.checker;

import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalGroupDAO;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

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

    public void check(UUID groupId, int addNum) throws BusinessException {

    }
}
