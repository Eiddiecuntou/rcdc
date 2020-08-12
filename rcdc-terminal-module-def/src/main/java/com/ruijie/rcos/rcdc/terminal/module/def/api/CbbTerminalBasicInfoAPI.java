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
     * @apiSuccess (响应字段说明) {CbbTerminalBasicInfoResponse} response CbbTerminalBasicInfoResponse
     * @apiSuccess (响应字段说明) {UUID} response.id 终端id
     * @apiSuccess (响应字段说明) {String} response.terminalName 终端名称
     * @apiSuccess (响应字段说明) {String} response.terminalId 终端id
     * @apiSuccess (响应字段说明) {String} response.macAddr 终端mac
     * @apiSuccess (响应字段说明) {String} response.ip 终端ip
     * @apiSuccess (响应字段说明) {String} response.subnetMask 子网掩码
     * @apiSuccess (响应字段说明) {String} response.gateway 网关
     * @apiSuccess (响应字段说明) {String} response.mainDns 首选dns
     * @apiSuccess (响应字段说明) {String} response.secondDns 备用dns
     * @apiSuccess (响应字段说明) {CbbGetNetworkModeEnums="AUTO","MANUAL"} response.getIpMode 获取ip模式，自动、手动
     * @apiSuccess (响应字段说明) {CbbGetNetworkModeEnums="AUTO","MANUAL"} response.getDnsMode 获取dns模式，自动、手动
     * @apiSuccess (响应字段说明) {String} response.productType 终端产品类型
     * @apiSuccess (响应字段说明) {CbbTerminalPlatformEnums="VDI","IDV","APP"} response.terminalPlatform 终端平台类型
     * @apiSuccess (响应字段说明) {String} response.serialNumber 序列号
     * @apiSuccess (响应字段说明) {String} response.cpuMode cpu模式
     * @apiSuccess (响应字段说明) {String} response.memorySize 内存大小
     * @apiSuccess (响应字段说明) {String} response.diskSize 磁盘大小
     * @apiSuccess (响应字段说明) {String} response.terminalOsType 终端操作系统类型
     * @apiSuccess (响应字段说明) {String} response.terminalOsVersion 终端操作系统版本
     * @apiSuccess (响应字段说明) {String} response.rainOsVersion rainOs版本
     * @apiSuccess (响应字段说明) {String} response.rainUpgradeVersion rua升级版本
     * @apiSuccess (响应字段说明) {String} response.hardwareVersion 硬件版本
     * @apiSuccess (响应字段说明) {CbbNetworkModeEnums="WIRED","WIRELESS"} response.networkAccessMode 网络类型，有线、无线
     * @apiSuccess (响应字段说明) {Date} response.createTime 创建时间
     * @apiSuccess (响应字段说明) {Date} response.lastOnlineTime 最后一次在线时间
     * @apiSuccess (响应字段说明) {Date} response.lastOfflineTime 最后一次离线时间
     * @apiSuccess (响应字段说明) {Integer} response.version 版本
     * @apiSuccess (响应字段说明) {CbbTerminalStateEnums="OFFLINE","ONLINE","UPGRADING"} response.state 终端状态
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
     * @apiParam (请求体字段说明) {String} request.cbbTerminalId 终端id
     * @apiParam (请求体字段说明) {String} request.terminalName 终端名称
     * @apiParam (请求体字段说明) {String} request.groupId 终端组Id
     *
     */
    /**
     *  编辑终端信息
     *
     * @param request 请求参数
     * @throws BusinessException 业务异常
     */
    void modifyTerminal(CbbModifyTerminalRequest request) throws BusinessException;

}
