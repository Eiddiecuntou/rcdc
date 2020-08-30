package com.ruijie.rcos.rcdc.terminal.module.impl.connect;

import com.ruijie.rcos.rcdc.codec.compatible.base.sender.DefaultRequestMessageSender;
import com.ruijie.rcos.rcdc.terminal.module.def.PublicBusinessKey;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.connectkit.api.tcp.session.Session;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionManager.class);

    /**
     * key 为terminalId,value为Session
     */
    private static final Map<String, Session> SESSION_ALIAS_MAP = new ConcurrentHashMap<>();

    /**
     * key 为sessionId,value为Session
     */
    private static final Map<String, Session> SESSION_MAP = new ConcurrentHashMap<>();

    /**
     * 绑定终端连接Session
     *
     * @param terminalId 终端id
     * @param session 要绑定的Session
     */
    public void bindSession(String terminalId,  Session session) {
        Assert.hasText(terminalId, "terminalId不能为空");
        Assert.notNull(session, "Session 不能为null");
        session.setSessionAlias(terminalId);
        SESSION_ALIAS_MAP.put(terminalId, session);
        SESSION_MAP.put(session.getId(), session);
        LOGGER.info("绑定终端session，terminalId={};当前在线终端数量为：{}", terminalId, SESSION_MAP.size());
    }

    /**
     * 移除终端绑定的Session
     *
     * @param terminalId 终端id
     * @param session 绑定的Session
     * @return true 移除成功
     */
    public boolean removeSession(String terminalId,  Session session) {
        Assert.hasText(terminalId, "terminalId不能为空");
        Assert.notNull(session, "session can not null");
        SESSION_MAP.remove(session.getId());
        boolean isSuccess = SESSION_ALIAS_MAP.remove(terminalId, session);
        if (isSuccess) {
            LOGGER.info("移除终端session，terminalId={};ip={},当前在线终端数量为：{}", terminalId, SESSION_MAP.size());
            return true;
        }
        LOGGER.info("关闭前一次连接的session，不移除当前绑定的terminalId={};ip={}的session;当前在线终端数量为：{}", terminalId,
                SESSION_MAP.size());
        return false;
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
        Session session = SESSION_MAP.get(sessionId);
        return session;
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
        SESSION_MAP.forEach((k, v) -> terminalIdList.add(k));
        LOGGER.debug("当前在线终端数量:{},返回的终端在线数量：{}", SESSION_MAP.size(), terminalIdList.size());
        return terminalIdList;
    }

}
