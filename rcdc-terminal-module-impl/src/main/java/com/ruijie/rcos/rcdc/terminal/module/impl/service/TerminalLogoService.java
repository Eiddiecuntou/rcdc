package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
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

    /**
     * 上传终端Logo
     *
     * @param logoName Logo名字
     * @param name 发送给终端的Action
     * @throws BusinessException 业务异常
     */
    void syncTerminalLogo(String logoName, SendTerminalEventEnums name) throws BusinessException;

    /**
     * 获取终端Logo名
     *
     * @return 终端Logo名
     * @throws BusinessException 业务异常
     */
    String getTerminalLogoName() throws BusinessException;

}
