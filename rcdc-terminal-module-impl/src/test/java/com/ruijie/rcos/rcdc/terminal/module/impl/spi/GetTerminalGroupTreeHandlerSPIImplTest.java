package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalGroupTreeNodeDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalGroupEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.TerminalGroupServiceImpl;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.TerminalGroupHandler;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
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
    private TerminalGroupServiceImpl terminalGroupService;

    @Injectable
    private TerminalGroupHandler terminalGroupHandler;

    @Injectable
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    /**
     * 获取终端组树状列表
     */
    @Test
    public void testDispatch() {
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        request.setDispatcherKey(ShineAction.GET_TERMINAL_GROUP_LIST);

        TerminalGroupEntity entity = new TerminalGroupEntity();
        List<TerminalGroupEntity> groupList = Lists.newArrayList();
        groupList.add(entity);

        CbbTerminalGroupTreeNodeDTO nodeDTO = new CbbTerminalGroupTreeNodeDTO();
        nodeDTO.setId(UUID.randomUUID());
        nodeDTO.setParentId(UUID.randomUUID());
        nodeDTO.setEnableDefault(true);
        nodeDTO.setLabel("label");
        CbbTerminalGroupTreeNodeDTO[] nodeDTOArr = {nodeDTO};

        new Expectations() {
            {
                terminalGroupService.findAll();
                result = groupList;
                terminalGroupHandler.assembleGroupTree((UUID) any, (List<TerminalGroupEntity>) any, (UUID) any);
                result = nodeDTOArr;
            }
        };

        handlerSPI.dispatch(request);

        new Verifications() {
            {
                CbbResponseShineMessage shineMessage;
                messageHandlerAPI.response(shineMessage = withCapture());
                Assert.assertEquals(Constants.SUCCESS, shineMessage.getCode().intValue());
            }
        };

    }

    /**
     * 获取终端组树状列表，参数错误
     */
    @Test
    public void testDispatchArgError() {
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        request.setDispatcherKey(ShineAction.GET_TERMINAL_GROUP_LIST);

        new Expectations() {
            {
                terminalGroupService.findAll();
                result = new Exception("xxx");
            }
        };

        handlerSPI.dispatch(request);

        new Verifications() {
            {
                terminalGroupHandler.assembleGroupTree((UUID) any, (List<TerminalGroupEntity>) any, (UUID) any);
                times = 0;
                CbbResponseShineMessage shineMessage;
                messageHandlerAPI.response(shineMessage = withCapture());
                Assert.assertEquals(Constants.FAILURE, shineMessage.getCode().intValue());
            }
        };

    }
}