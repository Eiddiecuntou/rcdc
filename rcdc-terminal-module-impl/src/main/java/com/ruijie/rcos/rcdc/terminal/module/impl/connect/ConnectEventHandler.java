package com.ruijie.rcos.rcdc.terminal.module.impl.connect;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.SyncServerTimeResponse;
import com.ruijie.rcos.sk.base.concorrent.executor.SkyengineScheduledThreadPoolExecutor;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.commkit.base.Session;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.message.base.BaseMessage;
import com.ruijie.rcos.sk.commkit.base.sender.RequestMessageSender;
import com.ruijie.rcos.sk.commkit.base.sender.ResponseMessageSender;
import com.ruijie.rcos.sk.commkit.server.AbstractServerMessageHandler;

/**
 * Description: 连接事件处理器
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/24
 *
 * @author Jarman
 */
@Service
public class ConnectEventHandler extends AbstractServerMessageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectEventHandler.class);

    @Autowired
    private CbbDispatcherHandlerSPI cbbDispatcherHandlerSPI;

    @Autowired
    private SessionManager sessionManager;

    /**
     * 接收报文处理线程池,分配50个线程数
     */
    private static final SkyengineScheduledThreadPoolExecutor MESSAGE_HANDLER_THREAD_POOL =
            new SkyengineScheduledThreadPoolExecutor(50, ConnectEventHandler.class.getName());

    /**
     * 绑定终端session的key
     */
    private static final String TERMINAL_BIND_KEY = "terminal_bind_session_key";

    @Override
    public void onReceive(ResponseMessageSender sender, BaseMessage message) {
        Assert.notNull(sender, "ResponseMessageSender不能为null");
        Assert.notNull(message, "BaseMessage不能为null");
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
        LOGGER.debug("接收到的报文：action:{};data:{}", message.getAction(), String.valueOf(message.getData()));
        // 处理非业务报文
        if (handleNonBusinessMessage(sender, message)) {
            return;
        }
        // 检查session是否已绑定终端，未绑定且不是升级报文则不处理报文
        if (!hasBindSession(sender.getSession(), message.getAction())) {
            LOGGER.warn("终端未绑定session，不处理报文。action：{};data:{}", message.getAction(), String.valueOf(message.getData()));
            return;
        }
        // 处理升级报文，获取terminalId绑定终端
        if (ShineAction.CHECK_UPGRADE.equals(message.getAction())) {
            LOGGER.debug("开始处理检查升级报文[{}]", ShineAction.CHECK_UPGRADE);
            // 绑定终端
            bindSession(sender, message.getData());
        }
        // 消息分发
        dispatchMessage(sender, message);
    }

    private boolean handleNonBusinessMessage(ResponseMessageSender sender, BaseMessage message) {
        // 收到心跳报文，直接应答
        if (ShineAction.HEARTBEAT.equals(message.getAction())) {
            sender.response(new Message(Constants.SYSTEM_TYPE, ShineAction.HEARTBEAT, null));
            return true;
        }
        // 同步服务器时间，直接应答
        if (ShineAction.SYNC_SERVER_TIME.equals(message.getAction())) {
            LOGGER.debug("同步服务器时间");
            SyncServerTimeResponse syncServerTimeResponse = SyncServerTimeResponse.build();
            sender.response(new Message(Constants.SYSTEM_TYPE, ShineAction.SYNC_SERVER_TIME, syncServerTimeResponse));
            return true;
        }
        return false;
    }

    /**
     * 执行消息分发
     */
    private void dispatchMessage(ResponseMessageSender sender, BaseMessage message) {
        String terminalId = getTerminalIdFromSession(sender.getSession());
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        request.setDispatcherKey(message.getAction());
        request.setRequestId(sender.getResponseId());
        request.setTerminalId(terminalId);
        String data = message.getData() == null ? null : String.valueOf(message.getData());
        request.setData(data);
        try {
            LOGGER.debug("分发消息，terminalId:{}; action: {}; data:{}", terminalId, message.getAction(), data);
            cbbDispatcherHandlerSPI.dispatch(request);
        } catch (Exception e) {
            LOGGER.error("消息分发执行异常;action:" + message.getAction() + ",terminalId:" + terminalId + ",data:" + message.getData(), e);
        }
    }

    /**
     * 处理终端请求的第一个报文,解析出terminalId值
     * 设置terminalId，绑定连接session,
     * 后续报文请求都是基于已绑定terminalId的连接
     */
    private void bindSession(ResponseMessageSender sender, Object message) {
        Assert.notNull(message, "终端信息不能为空");
        String data = String.valueOf(message);
        ShineTerminalBasicInfo basicInfo;
        try {
            basicInfo = JSON.parseObject(data, ShineTerminalBasicInfo.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("接收到的报文格式错误;data:" + data, e);
        }
        String terminalId = basicInfo.getTerminalId();
        Assert.hasText(terminalId, "绑定终端连接session的terminalId不能为空");
        Session session = sender.getSession();
        session.setAttribute(TERMINAL_BIND_KEY, terminalId);
        sessionManager.bindSession(terminalId, session);
    }

    private boolean hasBindSession(Session session, String action) {
        Assert.notNull(session, "Session为null,连接异常");
        if (ShineAction.CHECK_UPGRADE.equals(action)) {
            // 升级报文不做session绑定判断
            return true;
        }
        String terminalId = session.getAttribute(TERMINAL_BIND_KEY);
        return StringUtils.isNotBlank(terminalId);
    }

    @Override
    public void onConnectSuccess(RequestMessageSender requestMessageSender) {
        Assert.notNull(requestMessageSender, "RequestMessageSender不能为null");
        LOGGER.debug("=====建立成功=====");
    }

    @Override
    public void onConnectClosed(Session session) {
        LOGGER.debug("====连接关闭=====");
        Assert.notNull(session, "session 不能为null");
        String terminalId = getTerminalIdFromSession(session);
        // 移除Session绑定
        sessionManager.removeSession(terminalId);
        LOGGER.debug("terminalId[{}]连接关闭", terminalId);
        // 发送连接关闭事件
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        request.setDispatcherKey(ShineAction.CONNECT_CLOSE);
        request.setTerminalId(terminalId);
        cbbDispatcherHandlerSPI.dispatch(request);
    }

    private String getTerminalIdFromSession(Session session) {
        String terminalId = session.getAttribute(TERMINAL_BIND_KEY);
        Assert.hasText(terminalId, "session 未绑定终端");
        return terminalId;
    }

    @Override
    public void exceptionCaught(Throwable throwable) {
        Assert.notNull(throwable, "Throwable不能为null");
        LOGGER.error("连接异常", throwable);
    }
}
