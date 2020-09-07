package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruijie.rcos.rcdc.codec.adapter.base.sender.DefaultRequestMessageSender;
import com.ruijie.rcos.rcdc.terminal.module.def.PublicBusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.SyncTerminalLogoRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalLogoInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLogoService;
import com.ruijie.rcos.sk.base.concurrent.ThreadExecutor;
import com.ruijie.rcos.sk.base.concurrent.ThreadExecutors;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.i18n.LocaleI18nResolver;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    private static final ThreadExecutor NOTICE_HANDLER_THREAD_POOL =
            ThreadExecutors.newBuilder(TerminalLogoService.class.getName()).maxThreadNum(20).queueSize(50).build();

    @Override
    public void syncTerminalLogo(TerminalLogoInfo terminalLogoInfo, SendTerminalEventEnums name) throws BusinessException {
        LOGGER.info("向在线终端下发Logo名");
        NOTICE_HANDLER_THREAD_POOL.execute(() -> sendNewLogoNameToOnlineTerminal(terminalLogoInfo, name));

    }

    private void sendNewLogoNameToOnlineTerminal(TerminalLogoInfo terminalLogoInfo, SendTerminalEventEnums name) {
        List<String> onlineTerminalIdList = sessionManager.getOnlineTerminalId();
        if (CollectionUtils.isEmpty(onlineTerminalIdList)) {
            LOGGER.info("无在线终端");
            return;
        }

        for (String terminalId : onlineTerminalIdList) {
            SyncTerminalLogoRequest request = new SyncTerminalLogoRequest(terminalLogoInfo.getLogoPath(), terminalLogoInfo.getMd5());
            try {
                operateTerminal(terminalId, name, request, BusinessKey.RCDC_TERMINAL_OPERATE_ACTION_SEND_LOGO);
            } catch (Exception e) {
                LOGGER.error("send new logo name to terminal failed, terminalId[" + terminalId + "], " +
                        "logoInfo[" + JSON.toJSONString(terminalLogoInfo) + "]", e);
            }
        }

    }

    private void operateTerminal(String terminalId, SendTerminalEventEnums terminalEvent, Object data, String operateActionKey)
            throws BusinessException {
        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(terminalId);
        if (sender == null) {
            throw new BusinessException(PublicBusinessKey.RCDC_TERMINAL_OFFLINE);
        }
        LOGGER.info("rcdc发送给终端的logo信息：{}", JSON.toJSONString(data));
        Message message = new Message(Constants.SYSTEM_TYPE, terminalEvent.getName(), data);
        try {
            sender.request(message);
        } catch (Exception e) {
            LOGGER.error("发送消息给终端[" + terminalId + "]失败", e);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OPERATE_MSG_SEND_FAIL, e,
                    LocaleI18nResolver.resolve(operateActionKey));
        }

    }

    @Override
    public TerminalLogoInfo getTerminalLogoInfo() {
        String logoInfo = globalParameterAPI.findParameter(TERMINAL_LOGO);
        if (StringUtils.isEmpty(logoInfo)) {
            // 如果全局表没有保存Logo信息，则返回空
            return new TerminalLogoInfo();
        }
        TerminalLogoInfo terminalLogoInfo = JSONObject.parseObject(logoInfo, TerminalLogoInfo.class);

        return terminalLogoInfo;
    }

}
