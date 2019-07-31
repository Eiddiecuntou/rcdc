package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.SyncTerminalLogoRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLogoService;
import com.ruijie.rcos.sk.base.concorrent.executor.SkyengineScheduledThreadPoolExecutor;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.i18n.LocaleI18nResolver;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultRequestMessageSender;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;


/**
 * Description:终端Logo操作
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/7/18
 *
 * @author hs
 */
@Service
public class TerminalLogoServiceImpl implements TerminalLogoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalLogoService.class);

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private GlobalParameterAPI globalParameterAPI;
    
    private static final String TERMINAL_LOGO = "terminalLogo";

    private static final SkyengineScheduledThreadPoolExecutor NOTICE_HANDLER_THREAD_POOL =
            new SkyengineScheduledThreadPoolExecutor(1, TerminalLogoService.class.getName());

    @Override
    public void syncTerminalLogo(String logoName, SendTerminalEventEnums name) throws BusinessException {
        Assert.hasText(logoName, "logoName不能为空");
        LOGGER.info("向在线终端下发Logo名");
        NOTICE_HANDLER_THREAD_POOL.execute(() -> sendNewLogoNameToOnlineTerminal(logoName, name));


    }

    private void sendNewLogoNameToOnlineTerminal(String logoName, SendTerminalEventEnums name) {
        List<String> onlineTerminalIdList = sessionManager.getOnlineTerminalId();
        LOGGER.info("logo名为:" + logoName);
        if (CollectionUtils.isEmpty(onlineTerminalIdList)) {
            LOGGER.info("无在线终端");
            return;
        }

        for (String terminalId : onlineTerminalIdList) {
            SyncTerminalLogoRequest request = new SyncTerminalLogoRequest(logoName);
            try {
                operateTerminal(terminalId, name, request,
                        BusinessKey.RCDC_TERMINAL_OPERATE_ACTION_SEND_LOGO);
            } catch (Exception e) {
                LOGGER.error("send new logo name to terminal failed, terminalId[" + terminalId + "], logoName["
                        + logoName + "]", e);
            }
        }

    }

    private void operateTerminal(String terminalId, SendTerminalEventEnums terminalEvent, Object data, String operateActionKey)
            throws BusinessException {
        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(terminalId);
        if (sender == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OFFLINE);
        }
        Message message = new Message(Constants.SYSTEM_TYPE, terminalEvent.getName(), data);
        try {
            sender.syncRequest(message);
        } catch (Exception e) {
            LOGGER.error("发送消息给终端[" + terminalId + "]失败", e);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OPERATE_MSG_SEND_FAIL, e,
                    new String[] {LocaleI18nResolver.resolve(operateActionKey, new String[]{})});
        }

    }

    @Override
    public String getTerminalLogoName() throws BusinessException {
        String logoPath = globalParameterAPI.findParameter(TERMINAL_LOGO);
        if (StringUtils.isBlank(logoPath)) {
            // 如果全局表没有保存Logo信息，则返回空
            return StringUtils.EMPTY;
        }
        String logoName = logoPath.substring(logoPath.lastIndexOf("/") + 1);
        return logoName;
    }

}
