package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.GatherLogCache;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.GatherLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.DetectStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.GatherLogStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalOperatorService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalDetectService;
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
    private GatherLogCacheManager gatherLogCacheManager;

    @Autowired
    private TerminalBasicInfoDAO basicInfoDAO;

    @Autowired
    private TerminalDetectService terminalDetectService;

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
        Assert.hasText(terminalId, "terminalId不能为空");
        GatherLogCache gatherLogCache = gatherLogCacheManager.getCache(terminalId);
        if (gatherLogCache == null) {
            gatherLogCache = gatherLogCacheManager.addCache(terminalId);
        }
        // 正在收集中,不允许重复执行
        if (GatherLogStateEnums.DOING == gatherLogCache.getState()) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_GATHER_LOG_DOING);
        }

        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(terminalId);
        Message message = new Message(Constants.SYSTEM_TYPE, SendTerminalEventEnums.GATHER_TERMINAL_LOG.getName(), "");
        // 发消息给shine，执行日志收集，异步等待日志收集结果
        sender.asyncRequest(message, new GatherLogRequestCallbackImpl(gatherLogCacheManager, terminalId));
    }
    
    @Override
    public void detect(String[] terminalIdArr) throws BusinessException {
        Assert.notEmpty(terminalIdArr, "terminalIdArr大小不能为0");
        for (String terminalId : terminalIdArr) {
            detect(terminalId);
        }
    }

    @Override
    public void detect(String terminalId) throws BusinessException {
        Assert.hasText(terminalId, "terminalId不能为空");
        
        // 当天是否含有该终端检测记录，若有且检测已完成，重新开始检测，正在检测则忽略
        TerminalDetectionEntity detection = terminalDetectService.findInCurrentDate(terminalId);
        if (detection == null) {
            terminalDetectService.save(terminalId);
            sendDetectRequest(terminalId);
            return;
        }

        if (detection.getDetectState() == DetectStateEnums.CHECKING) {
            return;
        }

        // 删除原记录，重新添加检测记录
        terminalDetectService.delete(detection.getId());
        terminalDetectService.save(terminalId);
        sendDetectRequest(terminalId);
    }

    private void sendDetectRequest(String terminalId) throws BusinessException {
        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(terminalId);
        Message message = new Message(Constants.SYSTEM_TYPE, SendTerminalEventEnums.DETECT_TERMINAL.getName(), "");
        sender.request(message);
    }
}
