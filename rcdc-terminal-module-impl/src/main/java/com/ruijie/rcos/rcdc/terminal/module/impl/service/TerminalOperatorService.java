package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.TerminalDetectRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.TerminalIdRequest;
import com.ruijie.rcos.sk.base.exception.BusinessException;

/**
 * Description: 终端操作接口
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/31
 *
 * @author Jarman
 */
public interface TerminalOperatorService {

    /**
     * 关闭终端
     *
     * @param terminalId
     * @throws BusinessException
     */
    void shutdown(String terminalId) throws BusinessException;

    /**
     * 重启终端
     *
     * @param terminalId
     * @throws BusinessException
     */
    void restart(String terminalId) throws BusinessException;

    /**
     * 修改终端管理员密码
     *
     * @param terminalId
     * @param password
     * @throws BusinessException
     */
    void changePassword(String terminalId, String password) throws BusinessException;

    /**
     * 收集终端日志
     *
     * @param terminalId
     */
    void gatherLog(String terminalId) throws BusinessException;

    ;

    /**
     * 终端检测
     *
     * @param terminalId
     */
    void detect(String terminalId) throws BusinessException;

    ;
}
