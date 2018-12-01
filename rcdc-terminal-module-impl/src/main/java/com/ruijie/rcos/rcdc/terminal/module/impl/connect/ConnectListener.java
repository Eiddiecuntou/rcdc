package com.ruijie.rcos.rcdc.terminal.module.impl.connect;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalEventNoticeSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.NoticeEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbNoticeRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.GatherLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.ReceiveTerminalEvent;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalDetectService;
import com.ruijie.rcos.sk.base.concorrent.executor.SkyengineScheduledThreadPoolExecutor;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.util.Assert;
import com.ruijie.rcos.sk.commkit.base.Session;
import com.ruijie.rcos.sk.commkit.base.message.base.BaseMessage;
import com.ruijie.rcos.sk.commkit.base.sender.RequestMessageSender;
import com.ruijie.rcos.sk.commkit.base.sender.ResponseMessageSender;
import com.ruijie.rcos.sk.commkit.server.AbstractServerMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description: 连接监听
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/24
 *
 * @author Jarman
 */
@Service
public class ConnectListener extends AbstractServerMessageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectListener.class);

    @Autowired
    private CbbDispatcherHandlerSPI cbbDispatcherHandlerSPI;

    @Autowired
    private CbbTerminalEventNoticeSPI cbbTerminalEventNoticeSPI;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private TerminalBasicInfoService basicInfoService;

    @Autowired
    private GatherLogCacheManager gatherLogCacheManager;

    @Autowired
    private TerminalDetectService detectService;

    /**
     * 接收报文处理线程池,分配50个线程数
     */
    private static final SkyengineScheduledThreadPoolExecutor MESSAGE_HANDLER_THREAD_POOL
            = new SkyengineScheduledThreadPoolExecutor(50, ConnectListener.class.getName());

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
        //处理第一个报文
        handleFirstMessage(sender, message);
        String terminalId = getTerminalIdFromSession(sender.getSession());
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        request.setDispatcherKey(message.getAction());
        request.setRequestId(sender.getResponseId());
        request.setTerminalId(terminalId);
        request.setData(message.getData());
        cbbDispatcherHandlerSPI.dispatch(request);
    }

    /**
     * 处理终端请求的第一个报文,解析出terminalId值
     * 设置terminalId，绑定连接session
     *
     * @param sender
     * @param message
     */
    private void handleFirstMessage(ResponseMessageSender sender, BaseMessage message) {
        if (ReceiveTerminalEvent.CHECK_UPGRADE.equals(message.getAction())) {
            LOGGER.debug("开始处理第一个报文{}", ReceiveTerminalEvent.CHECK_UPGRADE);
            Assert.notNull(message.getData(), "终端信息不能为空");
            String data = String.valueOf(message.getData());
            ShineTerminalBasicInfo basicInfo = JSON.parseObject(data, ShineTerminalBasicInfo.class);
            String terminalId = basicInfo.getTerminalId();
            Assert.hasLength(terminalId, "terminalId不能为空");
            //Session 绑定terminalId
            sender.getSession().setAttribute(TERMINAL_BIND_KEY, terminalId);
            sessionManager.bindSession(terminalId, sender.getSession());
            LOGGER.debug("终端terminalId={}绑定Session", basicInfo.getTerminalId());
        }
    }

    @Override
    public void onConnectSuccess(RequestMessageSender requestMessageSender) {
        Assert.notNull(requestMessageSender, "RequestMessageSender不能为null");
        LOGGER.debug("连接成功");
    }

    @Override
    public void onConnectClosed(Session session) {
        LOGGER.debug("连接关闭");
        Assert.notNull(session, "session 不能为null");
        String terminalId = getTerminalIdFromSession(session);
        sessionManager.removeSession(terminalId);
        try {
            basicInfoService.modifyTerminalState(terminalId, CbbTerminalStateEnums.OFFLINE);
        } catch (BusinessException e) {
            LOGGER.error("修改终端状态失败", e);
        }
        //发出连接关闭通知
        CbbNoticeRequest cbbNoticeRequest = new CbbNoticeRequest(NoticeEventEnums.OFFLINE, terminalId);
        cbbTerminalEventNoticeSPI.notify(cbbNoticeRequest);
        //清除收集日志缓存
        gatherLogCacheManager.removeCache(terminalId);
        //更新终端检测状态
        detectService.setOfflineTerminalToFailureState();

    }

    @Override
    public void exceptionCaught() {
        LOGGER.error("连接异常");
    }

    private String getTerminalIdFromSession(Session session) {
        String terminalId = session.getAttribute(TERMINAL_BIND_KEY);
        Assert.hasLength(terminalId, "session 未绑定终端");
        return terminalId;
    }
}
