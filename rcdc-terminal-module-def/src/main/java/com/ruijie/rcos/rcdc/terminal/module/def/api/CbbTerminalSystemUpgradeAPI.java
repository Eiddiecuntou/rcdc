package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.*;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.*;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbAddSystemUpgradeTaskResultDTO;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageResponse;
import com.ruijie.rcos.sk.pagekit.api.PageQueryAPI;

import java.util.List;
import java.util.UUID;


/**
 *
 * Description: 终端升级接口
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月19日
 *
 * @author nt
 */
public interface CbbTerminalSystemUpgradeAPI extends PageQueryAPI<CbbUpgradeableTerminalListDTO> {

    /**
     * @api {POST} CbbTerminalSystemUpgradeAPI.addSystemUpgradeTask 添加刷机任务
     * @apiName addSystemUpgradeTask
     * @apiGroup CbbTerminalSystemUpgradeAPI
     * @apiDescription 添加刷机任务
     * @apiParam (请求体字段说明) {CbbAddSystemUpgradeTaskDTO} request CbbAddSystemUpgradeTaskDTO
     * @apiParam (请求体字段说明) {UUID} request.packageId 升级包id
     * @apiParam (请求体字段说明) {CbbSystemUpgradeModeEnums="AUTO","MANUAL"} [request.upgradeMode] 升级模式：自动升级、手动升级
     * @apiParam (请求体字段说明) {CbbFlashModeEnums="FAST","FULL"} request.flashMode 刷机模式：快速刷机，完整刷机
     * @apiParam (请求体字段说明) {String[]} [request.terminalIdArr] 终端id数组
     * @apiParam (请求体字段说明) {UUID[]} [request.terminalGroupIdArr] 终端组id数组
     *
     * @apiSuccess (响应字段说明) {CbbAddSystemUpgradeTaskResultDTO} response CbbAddSystemUpgradeTaskResultDTO
     * @apiSuccess (响应字段说明) {UUID} response.upgradeTaskId 升级任务id
     * @apiSuccess (响应字段说明) {String} response.imgName 升级包名称
     *
     * @apiErrorExample {json} 异常码列表
     *  {code:rcdc_terminal_system_upgrade_package_not_exist message:终端系统升级包不存在}
     *  {code:rcdc_terminal_system_upgrade_package_is_uploading message:终端系统升级包正在上传中}
     *  {code:rcdc_terminal_system_upgrade_task_is_running message:终端系统升级任务正在进行中}
     *  {code:rcdc_terminal_system_upgrade_file_not_exist message:终端系统升级包[{0}]文件丢失}
     */
    /**
     * 添加刷机任务
     *
     * @param request 请求参数
     * @return 请求结果
     * @throws BusinessException 业务异常
     */
    CbbAddSystemUpgradeTaskResultDTO addSystemUpgradeTask(CbbAddSystemUpgradeTaskDTO request) throws BusinessException;

    /**
     * @api {POST} CbbTerminalSystemUpgradeAPI.addSystemUpgradeTerminal 添加刷机任务终端
     * @apiName addSystemUpgradeTerminal
     * @apiGroup CbbTerminalSystemUpgradeAPI
     * @apiDescription 添加刷机任务终端
     * @apiParam (请求体字段说明) {CbbUpgradeTerminalDTO} request CbbUpgradeTerminalDTO
     * @apiParam (请求体字段说明) {UUID} request.upgradeTaskId 升级任务id
     * @apiParam (请求体字段说明) {String} request.terminalId 终端id
     *
     * @apiSuccess (响应字段说明) {String} terminalName 终端名
     *
     * @apiErrorExample {json} 异常码列表
     *  {code:rcdc_terminal_not_found_terminal message:终端数据已不存在}
     *  {code:rcdc_terminal_system_upgrade_task_not_exist message:终端系统升级任务不存在}
     *  {code:rcdc_terminal_system_upgrade_task_has_closed message:终端系统升级任务已关闭或正在关闭中}
     *  {code:rcdc_terminal_system_upgrade_terminal_exist message:刷机终端[{0}]已添加}
     */
    /**
     *
     * 添加刷机任务终端
     *
     * @param request 请求参数
     * @return 添加结果
     * @throws BusinessException 业务异常
     */
    String addSystemUpgradeTerminal(CbbUpgradeTerminalDTO request) throws BusinessException;

