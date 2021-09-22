package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalAuthorizationWhitelistDao;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalAuthorizationWhitelistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Description:
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/9/16
 *
 * @author zhangsiming
 */
@Service
public class TerminalAuthorizationWhitelistServiceImpl implements TerminalAuthorizationWhitelistService {



    @Autowired
    private TerminalAuthorizationWhitelistDao terminalAuthorizationWhitelistDao;

    @Autowired
    private TerminalBasicInfoDAO terminalBasicInfoDAO;

    @Override
    public Boolean isOCSFreeAuthorization(String terminalId) {
        Assert.notNull(terminalId, "terminal id is null");
        String ocsSn = terminalBasicInfoDAO.getOcsSnByTerminalId(terminalId);
        return !StringUtils.isEmpty(ocsSn);
    }

    @Override
    public boolean checkWhiteList(CbbShineTerminalBasicInfo terminalBasicInfo) {
        return false;
    }
}
