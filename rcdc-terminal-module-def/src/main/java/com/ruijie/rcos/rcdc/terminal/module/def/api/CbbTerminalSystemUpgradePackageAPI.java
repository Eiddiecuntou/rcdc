package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalSystemUpgradePackageInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbCheckAllowUploadPackageDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalUpgradePackageUploadDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbCheckAllowUploadPackageResultDTO;
import com.ruijie.rcos.sk.base.exception.BusinessException;

import java.util.UUID;


/**
 *
 * Description: 终端刷机包接口
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月17日
 * 
 * @author nt
 */
public interface CbbTerminalSystemUpgradePackageAPI {

    /**
     * @api {POST} CbbTerminalSystemUpgradePackageAPI.uploadUpgradePackage 上传终端系统升级文件
     * @apiName uploadUpgradePackage
     * @apiGroup CbbTerminalSystemUpgradePackageAPI
     * @apiDescription 上传终端系统升级文件
     * @apiParam (请求体字段说明) {CbbTerminalUpgradePackageUploadDTO}request CbbTerminalUpgradePackageUploadDTO 请求实体
     * @apiParam (请求体字段说明) {String} request.filePath 升级包路径
     * @apiParam (请求体字段说明) {String} request.fileName 升级包名称
     * @apiParam (请求体字段说明) {String} request.fileMD5 升级包MD5
     * @apiParam (请求体字段说明) {CbbTerminalTypeEnums="VDI_LINUX","VDI_ANDROID","VDI_WINDOWS","IDV_LINUX","APP_WINDOWS","APP_ANDROID","APP_MACOS",
     * "APP_IOS","APP_LINUX"} request.terminalType 终端类型
     *
     * @apiErrorExample {json} 异常码列表
     *  {code:rcdc_terminal_system_upgrade_task_is_running message:终端系统升级任务正在进行中}
     *
     */
    /**
     * 
     * 上传终端系统升级文件
     * 
     * @param request 请求参数
     * @throws BusinessException 业务异常
     */
    void uploadUpgradePackage(CbbTerminalUpgradePackageUploadDTO request) throws BusinessException;

    /**
     * @api {POST} CbbTerminalSystemUpgradePackageAPI.deleteUpgradePackage 删除终端系统升级文件
     * @apiName deleteUpgradePackage
     * @apiGroup CbbTerminalSystemUpgradePackageAPI
     * @apiDescription 删除终端系统升级文件
     * @apiParam (请求体字段说明) {UUID} packageId 升级包id
     *
     * @apiSuccess (响应字段说明) {String} packageName 文件名
     *
     * @apiErrorExample {json} 异常码列表
     *  {code:rcdc_terminal_upgrade_package_has_running_task_not_allow_delete message:终端升级包存在进行中的升级任务，不可删除}
     *  {code:rcdc_terminal_system_upgrade_package_not_exist message:终端系统升级包不存在}
     */
    /**
     *
     * 删除终端系统升级文件
     * 
     * @param packageId 请求参数
     * @return 请求结果
     * @throws BusinessException 业务异常
     */
    String deleteUpgradePackage(UUID packageId) throws BusinessException;

    /**
     * @api {POST} CbbTerminalSystemUpgradePackageAPI.listSystemUpgradePackage 获取系统升级包列表
     * @apiName listSystemUpgradePackage
     * @apiGroup CbbTerminalSystemUpgradePackageAPI
     * @apiDescription 获取系统升级包列表
     *
     * @apiSuccess (响应字段说明) {CbbTerminalSystemUpgradePackageInfoDTO[]} itemArr 响应实体类
     * @apiSuccess (响应字段说明) {UUID} itemArr.id 升级包id
     * @apiSuccess (响应字段说明) {String} itemArr.name 升级包名称
     * @apiSuccess (响应字段说明) {CbbTerminalTypeEnums="VDI_LINUX","VDI_ANDROID","VDI_WINDOWS","IDV_LINUX","APP_WINDOWS","APP_ANDROID","APP_MACOS",
     * "APP_IOS","APP_LINUX"} itemArr.terminalType 终端类型
     * @apiSuccess (响应字段说明) {CbbSystemUpgradePackageOriginEnums="USER_UPLOAD"} itemArr.origin 升级包来源
     * @apiSuccess (响应字段说明) {CbbSystemUpgradeDistributionModeEnums="FAST_UPGRADE"} itemArr.distributionMode 系统刷机包分发方式
     * @apiSuccess (响应字段说明) {CbbSystemUpgradeTaskStateEnums="UPGRADING","FINISH"} itemArr.status 升级任务状态
     * @apiSuccess (响应字段说明) {UUID} itemArr.upgradeTaskId 升级任务id
     * @apiSuccess (响应字段说明) {Date} itemArr.uploadTime 上传时间
     * @apiSuccess (响应字段说明) {CbbSystemUpgradeModeEnums="AUTO","MANUAL"} itemArr.upgradeMode 升级模式，自动、手动
     *
     */
    /**
     *
     * 获取系统升级包列表
     *
     *
     * @return 升级包列表
     * @throws BusinessException 业务异常
     */
    CbbTerminalSystemUpgradePackageInfoDTO[] listSystemUpgradePackage()
            throws BusinessException;

