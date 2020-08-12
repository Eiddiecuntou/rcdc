package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbSystemUpgradeTaskDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbSystemUpgradeTaskTerminalDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbUpgradeableTerminalListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.TerminalGroupDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.*;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbAddSystemUpgradeTaskResponse;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageResponse;

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
public interface CbbTerminalSystemUpgradeAPI {

    /**
     * @api {POST} CbbTerminalSystemUpgradeAPI.addSystemUpgradeTask 添加刷机任务
     * @apiName addSystemUpgradeTask
     * @apiGroup CbbTerminalSystemUpgradeAPI
     * @apiDescription 添加刷机任务
     * @apiParam (请求体字段说明) {CbbAddSystemUpgradeTaskRequest} request CbbAddSystemUpgradeTaskRequest
     * @apiParam (请求体字段说明) {UUID} request.packageId 终端id
     * @apiParam (请求体字段说明) {CbbSystemUpgradeModeEnums} [request.upgradeMode] 取值范围：AUTO（自动升级）,MANUAL（手动升级）
     * @apiParam (请求体字段说明) {String[]} [request.terminalIdArr] 终端id数组
     * @apiParam (请求体字段说明) {UUID[]} [request.terminalGroupIdArr] 终端组id数组
     *
     * @apiSuccess (响应字段说明) {CbbAddSystemUpgradeTaskResponse} result CbbAddSystemUpgradeTaskResponse
     * @apiSuccess (响应字段说明) {UUID} result.upgradeTaskId upgradeTaskId 升级任务id
     * @apiSuccess (响应字段说明) {String} result.imgName imgName
     */
    /**
     * 添加刷机任务
     *
     * @param request 请求参数
     * @return 请求结果
     * @throws BusinessException 业务异常
     */
    CbbAddSystemUpgradeTaskResponse addSystemUpgradeTask(CbbAddSystemUpgradeTaskRequest request) throws BusinessException;

    /**
     * @api {POST} CbbTerminalSystemUpgradeAPI.addSystemUpgradeTerminal 添加刷机任务终端
     * @apiName addSystemUpgradeTerminal
     * @apiGroup CbbTerminalSystemUpgradeAPI
     * @apiDescription 添加刷机任务终端
     * @apiParam (请求体字段说明) {CbbUpgradeTerminalRequest} request CbbUpgradeTerminalRequest
     * @apiParam (请求体字段说明) {UUID} request.upgradeTaskId upgradeTaskId
     * @apiParam (请求体字段说明) {String} request.imgName imgName
     *
     * @apiSuccess (响应字段说明) {String} terminalName 终端名
     */
    /**
     *
     * 添加刷机任务终端
     *
     * @param request 请求参数
     * @return 添加结果
     * @throws BusinessException 业务异常
     */
    String addSystemUpgradeTerminal(CbbUpgradeTerminalRequest request) throws BusinessException;

    /**
     * @api {POST} CbbTerminalSystemUpgradeAPI.editSystemUpgradeTerminalGroup 编辑刷机任务终端分组
     * @apiName editSystemUpgradeTerminalGroup
     * @apiGroup CbbTerminalSystemUpgradeAPI
     * @apiDescription 编辑刷机任务终端分组
     * @apiParam (请求体字段说明) {CbbUpgradeTerminalGroupRequest} request CbbUpgradeTerminalGroupRequest
     * @apiParam (请求体字段说明) {UUID} request.upgradeTaskId upgradeTaskId
     * @apiParam (请求体字段说明) {UUID[]} request.terminalGroupIdArr 终端组数组
     *
     * @apiSuccess (响应字段说明) {void} void 无返回值参数
     */
    /**
     *
     * 编辑刷机任务终端分组
     *
     * @param request 请求参数
     * @throws BusinessException 业务异常
     */
    void editSystemUpgradeTerminalGroup(CbbUpgradeTerminalGroupRequest request) throws BusinessException;

