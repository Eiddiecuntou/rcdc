package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.ruijie.rcos.rcdc.codec.adapter.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.codec.adapter.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalGroupTreeNodeDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.GetTerminalGroupTreeDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalGroupEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.MessageUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalGroupService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.TerminalGroupHandler;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Description: 获取终端组树型结构列表
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/3/9 14:07
 *
 * @author zhangyichi
 */
@DispatcherImplemetion(ShineAction.GET_TERMINAL_GROUP_LIST)
public class GetTerminalGroupTreeHandlerSPIImpl implements CbbDispatcherHandlerSPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetTerminalGroupTreeHandlerSPIImpl.class);

    @Autowired
    private TerminalGroupService terminalGroupService;

    @Autowired
    private TerminalGroupHandler terminalGroupHandler;

    @Autowired
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request, "request cannot be null!");

        try {
            GetTerminalGroupTreeDTO groupTreeResponse = loadTerminalGroupCompleteTree();
            Assert.notNull(groupTreeResponse.getItemArr(), "items in groupTreeResponse is null!");

            CbbResponseShineMessage shineMessage = MessageUtils.buildResponseMessage(request, groupTreeResponse);
            messageHandlerAPI.response(shineMessage);
        } catch (Exception e) {
            LOGGER.error("获取终端组列表失败", e);
            CbbResponseShineMessage shineMessage = MessageUtils.buildErrorResponseMessage(request);
            messageHandlerAPI.response(shineMessage);
        }
    }

    private GetTerminalGroupTreeDTO loadTerminalGroupCompleteTree() {
        List<TerminalGroupEntity> groupList = terminalGroupService.findAll();
        if (CollectionUtils.isEmpty(groupList)) {
            return new GetTerminalGroupTreeDTO(new CbbTerminalGroupTreeNodeDTO[0]);
        }
        // 不过滤任何分组（包括"未分组"）
        CbbTerminalGroupTreeNodeDTO[] dtoArr = terminalGroupHandler.assembleGroupTree(null, groupList, null);
        return new GetTerminalGroupTreeDTO(dtoArr);
    }
}