    /**
     * @api {POST} CbbTerminalSystemUpgradePackageAPI.findById 获取终端升级包信息
     * @apiName findById
     * @apiGroup CbbTerminalSystemUpgradePackageAPI
     * @apiDescription 获取终端升级包信息
     * @apiParam (请求体字段说明) {UUID} UUID 升级包id
     *
     * @apiSuccess (响应字段说明) {CbbTerminalSystemUpgradePackageInfoDTO} terminalSystemUpgradePackageInfoDTO 响应实体类
     * @apiSuccess (响应字段说明) {UUID} terminalSystemUpgradePackageInfoDTO.id 升级包id
     * @apiSuccess (响应字段说明) {String} terminalSystemUpgradePackageInfoDTO.name 升级包名称
     * @apiSuccess (响应字段说明) {CbbTerminalTypeEnums="VDI_LINUX","VDI_ANDROID","VDI_WINDOWS","IDV_LINUX","APP_WINDOWS","APP_ANDROID","APP_MACOS",
     * "APP_IOS","APP_LINUX"} terminalSystemUpgradePackageInfoDTO.terminalType 终端类型
     * @apiSuccess (响应字段说明) {CbbSystemUpgradePackageOriginEnums="USER_UPLOAD"} terminalSystemUpgradePackageInfoDTO.origin 升级包来源
     * @apiSuccess (响应字段说明) {CbbSystemUpgradeDistributionModeEnums="FAST_UPGRADE"} terminalSystemUpgradePackageInfoDTO
     * .distributionMode 系统刷机包分发方式
     * @apiSuccess (响应字段说明) {CbbSystemUpgradeTaskStateEnums="UPGRADING","FINISH"} terminalSystemUpgradePackageInfoDTO.status 升级任务状态
     * @apiSuccess (响应字段说明) {UUID} terminalSystemUpgradePackageInfoDTO.upgradeTaskId 升级任务id
     * @apiSuccess (响应字段说明) {Date} terminalSystemUpgradePackageInfoDTO.uploadTime 上传时间
     * @apiSuccess (响应字段说明) {CbbSystemUpgradeModeEnums="AUTO","MANUAL"} terminalSystemUpgradePackageInfoDTO.upgradeMode 升级模式，自动、手动
     *
     * @apiErrorExample {json} 异常码列表
     *  {code:rcdc_terminal_system_upgrade_package_not_exist message:终端系统升级包不存在}
     */
    /**
     * 获取终端升级包信息
     * 
     * @param packageId 请求参数
     * @return 终端升级包信息
     * @throws BusinessException 业务异常
     */
    CbbTerminalSystemUpgradePackageInfoDTO findById(UUID packageId) throws BusinessException;

    /**
     * @api {POST} CbbTerminalSystemUpgradePackageAPI.checkAllowUploadPackage 校验升级包是否允许上传
     * @apiName checkAllowUploadPackage
     * @apiGroup CbbTerminalSystemUpgradePackageAPI
     * @apiDescription 校验升级包是否允许上传
     * @apiParam (请求体字段说明) {CbbCheckAllowUploadPackageDTO} request CbbCheckAllowUploadPackageDTO
     * @apiParam (请求体字段说明) {Long} request.fileSize 文件大小
     * @apiParam (请求体字段说明) {CbbTerminalTypeEnums="VDI_LINUX","VDI_ANDROID","VDI_WINDOWS","IDV_LINUX","APP_WINDOWS","APP_ANDROID","APP_MACOS",
     * "APP_IOS","APP_LINUX"} request.terminalType 终端类型
     *
     * @apiSuccess (响应字段说明) {CbbCheckAllowUploadPackageResultDTO} response CbbCheckAllowUploadPackageResultDTO
     * @apiSuccess (响应字段说明) {Boolean} response.allowUpload 是否允许上传
     * @apiSuccess (响应字段说明) {List} response.errorList 错误信息列表
     *
     * @apiErrorExample {json} 异常码列表
     *  {code:rcdc_terminal_system_upgrade_task_is_running message:终端系统升级任务正在进行中}
     *  {code:rcdc_terminal_upgrade_package_disk_space_not_enough message:终端系统升级包存放磁盘空间不足}
     */
    /**
     * 校验升级包是否允许上传
     *
     * @param request 请求参数
     * @return 请求响应
     * @throws BusinessException 业务异常
     */
    CbbCheckAllowUploadPackageResultDTO checkAllowUploadPackage(CbbCheckAllowUploadPackageDTO request)
            throws BusinessException;

}
