package com.ruijie.rcos.rcdc.terminal.module.impl.init;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.connector.tcp.api.TerminalFtpAccountInfoAPI;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.response.FtpConfigInfo;
import com.ruijie.rcos.sk.base.concurrent.ThreadExecutor;
import com.ruijie.rcos.sk.base.concurrent.ThreadExecutors;
import com.ruijie.rcos.sk.base.crypto.AesUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.bootstrap.SafetySingletonInitializer;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * Description: 初始化ftp的密码
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/4/7 5:00 下午
 *
 * @author zhouhuan
 */
@Service
public class TerminalFtpAccountInfoInit implements SafetySingletonInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalFtpAccountInfoInit.class);

    private static final String TERMINAL_FTP_CONFIG_KEY = "terminal_ftp_config";

    private static final String TERMINAL_FTP_DEFAULT_PASSWORD = "21Wq_Er";

    private static final Integer FTP_PASSWORD_LENGTH = 8;

    @Autowired
    private GlobalParameterAPI globalParameterAPI;

    private static final ThreadExecutor NOTICE_HANDLER_THREAD_POOL =
            ThreadExecutors.newBuilder(TerminalFtpAccountInfoInit.class.getName()).maxThreadNum(20).queueSize(50).build();
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private TerminalFtpAccountInfoAPI terminalFtpAccountInfoAPI;

    @Override
    public void safeInit() {
        LOGGER.info("start to update terminal ftp passwd");
        String passwd = getRandomFtpPasswd();
        String ftpConfigInfo = globalParameterAPI.findParameter(TERMINAL_FTP_CONFIG_KEY);
        FtpConfigInfo config = JSONObject.parseObject(ftpConfigInfo, FtpConfigInfo.class);
        String userName = config.getFtpUserName();
        config.setFtpUserPassword(passwd);

        try {
            String command = String.format("echo %s|passwd --stdin %s", passwd, userName);
            String[] commandArr = new String[]{"sh", "-c", command};
            updatePasswd(commandArr);
            globalParameterAPI.updateParameter(TERMINAL_FTP_CONFIG_KEY, JSON.toJSONString(config));
        } catch (Exception e) {
            LOGGER.error("执行系统命令修改用于终端的ftp账号的密码失败", e);
            config.setFtpUserPassword(TERMINAL_FTP_DEFAULT_PASSWORD);
            globalParameterAPI.updateParameter(TERMINAL_FTP_CONFIG_KEY, JSON.toJSONString(config));
        }

        LOGGER.info("向在线终端同步ftp账号信息");
        NOTICE_HANDLER_THREAD_POOL.execute(() -> sendFtpAccountInfoToOnlineTerminal());
    }

    public void sendFtpAccountInfoToOnlineTerminal() {
        List<String> onlineTerminalIdList = sessionManager.getOnlineTerminalId();
        if (CollectionUtils.isEmpty(onlineTerminalIdList)) {
            LOGGER.info("无在线终端");
            return;
        }

        for (String terminalId : onlineTerminalIdList) {
            try {
                String ftpConfigInfo = globalParameterAPI.findParameter(TERMINAL_FTP_CONFIG_KEY);
                FtpConfigInfo config = JSONObject.parseObject(ftpConfigInfo, FtpConfigInfo.class);
                config.setFtpUserPassword(AesUtil.encrypt(config.getFtpUserPassword(), Constants.FTP_PASSWORD_KEY));
                LOGGER.info("向终端:[{}]发送的ftp账号信息:{}", terminalId, JSON.toJSONString(config));
                terminalFtpAccountInfoAPI.syncFtpAccountInfo(terminalId, config);
            } catch (Exception e) {
                LOGGER.error("同步给终端:[{}]ftp账号信息失败", terminalId);
            }
        }
    }

    private void updatePasswd(String... commandArr) throws BusinessException {
        int ret = 0;
        try {
            Process process = new ProcessBuilder(commandArr).start();
            ret = process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new BusinessException(BusinessKey.RCDC_SYSTEM_CMD_EXECUTE_FAIL, e);
        }

        if (ret != 0) {
            throw new BusinessException(BusinessKey.RCDC_SYSTEM_CMD_EXECUTE_FAIL);
        }
    }

    //生成随机密码，截取UUID的前8位
    private String getRandomFtpPasswd() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().substring(0, FTP_PASSWORD_LENGTH);
    }
}
