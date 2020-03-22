package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.CbbTerminalModelDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalProductIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalModelService;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.modulekit.api.comm.DtoResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.Response.Status;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Description:
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/2
 *
 * @author wjp
 */
@RunWith(JMockit.class)
public class CbbTerminalModelAPIImplTest {

    @Tested
    CbbTerminalModelAPIImpl terminalModelAPI;

    @Injectable
    TerminalModelService terminalModelService;

    /**
     * 测试listTerminalModel参数为空
     *
     * @throws Exception 异常
     */
    @Test
    public void testListTerminalModelArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> terminalModelAPI.listTerminalModel(null), "request can not be null");
        assertTrue(true);
    }

    /**
     * 测试queryByProductId参数为空
     *
     * @throws Exception 异常
     */
    @Test
    public void testQueryByProductIdArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> terminalModelAPI.queryByProductId(null), "request can not be null");
        assertTrue(true);
    }

    /**
     * 测试queryByProductId
     *
     * @throws Exception 异常
     */
    @Test
    public void testQueryByProductIdSuccess() throws Exception {
        final CbbTerminalProductIdRequest request = new CbbTerminalProductIdRequest();
        request.setProductId("aaa");
        new Expectations() {
            {
                terminalModelAPI.queryByProductId(request);
            }
        };
        DtoResponse<CbbTerminalModelDTO> response = terminalModelAPI.queryByProductId(request);
        assertEquals(Status.SUCCESS, response.getStatus());
    }
}
