package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbDetectStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.GatherLogCache;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.GatherLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalBasicInfoEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.GatherLogStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalOperatorService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.util.Assert;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultRequestMessageSender;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import java.util.Date;

/**
 * Description: 终端操作
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/31
 *
 * @author Jarman
 */
@Service
public class TerminalOperatorServiceImpl implements TerminalOperatorService, ApplicationContextAware {

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private GatherLogCacheManager gatherLogCacheManager;

    @Autowired
    private TerminalBasicInfoDAO basicInfoDAO;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

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
            gatherLogCache = gatherLogCacheManager.addCache(terminalId);
        }
        // 正在收集中,不允许重复执行
        if (GatherLogStateEnums.DOING == gatherLogCache.getState()) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_GATHER_LOG_DOING);
        }

        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(terminalId);
        Message message = new Message(Constants.SYSTEM_TYPE, SendTerminalEventEnums.GATHER_TERMINAL_LOG.getName(), "");
        // 发消息给shine，执行日志收集，异步等待日志收集结果
        sender.asyncRequest(message, applicationContext.getBean(GatherLogRequestCallbackImpl.class, terminalId));
    }

    @Override
    public void detect(String terminalId) throws BusinessException {
        Assert.hasLength(terminalId, "terminalId不能为空");
        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(terminalId);
        Message message = new Message(Constants.SYSTEM_TYPE, SendTerminalEventEnums.DETECT_TERMINAL.getName(), "");
        sender.request(message);
        // 更新检测状态未正在检测中
        // FIXME 当RCDC服务异常退出后，存在状态无法更新的情况，所以需要在RCDC初始化的时候把检测状态为正在检测的终端更新为检测失败
        TerminalBasicInfoEntity basicInfoEntity = basicInfoDAO.findTerminalBasicInfoEntitiesByTerminalId(terminalId);
        basicInfoDAO.modifyDetectInfo(terminalId, basicInfoEntity.getVersion(), new Date(),
                CbbDetectStateEnums.DOING.ordinal());
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
