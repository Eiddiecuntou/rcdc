package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.ruijie.rcos.base.sysmanage.module.def.api.SystemUpgradeAPI;
import com.ruijie.rcos.base.sysmanage.module.def.api.request.upgrade.BaseObtainSystemReleaseVersionRequest;
import com.ruijie.rcos.base.sysmanage.module.def.api.response.upgrade.BaseObtainSystemReleaseVersionResponse;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.SoftwareVersionResponseContent;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.MessageUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalOperatorService;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.response.TerminalPassword;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;

import mockit.*;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: Function Description Copyright: Copyright (c) 2019 Company:
 * Ruijie Co., Ltd. Create Time: 2019年05月06日
 * 
 * @author nt
 */
@RunWith(JMockit.class)
public class SyncSoftwareVersionHandlerSPIImplTest {

    @Tested
    private SyncSoftwareVersionHandlerSPIImpl spiImpl;

    @Injectable
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    @Injectable
    private SystemUpgradeAPI systemUpgradeAPI;

    /**
     * 测试dispatch,参数为空
     * 
     * @throws Exception
     *             异常
     */
    @Test
    public void testDispatchArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> spiImpl.dispatch(null), "CbbDispatcherRequest不能为空");
        assertTrue(true);
    }

    /**
     * 测试dispatch
     * 
     * @param utils
     *            mock MessageUtils
     * @throws BusinessException
     *             异常
     */
    @Test
    public void testDispatch(@Mocked MessageUtils utils) throws BusinessException {
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        CbbResponseShineMessage responseMessage = new CbbResponseShineMessage<>();

        BaseObtainSystemReleaseVersionResponse versionResponse = new BaseObtainSystemReleaseVersionResponse();
        versionResponse.setSystemReleaseVersion("123");
        new Expectations() {
            {
                systemUpgradeAPI.obtainSystemReleaseVersion((BaseObtainSystemReleaseVersionRequest) any);
                result = versionResponse;
                MessageUtils.buildResponseMessage(request, any);
                result = responseMessage;
            }
        };
        spiImpl.dispatch(request);
        new Verifications() {
            {
                SoftwareVersionResponseContent content;
                MessageUtils.buildResponseMessage(request, content = withCapture());
                times = 1;
                assertEquals("123", versionResponse.getSystemReleaseVersion());
                messageHandlerAPI.response(responseMessage);
                times = 1;
            }
        };
    }

    /**
     * 测试dispatch，响应失败
     *
     * @param utils
     *            mock MessageUtils
     * @throws BusinessException
     *             异常
     */
    @Test
    public void testDispatchFail(@Mocked MessageUtils utils) throws BusinessException {
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        CbbResponseShineMessage responseMessage = new CbbResponseShineMessage<>();

        BaseObtainSystemReleaseVersionResponse versionResponse = new BaseObtainSystemReleaseVersionResponse();
        versionResponse.setSystemReleaseVersion("123");
        new Expectations() {
            {
                systemUpgradeAPI.obtainSystemReleaseVersion((BaseObtainSystemReleaseVersionRequest) any);
                result = versionResponse;

                MessageUtils.buildResponseMessage(request, any);
                result = responseMessage;

                messageHandlerAPI.response(responseMessage);
                result = new Exception("123");
            }
        };
        spiImpl.dispatch(request);
        new Verifications() {
            {
                SoftwareVersionResponseContent content;
                MessageUtils.buildResponseMessage(request, content = withCapture());
                times = 1;
                assertEquals("123", versionResponse.getSystemReleaseVersion());
                messageHandlerAPI.response(responseMessage);
                times = 1;
            }
        };
    }

    /**
     * 测试dispatch失败，获取版本号异常
     * 
     * @throws BusinessException
     *             异常
     */
    @Test
    public void testDispatchGetVersionFail() throws BusinessException {
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        CbbResponseShineMessage responseMessage = new CbbResponseShineMessage<>();

        new Expectations() {
            {
                systemUpgradeAPI.obtainSystemReleaseVersion((BaseObtainSystemReleaseVersionRequest) any);
                result = new BusinessException("key");
            }
        };

        new MockUp<MessageUtils>() {
            @Mock
            public CbbResponseShineMessage buildErrorResponseMessage(CbbDispatcherRequest request) {
                return responseMessage;
            }
        };
        spiImpl.dispatch(request);
        new Verifications() {
            {
                systemUpgradeAPI.obtainSystemReleaseVersion((BaseObtainSystemReleaseVersionRequest) any);
                times = 1;

                MessageUtils.buildErrorResponseMessage((CbbDispatcherRequest) any);
                times = 1;

                messageHandlerAPI.response((CbbResponseShineMessage) any);
                times = 1;
            }
        };
    }
}
