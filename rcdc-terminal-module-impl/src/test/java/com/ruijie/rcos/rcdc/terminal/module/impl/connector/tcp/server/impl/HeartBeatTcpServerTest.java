package com.ruijie.rcos.rcdc.terminal.module.impl.connector.tcp.server.impl;

import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.log.Logger;
import mockit.Mocked;
import mockit.Tested;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Description: ShineRequestPartTypeSPIImpl
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/2/6
 *
 * @author nting
 */
@RunWith(SkyEngineRunner.class)
public class HeartBeatTcpServerTest {

    @Tested
    private HeartBeatTcpServerImpl heartBeatTcpServer;

    @Mocked
    private Logger logger;

    /**
     * testHeartBeat
     */
    @Test
    public void testHeartBeat() {

        Object obj = heartBeatTcpServer.heartBeat();

        Assert.assertTrue(obj instanceof Object);


    }

}
