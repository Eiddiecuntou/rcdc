package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.logo.CbbUploadLogoRequest;
import com.ruijie.rcos.sk.base.exception.BusinessException;




/**
 * Description: 终端Logo操作接口
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年7月8日
 *
 * @author huangsen
 */
public interface CbbTerminalLogoAPI {

    /**
     * @api {POST} CbbTerminalLogoAPI.uploadLogo 上传Logo
     * @apiName uploadLogo
     * @apiGroup CbbTerminalLogoAPI
     * @apiDescription 上传Logo
     * @apiParam (请求体字段说明) {CbbUploadLogoRequest} request CbbUploadLogoRequest
     * @apiParam (请求体字段说明) {String} request.logoPath 文件路径
     * @apiParam (请求体字段说明) {String} request.logoName 文件名称
     * @apiParam (请求体字段说明) {String} request.logoMD5 文件MD5
     *
     */
    /**
     * 上传Logo
     *
     * @param request 请求参数
     * @return 上传Logo结果
     * @throws BusinessException 请求异常
     */
    void uploadLogo(CbbUploadLogoRequest request) throws BusinessException;

    /**
     * @api {POST} CbbTerminalLogoAPI.getLogoPath 获取logo路径
     * @apiName getLogoPath
     * @apiGroup CbbTerminalLogoAPI
     * @apiDescription 获取logo路径
     *
     * @apiSuccess (响应字段说明) {String} logoPath logo相对ftp路径
     */
    /**
     * 获取logo路径
     *
     * @return 获取Logo路径结果
     * @throws BusinessException 请求异常
     */

    String getLogoPath() throws BusinessException;

    /**
     * @api {POST} CbbTerminalLogoAPI.initLogo 初始化Logo
     * @apiName initLogo
     * @apiGroup CbbTerminalLogoAPI
     * @apiDescription 初始化Logo
     *
     */
    /**
     * 初始化Logo
     *
     * @throws BusinessException 请求异常
     */
    void initLogo() throws BusinessException;
}

