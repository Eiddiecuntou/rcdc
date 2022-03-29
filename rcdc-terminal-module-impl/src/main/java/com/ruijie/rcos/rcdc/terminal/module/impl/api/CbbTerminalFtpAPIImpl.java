package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.alibaba.fastjson.JSONObject;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalFtpAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.TerminalFtpConfigInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.sk.base.crypto.AesUtil;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * Description: 终端ftp信息api实现
 * Copyright: Copyright (c) 2022
 * Company: RuiJie Co., Ltd.
 * Create Time: 2022/3/28 9:54 上午
 *
 * @author zhouhuan
 */
public class CbbTerminalFtpAPIImpl implements CbbTerminalFtpAPI {

    @Autowired
    private GlobalParameterAPI globalParameterAPI;

    @Override
    public TerminalFtpConfigInfo getTerminalFtpConfigInfo() {

        String ftpConfigInfo = globalParameterAPI.findParameter(Constants.TERMINAL_FTP_CONFIG_KEY);
        TerminalFtpConfigInfo config = JSONObject.parseObject(ftpConfigInfo, TerminalFtpConfigInfo.class);
        Assert.notNull(config, "config can not be null");

        String password = config.getFtpUserPassword();
        Assert.hasText(password, "password can not be blank");
        config.setFtpUserPassword(AesUtil.encrypt(password, Constants.FTP_PASSWORD_KEY));

        return config;
    }
}