    /**
     * @api {POST} CbbTerminalSystemUpgradeAPI.editSystemUpgradeTerminalGroup 编辑刷机任务终端分组
     * @apiName editSystemUpgradeTerminalGroup
     * @apiGroup CbbTerminalSystemUpgradeAPI
     * @apiDescription 编辑刷机任务终端分组
     * @apiParam (请求体字段说明) {CbbUpgradeTerminalGroupDTO} request CbbUpgradeTerminalGroupDTO
     * @apiParam (请求体字段说明) {UUID} request.upgradeTaskId 升级任务id
     * @apiParam (请求体字段说明) {UUID[]} request.terminalGroupIdArr 终端组id数组
     *
     */
    /**
     *
     * 编辑刷机任务终端分组
     *
     * @param request 请求参数
     * @throws BusinessException 业务异常
     */
    void editSystemUpgradeTerminalGroup(CbbUpgradeTerminalGroupDTO request) throws BusinessException;

    /**
     * @api {POST} CbbTerminalSystemUpgradeAPI.pageQuerySystemUpgradeTask 获取刷机任务列表信息
     * @apiName pageQuerySystemUpgradeTask
     * @apiGroup CbbTerminalSystemUpgradeAPI
     * @apiDescription 获取刷机任务列表信息
     * @apiParam (请求体字段说明) {PageSearchRequest} request PageSearchRequest
     * @apiParam (请求体字段说明) {String} [request.searchKeyword] 关键字搜索
     * @apiParam (请求体字段说明) {MatchEqual[]} [request.matchEqualArr] 精确匹配字段
     * @apiParam (请求体字段说明) {Sort} [request.sort] 顺序
     * @apiParam (请求体字段说明) {Integer} request.page 页码
     * @apiParam (请求体字段说明) {Integer} request.limit 每页数量

     * @apiSuccess (响应字段说明) {DefaultPageResponse} response DefaultPageResponse<CbbSpecialDeviceConfigDTO>
     * @apiSuccess (响应字段说明) {CbbSystemUpgradeTaskDTO[]} response.itemArr 分页数据数组
     * @apiSuccess (响应字段说明) {UUID} response.itemArr.id 刷机任务id
     * @apiSuccess (响应字段说明) {String} response.itemArr.packageVersion 刷机包版本
     * @apiSuccess (响应字段说明) {String} response.itemArr.packageName 刷机包名称
     * @apiSuccess (响应字段说明) {Date} response.itemArr.createTime 创建时间
     * @apiSuccess (响应字段说明) {CbbSystemUpgradeTaskStateEnums="UPGRADING","FINISH"} response.itemArr.upgradeTaskState 任务状态
     * @apiSuccess (响应字段说明) {Integer} response.itemArr.successNum 成功数量
     * @apiSuccess (响应字段说明) {CbbSystemUpgradeModeEnums="AUTO","MANUAL"} response.itemArr.upgradeMode 升级模式：自动升级、手动升级
     * @apiSuccess (响应字段说明) {CbbTerminalTypeEnums="VDI_LINUX","VDI_ANDROID","VDI_WINDOWS","IDV_LINUX","APP_WINDOWS","APP_ANDROID","APP_MACOS",
     * "APP_IOS","APP_LINUX"} response.itemArr.terminalType 终端类型
     * @apiSuccess (响应字段说明) {long} response.total 分页数据总数
     *
     */
    /**
     *
     * 获取刷机任务列表信息
     *
     * @param request 请求参数
     * @return 任务列表
     * @throws BusinessException 业务异常
     */
    DefaultPageResponse<CbbSystemUpgradeTaskDTO> pageQuerySystemUpgradeTask(PageSearchRequest request) throws BusinessException;

