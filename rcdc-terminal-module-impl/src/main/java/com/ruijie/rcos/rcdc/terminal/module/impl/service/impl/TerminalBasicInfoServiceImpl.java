package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.sk.commkit.base.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ChangeHostNameRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineNetworkConfig;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultRequestMessageSender;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalBasicInfoServiceImpl.class);

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private TerminalBasicInfoDAO basicInfoDAO;

    private static final int FAIL_TRY_COUNT = 3;

    @Override
    public void modifyTerminalName(String terminalId, String terminalName) throws BusinessException {
        Assert.hasText(terminalId, "terminalId 不能为空");
        Assert.notNull(terminalName, "terminalName 不能为空");
        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(terminalId);
        if (sender == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OFFLINE);
        }
        ChangeHostNameRequest changeRequest = new ChangeHostNameRequest(terminalName);
        Message message = new Message(Constants.SYSTEM_TYPE,
                SendTerminalEventEnums.MODIFY_TERMINAL_NAME.getName(), changeRequest);
        sender.request(message);
    }

    @Override
    public void modifyTerminalNetworkConfig(String terminalId, ShineNetworkConfig shineNetworkConfig)
            throws BusinessException {
        Assert.hasText(terminalId, "terminalId 不能为空");
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
    public void modifyTerminalState(String terminalId, CbbTerminalStateEnums state) {
        Assert.hasText(terminalId, "terminalId 不能为空");
        Assert.notNull(state, "CbbTerminalStateEnums 不能为空");
        boolean isSuccess = updateTerminalState(terminalId, state);
        int count = 0;
        //失败，尝试3次
        while (!isSuccess && count++ < FAIL_TRY_COUNT) {
            LOGGER.error("开始第{}次修改终端状态，terminalId=[{}],需要修改状态为：[{}]", count, terminalId, state.name());
            isSuccess = updateTerminalState(terminalId, state);
        }
    }

    private boolean updateTerminalState(String terminalId, CbbTerminalStateEnums state) {
        TerminalEntity basicInfoEntity = basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
        if (basicInfoEntity == null) {
            LOGGER.error("不存在terminalId=[{}]的终端", terminalId);
            return false;
        }
        int effectRow = basicInfoDAO.modifyTerminalState(terminalId, basicInfoEntity.getVersion(), state);
        if (effectRow == 0) {
            LOGGER.error("修改终端状态失败(updateTerminalState)，terminalId=[{}],需要修改状态为：[{}]", terminalId, state.name());
            return false;
        }
        return true;
    }

    @Override
    public boolean isTerminalOnline(String terminalId) {
        Assert.hasText(terminalId,"terminalId can not empty");
        Session session = sessionManager.getSession(terminalId);
        return session == null ? false : true;
    }
}
