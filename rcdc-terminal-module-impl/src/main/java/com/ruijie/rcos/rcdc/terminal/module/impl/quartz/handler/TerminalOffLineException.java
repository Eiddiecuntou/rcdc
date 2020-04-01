package com.ruijie.rcos.rcdc.terminal.module.impl.quartz.handler;

import com.ruijie.rcos.rcdc.terminal.module.def.PublicBusinessKey;
import com.ruijie.rcos.sk.base.exception.BusinessException;

/**
 * 
 * Description: 终端离线异常
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月22日
 * 
 * @author nt
 */
public class TerminalOffLineException extends BusinessException {

    /**
     * 
     */
    private static final long serialVersionUID = 4403025956187395728L;

    public TerminalOffLineException(Exception ex) {
        super(PublicBusinessKey.RCDC_TERMINAL_OFFLINE, ex);
    }

}