    /**
     * @api {POST} CbbTerminalSystemUpgradeAPI.listSystemUpgradeTask 获取刷机任务列表信息
     * @apiName listSystemUpgradeTask
     * @apiGroup CbbTerminalSystemUpgradeAPI
     * @apiDescription 获取刷机任务列表信息
     * @apiParam (请求体字段说明) {PageSearchRequest} request PageSearchRequest
     * @apiParam (请求体字段说明) {String} [request.searchKeyword] searchKeyword 索引关键字
     * @apiParam (请求体字段说明) {MatchEqual[]} [request.matchEqualArr] matchEqual数组
     * @apiParam (请求体字段说明) {String} [request.matchEqualArr.name] 名称
     * @apiParam (请求体字段说明) {Object[]} [request.matchEqualArr.valueArr] 数值数组
     * @apiParam (请求体字段说明) {Sort} [request.sort] sort
     * @apiParam (请求体字段说明) {String} [request.sort.sortField] 排序字段
     * @apiParam (请求体字段说明) {Direction="DESC","ASC"} [request.sort.direction] 升降序
     * @apiParam (请求体字段说明) {Date} [request.betweenTimeRangeMatch.startTime] 起始时间
     * @apiParam (请求体字段说明) {Date} [request.betweenTimeRangeMatch.endTime] 截止时间
     * @apiParam (请求体字段说明) {String} [request.betweenTimeRangeMatch.timeKey] 时间标志位
     *
     * @apiSuccess (响应字段说明) {CbbSystemUpgradeTaskDTO} itemArr 响应实体
     * @apiSuccess (响应字段说明) {UUID} itemArr.id id
     * @apiSuccess (响应字段说明) {String} itemArr.packageVersion package版本
     * @apiSuccess (响应字段说明) {String} itemArr.packageName package名称
     * @apiSuccess (响应字段说明) {Date} itemArr.createTime 创建时间
     * @apiSuccess (响应字段说明) {CbbSystemUpgradeTaskStateEnums="UPGRADING","FINISH"} itemArr.upgradeTaskState
     * @apiSuccess (响应字段说明) {Integer} itemArr.successNum 成功数字
     * @apiSuccess (响应字段说明) {CbbSystemUpgradeModeEnums="AUTO","MANUAL"} itemArr.upgradeMode
     * @apiSuccess (响应字段说明) {CbbTerminalTypeEnums="VDI_LINUX("VDI", "Linux")","VDI_ANDROID("VDI", "Android")","VDI_WINDOWS("VDI", "Windows")","IDV_LINUX("IDV", "Linux")","APP_WINDOWS("APP", "Windows")","APP_ANDROID("APP", "Android")","APP_MACOS("APP", "macOS")","APP_IOS("APP", "iOS")","APP_LINUX("APP", "Linux")"} itemArr.packageType TODO
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
    DefaultPageResponse<CbbSystemUpgradeTaskDTO> listSystemUpgradeTask(PageSearchRequest request) throws BusinessException;

    /**
     * @api {POST} CbbTerminalSystemUpgradeAPI.listSystemUpgradeTaskTerminal 获取刷机任务终端列表
     * @apiName listSystemUpgradeTaskTerminal
     * @apiGroup CbbTerminalSystemUpgradeAPI
     * @apiDescription 获取刷机任务终端列表
     * @apiParam (请求体字段说明) {PageSearchRequest} request PageSearchRequest
     * @apiParam (请求体字段说明) {String} [request.searchKeyword] searchKeyword
     * @apiParam (请求体字段说明) {MatchEqual[]} [request.matchEqualArr] matchEqual数组
     * @apiParam (请求体字段说明) {Sort} [request.sort] sort
     * @apiParam (请求体字段说明) {String} [request.sort.sortField] 排序字段
     * @apiParam (请求体字段说明) {Direction="DESC","ASC"} [request.sort.direction] 升降序
     *
     * @apiSuccess (响应字段说明) {CbbSystemUpgradeTaskTerminalDTO} itemArr 响应实体
     * @apiSuccess (响应字段说明) {UUID} itemArr.id id
     * @apiSuccess (响应字段说明) {String} itemArr.terminalId id
     * @apiSuccess (响应字段说明) {String} itemArr.terminalName 名称
     * @apiSuccess (响应字段说明) {String} itemArr.ip ip
     * @apiSuccess (响应字段说明) {String} itemArr.mac mac
     * @apiSuccess (响应字段说明) {Date} itemArr.startTime 开始时间
     * @apiSuccess (响应字段说明) {CbbSystemUpgradeStateEnums="WAIT","UPGRADING","SUCCESS","FAIL","UNDO","UNSUPPORTED","NO_NEED"} itemArr.terminalUpgradeState
     */
    /**
     * 获取刷机任务终端列表
     *
     * @param request 请求参数
     * @return 刷机终端列表
     */
    DefaultPageResponse<CbbSystemUpgradeTaskTerminalDTO> listSystemUpgradeTaskTerminal(PageSearchRequest request);

