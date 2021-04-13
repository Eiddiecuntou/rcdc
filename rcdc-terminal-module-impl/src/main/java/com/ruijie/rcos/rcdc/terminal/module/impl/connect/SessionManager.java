package com.ruijie.rcos.rcdc.terminal.module.impl.connect;

import com.google.common.collect.Maps;
import com.ruijie.rcos.rcdc.codec.adapter.base.sender.DefaultRequestMessageSender;
import com.ruijie.rcos.rcdc.terminal.module.def.PublicBusinessKey;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.connectkit.api.tcp.session.Session;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
    private static final Map<String, Session> SESSION_ALIAS_MAP = new ConcurrentHashMap<>();

    /**
     * key 为terminalId,value为sessionId
     */
    private static final Map<String, String> TERMINALID_SESSIONID_MAPPING_MAP = new ConcurrentHashMap<>();

    /**
     * key 为sessionId,value为terminalId
     */
    private static final Map<String, String> SESSIONID_TERMINALID_MAPPING_MAP = new ConcurrentHashMap<>();

    private Map<String, Lock> sessionOperateLockMap = Maps.newConcurrentMap();

    /**
     * 绑定终端连接Session
     *
     * @param terminalId 终端id
     * @param session 要绑定的Session
     */
    public void bindSession(String terminalId,  Session session) {
        Assert.hasText(terminalId, "terminalId不能为空");
        Assert.notNull(session, "Session 不能为null");
        try {
            getTerminalIdLock(terminalId).lock();
            session.setSessionAlias(terminalId);
            SESSION_ALIAS_MAP.put(terminalId, session);
            SESSIONID_TERMINALID_MAPPING_MAP.put(session.getId(), terminalId);
            TERMINALID_SESSIONID_MAPPING_MAP.put(terminalId, session.getId());
            LOGGER.info("绑定终端session，terminalId={};当前在线终端数量为：{}", terminalId, SESSION_ALIAS_MAP.size());
        } finally {
            getTerminalIdLock(terminalId).unlock();
        }
    }

    /**
     * 移除终端绑定的Session
     *
     * @param sessionId 绑定的SessionId
     * @return true 移除成功
     */
    public boolean removeSession(String sessionId) {
        Assert.hasText(sessionId, "sessionId can not null");

        String terminalId = SESSIONID_TERMINALID_MAPPING_MAP.remove(sessionId);
        if (StringUtils.isEmpty(terminalId)) {
            LOGGER.info("session未绑定，跳过");
            return false;
        }

        try {
            getTerminalIdLock(terminalId).lock();
            String bindSessionId = TERMINALID_SESSIONID_MAPPING_MAP.get(terminalId);
            if (!sessionId.equals(bindSessionId)) {
                LOGGER.info("终端已绑定其他连接，sessionId : {}", bindSessionId);
                return false;
            }

            TERMINALID_SESSIONID_MAPPING_MAP.remove(terminalId);
            Session session = SESSION_ALIAS_MAP.remove(terminalId);
            if (session == null) {
                LOGGER.info("关闭前一次连接的session，不移除当前绑定的terminalId={} session;当前在线终端数量为：{}", terminalId,
                        SESSION_ALIAS_MAP.size());
                return false;
            }

            LOGGER.info("移除终端session，terminalId={};当前在线终端数量为：{}", terminalId, SESSION_ALIAS_MAP.size());
            return true;
        } finally {
            getTerminalIdLock(terminalId).unlock();
        }

    }

    /**
     * 获取终端Session
     *
     * @param terminalId 终端id
     * @return 返回Session
     */
    public Session getSessionByAlias(String terminalId) {
        Assert.hasText(terminalId, "terminalId不能为空");
        Session session = SESSION_ALIAS_MAP.get(terminalId);
        return session;
    }

    /**
     * 获取终端Session
     *
     * @param sessionId sessionId
     * @return 返回Session
     */
    public Session getSessionById(String sessionId) {
        Assert.hasText(sessionId, "sessionId");

        String terminalId = SESSIONID_TERMINALID_MAPPING_MAP.get(sessionId);
        if (StringUtils.isEmpty(terminalId)) {
            // 未绑定
            return null;
        }

        Session session = SESSION_ALIAS_MAP.get(terminalId);
        if (sessionId.equals(session.getId())) {
            return session;
        }

        // 查找不到符合的session
        return null;
    }

    /**
     * 获取连接通道的发送对象
     *
     * @param terminalId 终端id
     * @return 返回封装的通道对象
     * @throws BusinessException 业务异常
     */
    public DefaultRequestMessageSender getRequestMessageSender(String terminalId) throws BusinessException {
        Assert.hasText(terminalId, "terminalId不能为空");
        Session session = getSessionByAlias(terminalId);
        if (session == null) {
            LOGGER.error("获取终端session失败，terminalId:{}", terminalId);
            throw new BusinessException(PublicBusinessKey.RCDC_TERMINAL_OFFLINE);
        }
        return new DefaultRequestMessageSender(session);
    }

    /**
     * 获取在线终端id
     *
     * @return 返回在线终端id列表
     */
    public List<String> getOnlineTerminalId() {
        List<String> terminalIdList = new ArrayList<>();
        SESSION_ALIAS_MAP.forEach((k, v) -> terminalIdList.add(k));
        LOGGER.debug("当前在线终端数量:{},返回的终端在线数量：{}", SESSION_ALIAS_MAP.size(), terminalIdList.size());
        return terminalIdList;
    }

    /**
     *  根据sessionId获取绑定的终端id
     *
     * @param sessionId sessionId
     * @return 终端id
     */
    public String getTerminalIdBySessionId(String sessionId) {
        return SESSIONID_TERMINALID_MAPPING_MAP.get(sessionId);
    }

    private synchronized Lock getTerminalIdLock(String terminalId) {
        Lock lock = sessionOperateLockMap.get(terminalId);
        if (lock == null) {
            lock = new ReentrantLock();
            sessionOperateLockMap.put(terminalId, lock);
        }
        return lock;
    }
}
