package com.ruijie.rcos.rcdc.terminal.module.impl.connect;

import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.codec.adapter.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.connectkit.api.connect.ConnectInfo;
import com.ruijie.rcos.sk.connectkit.api.connect.ConnectorListener;
import com.ruijie.rcos.sk.connectkit.api.tcp.session.Session;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/9/4
 *
 * @author hs
 */
@RunWith(SkyEngineRunner.class)
public class DefaultConnectorListenerTest {

    @Tested
    private DefaultConnectorListener connectorListener;

    @Injectable
    private SessionManager sessionManager;

    @Injectable
    private CbbDispatcherHandlerSPI cbbDispatcherHandlerSPI;

    @Injectable
    private ConnectInfo connectInfo;

    @Injectable
    private Session session;

    /**
     * 测试onOpen
     */
    @Test
    public void testOnOpen() {
        connectorListener.onOpen(connectInfo);
        new Verifications() {
            {

            }
        };
    }

    /**
     * 测试onClose
     */
    @Test
    public void testOnCloseRemoveSessionFalse() {
        new Expectations() {
            {
                sessionManager.getTerminalIdBySessionId(anyString);
                result = "123";
                sessionManager.removeSession(anyString);
                result = true;
            }
        };
        connectorListener.onClose(connectInfo);
        new Verifications() {
            {
                sessionManager.getTerminalIdBySessionId(anyString);
                times = 1;
                sessionManager.removeSession(anyString);
                times = 1;

            }
        };
    }

    /**
     * 测试onClose
     */
    @Test
    public void testOnCloseRemoveSessionSuccess() {

        new Expectations() {
            {
                sessionManager.getTerminalIdBySessionId(anyString);
                result = "123";
                sessionManager.removeSession(anyString);
                result = true;

                cbbDispatcherHandlerSPI.dispatch((CbbDispatcherRequest) any);
            }
        };
        connectorListener.onClose(connectInfo);
        new Verifications() {
            {
                sessionManager.getTerminalIdBySessionId(anyString);
                times = 1;
                sessionManager.removeSession(anyString);
                times = 1;
                cbbDispatcherHandlerSPI.dispatch((CbbDispatcherRequest) any);
                times = 1;
            }
        };
    }

    /**
     * 测试onFailure
     */
    @Test
    public void testOnFailure() {
        Throwable throwable = new Throwable();
        connectorListener.onFailure(connectInfo, throwable);
        new Verifications() {
            {

            }
        };

    }

    @Test
    public void testOnIdle() {
        ConnectorListener.IdleType idleType = ConnectorListener.IdleType.READER_IDLE;
        connectorListener.onIdle(connectInfo, idleType);
        new Verifications() {
            {

            }
        };
    }
}
