package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
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
     * @param terminalId 终端id
     * @throws BusinessException 业务异常
     */
    void shutdown(String terminalId) throws BusinessException;

    /**
     * 重启终端
     *
     * @param terminalId 终端id
     * @throws BusinessException 业务异常
     */
    void restart(String terminalId) throws BusinessException;

    /**
     * 修改终端管理员密码
     *
     * @param password 密码
     * @throws BusinessException 业务异常
     */
    void changePassword(String password) throws BusinessException;

    /**
     * 收集终端日志
     *
     * @param terminalId 终端id
     * @throws BusinessException 业务异常
     */
    void collectLog(String terminalId) throws BusinessException;

    /**
     * 终端检测
     *
     * @param terminalId 终端id
     * @throws BusinessException 业务异常
     */
    void detect(String terminalId) throws BusinessException;

    /**
     * 发送终端检测命令
     *
     * @param detection 检测记录对象
     * @throws BusinessException 业务异常
     */
    void sendDetectRequest(TerminalDetectionEntity detection) throws BusinessException;

    /**
     * 获取终端管理员密码
     * 
     * @return 终端管理员密码
     * @throws BusinessException 业务异常
     */
    String getTerminalPassword() throws BusinessException;

    /**
     * 解除报障
     * @param terminalId 终端id
     * @throws BusinessException 业务异常
     */
    void relieveFault(String terminalId) throws BusinessException;
}
