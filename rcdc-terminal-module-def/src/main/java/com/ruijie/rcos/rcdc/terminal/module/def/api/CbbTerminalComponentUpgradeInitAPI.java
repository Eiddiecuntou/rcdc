package com.ruijie.rcos.rcdc.terminal.module.def.api;


/**
 * 
 * Description: 终端组件升级包初始化API接口
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020年10月14日
 * 
 * @author nting
 */
public interface CbbTerminalComponentUpgradeInitAPI {


    /**
     * @api {POST} CbbTerminalComponentUpgradeInitAPI.initLinuxVDI  初始化Linux vdi组件升级包
     * @apiName initLinuxVDI
     * @apiGroup CbbTerminalComponentUpgradeInitAPI
     * @apiDescription 初始化Linux vdi组件升级包
     *
     */
    /**
     * 初始化Linux vdi组件升级包
     *
     */
    void initLinuxVDI();

    /**
     * @api {POST} CbbTerminalComponentUpgradeInitAPI.initAndroidVDI  初始化Android vdi组件升级包
     * @apiName initAndroidVDI
     * @apiGroup CbbTerminalComponentUpgradeInitAPI
     * @apiDescription 初始化Android vdi组件升级包
     *
     */
    /**
     * 初始化Android vdi组件升级包
     *
     */
    void initAndroidVDI();

    /**
     * @api {POST} CbbTerminalComponentUpgradeInitAPI.initLinuxIDV  初始化Linux idv组件升级包
     * @apiName initAndroidOTA
     * @apiGroup CbbTerminalComponentUpgradeInitAPI
     * @apiDescription 初始化Linux idv组件升级包
     *
     */
    /**
     * 初始化Linux idv组件升级包
     *
     */
    void initLinuxIDV();
}
