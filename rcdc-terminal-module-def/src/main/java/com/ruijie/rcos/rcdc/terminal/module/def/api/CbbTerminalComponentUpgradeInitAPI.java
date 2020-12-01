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
     * @api {POST} CbbTerminalComponentUpgradeInitAPI.initLinux  初始化Linux 组件升级包
     * @apiName initLinux
     * @apiGroup CbbTerminalComponentUpgradeInitAPI
     * @apiDescription 初始化Linux 组件升级包
     *
     */
    /**
     * 初始化Linux 组件升级包
     *
     */
    void initLinux();

    /**
     * @api {POST} CbbTerminalComponentUpgradeInitAPI.initAndroid  初始化Android 组件升级包
     * @apiName initAndroid
     * @apiGroup CbbTerminalComponentUpgradeInitAPI
     * @apiDescription 初始化Android 组件升级包
     *
     */
    /**
     * 初始化Android 组件升级包
     *
     */
    void initAndroid();

}
