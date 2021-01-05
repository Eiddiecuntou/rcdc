package com.ruijie.rcos.rcdc.terminal.module.impl.connector.tcp.server.impl;

import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.connector.tcp.server.SyncTerminalStartModeTcpServer;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/1/5
 *
 * @author jarman
 */
public class SyncTerminalStartModeTcpServerImpl implements SyncTerminalStartModeTcpServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncTerminalStartModeTcpServerImpl.class);

    @Autowired
    private TerminalBasicInfoDAO basicInfoDAO;

    @Override
    public String handle(String terminalId) throws BusinessException {
        Assert.hasText(terminalId, "terminalId can not be empty");
        TerminalEntity terminalEntity = basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
        if (terminalEntity == null) {
            LOGGER.error("终端[{}]不存在", terminalId);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL);
        }
        return terminalEntity.getStartMode();
    }
}
