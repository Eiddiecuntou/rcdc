package com.ruijie.rcos.rcdc.terminal.module.impl.init;

import com.ruijie.rcos.base.sysmanage.module.def.api.NetworkAPI;
import com.ruijie.rcos.base.sysmanage.module.def.api.request.network.BaseDetailNetworkRequest;
import com.ruijie.rcos.base.sysmanage.module.def.api.response.network.BaseDetailNetworkInfoResponse;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist.AndroidVDIUpdatelistCacheInit;
import com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist.LinuxVDIUpdatelistCacheInit;
import com.ruijie.rcos.sk.base.concurrent.ThreadExecutors;
import com.ruijie.rcos.sk.base.env.Enviroment;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.shell.ShellCommandRunner;
import com.ruijie.rcos.sk.base.shell.ShellCommandRunner.ReturnValueResolver;
import com.ruijie.rcos.sk.base.util.StringUtils;
import com.ruijie.rcos.sk.modulekit.api.bootstrap.SafetySingletonInitializer;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.util.concurrent.ExecutorService;

/**
 * Description: 终端组件升级bt服务初始化
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月27日
 *
 * @author nt
 */
@Service
public class VDITerminalComponentUpgradeInit implements SafetySingletonInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(VDITerminalComponentUpgradeInit.class);

    private static final String INIT_PYTHON_SCRIPT_PATH_VDI_LINUX = "/data/web/rcdc/shell/updateLinuxVDI.py";

    private static final String INIT_PYTHON_SCRIPT_PATH_VDI_ANDROID = "/data/web/rcdc/shell/updateAndroidVDI.py";

    private static final String INIT_COMMAND = "python %s %s";

    private static final ExecutorService EXECUTOR_SERVICE =
            ThreadExecutors.newBuilder(VDITerminalComponentUpgradeInit.class.getName()).maxThreadNum(2).queueSize(1).build();

    @Autowired
    private GlobalParameterAPI globalParameterAPI;

    @Autowired
    private LinuxVDIUpdatelistCacheInit linuxVDIUpdatelistCacheInit;

    @Autowired
    private AndroidVDIUpdatelistCacheInit androidVDIUpdatelistCacheInit;

    @Autowired
    private NetworkAPI networkAPI;

    @Override
    public void safeInit() {
        LOGGER.info("开始异步执行初始化Linux VDI终端升级组件");
        EXECUTOR_SERVICE.execute(() -> initLinuxVDITerminalComponent());
        LOGGER.info("开始异步执行初始化Android VDI终端升级组件");
        EXECUTOR_SERVICE.execute(() -> initAndroidVDITerminalComponent());
    }

    private void initLinuxVDITerminalComponent() {
        String pythonScriptPath = INIT_PYTHON_SCRIPT_PATH_VDI_LINUX;
        TerminalTypeEnums terminalType = TerminalTypeEnums.VDI_LINUX;
        String tempPath = Constants.LINUX_VDI_TERMINAL_TERMINAL_COMPONET_UPGRADE_TEMP_PATH;
        // 检查环境,判断是否需要升级,需要则进行升级
        checkAndUpgrade(pythonScriptPath, terminalType, tempPath);
        LOGGER.info("init upgrade ceche");
        // 更新Linux VDI 终端缓存中的updatelist
        linuxVDIUpdatelistCacheInit.init();
    }

    private void initAndroidVDITerminalComponent() {
        String pythonScriptPath = INIT_PYTHON_SCRIPT_PATH_VDI_ANDROID;
        TerminalTypeEnums terminalType = TerminalTypeEnums.VDI_ANDROID;
        String tempPath = Constants.ANDROID_VDI_TERMINAL_TERMINAL_COMPONET_UPGRADE_TEMP_PATH;
        // 检查环境,判断是否需要升级,需要则进行升级
        checkAndUpgrade(pythonScriptPath, terminalType, tempPath);
        LOGGER.info("init upgrade ceche");
        // 更新android终端缓存中的updatelist
        androidVDIUpdatelistCacheInit.init();
    }

    private void checkAndUpgrade(String pythonScriptPath, TerminalTypeEnums terminalType, String upgradeTempPath) {
        // 添加操作系统判断，使初始化失败不影响开发阶段的调试
        boolean isDevelop = Enviroment.isDevelop();
        LOGGER.info("enviroment is develope: {}", isDevelop);
        if (isDevelop) {
            LOGGER.info("enviroment is develope, skip upgrade bt share init...");
            return;
        }
        // bt服务初始化，判断ip是否变更，如果变化则进行bt服务的初始化操作
        LOGGER.info("start upgrade bt share init...");
        String currentIp;
        try {
            currentIp = getLocalIP();
        } catch (BusinessException e) {
            LOGGER.error("obtain host ip error, can not make init bt server.", e);
            return;
        }
        if (needUpgrade(currentIp, upgradeTempPath)) {
            executeUpdate(currentIp, pythonScriptPath, terminalType);
            return;
        }
    }


    private boolean needUpgrade(String currentIp, String upgradeTempPath) {

        String ip = globalParameterAPI.findParameter(Constants.RCDC_SERVER_IP_GLOBAL_PARAMETER_KEY);

        if (StringUtils.isBlank(ip)) {
            // 第一次启动时，制作新版本的升级业务
            return true;
        }

        if (!ip.equals(currentIp)) {
            // 服务器ip变更时，bt种子需要重新制作
            return true;
        }

        File tempDir = new File(upgradeTempPath);
        if (tempDir.isDirectory()) {
            // 系统补丁包升级后，需要制作新版本的升级业务
            return true;
        }

        return false;
    }

    private void executeUpdate(String currentIp, String pythonScriptPath, TerminalTypeEnums terminalType) {
        LOGGER.info("start invoke pythonScript...");
        ShellCommandRunner runner = new ShellCommandRunner();
        String shellCmd = String.format(INIT_COMMAND, pythonScriptPath, currentIp);
        LOGGER.info("execute shell cmd : {}", shellCmd);
        runner.setCommand(shellCmd);
        try {
            String outStr = runner.execute(new BtShareInitReturnValueResolver(terminalType));
            LOGGER.debug("out String is :{}", outStr);
        } catch (BusinessException e) {
            LOGGER.error("bt share init error", e);
        }

        LOGGER.info("success invoke [{}] pythonScript...", terminalType.toString());
    }


    /**
     * Description: Function Description
     * Copyright: Copyright (c) 2018
     * Company: Ruijie Co., Ltd.
     * Create Time: 2019年1月28日
     *
     * @author nt
     */
    public class BtShareInitReturnValueResolver implements ReturnValueResolver<String> {

        private TerminalTypeEnums terminalType;

        public BtShareInitReturnValueResolver(TerminalTypeEnums terminalType) {
            Assert.notNull(terminalType, "terminalType cannot be null");
            this.terminalType = terminalType;
        }

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
            if (terminalType == TerminalTypeEnums.VDI_LINUX) {
                linuxVDIUpdatelistCacheInit.init();
            }
            if (terminalType == TerminalTypeEnums.VDI_ANDROID) {
                androidVDIUpdatelistCacheInit.init();
            }
            return outStr;
        }
    }

    /**
     * 获取ip
     *
     * @return ip
     */
    private String getLocalIP() throws BusinessException {
        BaseDetailNetworkRequest request = new BaseDetailNetworkRequest();
        BaseDetailNetworkInfoResponse response = networkAPI.detailNetwork(request);
        return response.getNetworkDTO().getIp();
    }
}