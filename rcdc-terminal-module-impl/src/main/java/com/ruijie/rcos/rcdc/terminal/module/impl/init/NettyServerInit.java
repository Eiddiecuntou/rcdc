package com.ruijie.rcos.rcdc.terminal.module.impl.init;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.ConnectEventHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.sk.base.concorrent.executor.SkyengineScheduledThreadPoolExecutor;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.commkit.server.TcpServer;
import com.ruijie.rcos.sk.modulekit.api.bootstrap.SafetySingletonInitializer;

/**
 * Description: 初始化启动netty server
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/2/13
 *
 * @author Jarman
 */
@Service
public class NettyServerInit implements SafetySingletonInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerInit.class);

    private static final SkyengineScheduledThreadPoolExecutor START_NETTY_SERVER_THREAD_POOL =
            new SkyengineScheduledThreadPoolExecutor(1, NettyServerInit.class.getName());

    @Autowired
    private TerminalBasicInfoDAO terminalBasicInfoDAO;

    @Autowired
    private TerminalBasicInfoService terminalBasicInfoService;

    @Autowired
    private ConnectEventHandler connectEventHandler;

    @Override
    public void safeInit() {
        // 初始化终端状态
        initTerminalState();
        // 启动Netty服务
        startNettyServer();
    }

    private void startNettyServer() {
        START_NETTY_SERVER_THREAD_POOL.execute(() -> {
            LOGGER.info("======启动NettyServer======");
            TcpServer tcpServer = new TcpServer(connectEventHandler);
            tcpServer.start();
        });
    }

    /**
     * 把在线状态初始化为离线状态
     */
    private void initTerminalState() {
        List<TerminalEntity> terminalList = terminalBasicInfoDAO.findTerminalEntitiesByState(CbbTerminalStateEnums.ONLINE);
        if (CollectionUtils.isEmpty(terminalList)) {
            LOGGER.debug("没有需要初始化的终端状态");
            return;
        }
        LOGGER.warn("存在异常关机导致终端状态不一致的情况，总共有{}台终端状态需要初始化", terminalList.size());
        terminalList.forEach(item -> terminalBasicInfoService.modifyTerminalStateToOffline(item.getTerminalId()));
    }
}
