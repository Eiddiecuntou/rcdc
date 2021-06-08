package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.hciadapter.module.def.api.CloudPlatformMgmtAPI;
import com.ruijie.rcos.rcdc.hciadapter.module.def.dto.ClusterVirtualIpDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalOsTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist.AndroidUpdatelistCacheInit;
import com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist.LinuxUpdatelistCacheInit;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalComponentInitService;
import com.ruijie.rcos.sk.base.concurrent.ThreadExecutors;
import com.ruijie.rcos.sk.base.env.Enviroment;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.shell.ShellCommandRunner;
import com.ruijie.rcos.sk.base.util.StringUtils;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultRequest;
import com.ruijie.rcos.sk.modulekit.api.comm.DtoResponse;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.util.concurrent.ExecutorService;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/10/19
 *
 * @author nting
 */
@Service
public class TerminalComponentInitServiceImpl implements TerminalComponentInitService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalComponentInitServiceImpl.class);

    private static final String INIT_PYTHON_SCRIPT_PATH = "/data/web/rcdc/shell/update_component_package.py";

    private static final String INIT_COMMAND = "python %s %s %s";

    private static final String EXECUTE_SHELL_SUCCESS_RESULT = "success";

    private static final String TERMINAL_COMPONENT_PACKAGE_INIT_STATUS_GLOBAL_PARAMETER_KEY_PREFIX = "terminal_component_package_init_status_";

    private static final String TERMINAL_COMPONENT_PACKAGE_INIT_SUCCESS = "success";

    private static final String TERMINAL_COMPONENT_PACKAGE_INIT_FAIL = "fail";

    private static ExecutorService EXECUTOR_SERVICE =
            ThreadExecutors.newBuilder(TerminalComponentInitServiceImpl.class.getName()).maxThreadNum(3).queueSize(1).build();

    @Autowired
    private GlobalParameterAPI globalParameterAPI;

    @Autowired
    private LinuxUpdatelistCacheInit linuxUpdatelistCacheInit;

    @Autowired
    private AndroidUpdatelistCacheInit androidUpdatelistCacheInit;

    @Autowired
    private CloudPlatformMgmtAPI cloudPlatformMgmtAPI;

    @Override
    public void initLinux() {
        LOGGER.info("开始异步执行初始化Linux 终端升级组件");
        EXECUTOR_SERVICE.execute(() -> initLinuxTerminalComponent());

        updateClusterVip();
    }

    @Override
    public void initAndroid() {
        LOGGER.info("开始异步执行初始化Android 终端升级组件");
        EXECUTOR_SERVICE.execute(() -> initAndroidTerminalComponent());

        updateClusterVip();
    }

    private void updateClusterVip() {
        // 更新数据库中终端组件升级包使用的服务器ip
        try {
            globalParameterAPI.updateParameter(Constants.RCDC_CLUSTER_VIRTUAL_IP_GLOBAL_PARAMETER_KEY, getLocalIP());
        } catch (BusinessException e) {
            LOGGER.error("更新终端组件升级包使用的服务器ip异常", e);
        }
    }

    private void initLinuxTerminalComponent() {
        CbbTerminalOsTypeEnums osType = CbbTerminalOsTypeEnums.LINUX;
        String tempPath = Constants.LINUX_TERMINAL_TERMINAL_COMPONET_UPGRADE_TEMP_PATH;
        // 检查环境,判断是否需要升级,需要则进行升级并更新缓存
        checkAndUpgrade(osType, tempPath);
    }

    private void initAndroidTerminalComponent() {
        CbbTerminalOsTypeEnums osType = CbbTerminalOsTypeEnums.ANDROID;
        String tempPath = Constants.ANDROID_TERMINAL_TERMINAL_COMPONET_UPGRADE_TEMP_PATH;
        // 检查环境,判断是否需要升级,需要则进行升级并更新缓存
        checkAndUpgrade(osType, tempPath);
    }

    private void checkAndUpgrade(CbbTerminalOsTypeEnums osType, String upgradeTempPath) {
        // 添加操作系统判断，使初始化失败不影响开发阶段的调试
        boolean isDevelop = Enviroment.isDevelop();
        LOGGER.info("environment is develop: {}", isDevelop);
        if (isDevelop) {
            LOGGER.info("environment is develop, skip upgrade bt share init...");
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

        String globalParameterKey = TERMINAL_COMPONENT_PACKAGE_INIT_STATUS_GLOBAL_PARAMETER_KEY_PREFIX + osType.name().toLowerCase();
        String lastUpgradeStatus = globalParameterAPI.findParameter(globalParameterKey);
        if (needUpgrade(currentIp, upgradeTempPath, lastUpgradeStatus)) {
            executeUpdate(currentIp, osType);
            return;
        }
        updateCache(osType);
    }

    private boolean needUpgrade(String currentIp, String upgradeTempPath, String lastUpgradeStatus) {

        String ip = globalParameterAPI.findParameter(Constants.RCDC_CLUSTER_VIRTUAL_IP_GLOBAL_PARAMETER_KEY);

        if (StringUtils.isBlank(ip)) {
            // 第一次启动时，制作新版本的升级业务
            return true;
        }

        if (!ip.equals(currentIp)) {
            // 服务器ip变更时，bt种子需要重新制作
            return true;
        }

        File tempDir = new File(upgradeTempPath);
        LOGGER.info("upgradeTempPath: {}", upgradeTempPath);
        LOGGER.info("tempDir.isDirectory() {}", tempDir.isDirectory());
        if (tempDir.isDirectory()) {
            // 系统补丁包升级后，需要制作新版本的升级业务
            return true;
        }

        return TERMINAL_COMPONENT_PACKAGE_INIT_FAIL.equals(lastUpgradeStatus);
    }

    private void executeUpdate(String currentIp, CbbTerminalOsTypeEnums osType) {

        String globalParameterKey = TERMINAL_COMPONENT_PACKAGE_INIT_STATUS_GLOBAL_PARAMETER_KEY_PREFIX + osType.name().toLowerCase();

        // 先将执行结果设置为失败，防止异常中断不再执行脚本
        globalParameterAPI.updateParameter(globalParameterKey, TERMINAL_COMPONENT_PACKAGE_INIT_FAIL);

        LOGGER.info("start invoke pythonScript...");
        ShellCommandRunner runner = new ShellCommandRunner();
        String shellCmd = String.format(INIT_COMMAND, INIT_PYTHON_SCRIPT_PATH, currentIp, osType.name().toLowerCase());
        LOGGER.info("execute shell cmd : {}", shellCmd);
        runner.setCommand(shellCmd);

        try {
            String outStr = runner.execute(new BtShareInitReturnValueResolver(osType));
            LOGGER.debug("out String is :{}", outStr);
            LOGGER.info("success invoke [{}] pythonScript...", osType.name());
            globalParameterAPI.updateParameter(globalParameterKey, TERMINAL_COMPONENT_PACKAGE_INIT_SUCCESS);
        } catch (BusinessException ex) {
            LOGGER.error("bt share init error", ex);
            // 脚本执行失败后进行重试
            LOGGER.info("invoke [{}] pythonScript failed, retry", osType.name());
            globalParameterAPI.updateParameter(globalParameterKey, TERMINAL_COMPONENT_PACKAGE_INIT_FAIL);
            waitSeconds();
            executeUpdate(currentIp, osType);
        }

    }

    private void waitSeconds() {
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            LOGGER.error("等待异常！", e);
        }
    }

    /**
     * Description: Function Description
     * Copyright: Copyright (c) 2018
     * Company: Ruijie Co., Ltd.
     * Create Time: 2019年1月28日
     *
     * @author nting
     */
    public class BtShareInitReturnValueResolver implements ShellCommandRunner.ReturnValueResolver<String> {

        private CbbTerminalOsTypeEnums osType;

        public BtShareInitReturnValueResolver(CbbTerminalOsTypeEnums osType) {
            Assert.notNull(osType, "osType cannot be null");
            this.osType = osType;
        }

        @Override
        public String resolve(String command, Integer exitValue, String outStr) throws BusinessException {
            Assert.hasText(command, "command can not be null");
            Assert.notNull(exitValue, "existValue can not be null");
            Assert.hasText(outStr, "outStr can not be null");

            if (exitValue.intValue() != 0 || !EXECUTE_SHELL_SUCCESS_RESULT.equals(outStr.toLowerCase().trim())) {
                LOGGER.error("bt share init python script execute error, exitValue: {}, outStr: {}", exitValue, outStr);
                throw new BusinessException(BusinessKey.RCDC_SYSTEM_CMD_EXECUTE_FAIL);
            }

            // 更新缓存中的updatelist
            updateCache(osType);
            return outStr;
        }
    }

    private void updateCache(CbbTerminalOsTypeEnums osType) {
        // 更新缓存中的updatelist
        if (osType == CbbTerminalOsTypeEnums.LINUX) {
            LOGGER.info("init linux updatelist cache");
            linuxUpdatelistCacheInit.init();
        }
        if (osType == CbbTerminalOsTypeEnums.ANDROID) {
            LOGGER.info("init android updatelist cache");
            androidUpdatelistCacheInit.init();
        }
    }

    /**
     * 获取ip
     *
     * @return ip
     */
    private String getLocalIP() throws BusinessException {
        DtoResponse<ClusterVirtualIpDTO> response = cloudPlatformMgmtAPI.getClusterVirtualIp(new DefaultRequest());
        Assert.notNull(response, "response can not be null");

        return response.getDto().getClusterVirtualIpIp();
    }
}
