package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.sk.base.exception.BusinessException;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/10
 *
 * @author hs
 */
public interface TerminalSystemUpgradeHandler {

    /**
     * 上传系统升级包
     * @param request 请求参数
     * @throws BusinessException 异常
     */
    void uploadUpgradePackage(CbbTerminalUpgradePackageUploadRequest request) throws BusinessException;
}
