package com.ruijie.rcos.rcdc.terminal.module.def.api;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/8/26 18:03
 *
 * @author yanlin
 */
public interface CbbTerminalUseCvaLicenseAPI {

    /**
     * @api {POST} CbbTerminalUseCvaLicenseAPI.obtainTerminalUseCvaLicenseNum 获取当前IDV终端使用CVA云应用合并授权数量
     * @apiName obtainTerminalUseCvaLicenseNum
     * @apiGroup CbbTerminalUseCvaLicenseAPI
     * @apiDescription 获取当前IDV终端使用CVA云应用合并授权数
     * @apiSuccess (响应字段说明) {Integer} CVA授权使用数
     */

    /**
     * 获取当前IDV终端使用CVA云应用合并授权数量
      * @return 使用数量
     */
    int obtainTerminalUseCvaLicenseNum();

}
