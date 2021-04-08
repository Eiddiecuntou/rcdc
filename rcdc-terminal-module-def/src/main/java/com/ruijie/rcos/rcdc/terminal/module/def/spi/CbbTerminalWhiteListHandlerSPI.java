package com.ruijie.rcos.rcdc.terminal.module.def.spi;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;

/**
 * 终端白名单SPI接口定义
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021年04月06日
 *
 * @author nting
 */
public interface CbbTerminalWhiteListHandlerSPI {

    /**
     * @api {SPI} CbbTerminalConnectHandlerSPI.checkWhiteList 校验是否允许终端接入SPI
     * @apiName isAllowConnect
     * @apiGroup CbbTerminalConnectHandlerSPI
     * @apiDescription 校验是否允许终端接入SPI
     * @apiParam (请求体字段说明) {CbbShineTerminalBasicInfo} terminalBasicInfo CbbShineTerminalBasicInfo
     * @apiParam (请求体字段说明) {String} terminalBasicInfo.terminalName 终端名称
     * @apiParam (请求体字段说明) {String} terminalBasicInfo.terminalId 终端唯一标识
     * @apiParam (请求体字段说明) {String} terminalBasicInfo.macAddr 终端mac
     * @apiParam (请求体字段说明) {String} terminalBasicInfo.ip 终端ip
     * @apiParam (请求体字段说明) {String} terminalBasicInfo.subnetMask 子网掩码
     * @apiParam (请求体字段说明) {String} terminalBasicInfo.gateway 网关
     * @apiParam (请求体字段说明) {String} terminalBasicInfo.mainDns 首选DNS
     * @apiParam (请求体字段说明) {String} terminalBasicInfo.secondDns 备选DNS
     * @apiParam (请求体字段说明) {CbbGetNetworkModeEnums="AUTO","MANUAL"} terminalBasicInfo.getIpMode 获取IP模式
     * @apiParam (请求体字段说明) {CbbGetNetworkModeEnums="AUTO","MANUAL"} terminalBasicInfo.getDnsMode 获取DNS模式
     * @apiParam (请求体字段说明) {String} terminalBasicInfo.productType 产品类型
     * @apiParam (请求体字段说明) {CbbNetworkModeEnums="WIRED","WIRELESS"} terminalBasicInfo.networkAccessMode 网络接入方式（有线、无线）
     * @apiParam (请求体字段说明) {String} terminalBasicInfo.serialNumber 产品序列号
     * @apiParam (请求体字段说明) {String} terminalBasicInfo.cpuType cup型号
     * @apiParam (请求体字段说明) {Long} terminalBasicInfo.memorySize 内存大小，单位B
     * @apiParam (请求体字段说明) {Long} terminalBasicInfo.diskSize 磁盘大小，单位B
     * @apiParam (请求体字段说明) {String} terminalBasicInfo.terminalOsType 终端操作系统类型
     * @apiParam (请求体字段说明) {String} terminalBasicInfo.terminalOsVersion 终端操作系统版本
     * @apiParam (请求体字段说明) {String} terminalBasicInfo.rainOsVersion 终端系统版本号
     * @apiParam (请求体字段说明) {String} terminalBasicInfo.rainUpgradeVersion 软件版本号，指组件升级包的版本号
     * @apiParam (请求体字段说明) {String} terminalBasicInfo.hardwareVersion 硬件版本号
     * @apiParam (请求体字段说明) {CbbTerminalPlatformEnums="VDI","IDV","APP"} terminalBasicInfo.platform 平台（VDI/IDV/软件客户端）
     * @apiParam (请求体字段说明) {String} terminalBasicInfo.osInnerVersion 终端内部系统版本号
     * @apiParam (请求体字段说明) {String} terminalBasicInfo.idvTerminalMode idv模式
     * @apiParam (请求体字段说明) {String} terminalBasicInfo.ssid 无线网络ssid
     * @apiParam (请求体字段说明) {CbbTerminalWirelessAuthModeEnums="MODE_WPA_EAP","MODE_WPA_PSK","MODE_OPEN"} terminalBasicInfo.wirelessAuthMode 无线认证模式
     * @apiParam (请求体字段说明) {String} terminalBasicInfo.productId 终端产品ID
     * @apiParam (请求体字段说明) {Integer} terminalBasicInfo.wirelessNetCardNum 无线网卡数量
     * @apiParam (请求体字段说明) {String} terminalBasicInfo.allDiskInfo 终端所有磁盘信息
     * @apiParam (请求体字段说明) {Integer} terminalBasicInfo.ethernetNetCardNum 有线网卡数量
     * @apiParam (请求体字段说明) {CbbTerminalNetworkInfoDTO} terminalBasicInfo.networkInfoArr 终端所有网络信息集合
     * @apiParam (请求体字段说明) {String} terminalBasicInfo.networkInfoArr.macAddr 终端mac
     * @apiParam (请求体字段说明) {String} terminalBasicInfo.networkInfoArr.ip 终端ip
     * @apiParam (请求体字段说明) {String} terminalBasicInfo.networkInfoArr.subnetMask 子网掩码
     * @apiParam (请求体字段说明) {String} terminalBasicInfo.networkInfoArr.gateway 网关
     * @apiParam (请求体字段说明) {String} terminalBasicInfo.networkInfoArr.mainDns 首选DNS
     * @apiParam (请求体字段说明) {String} terminalBasicInfo.networkInfoArr.secondDns 备选DNS
     * @apiParam (请求体字段说明) {CbbGetNetworkModeEnums="AUTO","MANUAL"} terminalBasicInfo.networkInfoArr.getIpMode 获取IP模式 ,自动、手动
     * @apiParam (请求体字段说明) {CbbGetNetworkModeEnums="AUTO","MANUAL"} terminalBasicInfo.networkInfoArr.getDnsMode 获取DNS模式,自动、手动
     * @apiParam (请求体字段说明) {CbbNetworkModeEnums="WIRED","WIRELESS"} terminalBasicInfo.networkInfoArr.networkAccessMode 网络接入方式（有线、无线）
     * @apiParam (请求体字段说明) {String} terminalBasicInfo.networkInfoArr.ssid 无线网络ssid
     * @apiParam (请求体字段说明) {CbbTerminalWorkModeEnums} terminalBasicInfo.terminalWorkSupportModeArr 终端支持工作模式集合
     *
     * @apiSuccess (响应字段说明) {boolean} -- 是否允许终端接入
     *
     *
     */
    /**
     * 检验终端是否在白名单中
     *
     * @param terminalBasicInfo 终端信息
     * @return boolean 是否在终端白名单中
     */
    boolean checkWhiteList(CbbShineTerminalBasicInfo terminalBasicInfo);

}
