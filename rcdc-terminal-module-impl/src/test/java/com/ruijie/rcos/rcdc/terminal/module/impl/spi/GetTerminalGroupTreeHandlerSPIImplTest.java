package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalGroupMgmtAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.TerminalGroupTreeNodeDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.group.CbbGetTerminalGroupCompleteTreeRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.group.CbbGetTerminalGroupTreeResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/3/9 14:31
 *
 * @author zhangyichi
 */
@RunWith(SkyEngineRunner.class)
public class GetTerminalGroupTreeHandlerSPIImplTest {

    @Tested
    private GetTerminalGroupTreeHandlerSPIImpl handlerSPI;

    @Injectable
    private CbbTerminalGroupMgmtAPI cbbTerminalGroupMgmtAPI;

    @Injectable
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    /**
     * 获取终端组树状列表
     * @throws BusinessException 异常
     */
    @Test
    public void testDispatch() throws BusinessException {
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        request.setDispatcherKey(ShineAction.GET_TERMINAL_GROUP_LIST);
        request.setData("{\"enableFilterDefaultGroup\":true}");

        TerminalGroupTreeNodeDTO nodeDTO = new TerminalGroupTreeNodeDTO();
        nodeDTO.setId(UUID.randomUUID());
        nodeDTO.setParentId(UUID.randomUUID());
        nodeDTO.setEnableDefault(true);
        nodeDTO.setLabel("label");
        CbbGetTerminalGroupTreeResponse apiResponse = new CbbGetTerminalGroupTreeResponse(new TerminalGroupTreeNodeDTO[]{nodeDTO});

        new Expectations() {
            {
                cbbTerminalGroupMgmtAPI.loadTerminalGroupCompleteTree((CbbGetTerminalGroupCompleteTreeRequest) any);
                result = apiResponse;
            }
        };

        handlerSPI.dispatch(request);

        new Verifications() {
            {
                cbbTerminalGroupMgmtAPI.loadTerminalGroupCompleteTree((CbbGetTerminalGroupCompleteTreeRequest) any);
                times = 1;
                messageHandlerAPI.response((CbbResponseShineMessage) any);
                times = 1;
            }
        };

    }

    /**
     * 获取终端组树状列表，参数错误
     * @throws BusinessException 异常
     */
    @Test
    public void testDispatchArgError() throws BusinessException {
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        request.setDispatcherKey(ShineAction.GET_TERMINAL_GROUP_LIST);
        request.setData("123456");

        handlerSPI.dispatch(request);

        new Verifications() {
            {
                cbbTerminalGroupMgmtAPI.loadTerminalGroupCompleteTree((CbbGetTerminalGroupCompleteTreeRequest) any);
                times = 0;
                messageHandlerAPI.response((CbbResponseShineMessage) any);
                times = 0;
            }
        };

    }
}