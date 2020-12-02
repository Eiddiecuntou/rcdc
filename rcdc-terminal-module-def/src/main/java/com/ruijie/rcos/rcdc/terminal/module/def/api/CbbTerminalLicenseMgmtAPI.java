package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbIDVTerminalLicenseNumDTO;
import com.ruijie.rcos.sk.base.exception.BusinessException;

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
     * @apiErrorExample {json} 异常码列表
     *  {code:rcdc_terminal_not_allow_reduce_terminal_license_num message:终端正式授权证书个数不允许减少}
     */
    /**
     * 设置IDV终端授权数，限制IDV终端接入数量
     *
     * @param licenseNum 授权数
     * @throws BusinessException 业务异常
     */
    void setIDVTerminalLicenseNum(Integer licenseNum) throws BusinessException;

    /**
     * @api {GET} CbbTerminalLicenseMgmtAPI.getIDVTerminalLicenseNum 获取IDV终端授权信息
     * @apiName getIDVTerminalLicenseNum
     * @apiGroup CbbTerminalLicenseMgmtAPI
     * @apiDescription 获取IDV终端授权数
     *
     *
     * @apiSuccess (响应字段说明) {CbbIDVTerminalLicenseNumDTO} response CbbIDVTerminalLicenseNumDTO
     * @apiSuccess (响应字段说明) {Integer} response.licenseNum 证书授权数
     * @apiSuccess (响应字段说明) {Integer} response.usedNum 已用授权数
     */
    /**
     * 获取IDV终端授权数
     *
     * @return IDV终端授权信息
     */
    CbbIDVTerminalLicenseNumDTO getIDVTerminalLicenseNum();


}
