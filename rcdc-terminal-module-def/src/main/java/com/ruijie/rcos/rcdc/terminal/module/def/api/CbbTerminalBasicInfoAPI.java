package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbModifyTerminalRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalBasicInfoResponse;
import com.ruijie.rcos.sk.base.exception.BusinessException;


/**
 * Description: 终端基本信息操作接口
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/30
 *
 * @author Jarman
 */
public interface CbbTerminalBasicInfoAPI {

    /**
     * @api {POST} CbbTerminalBasicInfoAPI.delete 删除终端信息
     * @apiName delete
     * @apiGroup CbbTerminalBasicInfoAPI
     * @apiDescription 删除终端信息
     * @apiParam (请求体字段说明) {String} terminalId 终端id
     *
     * @apiSuccess (响应字段说明) {void} void 无返回值
     */
    /**
     * 删除终端信息
     *
     * @param terminalId 请求参数对象
     * @throws BusinessException 业务异常
     */
    void delete(String terminalId) throws BusinessException;

    /**
     * @api {POST} CbbTerminalBasicInfoAPI.findBasicInfoByTerminalId 根据terminalId获取终端基本信息
     * @apiName findBasicInfoByTerminalId
     * @apiGroup CbbTerminalBasicInfoAPI
     * @apiDescription 根据terminalId获取终端基本信息
     * @apiParam (请求体字段说明) {String} terminalId 终端id
     *
     * @apiSuccess (响应字段说明) {CbbTerminalBasicInfoResponse} result CbbTerminalBasicInfoResponse
     * @apiSuccess (响应字段说明) {UUID} result.id id
     * @apiSuccess (响应字段说明) {String} tresult.erminalName 终端名称
     * @apiSuccess (响应字段说明) {String} result.terminalId 终端id
     * @apiSuccess (响应字段说明) {String} result.macAddr mac地址
     * @apiSuccess (响应字段说明) {String} result.ip ip
     * @apiSuccess (响应字段说明) {String} result.subnetMask 子网掩码
     * @apiSuccess (响应字段说明) {String} result.gateway 网关
     * @apiSuccess (响应字段说明) {String} result.mainDns mainDns
     * @apiSuccess (响应字段说明) {String} result.secondDns secondDns
     * @apiSuccess (响应字段说明) {CbbGetNetworkModeEnums} result.getIpMode 取值范围：AUTO, MANUAL
     * @apiSuccess (响应字段说明) {CbbGetNetworkModeEnums} result.getDnsMode 取值范围：AUTO, MANUAL
     * @apiSuccess (响应字段说明) {String} result.productType productType
     * @apiSuccess (响应字段说明) {CbbTerminalPlatformEnums} result.terminalPlatform 取值范围：VDI,IDV,APP
     * @apiSuccess (响应字段说明) {String} result.serialNumber 序列号
     * @apiSuccess (响应字段说明) {String} result.cpuMode cpu模式
     * @apiSuccess (响应字段说明) {String} result.memorySize 内存大小
     * @apiSuccess (响应字段说明) {String} result.diskSize 磁盘大小
     * @apiSuccess (响应字段说明) {String} result.terminalOsType 终端操作系统类型
     * @apiSuccess (响应字段说明) {String} result.terminalOsVersion 终端操作系统版本
     * @apiSuccess (响应字段说明) {String} result.rainOsVersion rainOs版本
     * @apiSuccess (响应字段说明) {String} result.rainUpgradeVersion rain升级版本
     * @apiSuccess (响应字段说明) {String} result.hardwareVersion 硬件版本
     * @apiSuccess (响应字段说明) {CbbNetworkModeEnums} result.networkAccessMode 取值范围：WIRED,WIRELESS
     * @apiSuccess (响应字段说明) {Date} result.createTime 创建时间
     * @apiSuccess (响应字段说明) {Date} result.lastOnlineTime 最后一次Online
     * @apiSuccess (响应字段说明) {Date} result.lastOfflineTime 最后一次Offline
     * @apiSuccess (响应字段说明) {Integer} result.version 版本
     * @apiSuccess (响应字段说明) {CbbTerminalStateEnums} result.state 取值范围：state OFFLINE,ONLINE,UPGRADING
     */
    /**
     * 根据terminalId获取终端基本信息
     *
     * @param terminalId 请求参数对象
     * @return 终端基本信息DTO
     * @throws BusinessException 业务异常
     */
    CbbTerminalBasicInfoResponse findBasicInfoByTerminalId(String terminalId) throws BusinessException;

    /**
     * @api {POST} CbbTerminalBasicInfoAPI.modifyTerminal 编辑终端信息
     * @apiName modifyTerminal
     * @apiGroup CbbTerminalBasicInfoAPI
     * @apiDescription 编辑终端信息
     * @apiParam (请求体字段说明) {CbbModifyTerminalRequest} request CbbModifyTerminalRequest
     * @apiParam (请求体字段说明) {String} request.cbbTerminalId cbb终端id
     * @apiParam (请求体字段说明) {String} request.terminalName 终端名称
     * @apiParam (请求体字段说明) {String} [request.groupId] 分组Id
     *
     * @apiSuccess (响应字段说明) {void} void 无返回值
     */
    /**
     *  编辑终端信息
     *
     * @param request 请求参数
     * @throws BusinessException 业务异常
     */
    void modifyTerminal(CbbModifyTerminalRequest request) throws BusinessException;

}