    /**
     * @api {POST} CbbTerminalSystemUpgradeAPI.pageQuerySystemUpgradeTaskTerminal 获取刷机任务终端列表
     * @apiName pageQuerySystemUpgradeTaskTerminal
     * @apiGroup CbbTerminalSystemUpgradeAPI
     * @apiDescription 获取刷机任务终端列表
     * @apiParam (请求体字段说明) {PageSearchRequest} request PageSearchRequest
     * @apiParam (请求体字段说明) {String} [request.searchKeyword] 关键字搜索
     * @apiParam (请求体字段说明) {MatchEqual[]} [request.matchEqualArr] 精确匹配字段
     * @apiParam (请求体字段说明) {Sort} [request.sort] 顺序
     * @apiParam (请求体字段说明) {Integer} request.page 页码
     * @apiParam (请求体字段说明) {Integer} request.limit 每页数量
     *
     * @apiSuccess (响应字段说明) {DefaultPageResponse} result DefaultPageResponse<CbbSpecialDeviceConfigDTO>
     * @apiSuccess (响应字段说明) {CbbSystemUpgradeTaskTerminalDTO[]} result.itemArr 分页数据数组
     * @apiSuccess (响应字段说明) {UUID} result.itemArr.id 刷机任务id
     * @apiSuccess (响应字段说明) {String} result.itemArr.terminalId 终端id
     * @apiSuccess (响应字段说明) {String} result.itemArr.terminalName 终端名称
     * @apiSuccess (响应字段说明) {String} result.itemArr.ip 终端ip
     * @apiSuccess (响应字段说明) {String} result.itemArr.mac 终端mac
     * @apiSuccess (响应字段说明) {CbbNetworkModeEnums="WIRED","WIRELESS"} result.itemArr.networkMode 网络接入方式 有线、无线
     * @apiSuccess (响应字段说明) {CbbTerminalNetworkInfoDTO[]} result.itemArr.networkInfoArr 终端网络信息
     * @apiSuccess (请求体字段说明) {String} result.itemArr.networkInfoArr.macAddr 终端mac
     * @apiSuccess (请求体字段说明) {String} result.itemArr.networkInfoArr.ip 终端ip
     * @apiSuccess (请求体字段说明) {String} result.itemArr.networkInfoArr.subnetMask 子网掩码
     * @apiSuccess (请求体字段说明) {String} result.itemArr.networkInfoArr.gateway 网关
     * @apiSuccess (请求体字段说明) {String} result.itemArr.networkInfoArr.mainDns 首选DNS
     * @apiSuccess (请求体字段说明) {String} result.itemArr.networkInfoArr.secondDns 备选DNS
     * @apiSuccess (请求体字段说明) {CbbGetNetworkModeEnums="AUTO","MANUAL"} result.itemArr.networkInfoArr.getIpMode 获取IP模式 自动、手动
     * @apiSuccess (请求体字段说明) {CbbGetNetworkModeEnums="AUTO","MANUAL"} result.itemArr.networkInfoArr.getDnsMode 获取DNS模式 自动、手动
     * @apiSuccess (请求体字段说明) {CbbNetworkModeEnums="WIRED","WIRELESS"} result.itemArr.networkInfoArr.networkAccessMode 网络接入方式 有线、无线
     * @apiSuccess (请求体字段说明) {String} result.itemArr.networkInfoArr.ssid 无线网络ssid
     * @apiSuccess (响应字段说明) {Date} result.itemArr.startTime 开始时间
     * @apiSuccess (响应字段说明) {CbbFlashModeEnums="FAST","FULL"} result.itemArr.flashMode 刷机模式：快速刷机，完整刷机
     * @apiSuccess (响应字段说明) {CbbSystemUpgradeStateEnums="WAIT","UPGRADING","SUCCESS","FAIL","UNDO","UNSUPPORTED",
     * "NO_NEED"} result.itemArr.terminalUpgradeState 终端升级状态
     * @apiSuccess (响应字段说明) {long} result.total 分页数据总数
     *
     */
    /**
     * 获取刷机任务终端列表
     *
     * @param request 请求参数
     * @return 刷机终端列表
     * @throws BusinessException 业务异常
     */
    DefaultPageResponse<CbbSystemUpgradeTaskTerminalDTO> pageQuerySystemUpgradeTaskTerminal(PageSearchRequest request) throws BusinessException;