    /**
     * @api {POST} CbbTerminalSystemUpgradeAPI.listSystemUpgradeTaskTerminalGroup 获取刷机任务终端分组列表
     * @apiName listSystemUpgradeTaskTerminalGroup
     * @apiGroup CbbTerminalSystemUpgradeAPI
     * @apiDescription 获取刷机任务终端分组列表
     * @apiParam (请求体字段说明) {PageSearchRequest} request PageSearchRequest
     * @apiParam (请求体字段说明) {String} [request.searchKeyword] searchKeyword
     * @apiParam (请求体字段说明) {MatchEqual[]} [request.matchEqualArr] matchEqual数组
     * @apiParam (请求体字段说明) {Sort} [request.sort] sort
     * @apiParam (请求体字段说明) {String} [request.sort.sortField] 排序字段
     * @apiParam (请求体字段说明) {Direction="DESC","ASC"} [request.sort.direction] 升降序
     *
     * @apiSuccess (响应字段说明) {TerminalGroupDTO} itemArr 响应实体
     * @apiSuccess (响应字段说明) {UUID} itemArr.id 终端分组id
     * @apiSuccess (响应字段说明) {String} itemArr.groupName 终端分组名称
     * @apiSuccess (响应字段说明) {UUID} itemArr.parentGroupId 父级分组id
     * @apiSuccess (响应字段说明) {String} itemArr.parentGroupName 父级分组名称
     * @apiSuccess (响应字段说明) {Boolean} itemArr.enableDefault 父级分组名称
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
    DefaultPageResponse<TerminalGroupDTO> listSystemUpgradeTaskTerminalGroup(PageSearchRequest request) throws BusinessException;

    /**
     * @api {POST} CbbTerminalSystemUpgradeAPI.closeSystemUpgradeTask 关闭刷机任务
     * @apiName closeSystemUpgradeTask
     * @apiGroup CbbTerminalSystemUpgradeAPI
     * @apiDescription 关闭刷机任务
     * @apiParam (请求体字段说明) {UUID} task 刷机任务id
     *
     * @apiSuccess (响应字段说明) {void} void 无返回值参数
     */
    /**
     * 关闭刷机任务
     *
     * @param taskId 请求参数
     * @throws BusinessException 业务异常
     */
    
    void closeSystemUpgradeTask(UUID taskId) throws BusinessException;

    /**
     * @api {POST} CbbTerminalSystemUpgradeAPI.listUpgradeableTerminal 终端可刷机的列表
     * @apiName listUpgradeableTerminal
     * @apiGroup CbbTerminalSystemUpgradeAPI
     * @apiDescription 终端可刷机的列表
     * @apiParam (请求体字段说明) {CbbUpgradeableTerminalPageSearchRequest} apiRequest CbbUpgradeableTerminalPageSearchRequest
     * @apiParam (请求体字段说明) {String} [apiRequest.searchKeyword] 关键字搜索
     * @apiParam (请求体字段说明) {MatchEqual[]} [apiRequest.matchEqualArr] 精确匹配字段
     * @apiParam (请求体字段说明) {Sort} [apiRequest.sort]
     * @apiParam (请求体字段说明) {String} apiRequest.sort.sortField 排序字段
     * @apiParam (请求体字段说明) {Direction="DESC","ASC"} apiRequest.sort.direction 升降序
     *
     * @apiSuccess (响应字段说明) {CbbUpgradeableTerminalListDTO} itemArr 响应实体
     * @apiSuccess (响应字段说明) {UUID} itemArr.id 终端id
     * @apiSuccess (响应字段说明) {String} itemArr.terminalName 终端名称
     * @apiSuccess (响应字段说明) {String} itemArr.ip ip
     * @apiSuccess (响应字段说明) {String} itemArr.mac mac
     * @apiSuccess (响应字段说明) {CbbTerminalStateEnums="OFFLINE","ONLINE","UPGRADING"} itemArr.terminalState 终端状态
     * @apiSuccess (响应字段说明) {String} itemArr.productType 产品型号
     * @apiSuccess (响应字段说明) {Date} itemArr.lastUpgradeTime 最后一次升级时间
     * @apiSuccess (响应字段说明) {UUID} itemArr.groupId 终端组id
     */
    /**
     * 终端可刷机的列表
     *
     * @param apiRequest 请求参数
     * @return 终端列表
     * @throws BusinessException 业务异常
     */
    DefaultPageResponse<CbbUpgradeableTerminalListDTO> listUpgradeableTerminal(CbbUpgradeableTerminalPageSearchRequest apiRequest)
            throws BusinessException;

