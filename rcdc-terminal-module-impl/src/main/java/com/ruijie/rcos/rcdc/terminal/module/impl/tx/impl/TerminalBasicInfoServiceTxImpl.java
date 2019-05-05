package com.ruijie.rcos.rcdc.terminal.module.impl.tx.impl;

import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalDetectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalDetectionDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalBasicInfoServiceTx;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * 
 * Description: 终端基本信息存在事物的操作
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月31日
 * 
 * @author nt
 */
@Service
public class TerminalBasicInfoServiceTxImpl implements TerminalBasicInfoServiceTx {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalBasicInfoServiceTxImpl.class);

    @Autowired
    private TerminalDetectionDAO detectionDAO;

    @Autowired
    private TerminalBasicInfoDAO basicInfoDAO;

    @Autowired
    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;

    @Override
    public void deleteTerminal(String terminalId) throws BusinessException {
        Assert.hasText(terminalId, "terminalId can not be blank");

        LOGGER.warn("delete terminal basic info and detection info, terminalId[{}]", terminalId);
        deleteTerminalInfo(terminalId);
        deleteTerminalDetection(terminalId);
        deleteTerminalUpgradeRecord(terminalId);
    }

    private void deleteTerminalInfo(String terminalId) throws BusinessException {
        LOGGER.warn("delete terminal basic info, terminalId[{}]", terminalId);
        int effectRow = basicInfoDAO.deleteByTerminalId(terminalId);
        if (effectRow == 0) {
            LOGGER.error("delete terminal basic info fail, terminalId[{}]", terminalId);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL);
        }
    }

    private void deleteTerminalDetection(String terminalId) {
        LOGGER.warn("delete terminal detection info, terminalId[{}]", terminalId);
        detectionDAO.deleteByTerminalId(terminalId);
    }

    private void deleteTerminalUpgradeRecord(String terminalId) {
        LOGGER.warn("delete terminal upgrade info, terminalId[{}]", terminalId);
        systemUpgradeTerminalDAO.deleteByTerminalId(terminalId);
    }

}
