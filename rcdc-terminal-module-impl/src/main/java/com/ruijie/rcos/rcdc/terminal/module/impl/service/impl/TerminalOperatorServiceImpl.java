package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.GatherLogCache;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.GatherLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.GatherLogStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.StateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.CommonResponseMsg;
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

    @Autowired
    private GatherLogCacheManager gatherLogCacheManager;

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
    public void gatherLog(final String terminalId) throws BusinessException {
        Assert.hasLength(terminalId, "terminalId不能为空");
        GatherLogCache gatherLogCache = gatherLogCacheManager.getCache(terminalId);
        if (gatherLogCache == null) {
            gatherLogCacheManager.addCache(terminalId);
        }
        //正在收集中,不允许重复执行
        if (GatherLogStateEnums.DOING == gatherLogCache.getState()) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_GATHER_LOG_DOING);
        }

        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(terminalId);
        Message message = new Message(Constants.SYSTEM_TYPE, SendTerminalEventEnums.GARTHER_TERMINAL_LOG.getName(), "");
        //发消息给shine，执行日志收集，异步等待日志收集结果
        sender.asyncRequest(message, new RequestCallback() {
            @Override
            public void success(BaseMessage msg) {
                Assert.notNull(msg, "收集日志返回消息不能为null");
                Assert.notNull(msg.getData(), "收集日志返回报文消息体不能为null");
                String data = ((String) msg.getData()).trim();
                Assert.hasLength(data, "返回的应答消息不能为空");
                CommonResponseMsg responseMsg = JSON.parseObject(data, CommonResponseMsg.class);
                Assert.notNull(responseMsg, "应答消息格式错误");
                if (StateEnums.SUCCESS == responseMsg.getErrorCode()) {
                    String logZipName = responseMsg.getMsg();
                    Assert.hasLength(logZipName, "返回的日志文件名称不能为空");
                    gatherLogCacheManager.updateState(terminalId, GatherLogStateEnums.DONE, logZipName);
                    return;
                }
                gatherLogCacheManager.updateState(terminalId, GatherLogStateEnums.FAILURE);
            }

            @Override
            public void timeout(Throwable throwable) {
                gatherLogCacheManager.updateState(terminalId, GatherLogStateEnums.FAILURE);
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
    }

    @Override
    public void detect(String terminalId) throws BusinessException {
        Assert.hasLength(terminalId, "terminalId不能为空");

        //TODO
    }
}
