package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalOperatorService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.util.Assert;
import com.ruijie.rcos.sk.commkit.base.callback.RequestCallback;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.message.base.BaseMessage;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultRequestMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description: 终端操作
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/31
 *
 * @author Jarman
 */
@Service
public class TerminalOperatorServiceImpl implements TerminalOperatorService {


    @Autowired
    private SessionManager sessionManager;

    @Override
    public void shutdown(String terminalId) throws BusinessException {
        Assert.hasLength(terminalId, "terminalId 不能为空");
        operateTerminal(terminalId, SendTerminalEventEnums.SHUTDOWN_TERMINAL, "");
    }

    @Override
    public void restart(String terminalId) throws BusinessException {
        Assert.hasLength(terminalId, "terminalId 不能为空");
        operateTerminal(terminalId, SendTerminalEventEnums.RESTART_TERMINAL, "");
    }

    @Override
    public void changePassword(String terminalId, String password) throws BusinessException {
        Assert.hasLength(terminalId, "terminalId 不能为空");
        Assert.hasLength(password, "password 不能为空");
        operateTerminal(terminalId, SendTerminalEventEnums.CHANGE_TERMINAL_PASSWORD, password);
    }

    private void operateTerminal(String terminalId, SendTerminalEventEnums terminalEvent, String data)
            throws BusinessException {
        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(terminalId);
        if (sender == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OFFLINE);
        }

        Message message = new Message(Constants.SYSTEM_TYPE, terminalEvent.getName(), data);
        sender.request(message);
    }

    @Override
    public String gatherLog(String terminalId) throws BusinessException {
        Assert.hasLength(terminalId,"terminalId不能为空");
        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(terminalId);
        Message message = new Message(Constants.SYSTEM_TYPE, SendTerminalEventEnums.GARTHER_TERMINAL_LOG.getName(), "");
        sender.asyncRequest(message, new RequestCallback() {
            @Override
            public void success(BaseMessage msg) {
                Assert.notNull(msg, "收集日志返回消息不能为null");
                Assert.notNull(msg.getData(), "收集日志返回报文消息体不能为null");
                String logZipName = ((String) msg.getData()).trim();
                Assert.hasLength(logZipName, "返回的日志文件名称不能为空");
                //TODO

            }

            @Override
            public void timeout(Throwable throwable) {

            }

            @Override
            public boolean isTimeout(long l) {
                return false;
            }

            @Override
            public long getCreateTime() {
                return 0;
            }
        });
        //TODO 日志文件名
        return "";
    }

    @Override
    public void detect(String terminalId) throws BusinessException {
        Assert.hasLength(terminalId,"terminalId不能为空");

        //TODO
    }
}
