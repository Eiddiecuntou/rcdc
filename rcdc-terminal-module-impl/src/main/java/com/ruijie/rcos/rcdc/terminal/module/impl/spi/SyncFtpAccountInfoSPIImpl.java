package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.alibaba.fastjson.JSONObject;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.MessageUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.response.FtpConfigInfo;
import com.ruijie.rcos.sk.base.crypto.AesUtil;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;
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

    private static final String TERMINAL_FTP_CONFIG_KEY = "terminal_ftp_config";

    private static final String FTP_PASSWORD_KEY = "SHINEFTPPASSWORD";

    @Autowired
    private GlobalParameterAPI globalParameterAPI;

    @Autowired
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        try {
            Assert.notNull(request, "request can not be null");
            String ftpConfigInfo = globalParameterAPI.findParameter(TERMINAL_FTP_CONFIG_KEY);
            FtpConfigInfo config = JSONObject.parseObject(ftpConfigInfo, FtpConfigInfo.class);
            Assert.notNull(config, "config can not be null");

            String passwd = config.getFtpUserPassword();
            Assert.notNull(passwd, "passwd can not be null");
            config.setFtpUserPassword(AesUtil.encrypt(passwd, FTP_PASSWORD_KEY));
            CbbResponseShineMessage responseMessage = MessageUtils.buildResponseMessage(request, config);
            messageHandlerAPI.response(responseMessage);
        } catch (Exception e) {
            LOGGER.error("终端获取ftp账号消息应答失败", e);
            CbbResponseShineMessage responseMessage = MessageUtils.buildErrorResponseMessage(request);
            messageHandlerAPI.response(responseMessage);
        }
    }
}
