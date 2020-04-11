package com.ruijie.rcos.rcdc.terminal.module.impl.init;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.response.FtpConfigInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.CmdExecuteUtil;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.bootstrap.SafetySingletonInitializer;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description: 初始化ftp的密码
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/4/7 5:00 下午
 *
 * @author zhouhuan
 */
@Service
public class FtpAccountInfoInit implements SafetySingletonInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FtpAccountInfoInit.class);

    private static final String TERMINAL_FTP_CONFIG_KEY = "terminal_ftp_config";

    private static final String GUESTTOOL_LOG_FTP_CONFIG_KEY = "guesttool_log_ftp_config";

    private static final Integer FTP_PASSWORD_LENGTH = 8;

    @Autowired
    private GlobalParameterAPI globalParameterAPI;

    @Override
    public void safeInit() {
        LOGGER.info("start to update ftp passwd");
        updatePasswd(TERMINAL_FTP_CONFIG_KEY);
        updatePasswd(GUESTTOOL_LOG_FTP_CONFIG_KEY);
    }

    private void updatePasswd(String key) {
        String passwd = getRandomFtpPasswd();
        try {
            String ftpConfigInfo = globalParameterAPI.findParameter(key);
            FtpConfigInfo config = JSONObject.parseObject(ftpConfigInfo, FtpConfigInfo.class);
            String userName = config.getFtpUserName();
            config.setFtpUserPassword(passwd);
            String command = "echo " + passwd + "| passwd --stdin " + userName;
            CmdExecuteUtil.executeCmd(command);
            globalParameterAPI.updateParameter(key, JSON.toJSONString(config));
        } catch (Exception e) {
            LOGGER.error("更新全局配置表中key为{}的ftp密码失败", key, e);
        }
    }

    //生成随机密码，截取UUID的前8位
    private String getRandomFtpPasswd() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().substring(0, FTP_PASSWORD_LENGTH);
    }
}