    /**
     * @api {POST} CbbTerminalSystemUpgradeAPI.getUpgradeTerminalByTaskId 获取刷机任务终端列表
     * @apiName getUpgradeTerminalByTaskId
     * @apiGroup CbbTerminalSystemUpgradeAPI
     * @apiDescription 获取刷机任务终端列表
     * @apiParam (请求体字段说明) {CbbGetTaskUpgradeTerminalRequest} request CbbGetTaskUpgradeTerminalRequest
     * @apiParam (请求体字段说明) {CbbSystemUpgradeStateEnums="WAIT","UPGRADING","SUCCESS","FAIL","UNDO","UNSUPPORTED","NO_NEED"} [request.terminalState] 终端升级状态
     *
     * @apiSuccess (响应字段说明) {List} upgradeTerminalList 响应实体列表
     * @apiSuccess (响应字段说明) {UUID} upgradeTerminalList.id 终端分组名称
     * @apiSuccess (响应字段说明) {String} upgradeTerminalList.terminalId 父级分组id
     * @apiSuccess (响应字段说明) {String} upgradeTerminalList.terminalName 父级分组名称
     * @apiSuccess (响应字段说明) {String} upgradeTerminalList.ip总数
     * @apiSuccess (响应字段说明) {String} upgradeTerminalList.mac 响应实体列表
     * @apiSuccess (响应字段说明) {Date} upgradeTerminalList.startTime 终端分组名称
     * @apiSuccess (响应字段说明) {CbbSystemUpgradeStateEnums="WAIT","UPGRADING","SUCCESS","FAIL","UNDO","UNSUPPORTED","NO_NEED"} upgradeTerminalList.terminalUpgradeState 终端升级后的状态
     */
    /**
     * 获取刷机任务终端列表
     *
     * @param request 请求参数
     * @return 刷机终端信息列表
     */
    List<CbbSystemUpgradeTaskTerminalDTO> getUpgradeTerminalByTaskId(CbbGetTaskUpgradeTerminalRequest request);

    /**
     * @api {POST} CbbTerminalSystemUpgradeAPI.cancelUpgradeTerminal 取消刷机终端
     * @apiName cancelUpgradeTerminal
     * @apiGroup CbbTerminalSystemUpgradeAPI
     * @apiDescription 取消刷机终端
     * @apiParam (请求体字段说明) {CbbUpgradeTerminalRequest} request CbbUpgradeTerminalRequest
     * @apiParam (请求体字段说明) {UUID} [request.upgradeTaskId] upgradeTaskId
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
    String cancelUpgradeTerminal(CbbUpgradeTerminalRequest request) throws BusinessException;

    /**
     * @api {POST} CbbTerminalSystemUpgradeAPI.retryUpgradeTerminal 重试终端刷机
     * @apiName retryUpgradeTerminal
     * @apiGroup CbbTerminalSystemUpgradeAPI
     * @apiDescription 重试终端刷机
     * @apiParam (请求体字段说明) {CbbUpgradeTerminalRequest}request CbbUpgradeTerminalRequest
     * @apiParam (请求体字段说明) {UUID} [request.upgradeTaskId] upgradeTaskId
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
    String retryUpgradeTerminal(CbbUpgradeTerminalRequest request) throws BusinessException;

    /**
     * @api {POST} CbbTerminalSystemUpgradeAPI.getTerminalUpgradeTaskById 通过id获取终端刷机任务信息
     * @apiName getTerminalUpgradeTaskById
     * @apiGroup CbbTerminalSystemUpgradeAPI
     * @apiDescription 通过id获取终端刷机任务信息
     * @apiParam (请求体字段说明) {UUID} taskId 刷机任务id
     *
     * @apiSuccess (响应字段说明) {CbbSystemUpgradeTaskDTO} upgradeTask 响应实体
     * @apiSuccess (响应字段说明) {UUID} upgradeTask.id id
     * @apiSuccess (响应字段说明) {String} upgradeTask.packageVersion package版本
     * @apiSuccess (响应字段说明) {String} upgradeTask.packageName package名
     * @apiSuccess (响应字段说明) {Date} upgradeTask.createTime 创建时间
     * @apiSuccess (响应字段说明) {CbbSystemUpgradeTaskStateEnums="UPGRADING","FINISH"} upgradeTask.upgradeTaskState 升级任务状态
     * @apiSuccess (响应字段说明) {Integer} upgradeTask.successNum 成功数字
     * @apiSuccess (响应字段说明) {CbbSystemUpgradeModeEnums="AUTO","MANUAL"} upgradeTask.upgradeMode 升级模式 自动、手动
     * @apiSuccess (响应字段说明) {CbbTerminalTypeEnums="VDI_LINUX("VDI", "Linux")","VDI_ANDROID("VDI", "Android")","VDI_WINDOWS("VDI", "Windows")","IDV_LINUX("IDV", "Linux")","APP_WINDOWS("APP", "Windows")","APP_ANDROID("APP", "Android")","APP_MACOS("APP", "macOS")","APP_IOS("APP", "iOS")","APP_LINUX("APP", "Linux")"} upgradeTask.packageType 升级包类型
     */
    /**
     * 通过id获取终端刷机任务信息
     * 
     * @param taskId 请求参数
     * @return 请求结果
     * @throws BusinessException 业务异常
     */
    CbbSystemUpgradeTaskDTO getTerminalUpgradeTaskById(UUID taskId) throws BusinessException;

}
