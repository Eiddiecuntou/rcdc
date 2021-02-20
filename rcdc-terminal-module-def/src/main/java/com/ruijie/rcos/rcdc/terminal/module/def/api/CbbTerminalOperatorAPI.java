package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.*;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalStartMode;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import org.springframework.lang.Nullable;

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
     * @api {POST} CbbTerminalOperatorAPI.delete 删除终端信息
     * @apiName delete
     * @apiGroup CbbTerminalOperatorAPI
     * @apiDescription 删除终端信息
     * @apiParam (请求体字段说明) {String} terminalId 终端id
     *
     * @apiErrorExample {json} 异常码列表
     *  {code:rcdc_terminal_not_found_terminal message:终端数据已不存在}
     *  {code:rcdc_terminal_online_cannot_delete message:终端[{0}({1})]处于在线状态，不允许删除}
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
     * @api {POST} CbbTerminalOperatorAPI.findBasicInfoByTerminalId 根据terminalId获取终端基本信息
     * @apiName findBasicInfoByTerminalId
     * @apiGroup CbbTerminalOperatorAPI
     * @apiDescription 根据terminalId获取终端基本信息
     * @apiParam (请求体字段说明) {String} terminalId 终端id
     *
     * @apiSuccess (响应字段说明) {CbbTerminalBasicInfoDTO} response CbbTerminalBasicInfoDTO
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
     * @apiSuccess (响应字段说明) {String} response.dataDiskSize 数据分区大小
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
     * @apiSuccess (响应字段说明) {Integer} response.diskInfoArr 磁盘信息数组
     * @apiSuccess (响应字段说明) {Integer} response.wirelessNetCardNum 无线网卡数量
     * @apiSuccess (响应字段说明) {Integer} response.ethernetNetCardNum 有线网卡数量
     * @apiSuccess (响应字段说明) {Integer} response.startMode 终端启动方式:auto、uefi、tc
     * @apiSuccess (响应字段说明) {boolean} response.supportTcStart 终端是否支持tc启动
     *
     * @apiErrorExample {json} 异常码列表
     *  {code:rcdc_terminal_not_found_terminal message:终端数据已不存在}
     */
    /**
     * 根据terminalId获取终端基本信息
     *
     * @param terminalId 请求参数对象
     * @return 终端基本信息DTO
     * @throws BusinessException 业务异常
     */
    CbbTerminalBasicInfoDTO findBasicInfoByTerminalId(String terminalId) throws BusinessException;

    /**
     * @api {POST} CbbTerminalOperatorAPI.modifyTerminal 编辑终端信息
     * @apiName modifyTerminal
     * @apiGroup CbbTerminalOperatorAPI
     * @apiDescription 编辑终端信息
     * @apiParam (请求体字段说明) {CbbModifyTerminalDTO} request CbbModifyTerminalDTO
     * @apiParam (请求体字段说明) {String} request.cbbTerminalId 终端id
     * @apiParam (请求体字段说明) {String} request.terminalName 终端名称
     * @apiParam (请求体字段说明) {String} request.groupId 终端组Id
     *
     * @apiErrorExample {json} 异常码列表
     *  {code:rcdc_terminal_not_found_terminal message:终端数据已不存在}
     *  {code:rcdc_terminalgroup_group_not_exist message:终端分组[{0}]不存在}
     *  {code:rcdc_terminal_operate_msg_send_fail message:发送{0}消息超时，请检查网络或终端状态}
     *  {code:rcdc_terminal_operate_action_modify_name message:修改终端名称}
     *  {code:rcdc_terminal_offline message:终端连接断开}
     */
    /**
     *  编辑终端信息
     *
     * @param request 请求参数
     * @throws BusinessException 业务异常
     */
    void modifyTerminal(CbbModifyTerminalDTO request) throws BusinessException;

    /**
     * @api {POST} CbbTerminalOperatorAPI.shutdown 关闭终端
     * @apiName shutdown
     * @apiGroup CbbTerminalOperatorAPI
     * @apiDescription 关闭终端
     * @apiParam (请求体字段说明) {String} terminalId 终端id
     *
     * @apiErrorExample {json} 异常码列表
     *  {code:rcdc_terminal_offline_cannot_shutdown message:当前终端[{0}({1})]处于离线状态，无法关机}
     *  {code:rcdc_terminal_operate_action_shutdown message:关机}
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
     * @apiParam (请求体字段说明) {CbbChangePasswordDTO} request CbbChangePasswordDTO
     * @apiParam (请求体字段说明) {String} request.password 密码
     *
     */
    /**
     * 修改终端管理员密码
     *
     * @param request 修改密码请求参数对象
     * @throws BusinessException 业务异常
     */
    void changePassword(CbbChangePasswordDTO request) throws BusinessException;

    /**
     * @api {POST} CbbTerminalOperatorAPI.queryPassword 查询终端管理员密码
     * @apiName queryPassword
     * @apiGroup CbbTerminalOperatorAPI
     * @apiDescription 查询终端管理员密码
     * @apiSuccess (响应字段说明) {String} terminalPwd 终端管理员密码
     *
     */
    /**
     * 查询终端管理员密码
     *
     * @return 返回终端管理员密码
     * @throws BusinessException 业务异常
     */
    String queryPassword() throws BusinessException;

    /**
     * @api {POST} CbbTerminalOperatorAPI.relieveFault 解除故障
     * @apiName relieveFault
     * @apiGroup CbbTerminalOperatorAPI
     * @apiDescription 解除故障
     * @apiParam (请求体字段说明) {CbbTerminalIdRequest} request CbbTerminalIdRequest
     * @apiParam (请求体字段说明) {String} request.terminalId 终端id
     * @apiParam (请求体字段说明) {String} request.content 业务扩展字段
     *
     */
    /**
     * 解除故障
     *
     * @param terminalId 终端id请求参数对象
     * @param content 业务扩展字段
     * @throws BusinessException 业务异常
     */
    void relieveFault(String terminalId, @Nullable Object content) throws BusinessException;

    /**
     * @api {POST} CbbTerminalOperatorAPI.idvOfflineLoginSetting IDV终端离线登录设置
     * @apiName idvOfflineLoginSetting
     * @apiGroup CbbTerminalOperatorAPI
     * @apiDescription IDV终端离线登录设置
     * @apiParam (请求体字段说明) {CbbOfflineLoginSettingDTO} request CbbOfflineLoginSettingDTO
     * @apiParam (请求体字段说明) {Integer} request.offlineAutoLocked 离线自动锁定
     *
     */
    /**
     * IDV终端离线登录设置
     *
     * @param request 请求参数
     * @throws BusinessException 业务异常
     */
    void idvOfflineLoginSetting(CbbOfflineLoginSettingDTO request) throws BusinessException;

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

    /**
     * @api {POST} CbbTerminalOperatorAPI.setTerminalStartMode 设置终端启动方式
     * @apiName setTerminalStartMode
     * @apiGroup CbbTerminalOperatorAPI
     * @apiDescription 设置终端启动方式
     * @apiParam (请求体字段说明) {String} terminalId 终端id
     * @apiParam (请求体字段说明) {CbbTerminalStartMode} startMode 请求方式
     * @apiParam (请求体字段说明) {CbbTerminalStartMode} startMode.AUTO 自动
     * @apiParam (请求体字段说明) {CbbTerminalStartMode} startMode.TC tc启动
     * @apiParam (请求体字段说明) {CbbTerminalStartMode} startMode.UEFI uefi启动
     */
    /**
     * 设置终端启动方式
     *
     * @param terminalId 终端id
     * @param startMode  请求参数
     * @throws BusinessException 业务异常
     */
    void setTerminalStartMode(String terminalId, CbbTerminalStartMode startMode) throws BusinessException;

    /**
     * @api {POST} CbbTerminalOperatorAPI.closeTerminalConnection 关闭终端与rcdc的连接
     * @apiName closeTerminalConnection
     * @apiGroup CbbTerminalOperatorAPI
     * @apiDescription 关闭终端与rcdc的连接
     * @apiParam (请求体字段说明) {String} terminalId 终端id
     *
     */
    /**
     * 关闭终端与rcdc的连接
     * @param terminalId 终端id
     */
    void closeTerminalConnection(String terminalId);
}
