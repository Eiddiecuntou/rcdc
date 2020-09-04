package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.ruijie.rcos.rcdc.codec.adapter.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.hciadapter.module.def.api.SystemVersionMgmtAPI;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.MessageUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.SoftwareVersionResponseContent;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.modulekit.api.comm.DtoResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    private SystemVersionMgmtAPI systemVersionMgmtAPI;

    /**
     * 测试dispatch,参数为空
     * 
     * @throws Exception
     *         异常
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
     *        mock MessageUtils
     * @throws BusinessException
     *         异常
     */
    @Test
    public void testDispatch(@Mocked MessageUtils utils) throws BusinessException {
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        CbbResponseShineMessage responseMessage = new CbbResponseShineMessage<>();

        DtoResponse<String> versionResponse = DtoResponse.success("123");
        new Expectations() {
            {
                systemVersionMgmtAPI.obtainSystemReleaseVersion((Request) any);
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
                assertEquals("123", versionResponse.getDto());
                messageHandlerAPI.response(responseMessage);
                times = 1;
            }
        };
    }

    /**
     * 测试dispatch，响应失败
     *
     * @param utils
     *        mock MessageUtils
     * @throws BusinessException
     *         异常
     */
    @Test
    public void testDispatchFail(@Mocked MessageUtils utils) throws BusinessException {
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        CbbResponseShineMessage responseMessage = new CbbResponseShineMessage<>();

        DtoResponse<String> versionResponse = DtoResponse.success("123");
        new Expectations() {
            {
                systemVersionMgmtAPI.obtainSystemReleaseVersion((Request) any);
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
                assertEquals("123", versionResponse.getDto());
                messageHandlerAPI.response(responseMessage);
                times = 1;
            }
        };
    }

    /**
     * 测试dispatch失败，获取版本号异常
     * 
     * @throws BusinessException
     *         异常
     */
    @Test
    public void testDispatchGetVersionFail() throws BusinessException {
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        CbbResponseShineMessage responseMessage = new CbbResponseShineMessage<>();

        new Expectations() {
            {
                systemVersionMgmtAPI.obtainSystemReleaseVersion((Request) any);
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
                systemVersionMgmtAPI.obtainSystemReleaseVersion((Request) any);
                times = 1;

                MessageUtils.buildErrorResponseMessage((CbbDispatcherRequest) any);
                times = 1;

                messageHandlerAPI.response((CbbResponseShineMessage) any);
                times = 1;
            }
        };
    }
}
