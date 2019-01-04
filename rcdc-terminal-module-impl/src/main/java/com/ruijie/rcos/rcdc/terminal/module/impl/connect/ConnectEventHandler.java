package com.ruijie.rcos.rcdc.terminal.module.impl.connect;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.NoticeEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalEventNoticeSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbNoticeRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.ReceiveTerminalEvent;
import com.ruijie.rcos.sk.base.concorrent.executor.SkyengineScheduledThreadPoolExecutor;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.commkit.base.Session;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.message.base.BaseMessage;
import com.ruijie.rcos.sk.commkit.base.sender.RequestMessageSender;
import com.ruijie.rcos.sk.commkit.base.sender.ResponseMessageSender;
import com.ruijie.rcos.sk.commkit.server.AbstractServerMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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

    @Autowired
    private TerminalBasicInfoService basicInfoService;

    @Autowired
    private CollectLogCacheManager collectLogCacheManager;

    @Autowired
    private CbbTerminalEventNoticeSPI terminalEventNoticeSPI;

    /**
     * 接收报文处理线程池,分配50个线程数
     */
    private static final SkyengineScheduledThreadPoolExecutor MESSAGE_HANDLER_THREAD_POOL
            = new SkyengineScheduledThreadPoolExecutor(50, ConnectEventHandler.class.getName());

    /**
     * 绑定终端session的key
     */
    private static final String TERMINAL_BIND_KEY = "terminal_bind_session_key";

    @Override
    public void onReceive(ResponseMessageSender sender, BaseMessage message) {
        Assert.notNull(sender, "ResponseMessageSender不能为null");
        Assert.notNull(message, "BaseMessage不能为null");
        //使用线程池处理接收到的报文
        MESSAGE_HANDLER_THREAD_POOL.execute(() -> handleMessage(sender, message));
    }

    /**
     * 处理接收到的报文
     *
     * @param sender  连接通道对象
     * @param message 报文对象
     */
    private void handleMessage(ResponseMessageSender sender, BaseMessage message) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("接收到的报文：action:{}", message.getAction());
            if (message.getData() != null) {
                LOGGER.debug("报文消息体：{}", message.getData().toString());
            }
        }
        if (ReceiveTerminalEvent.HEARTBEAT.equals(message.getAction())) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("terminalId:[{}]收到心跳报文", sender.getSession().getAttribute(TERMINAL_BIND_KEY) + "");
            }
            //应答心跳报文
            sender.response(new Message(Constants.SYSTEM_TYPE, ReceiveTerminalEvent.HEARTBEAT, null));
            return;
        }
        if (ReceiveTerminalEvent.CHECK_UPGRADE.equals(message.getAction())) {
            LOGGER.debug("开始处理第一个报文[{}]", ReceiveTerminalEvent.CHECK_UPGRADE);
            //处理第一个报文
            handleFirstMessage(sender, message);
        }
        //执行消息分发
        String terminalId = getTerminalIdFromSession(sender.getSession());
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        request.setDispatcherKey(message.getAction());
        request.setRequestId(sender.getResponseId());
        request.setTerminalId(terminalId);
        request.setData(message.getData());
        try {
            cbbDispatcherHandlerSPI.dispatch(request);
        } catch (Exception e) {
            LOGGER.error("消息分发执行异常;action:" + message.getAction() + ",terminalId:" + terminalId + ",data:" + String.valueOf(message.getData()), e);
        }
    }

    /**
     * 处理终端请求的第一个报文,解析出terminalId值
     * 设置terminalId，绑定连接session,
     * 后续报文请求都是基于已绑定terminalId的连接
     *
     * @param sender  应答消息对象
     * @param message 接受到的报文
     */
    private void handleFirstMessage(ResponseMessageSender sender, BaseMessage message) {
        Assert.notNull(message.getData(), "终端信息不能为空");
        String data = String.valueOf(message.getData());
        ShineTerminalBasicInfo basicInfo = JSON.parseObject(data, ShineTerminalBasicInfo.class);
        String terminalId = basicInfo.getTerminalId();
        Assert.hasText(terminalId, "terminalId不能为空");
        //Session 绑定terminalId
        Session session = sender.getSession();
        session.setAttribute(TERMINAL_BIND_KEY, terminalId);
        sessionManager.bindSession(terminalId, session);
        LOGGER.debug("终端terminalId={}绑定Session", basicInfo.getTerminalId());
    }

    @Override
    public void onConnectSuccess(RequestMessageSender requestMessageSender) {
        Assert.notNull(requestMessageSender, "RequestMessageSender不能为null");
        LOGGER.debug("=====建立成功=====");
    }

    @Override
    public void onConnectClosed(Session session) {
        Assert.notNull(session, "session 不能为null");
        LOGGER.debug("====连接关闭=====");
        String terminalId = getTerminalIdFromSession(session);
        sessionManager.removeSession(terminalId);
        LOGGER.debug("terminalId:[{}]连接关闭", terminalId);
        basicInfoService.modifyTerminalState(terminalId, CbbTerminalStateEnums.OFFLINE);
        collectLogCacheManager.removeCache(terminalId);
        CbbNoticeRequest noticeRequest = new CbbNoticeRequest(NoticeEventEnums.OFFLINE, terminalId);
        terminalEventNoticeSPI.notify(noticeRequest);
    }

    @Override
    public void exceptionCaught(Throwable throwable) {
        Assert.notNull(throwable, "Throwable不能为null");
        LOGGER.error("连接异常", throwable);
    }

    private String getTerminalIdFromSession(Session session) {
        String terminalId = session.getAttribute(TERMINAL_BIND_KEY);
        Assert.hasText(terminalId, "session 未绑定终端");
        return terminalId;
    }
}
