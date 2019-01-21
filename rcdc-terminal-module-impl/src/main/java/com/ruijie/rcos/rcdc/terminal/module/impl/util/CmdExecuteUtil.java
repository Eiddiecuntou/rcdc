package com.ruijie.rcos.rcdc.terminal.module.impl.util;

import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.sk.base.exception.BusinessException;

/**
 * 
 * Description: 系统命令执行工具
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月19日
 * 
 * @author nt
 */
public class CmdExecuteUtil {

    /**
     * 执行系统指令
     * 
     * @param cmd 系统指令
     * @throws BusinessException 业务异常
     */
    public static void executeCmd(String cmd) throws BusinessException {
        Assert.notNull(cmd, "cmd 不能为空");

        int ret = 0;
        try {
            Process exec = Runtime.getRuntime().exec(cmd);
            ret = exec.waitFor();
        } catch (Exception e) {
            throw new BusinessException(BusinessKey.RCDC_SYSTEM_CMD_EXECUTE_FAIL , e);
        }

        if (ret != 0) {
            throw new BusinessException(BusinessKey.RCDC_SYSTEM_CMD_EXECUTE_FAIL);
        }

    }

}
