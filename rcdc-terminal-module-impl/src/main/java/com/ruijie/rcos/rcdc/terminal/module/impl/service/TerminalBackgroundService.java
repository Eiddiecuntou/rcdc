package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalBackgroundInfo;
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

    /**
     * 发起同步背景图片请求
     * @param terminalSyncBackgroundInfo 向终端同步图片的信息
     * @throws BusinessException 因为异常
     */
    void syncTerminalBackground(TerminalBackgroundInfo terminalSyncBackgroundInfo) throws BusinessException;
}