    /**
     * @api {POST} CbbTerminalSystemUpgradeAPI.pageQuerySystemUpgradeTaskTerminalGroup 获取刷机任务终端分组列表
     * @apiName pageQuerySystemUpgradeTaskTerminalGroup
     * @apiGroup CbbTerminalSystemUpgradeAPI
     * @apiDescription 获取刷机任务终端分组列表
     * @apiParam (请求体字段说明) {PageSearchRequest} request PageSearchRequest
     * @apiParam (请求体字段说明) {String} [request.searchKeyword] 关键字搜索
     * @apiParam (请求体字段说明) {MatchEqual[]} [request.matchEqualArr] 精确匹配字段
     * @apiParam (请求体字段说明) {Sort} [request.sort] 顺序
     * @apiParam (请求体字段说明) {Integer} request.page 页码
     * @apiParam (请求体字段说明) {Integer} request.limit 每页数量
     *
     * @apiSuccess (响应字段说明) {DefaultPageResponse} result DefaultPageResponse<CbbSpecialDeviceConfigDTO>
     * @apiSuccess (响应字段说明) {CbbTerminalGroupDetailDTO[]} result.itemArr 分页数据数组
     * @apiSuccess (响应字段说明) {UUID} result.itemArr.id 终端分组id
     * @apiSuccess (响应字段说明) {String} result.itemArr.groupName 终端分组名称
     * @apiSuccess (响应字段说明) {UUID} result.itemArr.parentGroupId 父级分组id
     * @apiSuccess (响应字段说明) {String} result.itemArr.parentGroupName 父级分组名称
     * @apiSuccess (响应字段说明) {Boolean} result.itemArr.enableDefault 父级分组名称
     * @apiSuccess (响应字段说明) {long} result.total 分页数据总数
     *
     */
    /**
     * 获取刷机任务终端分组列表
     *
     * @param request 请求参数
     * @return 刷机终端分组列表
     *
     * @throws BusinessException 业务异常
     */
    DefaultPageResponse<CbbTerminalGroupDetailDTO> pageQuerySystemUpgradeTaskTerminalGroup(PageSearchRequest request)
            throws BusinessException;

    /**
     * @api {POST} CbbTerminalSystemUpgradeAPI.closeSystemUpgradeTask 关闭刷机任务
     * @apiName closeSystemUpgradeTask
     * @apiGroup CbbTerminalSystemUpgradeAPI
     * @apiDescription 关闭刷机任务
     * @apiParam (请求体字段说明) {UUID} task 刷机任务id
     *
     */
    /**
     * 关闭刷机任务
     *
     * @param taskId 请求参数
     * @throws BusinessException 业务异常
     */
    void closeSystemUpgradeTask(UUID taskId) throws BusinessException;

