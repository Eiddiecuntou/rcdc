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

    private static final String BT_SHARE_COMMAND = "python %s %s";

    private static final String INIT_PYTHON_SCRIPT_PATH = "/data/web/rcdc/shell/";

    private static final String MAKE_BT_SCRIPT_NAME = "make_ota_bt.py";

    private static final String START_BT_SCRIPT_NAME = "start_ota_bt_share.py";

    private static final String STOP_BT_SCRIPT_NAME = "stop_ota_bt_share.py";

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
            LOGGER.debug("seed info str is :{}", seedInfoStr);
            return JSON.parseObject(seedInfoStr, SeedFileInfo.class);
        } catch (BusinessException e) {
            LOGGER.error("make seed file error", e);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OTA_UPGRADE_MAKE_SEED_FILE_FAIL, e);
        }
    }

    @Override
    public void startBtShare(String seedFilePath) throws BusinessException {
        Assert.hasText(seedFilePath, "seedFilePath can not be blank");
        String startBtScriptPath = INIT_PYTHON_SCRIPT_PATH + START_BT_SCRIPT_NAME;
        btShare(seedFilePath, startBtScriptPath);
    }

    @Override
    public void stopBtShare(String seedFilePath) throws BusinessException {
        Assert.hasText(seedFilePath, "seedFilePath can not be blank");
        String stopBtScriptPath = INIT_PYTHON_SCRIPT_PATH + STOP_BT_SCRIPT_NAME;
        btShare(seedFilePath, stopBtScriptPath);
    }

    private void btShare(String seedFilePath, String scriptPath) throws BusinessException {
        Assert.notNull(seedFilePath, "seedFilePath can not be null");
        Assert.notNull(scriptPath, "scriptPath can not be null");
        ShellCommandRunner runner = new ShellCommandRunner();
        String shellCmd = String.format(BT_SHARE_COMMAND, scriptPath, seedFilePath);
        runner.setCommand(shellCmd);
        runner.execute();
    }
}
