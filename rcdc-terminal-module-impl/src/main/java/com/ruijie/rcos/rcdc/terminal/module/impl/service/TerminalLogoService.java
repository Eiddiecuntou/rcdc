package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalLogoInfo;
import com.ruijie.rcos.sk.base.exception.BusinessException;

/**
 * Description:终端Logo操作接口
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/7/18
 *
 * @author hs
 */
public interface TerminalLogoService {

    String TERMINAL_LOGO = "terminalLogo";

    /**
     * 上传终端Logo
     *
     * @param terminalLogoInfo Logo信息
     * @param name     发送给终端的Action
     * @throws BusinessException 业务异常
     */
    void syncTerminalLogo(TerminalLogoInfo terminalLogoInfo, SendTerminalEventEnums name) throws BusinessException;

    /**
     * 获取终端Logo信息
     *
     * @return 终端Logo信息
     */
    TerminalLogoInfo getTerminalLogoInfo();

}
