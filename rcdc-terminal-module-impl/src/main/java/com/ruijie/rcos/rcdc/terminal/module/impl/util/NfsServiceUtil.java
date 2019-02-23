package com.ruijie.rcos.rcdc.terminal.module.impl.util;

import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
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
    
    private static final String NFS_SERVER_START_CMD = "exportfs -o rw,no_root_squash,insecure,async *:%s";
    
    private static final String NFS_SERVER_STOP_CMD = "exportfs -u *:%s";

    /**
     * 开启NFS服务
     * @throws BusinessException 
     */
    public static void startService() throws BusinessException {
        
        String startCmd = String.format(NFS_SERVER_START_CMD, Constants.TERMINAL_SYSTEM_UPGRADE_ISO_NFS_DIR);
        
        LOGGER.info("start nfs server, cmd : {}", startCmd);
        ShellCommandRunner runner = new ShellCommandRunner();
        runner.setCommand(startCmd);
        try {
            String outStr = runner.execute();
            LOGGER.debug("out String is :{}", outStr);
        } catch (BusinessException e) {
            LOGGER.error("start nfs server error", e);
            throw new BusinessException(BusinessKey.RCDC_SYSTEM_CMD_EXECUTE_FAIL, e);
        }
        LOGGER.info("start nfs server success");
    }
    
    /**
     * 关闭NFS服务
     * @throws BusinessException 
     */
    public static void shutDownService() throws BusinessException {
        
        String stopCmd = String.format(NFS_SERVER_STOP_CMD, Constants.TERMINAL_SYSTEM_UPGRADE_ISO_NFS_DIR);
        
        LOGGER.info("stop nfs server, cmd : {}", stopCmd);
        ShellCommandRunner runner = new ShellCommandRunner();
        runner.setCommand(stopCmd);
        try {
            String outStr = runner.execute();
            LOGGER.debug("out String is :{}", outStr);
        } catch (BusinessException e) {
            LOGGER.error("stop nfs server error", e);
            throw new BusinessException(BusinessKey.RCDC_SYSTEM_CMD_EXECUTE_FAIL, e);
        }
        LOGGER.info("stop nfs server success");
    }

}
