package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbDetectDateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectPageRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbDetectResultResponse;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageResponse;


/**
 * Description: 检测记录API
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/14
 *
 * @author nt
 */
public interface CbbTerminalDetectRecordAPI {

    /**
     * @api {POST} CbbTerminalDetectRecordAPI.listDetect 获取检测记录分页列表
     * @apiName listDetect
     * @apiGroup CbbTerminalDetectRecordAPI
     * @apiDescription 获取检测记录分页列表
     * @apiParam (请求体字段说明) {CbbTerminalDetectPageRequest} request CbbTerminalDetectPageRequest
     * @apiParam (请求体字段说明) {Date} [request.startTime] 开始时间
     * @apiParam (请求体字段说明) {Date} [request.endTime] 结束时间
     *
     * @apiSuccess (响应字段说明) {CbbTerminalDetectDTO[]} itemArr 响应实体数组
     * @apiSuccess (响应字段说明) {String} itemArr.terminalId id
     * @apiSuccess (响应字段说明) {String} itemArr.terminalName 名称
     * @apiSuccess (响应字段说明) {String} itemArr.ip ip
     * @apiSuccess (响应字段说明) {String} itemArr.mac mac
     * @apiSuccess (响应字段说明) {Integer} itemArr.ipConflict ip冲突结果，0 不冲突，1 冲突，如果有冲突则ipConflictMac字段保存冲突的mac地址，否则为空值
     * @apiSuccess (响应字段说明) {Integer} itemArr.accessInternet 是否可访问外网，0不能访问，1可访问
     * @apiSuccess (响应字段说明) {Double} itemArr.bandwidth 带宽大小
     * @apiSuccess (响应字段说明) {Double} itemArr.packetLossRate 丢包率
     * @apiSuccess (响应字段说明) {Double} itemArr.delay 网络时延
     * @apiSuccess (响应字段说明) {Double} itemArr.bandwidthThreshold 带宽阈值
     * @apiSuccess (响应字段说明) {Double} itemArr.packetLossRateThreshold 丢包率阈值
     * @apiSuccess (响应字段说明) {Double} itemArr.delayThreshold 时延阈值
     * @apiSuccess (响应字段说明) {Date} itemArr.detectTime 检测时间
     * @apiSuccess (响应字段说明) {DetectState} itemArr.checkState 响应实体数组DetectState类
     * @apiSuccess (响应字段说明) {String} itemArr.checkState.state 响应实体数组DetectState类state状态
     * @apiSuccess (响应字段说明) {String} itemArr.checkState.message 响应实体数组DetectState类message信息
     */
    /**
     * 获取检测记录分页列表
     *
     * @param request 分页请求参数
     * @return 检测记录分页列表
     * @throws BusinessException 业务异常
     */
    DefaultPageResponse<CbbTerminalDetectDTO> listDetect(CbbTerminalDetectPageRequest request) throws BusinessException;

    /**
     * @api {POST} CbbTerminalDetectRecordAPI.getRecentDetect 获取检测记录分页列表
     * @apiName getRecentDetect
     * @apiGroup CbbTerminalDetectRecordAPI
     * @apiDescription 获取检测记录分页列表
     * @apiParam (请求体字段说明) {String} terminalId 终端id
     *
     * @apiSuccess (响应字段说明) {CbbTerminalDetectDTO} detectInfo 响应实体
     * @apiSuccess (响应字段说明) {String} detectInfo.terminalId id
     * @apiSuccess (响应字段说明) {String} detectInfo.terminalName 名称
     * @apiSuccess (响应字段说明) {String} detectInfo.ip ip
     * @apiSuccess (响应字段说明) {String} detectInfo.mac mac
     * @apiSuccess (响应字段说明) {Integer} detectInfo.ipConflict ip冲突结果，0 不冲突，1 冲突，如果有冲突则ipConflictMac字段保存冲突的mac地址，否则为空值
     * @apiSuccess (响应字段说明) {Integer} detectInfo.accessInternet 是否可访问外网，0不能访问，1可访问
     * @apiSuccess (响应字段说明) {Double} detectInfo.bandwidth 带宽大小
     * @apiSuccess (响应字段说明) {Double} detectInfo.packetLossRate 丢包率
     * @apiSuccess (响应字段说明) {Double} detectInfo.delay 网络时延
     * @apiSuccess (响应字段说明) {Double} detectInfo.bandwidthThreshold 带宽阈值
     * @apiSuccess (响应字段说明) {Double} detectInfo.packetLossRateThreshold 丢包率阈值
     * @apiSuccess (响应字段说明) {Double} detectInfo.delayThreshold 时延阈值
     * @apiSuccess (响应字段说明) {Date} detectInfo.detectTime 检测时间
     * @apiSuccess (响应字段说明) {DetectState} detectInfo.checkState 响应实体数组DetectState类
     * @apiSuccess (响应字段说明) {String} detectInfo.checkState.state 响应实体数组DetectState类state状态
     * @apiSuccess (响应字段说明) {String} detectInfo.checkState.message 响应实体数组DetectState类message信息
     */
    /**
     * 获取终端最后检测记录
     *
     * @param terminalId 请求参数
     * @return 检测记录信息
     * @throws BusinessException 业务异常
     */

    CbbTerminalDetectDTO getRecentDetect(String terminalId) throws BusinessException;

    /**
     * @api {POST} CbbTerminalDetectRecordAPI.getDetectResult 获取终端检测记录结果
     * @apiName getDetectResult
     * @apiGroup CbbTerminalDetectRecordAPI
     * @apiDescription 获取终端检测记录结果
     * @apiParam (请求体字段说明) {CbbDetectDateEnums="TODAY","YESTERDAY"} detectDate
     *
     *
     * @apiSuccess (响应字段说明) {CbbTerminalDetectStatisticsDTO} result 响应实体
     * @apiSuccess (响应字段说明) {int} result.ipConflict ip冲突结果，0 不冲突，1 冲突，如果有冲突则ipConflictMac字段保存冲突的mac地址，否则为空值
     * @apiSuccess (响应字段说明) {int} result.bandwidth 带宽
     * @apiSuccess (响应字段说明) {int} result.accessInternet 是否可访问外网，0不能访问，1可访问
     * @apiSuccess (响应字段说明) {int} result.packetLossRate 丢包率
     * @apiSuccess (响应字段说明) {int} result.delay 网络时延
     * @apiSuccess (响应字段说明) {int} result.checking checking
     * @apiSuccess (响应字段说明) {int} result.all all
     * @apiSuccess (响应字段说明) {CbbTerminalDetectThresholdDTO} threshold 响应实体
     * @apiSuccess (响应字段说明) {Double} threshold.bandwidthThreshold TODO
     * @apiSuccess (响应字段说明) {Double} threshold.packetLossRateThreshold TODO
     * @apiSuccess (响应字段说明) {Double} threshold.delayThreshold TODO
     *
     *
     */
    /**
     * 获取终端检测记录结果
     *
     * @param detectDate 检测结果请求参数
     * @return 终端检测结果
     */
    CbbDetectResultResponse getDetectResult(CbbDetectDateEnums detectDate);
}
