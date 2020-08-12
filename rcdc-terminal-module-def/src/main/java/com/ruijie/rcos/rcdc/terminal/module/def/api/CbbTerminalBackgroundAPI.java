package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalBackgroundImageInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalBackgroundSaveRequest;
import com.ruijie.rcos.sk.base.exception.BusinessException;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time:  2019/11/6
 *
 * @author songxiang
 */
public interface CbbTerminalBackgroundAPI {

    /**
     * @api {POST} CbbTerminalBackgroundAPI.saveBackgroundImageConfig 上传终端背景图
     * @apiName saveBackgroundImageConfig
     * @apiGroup CbbTerminalBackgroundAPI
     * @apiDescription 上传终端背景图,保存背景图的相关配置，并且向在线终端同步背景图
     * @apiParam (请求体字段说明) {String} imageName 文件名称
     * @apiParam (请求体字段说明) {Long} imageSize 文件大小
     * @apiParam (请求体字段说明) {String} md5 文件md5
     * @apiParam (请求体字段说明) {String} imagePath 文件路径
     *
     */
    /**
     * 上传终端背景图,保存背景图的相关配置，并且向在线终端同步背景图
     * @param request 文件信息
     * @throws BusinessException 业务异常
     */
    void saveBackgroundImageConfig(CbbTerminalBackgroundSaveRequest request) throws BusinessException;

    /**
     * @api {POST} CbbTerminalBackgroundAPI.getBackgroundImageInfo 获取终端背景图信息
     * @apiName getBackgroundImageInfo
     * @apiGroup CbbTerminalBackgroundAPI
     * @apiDescription 获取终端背景图的路径
     *
     * @apiSuccess (响应字段说明) {CbbTerminalBackgroundImageInfoDTO} result 响应实体
     * @apiSuccess (响应字段说明) {String} result.imagePath 文件路径
     * @apiSuccess (响应字段说明) {String} result.imageName 文件名称
     */
    /**
     * 获取终端背景图的路径
     * @return DefaultResponse
     * @throws BusinessException 业务异常
     */
    CbbTerminalBackgroundImageInfoDTO getBackgroundImageInfo() throws BusinessException;

    /**
     * @api {POST} CbbTerminalBackgroundAPI.initBackgroundImage 初始化背景图
     * @apiName initBackgroundImage
     * @apiGroup CbbTerminalBackgroundAPI
     * @apiDescription 初始化背景图
     */
    /**
     * 初始化背景图
     * @throws BusinessException 业务异常
     */
    void initBackgroundImage() throws BusinessException;
}
