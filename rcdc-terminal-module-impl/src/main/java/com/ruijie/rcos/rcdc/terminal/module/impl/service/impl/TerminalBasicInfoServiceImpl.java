package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.CbbTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineNetworkConfig;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.util.Assert;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultRequestMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/31
 *
 * @author Jarman
 */
@Service
public class TerminalBasicInfoServiceImpl implements TerminalBasicInfoService {
    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private TerminalBasicInfoDAO basicInfoDAO;

    @Override
    public void modifyTerminalName(String terminalId, String terminalName) throws BusinessException {
        Assert.hasLength(terminalId, "terminalId 不能为空");
        Assert.notNull(terminalName, "terminalName 不能为空");
        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(terminalId);
        if (sender == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OFFLINE);
        }

        Message message = new Message(Constants.SYSTEM_TYPE,
                SendTerminalEventEnums.MODIFY_TERMINAL_NETWORK_CONFIG.getName(), terminalName);
        sender.request(message);
    }

    @Override
    public void modifyTerminalNetworkConfig(String terminalId, ShineNetworkConfig shineNetworkConfig)
            throws BusinessException {
        Assert.hasLength(terminalId, "terminalId 不能为空");
        Assert.notNull(shineNetworkConfig, "ShineNetworkConfig 不能为null");
        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(terminalId);
        if (sender == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OFFLINE);
        }

        Message message = new Message(Constants.SYSTEM_TYPE,
                SendTerminalEventEnums.MODIFY_TERMINAL_NETWORK_CONFIG.getName(), shineNetworkConfig);
        sender.request(message);
    }

    @Override
    public void modifyTerminalState(String terminalId, CbbTerminalStateEnums state) throws BusinessException {
        Assert.hasLength(terminalId, "terminalId 不能为空");
        Assert.notNull(state, "CbbTerminalStateEnums 不能为空");
        CbbTerminalEntity basicInfoEntity = basicInfoDAO.findTerminalBasicInfoEntitiesByTerminalId(terminalId);
        if (basicInfoEntity == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL);
        }

        int effectRow = basicInfoDAO.modifyTerminalState(terminalId, basicInfoEntity.getVersion(), state.ordinal());
        if (effectRow == 0) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL);
        }
    }
}
