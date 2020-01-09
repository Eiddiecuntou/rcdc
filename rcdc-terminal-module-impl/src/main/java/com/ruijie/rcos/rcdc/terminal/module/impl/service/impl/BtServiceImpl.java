package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.SeedFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.BtService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.shell.ShellCommandRunner;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/23
 *
 * @author hs
 */
@Service
public class BtServiceImpl implements BtService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BtServiceImpl.class);

    private static final String MAKE_BT_COMMAND = "python %s %s %s %s";

    private static final String START_BT_SHARE_COMMAND = "python %s %s %s";

    private static final String STOP_BT_SHARE_COMMAND = "python %s %s";

    private static final String INIT_PYTHON_SCRIPT_PATH = "/data/web/rcdc/shell/";

    private static final String MAKE_BT_SCRIPT_NAME = "make_ota_bt.py";

    private static final String START_BT_SCRIPT_NAME = "start_ota_bt_share.py";

    private static final String STOP_BT_SCRIPT_NAME = "stop_ota_bt_share.py";

    private static final String EXECUTE_SHELL_FAIL_RESULT = "fail";

    @Override
    public SeedFileInfo makeBtSeed(String filePath, String seedSavePath, String ipAddr) throws BusinessException {
        Assert.notNull(filePath, "filePath can not be blank");
        Assert.notNull(seedSavePath, "seedSavePath can not be blank");
        Assert.notNull(ipAddr, "ipAddr can not be blank");
        ShellCommandRunner runner = new ShellCommandRunner();
        String makeBtScriptPath = INIT_PYTHON_SCRIPT_PATH + MAKE_BT_SCRIPT_NAME;
        String seedInfoStr;
        String shellCmd = String.format(MAKE_BT_COMMAND, makeBtScriptPath, filePath, seedSavePath, ipAddr);
        LOGGER.info("excecute shell cmd : {}", shellCmd);
        runner.setCommand(shellCmd);
        try {
            seedInfoStr = runner.execute();
            checkResp(seedInfoStr, BusinessKey.RCDC_TERMINAL_OTA_UPGRADE_MAKE_SEED_FILE_FAIL);
            LOGGER.info("seed info str is :{}", seedInfoStr);
            return JSON.parseObject(seedInfoStr, SeedFileInfo.class);
        } catch (Exception e) {
            LOGGER.error("make seed file error", e);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OTA_UPGRADE_MAKE_SEED_FILE_FAIL, e);
        }
    }

    @Override
    public void startBtShare(String seedFilePath, String filePath) throws BusinessException {
        Assert.hasText(seedFilePath, "seedFilePath can not be blank");
        Assert.hasText(filePath, "filePath can not be blank");

        LOGGER.info("调用开启分享脚本");
        String startBtScriptPath = INIT_PYTHON_SCRIPT_PATH + START_BT_SCRIPT_NAME;
        String shellCmd = String.format(START_BT_SHARE_COMMAND, startBtScriptPath, seedFilePath, filePath);
        runShellCmd(shellCmd, BusinessKey.RCDC_TERMINAL_OTA_UPGRADE_BT_SHARE_SEED_FILE_FAIL);
    }

    @Override
    public void stopBtShare(String seedFilePath) throws BusinessException {
        Assert.hasText(seedFilePath, "seedFilePath can not be blank");

        String stopBtScriptPath = INIT_PYTHON_SCRIPT_PATH + STOP_BT_SCRIPT_NAME;
        String shellCmd = String.format(STOP_BT_SHARE_COMMAND, stopBtScriptPath, seedFilePath);
        runShellCmd(shellCmd, BusinessKey.RCDC_TERMINAL_OTA_UPGRADE_STOP_SHARE_SEED_FILE_FAIL);
    }

    private void runShellCmd(String shellCmd, String businessKey) throws BusinessException {
        Assert.hasText(shellCmd, "shellCmd can not be blank");

        LOGGER.info("shellCmd: {}", shellCmd);
        ShellCommandRunner runner = new ShellCommandRunner();
        runner.setCommand(shellCmd);
        String responseStr = runner.execute();
        LOGGER.info("responseStr: {}", responseStr);
        checkResp(responseStr, businessKey);
    }

    private void checkResp(String responseStr, String businessKey) throws BusinessException {
        Assert.hasText(responseStr, "response can not be blank");

        if (EXECUTE_SHELL_FAIL_RESULT.equals(responseStr.toLowerCase().trim())) {
            throw new BusinessException(businessKey);
        }
    }
}
