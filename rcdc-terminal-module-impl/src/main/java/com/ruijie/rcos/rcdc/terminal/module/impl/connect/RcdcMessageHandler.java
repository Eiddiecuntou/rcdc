package com.ruijie.rcos.rcdc.terminal.module.impl.connect;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.codec.adapter.base.handler.MessageHandler;
import com.ruijie.rcos.rcdc.codec.adapter.base.sender.ResponseMessageSender;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.codec.adapter.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.SyncServerTimeResponse;
import com.ruijie.rcos.sk.base.concurrent.ThreadExecutors;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.message.base.BaseMessage;
import com.ruijie.rcos.sk.connectkit.api.tcp.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.List;
import java.util.concurrent.ExecutorService;


/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/8/24
 *
 * @author hs
 */
public class RcdcMessageHandler implements MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RcdcMessageHandler.class);

    @Autowired
    private CbbDispatcherHandlerSPI cbbDispatcherHandlerSPI;

    @Autowired
    private SessionManager sessionManager;

    /**
     * 接收报文处理线程池,分配50个线程数
     */
    private static final ExecutorService MESSAGE_HANDLER_THREAD_POOL =
            ThreadExecutors.newBuilder("messageHandleThread").maxThreadNum(80).queueSize(1000).build();

    private static final ExecutorService NON_BUSINESS_HANDLER_THREAD_POOL =
            ThreadExecutors.newBuilder("noBusinessMessageHandleThread").maxThreadNum(80).queueSize(1000).build();

    private static final List<String> NON_BUSINESS_ACTION_LIST = Lists.newArrayList(ShineAction.HEARTBEAT, ShineAction.SYNC_SERVER_TIME);

    @Override
    public void onReceive(ResponseMessageSender sender, BaseMessage message) {
        Assert.notNull(sender, "ResponseMessageSender不能为null");
        Assert.notNull(message, "BaseMessage不能为null");

        if (NON_BUSINESS_ACTION_LIST.contains(message.getAction())) {
            NON_BUSINESS_HANDLER_THREAD_POOL.execute(() -> handleNonBusinessMessage(sender, message));
            return;
        }

        // 使用线程池处理接收到的报文
        MESSAGE_HANDLER_THREAD_POOL.execute(() -> handleMessage(sender, message));
    }

    /**
     * 处理接收到的报文
     *
     * @param sender 连接通道对象
     * @param message 报文对象
     */
    private void handleMessage(ResponseMessageSender sender, BaseMessage message) {

        // 检查session是否已绑定终端，
        if (!hasBindSession(sender.getSession(), message.getAction())) {
            LOGGER.warn("终端未绑定session，不处理报文。action：{};data:{}", message.getAction(), String.valueOf(message.getData()));
            return;
        }

        LOGGER.info("接收到的报文：action:{};data:{}", message.getAction(), String.valueOf(message.getData()));
        boolean isNewConnection = false;
        // 处理升级报文，获取terminalId绑定终端
        if (ShineAction.CHECK_UPGRADE.equals(message.getAction())) {
            String terminalId = parseTerminalInfo(message.getData());
            // 判断终端是否是新上线
            isNewConnection = isNewConnection(terminalId, sender.getSession());
            // 绑定终端
            bindSession(sender, terminalId);
        }
        // 消息分发
        dispatchMessage(sender, message, isNewConnection);
    }

    private boolean handleNonBusinessMessage(ResponseMessageSender sender, BaseMessage message) {
        // 收到心跳报文，直接应答
        if (ShineAction.HEARTBEAT.equals(message.getAction())) {
            sender.response(new Message(Constants.SYSTEM_TYPE, ShineAction.HEARTBEAT, null));
            return true;
        }

        // 同步服务器时间，直接应答
        if (ShineAction.SYNC_SERVER_TIME.equals(message.getAction())) {
            SyncServerTimeResponse syncServerTimeResponse = SyncServerTimeResponse.build();
            LOGGER.debug("同步服务器时间，{}", JSON.toJSONString(syncServerTimeResponse));
            sender.response(new Message(Constants.SYSTEM_TYPE, ShineAction.SYNC_SERVER_TIME, syncServerTimeResponse));
            return true;
        }

        return false;
    }

    /**
     * 判断终端是否是新上线
     *
     * @param terminalId terminalId
     * @param session session
     * @return 判断结果
     */
    private boolean isNewConnection(String terminalId, Session session) {
        Session latestSession = sessionManager.getSessionByAlias(terminalId);
        return latestSession != session;

    }

    private String parseTerminalInfo(Object message) {
        Assert.notNull(message, "终端信息不能为空");
        String data = String.valueOf(message);
        CbbShineTerminalBasicInfo basicInfo;
        try {
            basicInfo = JSON.parseObject(data, CbbShineTerminalBasicInfo.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("接收到的报文格式错误;data:" + data, e);
        }
        return basicInfo.getTerminalId();
    }

    /**
     * 处理终端请求的第一个报文,解析出terminalId值
     * 设置terminalId，绑定连接session,
     * 后续报文请求都是基于已绑定terminalId的连接
     */
    private void bindSession(ResponseMessageSender sender, String terminalId) {
        Assert.hasText(terminalId, "绑定终端连接session的terminalId不能为空");
        Session session = sender.getSession();
        LOGGER.info("开始绑定终端，终端id:{},session:{}", terminalId, JSON.toJSONString(session));
        session.setSessionAlias(terminalId);
        sessionManager.bindSession(terminalId, session);
    }

    /**
     * 执行消息分发
     */
    private void dispatchMessage(ResponseMessageSender sender, BaseMessage message, boolean isNewConnection) {
        String terminalId = getTerminalIdFromSession(sender.getSession());
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        request.setDispatcherKey(message.getAction());
        request.setRequestId(sender.getResponseId());
        request.setTerminalId(terminalId);
        request.setNewConnection(isNewConnection);
        String data = message.getData() == null ? null : String.valueOf(message.getData());
        request.setData(data);

        try {
            LOGGER.info("分发消息，terminalId:{}; action: {}; data:{}", terminalId, message.getAction(), data);
            cbbDispatcherHandlerSPI.dispatch(request);
        } catch (Exception e) {
            LOGGER.error("消息分发执行异常;action:" + message.getAction() + ",terminalId:" + terminalId + ",data:" + message.getData(), e);
        }
    }

    private boolean hasBindSession(Session session, String action) {
        Assert.notNull(session, "Session为null,连接异常");
        if (ShineAction.CHECK_UPGRADE.equals(action)) {
            // 升级报文不做session绑定判断
            return true;
        }
        return session.getSessionAlias() != null;
    }

    private String getTerminalIdFromSession(Session session) {
        String terminalId = session.getSessionAlias();
        Assert.notNull(terminalId, "session 未绑定终端");
        return terminalId;
    }
}
