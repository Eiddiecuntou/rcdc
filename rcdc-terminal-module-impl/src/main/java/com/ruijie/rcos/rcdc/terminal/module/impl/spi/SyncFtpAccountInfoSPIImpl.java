package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.ruijie.rcos.rcdc.codec.adapter.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.codec.adapter.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalFtpAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.TerminalFtpConfigInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.MessageUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * Description: 同步终端ftp密码spi
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/4/7 6:10 下午
 *
 * @author zhouhuan
 */
@DispatcherImplemetion(ShineAction.SYNC_FTP_ACCOUNT_INFO)
public class SyncFtpAccountInfoSPIImpl implements CbbDispatcherHandlerSPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncFtpAccountInfoSPIImpl.class);

    @Autowired
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    @Autowired
    private CbbTerminalFtpAPI cbbTerminalFtpAPI;

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        try {
            Assert.notNull(request, "request can not be null");

            TerminalFtpConfigInfo config = cbbTerminalFtpAPI.getTerminalFtpConfigInfo();
            CbbResponseShineMessage responseMessage = MessageUtils.buildResponseMessage(request, config);
            messageHandlerAPI.response(responseMessage);
        } catch (Exception e) {
            LOGGER.error("终端获取ftp账号消息应答失败", e);
            CbbResponseShineMessage responseMessage = MessageUtils.buildErrorResponseMessage(request);
            messageHandlerAPI.response(responseMessage);
        }
    }
}