    /**
     * @api {POST} CbbTerminalSystemUpgradeAPI.pageQueryUpgradeableTerminal 终端可刷机的列表
     * @apiName pageQueryUpgradeableTerminal
     * @apiGroup CbbTerminalSystemUpgradeAPI
     * @apiDescription 终端可刷机的列表
     * @apiParam (请求体字段说明) {PageSearchRequest} request PageSearchRequest
     * @apiParam (请求体字段说明) {String} [request.searchKeyword] 关键字搜索
     * @apiParam (请求体字段说明) {MatchEqual[]} [request.matchEqualArr] 精确匹配字段
     * @apiParam (请求体字段说明) {Sort} [request.sort] 顺序
     * @apiParam (请求体字段说明) {Integer} request.page 页码
     * @apiParam (请求体字段说明) {Integer} request.limit 每页数量
     *
     * @apiSuccess (响应字段说明) {DefaultPageResponse} response DefaultPageResponse<CbbSpecialDeviceConfigDTO>
     * @apiSuccess (响应字段说明) {CbbUpgradeableTerminalListDTO[]} response.itemArr 分页数据数组
     * @apiSuccess (响应字段说明) {UUID} response.itemArr.id 终端id
     * @apiSuccess (响应字段说明) {String} response.itemArr.terminalName 终端名称
     * @apiSuccess (响应字段说明) {String} response.itemArr.ip 终端ip
     * @apiSuccess (响应字段说明) {String} response.itemArr.mac 终端mac
     * @apiSuccess (响应字段说明) {CbbNetworkModeEnums="WIRED","WIRELESS"} result.itemArr.networkMode 网络接入方式 有线、无线
     * @apiSuccess (响应字段说明) {CbbTerminalNetworkInfoDTO[]} result.itemArr.networkInfoArr 终端网络信息
     * @apiSuccess (请求体字段说明) {String} result.itemArr.networkInfoArr.macAddr 终端mac
     * @apiSuccess (请求体字段说明) {String} result.itemArr.networkInfoArr.ip 终端ip
     * @apiSuccess (请求体字段说明) {String} result.itemArr.networkInfoArr.subnetMask 子网掩码
     * @apiSuccess (请求体字段说明) {String} result.itemArr.networkInfoArr.gateway 网关
     * @apiSuccess (请求体字段说明) {String} result.itemArr.networkInfoArr.mainDns 首选DNS
     * @apiSuccess (请求体字段说明) {String} result.itemArr.networkInfoArr.secondDns 备选DNS
     * @apiSuccess (请求体字段说明) {CbbGetNetworkModeEnums="AUTO","MANUAL"} result.itemArr.networkInfoArr.getIpMode 获取IP模式 自动、手动
     * @apiSuccess (请求体字段说明) {CbbGetNetworkModeEnums="AUTO","MANUAL"} result.itemArr.networkInfoArr.getDnsMode 获取DNS模式 自动、手动
     * @apiSuccess (请求体字段说明) {CbbNetworkModeEnums="WIRED","WIRELESS"} result.itemArr.networkInfoArr.networkAccessMode 网络接入方式 有线、无线
     * @apiSuccess (请求体字段说明) {String} result.itemArr.networkInfoArr.ssid 无线网络ssid
     * @apiSuccess (响应字段说明) {CbbTerminalStateEnums="OFFLINE","ONLINE","UPGRADING"} response.itemArr.terminalState 终端状态
     * @apiSuccess (响应字段说明) {String} response.itemArr.productType 产品型号
     * @apiSuccess (响应字段说明) {Date} response.itemArr.lastUpgradeTime 最后一次升级时间
     * @apiSuccess (响应字段说明) {UUID} response.itemArr.groupId 终端组id
     * @apiSuccess (响应字段说明) {long} response.total 分页数据总数
     *
     */
    /**
     * 终端可刷机的列表
     *
     * @param apiRequest 请求参数
     * @return 终端列表
     * @throws BusinessException 业务异常
     */
    DefaultPageResponse<CbbUpgradeableTerminalListDTO> pageQueryUpgradeableTerminal(CbbUpgradeableTerminalPageSearchRequest apiRequest)
            throws BusinessException;

    /**
     * @api {POST} CbbTerminalSystemUpgradeAPI.listUpgradeTerminalByTaskId 获取刷机任务终端列表
     * @apiName listUpgradeTerminalByTaskId
     * @apiGroup CbbTerminalSystemUpgradeAPI
     * @apiDescription 获取刷机任务终端列表
     * @apiParam (请求体字段说明) {CbbGetTaskUpgradeTerminalDTO} request CbbGetTaskUpgradeTerminalDTO
     * @apiParam (请求体字段说明) {UUID} id 终端id
     * @apiParam (请求体字段说明) {CbbSystemUpgradeStateEnums="WAIT","UPGRADING","SUCCESS","FAIL",
     * "UNDO","UNSUPPORTED","NO_NEED"} [request.terminalState] 终端升级状态
     *
     * @apiSuccess (响应字段说明) {List} terminalDtoList 响应实体列表
     * @apiSuccess (响应字段说明) {UUID} terminalDtoList.id 刷机任务id
     * @apiSuccess (响应字段说明) {String} terminalDtoList.terminalId 父级分组id
     * @apiSuccess (响应字段说明) {String} terminalDtoList.terminalName 父级分组名称
     * @apiSuccess (响应字段说明) {String} terminalDtoList.ip 终端ip
     * @apiSuccess (响应字段说明) {String} terminalDtoList.mac 终端mac
     * @apiSuccess (响应字段说明) {Date} terminalDtoList.startTime 终端分组名称
     * @apiSuccess (响应字段说明) {CbbSystemUpgradeStateEnums="WAIT","UPGRADING","SUCCESS","FAIL","UNDO",
     * "UNSUPPORTED","NO_NEED"} terminalDtoList.terminalUpgradeState 终端升级后的状态
     */
    /**
     * 获取刷机任务终端列表
     *
     * @param request 请求参数
     * @return 刷机终端信息列表
     */
    List<CbbSystemUpgradeTaskTerminalDTO> listUpgradeTerminalByTaskId(CbbGetTaskUpgradeTerminalDTO request);

