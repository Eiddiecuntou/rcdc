package com.ruijie.rcos.rcdc.terminal.module.impl.connect;

import com.ruijie.rcos.sk.base.concorrent.executor.SkyengineScheduledThreadPoolExecutor;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.commkit.server.TcpServer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description: TCP server启动
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/12/25
 * TODO 需要用spring配置实现
 *
 * @author Jarman
 */
@Service
public class NettyServerInitializingBean implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerInitializingBean.class);

    private static final SkyengineScheduledThreadPoolExecutor START_NETTY_SERVER_THREAD_POOL
            = new SkyengineScheduledThreadPoolExecutor(1, NettyServerInitializingBean.class.getName());

    @Autowired
    private ConnectEventHandler connectEventHandler;

    @Override
    public void afterPropertiesSet() throws Exception {
        START_NETTY_SERVER_THREAD_POOL.execute(() -> {
            LOGGER.info("======启动NettyServer======");
            TcpServer tcpServer = new TcpServer(connectEventHandler);
            tcpServer.start();
        });
    }
}
