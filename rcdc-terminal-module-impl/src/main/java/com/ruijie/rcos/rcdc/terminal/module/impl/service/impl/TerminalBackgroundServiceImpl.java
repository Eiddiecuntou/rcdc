package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBackgroundService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLogoService;
import com.ruijie.rcos.sk.base.concurrent.ThreadExecutor;
import com.ruijie.rcos.sk.base.concurrent.ThreadExecutors;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.i18n.LocaleI18nResolver;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultRequestMessageSender;
import com.ruijie.rcos.sk.modulekit.api.comm.NameRequest;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/11/6
 *
 * @author songxiang
 */
public class TerminalBackgroundServiceImpl implements TerminalBackgroundService {


    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalBackgroundServiceImpl.class);

    @Autowired
    private SessionManager sessionManager;

    private static final ThreadExecutor NOTICE_HANDLER_THREAD_POOL =
            ThreadExecutors.newBuilder(TerminalLogoService.class.getName()).maxThreadNum(1).queueSize(10).build();

    @Override
    public void syncTerminalLogo(String name) throws BusinessException {
        LOGGER.info("向在线终端下发背景桌面");
        NOTICE_HANDLER_THREAD_POOL.execute(() -> sendNewBackgroundNameToOnlineTerminal(name));
    }



    private void sendNewBackgroundNameToOnlineTerminal(String backgroundName) {
        List<String> onlineTerminalIdList = sessionManager.getOnlineTerminalId();
        if (CollectionUtils.isEmpty(onlineTerminalIdList)) {
            LOGGER.info("下发终端背景，找不到在线终端");
            return;
        }
        for (String terminalId : onlineTerminalIdList) {
            NameRequest request = new NameRequest(backgroundName);
            try {
                operateTerminal(terminalId, request);
            } catch (Exception e) {
                LOGGER.error("send new background name to terminal failed, terminalId[" + terminalId + "], backgroundName[" + backgroundName + "]",
                        e);
            }
        }

    }

    private void operateTerminal(String terminalId, NameRequest request) throws BusinessException {
        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(terminalId);
        if (sender == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OFFLINE);
        }
        Message message = new Message<>(Constants.SYSTEM_TYPE, SendTerminalEventEnums.CHANGE_TERMINAL_BACKGROUND.getName(), request);
        try {
            sender.syncRequest(message);
        } catch (Exception e) {
            LOGGER.error("发送消息给终端[" + terminalId + "]失败", e);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OPERATE_MSG_SEND_FAIL, e,
                    new String[] {LocaleI18nResolver.resolve(BusinessKey.RCDC_TERMINAL_OPERATE_ACTION_SEND_BACKGROUND, new String[] {})});
        }
    }
}
