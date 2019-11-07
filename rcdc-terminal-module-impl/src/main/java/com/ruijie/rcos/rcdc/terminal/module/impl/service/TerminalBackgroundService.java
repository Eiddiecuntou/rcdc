package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.sk.base.exception.BusinessException;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time:  2019/11/6
 *
 * @author songxiang
 */
public interface TerminalBackgroundService {

    String TERMINAL_BACKGROUND = "terminal_background";

    void syncTerminalLogo(String name) throws BusinessException;
}
