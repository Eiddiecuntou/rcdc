package com.ruijie.rcos.rcdc.terminal.module.impl.connect;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.util.Assert;
import com.ruijie.rcos.sk.base.concorrent.executor.SkyengineScheduledThreadPoolExecutor;
import com.ruijie.rcos.sk.commkit.server.TcpServer;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月23日
 * 
 * @author ls
 */
@RunWith(JMockit.class)
public class NettyServerInitializingBeanTest {

    @Tested
    private NettyServerInitializingBean initializingBean;
    
    @Injectable
    private ConnectEventHandler connectEventHandler;
    
    /**
     * 测试afterPropertiesSet，
     * @param tcpServer mock tcpServer
     * @throws Exception 异常
     */
    @Test
    public void testAfterPropertiesSet(@Mocked TcpServer tcpServer) throws Exception {
        
        new MockUp<SkyengineScheduledThreadPoolExecutor>() {
            @Mock
            public void execute(Runnable command) {
                Assert.notNull(command, "command can not be null");
                command.run();
            }
        };
        initializingBean.afterPropertiesSet();
        
        new Verifications() {
            {
                tcpServer.start();
                times = 1;
            }
        };
    }

}
