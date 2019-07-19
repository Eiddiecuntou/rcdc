package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import com.ruijie.rcos.sk.base.exception.BusinessException;

/**
 * Description:
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
     * @throws BusinessException 业务异常
     */
    void syncTerminalLogo(String logoName) throws BusinessException;

}
