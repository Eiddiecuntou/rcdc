package com.ruijie.rcos.rcdc.terminal.module.impl.model;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.shell.ShellCommandRunner.ReturnValueResolver;

/**
 * 
 * Description: shell指令执行返回值处理
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月20日
 * 
 * @author nt
 */
public class SimpleCmdReturnValueResolver implements ReturnValueResolver<String> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleCmdReturnValueResolver.class);

    @Override
    public String resolve(String command, Integer exitValue, @Nullable String outStr) throws BusinessException {
        Assert.hasText(command, "command can not be empty");
        Assert.notNull(exitValue, "exitValue can not be empty");
        
        if (exitValue.intValue() != 0) {
            LOGGER.error("shell cmd execute error, exitValue: {}, outStr: {}", exitValue, outStr);
            throw new BusinessException(BusinessKey.RCDC_SYSTEM_CMD_EXECUTE_FAIL);
        }
        
        return outStr;
    }

}