    /**
     * @api {POST} CbbTerminalSystemUpgradeAPI.cancelUpgradeTerminal 取消终端刷机
     * @apiName cancelUpgradeTerminal
     * @apiGroup CbbTerminalSystemUpgradeAPI
     * @apiDescription 取消刷机终端
     * @apiParam (请求体字段说明) {CbbUpgradeTerminalDTO} request CbbUpgradeTerminalDTO
     * @apiParam (请求体字段说明) {UUID} [request.upgradeTaskId] 升级任务id
     * @apiParam (请求体字段说明) {String} request.terminalId 终端ID
     *
     * @apiSuccess (响应字段说明) {String} terminalName 终端名
     */
    /**
     * 取消刷机终端
     *
     * @param request 请求参数
     * @return 请求结果
     * @throws BusinessException 业务异常
     */
    String cancelUpgradeTerminal(CbbUpgradeTerminalDTO request) throws BusinessException;

    /**
     * @api {POST} CbbTerminalSystemUpgradeAPI.retryUpgradeTerminal 重试终端刷机
     * @apiName retryUpgradeTerminal
     * @apiGroup CbbTerminalSystemUpgradeAPI
     * @apiDescription 重试终端刷机
     * @apiParam (请求体字段说明) {CbbUpgradeTerminalDTO}request CbbUpgradeTerminalDTO
     * @apiParam (请求体字段说明) {UUID} [request.upgradeTaskId] 升级任务id
     * @apiParam (请求体字段说明) {String} request.terminalId 终端ID
     *
     * @apiSuccess (响应字段说明) {String} terminalName 终端名
     */
    /**
     * 重试终端刷机
     *
     * @param request 请求参数
     * @return 请求结果
     * @throws BusinessException 业务异常
     */
    String retryUpgradeTerminal(CbbUpgradeTerminalDTO request) throws BusinessException;

    /**
     * @api {POST} CbbTerminalSystemUpgradeAPI.findTerminalUpgradeTaskById 通过id获取终端刷机任务信息
     * @apiName findTerminalUpgradeTaskById
     * @apiGroup CbbTerminalSystemUpgradeAPI
     * @apiDescription 通过id获取终端刷机任务信息
     * @apiParam (请求体字段说明) {UUID} taskId 刷机任务id
     *
     * @apiSuccess (响应字段说明) {CbbSystemUpgradeTaskDTO} systemUpgradeTaskDTO 响应实体
     * @apiSuccess (响应字段说明) {UUID} systemUpgradeTaskDTO.id 刷机任务id
     * @apiSuccess (响应字段说明) {String} systemUpgradeTaskDTO.packageVersion 刷机包版本
     * @apiSuccess (响应字段说明) {String} systemUpgradeTaskDTO.packageName 刷机包名称
     * @apiSuccess (响应字段说明) {Date} systemUpgradeTaskDTO.createTime 创建时间
     * @apiSuccess (响应字段说明) {CbbSystemUpgradeTaskStateEnums="UPGRADING","FINISH"} systemUpgradeTaskDTO.upgradeTaskState 升级任务状态
     * @apiSuccess (响应字段说明) {Integer} systemUpgradeTaskDTO.successNum 成功数字
     * @apiSuccess (响应字段说明) {CbbSystemUpgradeModeEnums="AUTO","MANUAL"} systemUpgradeTaskDTO.upgradeMode 升级模式 自动、手动
     * @apiSuccess (响应字段说明) {CbbTerminalTypeEnums="VDI_LINUX","VDI_ANDROID","VDI_WINDOWS","IDV_LINUX","APP_WINDOWS","APP_ANDROID","APP_MACOS",
     * "APP_IOS","APP_LINUX"} systemUpgradeTaskDTO.terminalType 终端类型
     */
    /**
     * 通过id获取终端刷机任务信息
     * 
     * @param taskId 请求参数
     * @return 请求结果
     * @throws BusinessException 业务异常
     */
    CbbSystemUpgradeTaskDTO findTerminalUpgradeTaskById(UUID taskId) throws BusinessException;

}
