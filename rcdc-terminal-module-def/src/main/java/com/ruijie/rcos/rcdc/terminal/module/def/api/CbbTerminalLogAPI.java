package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalCollectLogStatusDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalLogFileInfoDTO;
import com.ruijie.rcos.sk.base.exception.BusinessException;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/8/20
 *
 * @author hs
 */
public interface CbbTerminalLogAPI {

    /**
     * @api {POST} CbbTerminalLogAPI.collectLog 收集终端日志
     * @apiName collectLog
     * @apiGroup CbbTerminalLogAPI
     * @apiDescription 收集终端日志
     * @apiParam (请求体字段说明) {String} request.terminalId 终端id
     *
     * @apiErrorExample {json} 异常码列表
     *  {code:rcdc_terminal_offline_cannot_collect_log message:当前终端[{0}({1})]处于离线状态，无法收集日志}
     *  {code:rcdc_terminal_offline message:终端连接断开}
     *  {code:rcdc_terminal_operate_msg_send_fail message:发送{0}消息超时，请检查网络或终端状态}
     */
    /**
     * 收集终端日志
     *
     * @param terminalId 终端id请求参数对象
     * @throws BusinessException 业务异常
     */
    void collectLog(String terminalId) throws BusinessException;

    /**
     * @api {POST} CbbTerminalLogAPI.getCollectLog 获取终端收集日志状态
     * @apiName getCollectLog
     * @apiGroup CbbTerminalLogAPI
     * @apiDescription 获取终端收集日志状态
     * @apiParam (请求体字段说明) {String} terminalId 终端id
     *
     * @apiSuccess (响应字段说明) {CbbTerminalCollectLogStatusDTO} response CbbTerminalCollectLogStatusDTO
     * @apiSuccess (响应字段说明) {CbbCollectLogStateEnums="DOING","DONE","FAILURE"} response.state 日志收集状态
     * @apiSuccess (响应字段说明) {String} response.logName 日志名称
     *
     */
    /**
     * 获取终端收集日志状态
     *
     * @param terminalId id请求参数
     * @return 终端日志收集状态信息
     * @throws BusinessException 业务异常
     */
    CbbTerminalCollectLogStatusDTO getCollectLog(String terminalId) throws BusinessException;

    /**
     * @api {POST} CbbTerminalLogAPI.getTerminalLogFileInfo 获取终端收集日志路径
     * @apiName getTerminalLogFileInfo
     * @apiGroup CbbTerminalLogAPI
     * @apiDescription 获取终端收集日志路径
     * @apiParam (请求体字段说明) {String} logName 日志名称
     *
     * @apiSuccess (响应字段说明) {CbbTerminalLogFileInfoDTO} response CbbTerminalLogFileInfoDTO
     * @apiSuccess (响应字段说明) {String} response.logFilePath 日志路径
     * @apiSuccess (响应字段说明) {String} response.logFileName 日志文件名
     * @apiSuccess (响应字段说明) {String} response.suffix 日志文件名后缀
     *
     * @apiErrorExample {json} 异常码列表
     *  {code:rcdc_terminal_collect_log_not_exist message:日志不存在}
     *
     */
    /**
     * 获取终端收集日志路径
     *
     * @param logName 请求参数
     * @return 终端收集日志路径
     * @throws BusinessException 业务异常
     */
    CbbTerminalLogFileInfoDTO getTerminalLogFileInfo(String logName) throws BusinessException;
}
