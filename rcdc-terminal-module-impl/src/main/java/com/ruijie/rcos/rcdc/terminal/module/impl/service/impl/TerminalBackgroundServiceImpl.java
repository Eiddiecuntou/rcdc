package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.codec.adapter.base.sender.DefaultRequestMessageSender;
import com.ruijie.rcos.rcdc.terminal.module.def.PublicBusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalBackgroundInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBackgroundService;
import com.ruijie.rcos.sk.base.concurrent.ThreadExecutor;
import com.ruijie.rcos.sk.base.concurrent.ThreadExecutors;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.i18n.LocaleI18nResolver;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;


/**
 * Description: 终端背景业务类
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/11/6
 *
 * @author songxiang
 */
@Service
public class TerminalBackgroundServiceImpl implements TerminalBackgroundService {


    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalBackgroundServiceImpl.class);

    @Autowired
    private SessionManager sessionManager;

    private static final ThreadExecutor NOTICE_HANDLER_THREAD_POOL =
            ThreadExecutors.newBuilder(TerminalBackgroundService.class.getName()).maxThreadNum(20).queueSize(50).build();

    @Override
    public void syncTerminalBackground(TerminalBackgroundInfo terminalSyncBackgroundInfo) throws BusinessException {
        Assert.notNull(terminalSyncBackgroundInfo, "terminalSyncBackgroundInfo must not be null");
        LOGGER.info("向在线终端下发背景桌面");
        NOTICE_HANDLER_THREAD_POOL.execute(() -> sendNewBackgroundNameToOnlineTerminal(terminalSyncBackgroundInfo));
    }



    private void sendNewBackgroundNameToOnlineTerminal(TerminalBackgroundInfo backgroundInfo) {
        List<String> onlineTerminalIdList = sessionManager.getOnlineTerminalId();
        if (CollectionUtils.isEmpty(onlineTerminalIdList)) {
            LOGGER.info("下发终端背景，找不到在线终端");
            return;
        }
        for (String terminalId : onlineTerminalIdList) {

            try {
                operateTerminal(terminalId, backgroundInfo);
            } catch (Exception e) {
                LOGGER.error("向终端发送背景图错误, terminalId[" + terminalId + "]", e);
            }
        }

    }

    private void operateTerminal(String terminalId, TerminalBackgroundInfo request) throws BusinessException {
        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(terminalId);
        if (sender == null) {
            throw new BusinessException(PublicBusinessKey.RCDC_TERMINAL_OFFLINE);
        }
        Message message = new Message<>(Constants.SYSTEM_TYPE, SendTerminalEventEnums.CHANGE_TERMINAL_BACKGROUND.getName(), request);
        try {
            sender.request(message);
        } catch (Exception e) {
            LOGGER.error("发送消息给终端[" + terminalId + "]失败", e);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OPERATE_MSG_SEND_FAIL, e,
                    LocaleI18nResolver.resolve(BusinessKey.RCDC_TERMINAL_OPERATE_ACTION_SEND_BACKGROUND));
        }
    }
}
