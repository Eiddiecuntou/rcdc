package com.ruijie.rcos.rcdc.terminal.module.impl.connect;

import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.codec.adapter.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.connectkit.api.connect.ConnectInfo;
import com.ruijie.rcos.sk.connectkit.api.connect.ConnectorListener;
import com.ruijie.rcos.sk.connectkit.api.tcp.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/8/31
 *
 * @author hs
 */
public class DefaultConnectorListener implements ConnectorListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConnectorListener.class);

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private CbbDispatcherHandlerSPI cbbDispatcherHandlerSPI;

    @Override
    public void onOpen(ConnectInfo connectInfo) {
        Assert.notNull(connectInfo, "connectInfo can not be null");

        LOGGER.info("连接建立成功");

    }

    @Override
    public void onClose(ConnectInfo connectInfo) {
        Assert.notNull(connectInfo, "connectInfo can not be null");

        LOGGER.info("连接关闭");
        Session session = sessionManager.getSessionById(connectInfo.getId());
        String terminalId = session.getSessionAlias();
        // 移除Session绑定
        boolean isSuccess = sessionManager.removeSession(terminalId, session);
        // 发送连接关闭事件，只对当前的连接发送关闭通知
        if (isSuccess) {
            CbbDispatcherRequest request = new CbbDispatcherRequest();
            request.setDispatcherKey(ShineAction.CONNECT_CLOSE);
            request.setTerminalId(terminalId);
            cbbDispatcherHandlerSPI.dispatch(request);
        }


    }

    @Override
    public void onFailure(ConnectInfo connectInfo, Throwable throwable) {
        Assert.notNull(connectInfo, "connectInfo can not be null");
        Assert.notNull(throwable, "throwable can not be null");

        LOGGER.info("连接异常");


    }

    @Override
    public void onIdle(ConnectInfo connectInfo, IdleType idleType) {
        Assert.notNull(connectInfo, "connectInfo can not be null");
        Assert.notNull(idleType, "idleType can not be null");

        LOGGER.info("连接空闲");

    }
}
