package com.ruijie.rcos.rcdc.terminal.module.impl.util;

import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.SimpleCmdReturnValueResolver;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.shell.ShellCommandRunner;

/**
 * 
 * Description: NFS服务工具
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月30日
 * 
 * @author nt
 */
public class NfsServiceUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(NfsServiceUtil.class);

    private static final String NFS_SERVER_START_CMD = "systemctl start nfs";

    private static final String NFS_SERVER_STOP_CMD = "systemctl stop nfs";

    /**
     * 开启NFS服务
     * 
     * @throws BusinessException 业务异常
     */
    public static void startService() throws BusinessException {

        LOGGER.info("start nfs server, cmd : {}", NFS_SERVER_START_CMD);
        ShellCommandRunner runner = new ShellCommandRunner();
        runner.setCommand(NFS_SERVER_START_CMD);
        try {
            String outStr = runner.execute(new SimpleCmdReturnValueResolver());
            LOGGER.debug("out String is :{}", outStr);
        } catch (BusinessException e) {
            LOGGER.error("start nfs server error", e);
            throw new BusinessException(BusinessKey.RCDC_SYSTEM_CMD_EXECUTE_FAIL, e);
        }
        LOGGER.info("start nfs server success");
    }

    /**
     * 关闭NFS服务
     * 
     * @throws BusinessException 业务异常
     */
    public static void shutDownService() throws BusinessException {

        LOGGER.info("stop nfs server, cmd : {}", NFS_SERVER_STOP_CMD);
        ShellCommandRunner runner = new ShellCommandRunner();
        runner.setCommand(NFS_SERVER_STOP_CMD);
        try {
            String outStr = runner.execute(new SimpleCmdReturnValueResolver());
            LOGGER.debug("out String is :{}", outStr);
        } catch (Exception e) {
            // 卸载目录失败
            LOGGER.error("stop nfs server error", e);
        }
        LOGGER.info("stop nfs server finish");
    }

}
