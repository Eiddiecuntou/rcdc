package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbIDVTerminalLicenseNumDTO;

/**
 * 终端授权管理
 * <br>
 * Description: Function Description <br>
 * Copyright: Copyright (c) 2020 <br>
 * Company: Ruijie Co., Ltd. <br>
 * Create Time: 2020年9月21日 <br>
 *
 * @author jarman
 */
public interface CbbTerminalLicenseMgmtAPI {


    /**
     * @api {POST} CbbTerminalLicenseMgmtAPI.setIDVTerminalLicenseNum 设置IDV终端授权数
     * @apiName setDesktopLicenseNum
     * @apiGroup CbbLicenseMgmtAPI
     * @apiDescription 设置IDV终端授权数，限制IDV终端接入数量
     * @apiParam (请求体字段说明) {Integer} licenseNum 授权数
     *
     */
    /**
     * 设置IDV终端授权数，限制IDV终端接入数量
     *
     * @param licenseNum 授权数
     */
    void setIDVTerminalLicenseNum(Integer licenseNum);

    /**
     * @api {GET} CbbTerminalLicenseMgmtAPI.getIDVTerminalLicenseNum 获取IDV终端授权信息
     * @apiName getIDVTerminalLicenseNum
     * @apiGroup CbbTerminalLicenseMgmtAPI
     * @apiDescription 获取IDV终端授权数
     * @apiParam (请求体字段说明) {void} request 无请求参数
     *
     * @apiSuccess (响应字段说明) {CbbIDVTerminalLicenseNumDTO} response CbbIDVTerminalLicenseNumDTO
     * @apiSuccess (响应字段说明) {Integer} response.licenseNum 证书授权数
     * @apiSuccess (响应字段说明) {Integer} response.usedNum 已用授权数
     *
     */
    /**
     * 获取IDV终端授权数
     *
     * @return IDV终端授权信息
     */
    CbbIDVTerminalLicenseNumDTO getIDVTerminalLicenseNum();


}
