package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.*;
import com.ruijie.rcos.sk.base.exception.BusinessException;

import java.util.List;
import java.util.UUID;


/**
 * 
 * Description: 终端升级初始化API接口
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020年10月14日
 * 
 * @author nting
 */
public interface CbbTerminalUpgradeInitAPI {


    /**
     * @api {POST} CbbTerminalUpgradeInitAPI.initAndroidOTA 初始化安卓OTA
     * @apiName initAndroidOTA
     * @apiGroup CbbTerminalUpgradeInitAPI
     * @apiDescription 初始化安卓OTA
     *
     */
    /**
     * 初始化安卓OTA
     *
     */
    void initAndroidOTA();
}
