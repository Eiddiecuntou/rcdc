package com.ruijie.rcos.rcdc.terminal.module.impl.init;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.sk.base.env.Enviroment;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.shell.ShellCommandRunner;
import com.ruijie.rcos.sk.base.shell.ShellCommandRunner.ReturnValueResolver;
import com.ruijie.rcos.sk.base.util.StringUtils;
import com.ruijie.rcos.sk.modulekit.api.bootstrap.SafetySingletonInitializer;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;

/**
 * 
 * Description: 终端组件升级bt服务初始化
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月27日
 * 
 * @author nt
 */
@Service
public class TerminalUpgradeBtServerInit implements SafetySingletonInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalUpgradeBtServerInit.class);

    private static final String INIT_PYTHON_SCRIPT_PATH = "/data/web/rcdc/shell/update.py";

    private static final String INIT_COMMAND = "python %s";

    @Autowired
    private GlobalParameterAPI globalParameterAPI;

    @Autowired
    private TerminalComponentUpgradeCacheInit upgradeCacheInit;

    @Override
    public void safeInit() {

        // 添加操作系统判断，使初始化失败不影响开发阶段的调试
        boolean isDevelop = Enviroment.isDevelop();
        LOGGER.info("enviroment is develope: {}", isDevelop);
        if (isDevelop) {
            LOGGER.info("enviroment is develope, skip upgrade bt share init...");
            return;
        }

        // bt服务初始化，判断ip是否变更，如果变化则进行bt服务的初始化操作
        LOGGER.info("start upgrade bt share init...");
        String currentIp = getLocalIP();
        String ip = globalParameterAPI.findParameter(Constants.RCDC_SERVER_IP_GLOBAL_PARAMETER_KEY);
        if (StringUtils.isBlank(ip)) {
            LOGGER.info("ip parameter not exist, start bt share init");
            executeUpdate();
            return;
        }

        if (ip.equals(currentIp)) {
            LOGGER.info("ip not change");
            // 更新缓存中的updatelist
            upgradeCacheInit.safeInit();
            return;
        }

        LOGGER.info("ip changed, start bt share init");
        executeUpdate();
    }

    private void executeUpdate() {
        LOGGER.info("start invoke pythonScript...");
        ShellCommandRunner runner = new ShellCommandRunner();
        runner.setCommand(String.format(INIT_COMMAND, INIT_PYTHON_SCRIPT_PATH));
        try {
            String outStr = runner.execute(new BtShareInitReturnValueResolver());
            LOGGER.debug("out String is :{}", outStr);
        } catch (BusinessException e) {
            LOGGER.error("bt share init error", e);
        }

        LOGGER.info("success invoke pythonScript...");
    }



    /**
     * 
     * Description: Function Description
     * Copyright: Copyright (c) 2018
     * Company: Ruijie Co., Ltd.
     * Create Time: 2019年1月28日
     * 
     * @author nt
     */
    public class BtShareInitReturnValueResolver implements ReturnValueResolver<String> {

        @Override
        public String resolve(String command, Integer exitValue, String outStr) throws BusinessException {
            Assert.hasText(command, "command can not be null");
            Assert.notNull(exitValue, "existValue can not be null");
            Assert.hasText(outStr, "outStr can not be null");

            if (exitValue.intValue() != 0) {
                LOGGER.error("bt share init python script execute error, exitValue: {}, outStr: {}", exitValue, outStr);
                throw new BusinessException(BusinessKey.RCDC_SYSTEM_CMD_EXECUTE_FAIL);
            }

            // 更新数据库中的服务器ip
            globalParameterAPI.updateParameter(Constants.RCDC_SERVER_IP_GLOBAL_PARAMETER_KEY, getLocalIP());
            // 更新缓存中的updatelist
            upgradeCacheInit.safeInit();
            return outStr;
        }

    }

    /**
     * 获取ip
     * 
     * @return ip
     */
    private static String getLocalIP() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            LOGGER.error("get localhost address error, {}", e);
            throw new RuntimeException("get localhost address error,", e);
        }

        byte[] ipArr = addr.getAddress();
        String ipAddrStr = "";
        for (int i = 0; i < ipArr.length; i++) {
            if (i > 0) {
                ipAddrStr += ".";
            }
            ipAddrStr += ipArr[i] & 0xFF;
        }
        return ipAddrStr;
    }
}
