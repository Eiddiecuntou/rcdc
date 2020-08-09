package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbChangePasswordRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.offlinelogin.OfflineLoginSettingRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalCollectLogStatusResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalLogFileInfoResponse;
import com.ruijie.rcos.sk.base.exception.BusinessException;

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
     * @param terminalId 终端id请求参数对象
     * @throws BusinessException 业务异常
     */
    
    void shutdown(String terminalId) throws BusinessException;

    /**
     * 重启终端
     *
     * @param terminalId 终端id请求参数对象
     * @throws BusinessException 业务异常
     */
    
    void restart(String terminalId) throws BusinessException;

    /**
     * 修改终端管理员密码
     *
     * @param request 修改密码请求参数对象
     * @throws BusinessException 业务异常
     */
    
    void changePassword(CbbChangePasswordRequest request) throws BusinessException;

    /**
     * 收集终端日志
     *
     * @param terminalId 终端id请求参数对象
     * @throws BusinessException 业务异常
     */
    
    void collectLog(String terminalId) throws BusinessException;

    /**
     * 终端检测
     *
     * @param terminalId 终端检测请求参数对象
     * @throws BusinessException 业务异常
     */
    
    void singleDetect(String terminalId) throws BusinessException;

    /**
     * 获取终端收集日志状态
     * 
     * @param terminalId id请求参数
     * @return 终端日志收集状态信息
     * @throws BusinessException 业务异常
     */
    
    CbbTerminalCollectLogStatusResponse getCollectLog(String terminalId) throws BusinessException;

    /**
     * 获取终端收集日志路径
     * 
     * @param logName 请求参数
     * @return 终端收集日志路径
     * @throws BusinessException 业务异常
     */
    
    CbbTerminalLogFileInfoResponse getTerminalLogFileInfo(String logName) throws BusinessException;

    /**
     * 解除故障
     *
     * @param terminalId 终端id请求参数对象
     * @throws BusinessException 业务异常
     */
    void relieveFault(String terminalId) throws BusinessException;

    /**
     * IDV终端离线登录设置
     *
     * @param request 请求参数
     * @throws BusinessException 业务异常
     */
    void idvOfflineLoginSetting(OfflineLoginSettingRequest request) throws BusinessException;

    /**
     * IDV终端离线登录设置
     *
     * @return 返回成功失败
     * @throws BusinessException 业务异常
     */
    String queryOfflineLoginSetting() throws BusinessException;

    /**
     * 终端数据盘清空
     *
     * @param terminalId 请求参数
     * @throws BusinessException 业务异常
     */
    void clearIdvTerminalDataDisk(String terminalId) throws BusinessException;
}
