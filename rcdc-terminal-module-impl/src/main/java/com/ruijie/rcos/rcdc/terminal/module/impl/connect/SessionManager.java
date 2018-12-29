package com.ruijie.rcos.rcdc.terminal.module.impl.connect;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.commkit.base.Session;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultRequestMessageSender;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionManager.class);


    /**
     * key 为terminalId,value为Session
     */
    private static final Map<String, Session> SESSION_MAP = new ConcurrentHashMap<>();

    /**
     * 绑定终端连接Session
     *
     * @param terminalId 终端id
     * @param session 要绑定的Session
     */
    public void bindSession(String terminalId, Session session) {
        Assert.hasText(terminalId, "terminalId不能为空");
        Assert.notNull(session, "Session 不能为null");
        SESSION_MAP.put(terminalId, session);
        LOGGER.info("绑定终端session，terminalId={}",terminalId);
    }

    /**
     * 移除Session
     * @param terminalId 终端id
     */
    public void removeSession(String terminalId) {
        Assert.hasText(terminalId, "terminalId不能为空");
        SESSION_MAP.remove(terminalId);
        LOGGER.info("移除终端session，terminalId={}",terminalId);

    }

    /**
     * 获取终端Session
     * @param terminalId 终端id
     * @return 返回Session
     */
    public Session getSession(String terminalId) {
        Assert.hasText(terminalId, "terminalId不能为空");
        Session session = SESSION_MAP.get(terminalId);
        return session;
    }

    /**
     * 获取连接通道的发送对象
     * @param terminalId 终端id
     * @return 返回封装的通道对象
     * @throws BusinessException 业务异常
     */
    public DefaultRequestMessageSender getRequestMessageSender(String terminalId) throws BusinessException {
        Assert.hasText(terminalId, "terminalId不能为空");
        Session session = getSession(terminalId);
        if (session == null) {
            LOGGER.error("获取终端session失败，terminalId:{}",terminalId);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OFFLINE);
        }
        return new DefaultRequestMessageSender(session);
    }

}