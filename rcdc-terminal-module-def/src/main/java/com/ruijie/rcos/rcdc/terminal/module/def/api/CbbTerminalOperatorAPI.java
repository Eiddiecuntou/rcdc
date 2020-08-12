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
     * @api {POST} CbbTerminalOperatorAPI.shutdown 关闭终端
     * @apiName shutdown
     * @apiGroup CbbTerminalOperatorAPI
     * @apiDescription 关闭终端
     * @apiParam (请求体字段说明) {String} terminalId 终端id
     *
     */
    /**
     * 关闭终端
     *
     * @param terminalId 终端id请求参数对象
     * @throws BusinessException 业务异常
     */
    
    void shutdown(String terminalId) throws BusinessException;

    /**
     * @api {POST} CbbTerminalOperatorAPI.restart 重启终端
     * @apiName restart
     * @apiGroup CbbTerminalOperatorAPI
     * @apiDescription 重启终端
     * @apiParam (请求体字段说明) {String} terminalId 终端id
     *
     */
    /**
     * 重启终端
     *
     * @param terminalId 终端id请求参数对象
     * @throws BusinessException 业务异常
     */
    
    void restart(String terminalId) throws BusinessException;

    /**
     * @api {POST} CbbTerminalOperatorAPI.changePassword 修改终端管理员密码
     * @apiName changePassword
     * @apiGroup CbbTerminalOperatorAPI
     * @apiDescription 修改终端管理员密码
     * @apiParam (请求体字段说明) {CbbChangePasswordRequest} request CbbChangePasswordRequest
     * @apiParam (请求体字段说明) {String} request.password 密码
     *
     */
    /**
     * 修改终端管理员密码
     *
     * @param request 修改密码请求参数对象
     * @throws BusinessException 业务异常
     */
    void changePassword(CbbChangePasswordRequest request) throws BusinessException;

    /**
     * @api {POST} CbbTerminalOperatorAPI.collectLog 收集终端日志
     * @apiName collectLog
     * @apiGroup CbbTerminalOperatorAPI
     * @apiDescription 收集终端日志
     * @apiParam (请求体字段说明) {CbbTerminalIdRequest} request CbbTerminalIdRequest
     * @apiParam (请求体字段说明) {String} request.terminalId 终端id
     *
     */
    /**
     * 收集终端日志
     *
     * @param terminalId 终端id请求参数对象
     * @throws BusinessException 业务异常
     */
    void collectLog(String terminalId) throws BusinessException;

    /**
     * @api {POST} CbbTerminalOperatorAPI.singleDetect 终端检测
     * @apiName singleDetect
     * @apiGroup CbbTerminalOperatorAPI
     * @apiDescription 终端检测
     * @apiParam (请求体字段说明) {String} terminalId 终端id
     *
     */
    /**
     * 终端检测
     *
     * @param terminalId 终端检测请求参数对象
     * @throws BusinessException 业务异常
     */
    void singleDetect(String terminalId) throws BusinessException;

    /**
     * @api {POST} CbbTerminalOperatorAPI.getCollectLog 获取终端收集日志状态
     * @apiName getCollectLog
     * @apiGroup CbbTerminalOperatorAPI
     * @apiDescription 获取终端收集日志状态
     * @apiParam (请求体字段说明) {String} terminalId 终端id
     *
     * @apiSuccess (响应字段说明) {CbbTerminalCollectLogStatusResponse} response CbbTerminalCollectLogStatusResponse
     * @apiSuccess (响应字段说明) {CbbCollectLogStateEnums="DOING","DONE","FAILURE"} response.state 日志收集状态
     * @apiSuccess (响应字段说明) {String} response.logName 日志名称
     */
    /**
     * 获取终端收集日志状态
     * 
     * @param terminalId id请求参数
     * @return 终端日志收集状态信息
     * @throws BusinessException 业务异常
     */
    CbbTerminalCollectLogStatusResponse getCollectLog(String terminalId) throws BusinessException;

    /**
     * @api {POST} CbbTerminalOperatorAPI.getTerminalLogFileInfo 获取终端收集日志路径
     * @apiName getTerminalLogFileInfo
     * @apiGroup CbbTerminalOperatorAPI
     * @apiDescription 获取终端收集日志路径
     * @apiParam (请求体字段说明) {String} logName 日志名称
     *
     * @apiSuccess (响应字段说明) {CbbTerminalLogFileInfoResponse} response CbbTerminalLogFileInfoResponse
     * @apiSuccess (响应字段说明) {String} response.logFilePath 日志路径
     * @apiSuccess (响应字段说明) {String} response.logFileName 日志文件名
     * @apiSuccess (响应字段说明) {String} response.suffix 日志文件名后缀
     */
    /**
     * 获取终端收集日志路径
     * 
     * @param logName 请求参数
     * @return 终端收集日志路径
     * @throws BusinessException 业务异常
     */
    CbbTerminalLogFileInfoResponse getTerminalLogFileInfo(String logName) throws BusinessException;

    /**
     * @api {POST} CbbTerminalOperatorAPI.relieveFault 解除故障
     * @apiName relieveFault
     * @apiGroup CbbTerminalOperatorAPI
     * @apiDescription 解除故障
     * @apiParam (请求体字段说明) {CbbTerminalIdRequest} request CbbTerminalIdRequest
     * @apiParam (请求体字段说明) {String} request.terminalId 终端id
     *
     */
    /**
     * 解除故障
     *
     * @param terminalId 终端id请求参数对象
     * @throws BusinessException 业务异常
     */
    void relieveFault(String terminalId) throws BusinessException;

    /**
     * @api {POST} CbbTerminalOperatorAPI.idvOfflineLoginSetting IDV终端离线登录设置
     * @apiName idvOfflineLoginSetting
     * @apiGroup CbbTerminalOperatorAPI
     * @apiDescription IDV终端离线登录设置
     * @apiParam (请求体字段说明) {OfflineLoginSettingRequest} request OfflineLoginSettingRequest
     * @apiParam (请求体字段说明) {Integer} request.offlineAutoLocked 离线自动锁定
     *
     */
    /**
     * IDV终端离线登录设置
     *
     * @param request 请求参数
     * @throws BusinessException 业务异常
     */
    void idvOfflineLoginSetting(OfflineLoginSettingRequest request) throws BusinessException;

    /**
     * @api {POST} CbbTerminalOperatorAPI.queryOfflineLoginSetting 查询IDV终端离线登录设置
     * @apiName queryOfflineLoginSetting
     * @apiGroup CbbTerminalOperatorAPI
     * @apiDescription 查询IDV终端离线登录设置
     *
     * @apiSuccess (响应字段说明) {String} setting 脱网登录锁定日期设定值
     */
    /**
     * IDV终端离线登录设置
     *
     * @return 返回成功失败
     * @throws BusinessException 业务异常
     */
    String queryOfflineLoginSetting() throws BusinessException;

    /**
     * @api {POST} CbbTerminalOperatorAPI.clearIdvTerminalDataDisk 终端数据盘清空
     * @apiName clearIdvTerminalDataDisk
     * @apiGroup CbbTerminalOperatorAPI
     * @apiDescription 终端数据盘清空
     * @apiParam (请求体字段说明) {String} terminalId 终端id
     *
     */
    /**
     * 终端数据盘清空
     *
     * @param terminalId 请求参数
     * @throws BusinessException 业务异常
     */
    void clearIdvTerminalDataDisk(String terminalId) throws BusinessException;
}
