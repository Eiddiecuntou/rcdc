package com.ruijie.rcos.rcdc.terminal.module.impl.connect;

import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.util.Assert;
import com.ruijie.rcos.sk.commkit.base.Session;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultRequestMessageSender;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: 终端连接Session管理
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/24
 *
 * @author Jarman
 */
@Service
public class SessionManager {

    /**
     * key 为terminalId,value为Session
     */
    private static final Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    /**
     * 绑定终端连接Session
     *
     * @param terminalId
     * @param session
     */
    public void bindSession(String terminalId, Session session) {
        Assert.hasLength(terminalId, "terminalId不能为空");
        Assert.notNull(session, "Session 不能为null");
        sessionMap.put(terminalId, session);
    }

    public void removeSession(String terminalId) {
        Assert.hasLength(terminalId, "terminalId不能为空");
        sessionMap.remove(terminalId);
    }

    public Session getSession(String terminalId) {
        Assert.hasLength(terminalId, "terminalId不能为空");
        Session session = sessionMap.get(terminalId);
        return session;
    }

    public DefaultRequestMessageSender getRequestMessageSender(String terminalId) throws BusinessException {
        Assert.hasLength(terminalId, "terminalId不能为空");
        Session session = getSession(terminalId);
        if (session == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OFFLINE);
        }
        return new DefaultRequestMessageSender(session);
    }

}
