package com.ruijie.rcos.rcdc.terminal.module.impl.tx;

import com.ruijie.rcos.sk.base.exception.BusinessException;

/**
 * 
 * Description: 终端基本信息存在事物的操作
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月31日
 * 
 * @author nt
 */
public interface TerminalBasicInfoServiceTx {

    /**
     * 删除终端基本信息
     * 
     * @param terminalId 终端id
     * @throws BusinessException 业务异常
     */
    void deleteTerminal(String terminalId) throws BusinessException;

}
