package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalSystemUpgradePackageInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbCheckAllowUploadPackageRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbCheckAllowUploadPackageResponse;
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
     * @apiParam (请求体字段说明) {CbbTerminalUpgradePackageUploadRequest}request CbbTerminalUpgradePackageUploadRequest
     * @apiParam (请求体字段说明) {String} request.filePath 文件路径
     * @apiParam (请求体字段说明) {String} request.fileName 文件名称
     * @apiParam (请求体字段说明) {String} request.fileMD5 文件MD5
     * @apiParam (请求体字段说明) {CbbTerminalTypeEnums="VDI_LINUX("VDI", "Linux")","VDI_ANDROID("VDI", "Android")","VDI_WINDOWS("VDI", "Windows")","IDV_LINUX("IDV", "Linux")","APP_WINDOWS("APP", "Windows")","APP_ANDROID("APP", "Android")","APP_MACOS("APP", "Mac_OS")","APP_IOS("APP", "iOS")","APP_LINUX("APP", "Linux")"} request.terminalType
     *
     * @apiSuccess (响应字段说明) {void} void 无返回值
     *
     */
    /**
     * 
     * 上传终端系统升级文件
     * 
     * @param request 请求参数
     * @throws BusinessException 业务异常
     */
    void uploadUpgradePackage(CbbTerminalUpgradePackageUploadRequest request) throws BusinessException;

    /**
     * @api {POST} CbbTerminalSystemUpgradePackageAPI.deleteUpgradePackage 删除终端系统升级文件
     * @apiName deleteUpgradePackage
     * @apiGroup CbbTerminalSystemUpgradePackageAPI
     * @apiDescription 删除终端系统升级文件
     * @apiParam (请求体字段说明) {UUID} packageId 升级包id
     *
     * @apiSuccess (响应字段说明) {String}  packageName 文件名
     *
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
     * @apiParam (请求体字段说明) {void} request 无请求参数
     *
     * @apiSuccess (响应字段说明) {CbbTerminalSystemUpgradePackageInfoDTO[]} itemArr 响应实体类
     * @apiSuccess (响应字段说明) {UUID} itemArr.id id
     * @apiSuccess (响应字段说明) {String} itemArr.name 名称
     * @apiSuccess (响应字段说明) {CbbTerminalTypeEnums="VDI_LINUX("VDI", "Linux")","VDI_ANDROID("VDI", "Android")","VDI_WINDOWS("VDI", "Windows")","IDV_LINUX("IDV", "Linux")","APP_WINDOWS("APP", "Windows")","APP_ANDROID("APP", "Android")","APP_MACOS("APP", "Mac_OS")","APP_IOS("APP", "iOS")","APP_LINUX("APP", "Linux")"} itemArr.packageType
     * @apiSuccess (响应字段说明) {CbbSystemUpgradePackageOriginEnums="USER_UPLOAD"} itemArr.origin TODO
     * @apiSuccess (响应字段说明) {CbbSystemUpgradeDistributionModeEnums="FAST_UPGRADE"} itemArr.distributionMode TODO
     * @apiSuccess (响应字段说明) {CbbSystemUpgradeTaskStateEnums="UPGRADING","FINISH"} itemArr.status TODO
     * @apiSuccess (响应字段说明) {UUID} itemArr.upgradeTaskId upgradeTaskId
     * @apiSuccess (响应字段说明) {Date} itemArr.uploadTime 上传时间
     * @apiSuccess (响应字段说明) {CbbSystemUpgradeModeEnums="AUTO","MANUAL"} itemArr.upgradeMode TODO
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
     * @api {POST} CbbTerminalSystemUpgradePackageAPI.getById 获取终端升级包信息
     * @apiName getById
     * @apiGroup CbbTerminalSystemUpgradePackageAPI
     * @apiDescription 获取终端升级包信息
     * @apiParam (请求体字段说明) {UUID} UUID 升级包id
     *
     * @apiSuccess (响应字段说明) {CbbTerminalSystemUpgradePackageInfoDTO[]} itemArr 响应实体类
     * @apiSuccess (响应字段说明) {UUID} itemArr.id id
     * @apiSuccess (响应字段说明) {String} itemArr.name 名称
     * @apiSuccess (响应字段说明) {CbbTerminalTypeEnums=VDI_LINUX("VDI", "Linux")","VDI_ANDROID("VDI", "Android")","VDI_WINDOWS("VDI", "Windows")","IDV_LINUX("IDV", "Linux")","APP_WINDOWS("APP", "Windows")","APP_ANDROID("APP", "Android")","APP_MACOS("APP", "Mac_OS")","APP_IOS("APP", "iOS")","APP_LINUX("APP", "Linux")"} itemArr.packageType
     * @apiSuccess (响应字段说明) {CbbSystemUpgradePackageOriginEnums="USER_UPLOAD"} itemArr.origin TODO
     * @apiSuccess (响应字段说明) {CbbSystemUpgradeDistributionModeEnums="FAST_UPGRADE"} itemArr.distributionMode TODO
     * @apiSuccess (响应字段说明) {CbbSystemUpgradeTaskStateEnums="UPGRADING","FINISH"} itemArr.status TODO
     * @apiSuccess (响应字段说明) {UUID} itemArr.upgradeTaskId upgradeTaskId
     * @apiSuccess (响应字段说明) {Date} itemArr.uploadTime 上传时间
     * @apiSuccess (响应字段说明) {CbbSystemUpgradeModeEnums="AUTO","MANUAL"} itemArr.upgradeMode TODO
     */
    /**
     * 获取终端升级包信息
     * 
     * @param packageId 请求参数
     * @return 终端升级包信息
     * @throws BusinessException 业务异常
     */

    CbbTerminalSystemUpgradePackageInfoDTO getById(UUID packageId) throws BusinessException;

    /**
     * @api {POST} CbbTerminalSystemUpgradePackageAPI.checkAllowUploadPackage 校验升级包是否允许上传
     * @apiName checkAllowUploadPackage
     * @apiGroup CbbTerminalSystemUpgradePackageAPI
     * @apiDescription 校验升级包是否允许上传
     * @apiParam (请求体字段说明) {Long}fileSize 文件大小
     * @apiParam (请求体字段说明) {CbbTerminalTypeEnums=VDI_LINUX("VDI", "Linux")","VDI_ANDROID("VDI", "Android")","VDI_WINDOWS("VDI", "Windows")","IDV_LINUX("IDV", "Linux")","APP_WINDOWS("APP", "Windows")","APP_ANDROID("APP", "Android")","APP_MACOS("APP", "Mac_OS")","APP_IOS("APP", "iOS")","APP_LINUX("APP", "Linux")"}terminalType
     *
     * @apiSuccess (响应字段说明) {CbbCheckAllowUploadPackageResponse} result CbbCheckAllowUploadPackageResponse
     * @apiSuccess (响应字段说明) {Boolean} result.allowUpload 是否允许上传
     * @apiSuccess (响应字段说明) {List} result.errorList 错误信息列表
     */
    /**
     * 校验升级包是否允许上传
     *
     * @param request 请求参数
     * @return 请求响应
     * @throws BusinessException 业务异常
     */
    CbbCheckAllowUploadPackageResponse checkAllowUploadPackage(CbbCheckAllowUploadPackageRequest request)
            throws BusinessException;

}
