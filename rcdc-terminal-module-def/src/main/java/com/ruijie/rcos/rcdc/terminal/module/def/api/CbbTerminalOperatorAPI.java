package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.tx.NoRollback;

/**
 * Description: 终端操作接口
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/5
 *
 * @author Jarman
 */
public interface CbbTerminalOperatorAPI {

    /**
     * 关闭终端
     *
     * @param terminalId 终端id
     * @throws BusinessException 业务异常
     */
    @NoRollback
    void shutdown(String terminalId) throws BusinessException;

    /**
     * 重启终端
     *
     * @param terminalId 终端id
     * @throws BusinessException 业务异常
     */
    @NoRollback
    void restart(String terminalId) throws BusinessException;

    /**
     * 修改终端管理员密码
     *
     * @param terminalId 终端id
     * @param password   密码
     * @throws BusinessException 业务异常
     */
    @NoRollback
    void changePassword(String terminalId, String password) throws BusinessException;

    /**
     * 收集终端日志
     *
     * @param terminalId 终端id
     * @throws BusinessException 业务异常
     */
    @NoRollback
    void gatherLog(String terminalId) throws BusinessException;

    /**
     * 终端检测
     *
     * @param terminalId 终端id
     * @throws BusinessException 业务异常
     */
    @NoRollback
    void detect(String terminalId) throws BusinessException;

    /**
     * 批量终端检测
     *
     * @param terminalIdArr 终端id数组
     * @throws BusinessException 业务异常
     */
    @NoRollback
    void detect(String[] terminalIdArr) throws BusinessException;

    /**
     * 获取终端日志文件名
     *
     * @param terminalId 终端id
     * @return 返回日志文件名
     */
    @NoRollback
    String getTerminalLogName(String terminalId) throws BusinessException;


}
