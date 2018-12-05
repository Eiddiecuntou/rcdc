package com.ruijie.rcos.rcdc.terminal.module.impl.util;

import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.util.Assert;

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

        boolean isSuccess = false;

        try {
            Process exec = Runtime.getRuntime().exec(cmd);
            int ret = exec.waitFor();
            if (ret == 0) {
                isSuccess = true;
            }

        } catch (Exception e) {
            throw new BusinessException(BusinessKey.RCDC_SYSTEM_CMD_EXECUTE_FAIL , e);
        }

        if (!isSuccess) {
            throw new BusinessException(BusinessKey.RCDC_SYSTEM_CMD_EXECUTE_FAIL);
        }

    }

}
