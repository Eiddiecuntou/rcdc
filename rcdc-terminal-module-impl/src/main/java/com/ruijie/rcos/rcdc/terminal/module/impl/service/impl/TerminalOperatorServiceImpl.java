package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCache;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.CollectLogStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ChangeTerminalPasswordRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalOperatorService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultRequestMessageSender;

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
    private CollectLogCacheManager collectLogCacheManager;

    @Override
    public void shutdown(String terminalId) throws BusinessException {
        Assert.hasText(terminalId, "terminalId 不能为空");
        operateTerminal(terminalId, SendTerminalEventEnums.SHUTDOWN_TERMINAL, "");
    }

    @Override
    public void restart(String terminalId) throws BusinessException {
        Assert.hasText(terminalId, "terminalId 不能为空");
        operateTerminal(terminalId, SendTerminalEventEnums.RESTART_TERMINAL, "");
    }

    @Override
    public void changePassword(String terminalId, String password) throws BusinessException {
        Assert.hasText(terminalId, "terminalId 不能为空");
        Assert.hasText(password, "password 不能为空");
        ChangeTerminalPasswordRequest request = new ChangeTerminalPasswordRequest(password);
        operateTerminal(terminalId, SendTerminalEventEnums.CHANGE_TERMINAL_PASSWORD, request);
    }

    private void operateTerminal(String terminalId, SendTerminalEventEnums terminalEvent, Object data)
            throws BusinessException {
        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(terminalId);
        if (sender == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OFFLINE);
        }

        Message message = new Message(Constants.SYSTEM_TYPE, terminalEvent.getName(), data);
        sender.request(message);
    }

    @Override
    public void collectLog(final String terminalId) throws BusinessException {
        Assert.hasText(terminalId, "terminalId不能为空");
        CollectLogCache collectLogCache = collectLogCacheManager.getCache(terminalId);
        if (collectLogCache == null) {
            collectLogCache = collectLogCacheManager.addCache(terminalId);
        }
        // 正在收集中,不允许重复执行
        if (CollectLogStateEnums.DOING == collectLogCache.getState()) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_COLLECT_LOG_DOING);
        }

        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(terminalId);
        Message message = new Message(Constants.SYSTEM_TYPE, SendTerminalEventEnums.COLLECT_TERMINAL_LOG.getName(), "");
        // 发消息给shine，执行日志收集，异步等待日志收集结果
        sender.asyncRequest(message, new CollectLogRequestCallbackImpl(collectLogCacheManager, terminalId));
    }

    @Override
    public void detect(String terminalId) throws BusinessException {
        Assert.hasText(terminalId, "terminalId不能为空");
        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(terminalId);
        Message message = new Message(Constants.SYSTEM_TYPE, SendTerminalEventEnums.DETECT_TERMINAL.getName(), "");
        sender.request(message);
        // 更新检测状态为正在检测中
        // FIXME 当RCDC服务异常退出后，存在状态无法更新的情况，所以需要在RCDC初始化的时候把检测状态为正在检测的终端更新为检测失败
    }

    @Override
    public void detect(String[] terminalIdArr) throws BusinessException {
        Assert.notNull(terminalIdArr, "terminalIdArr不能为null");
        Assert.state(terminalIdArr.length > 0, "terminalIdArr大小不能为0");
        for (String terminalId : terminalIdArr) {
            detect(terminalId);
        }
    }
}
