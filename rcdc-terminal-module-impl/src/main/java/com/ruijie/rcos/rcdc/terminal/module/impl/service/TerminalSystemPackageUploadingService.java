package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.tx.NoRollback;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/30
 *
 * @author hs
 */
public interface TerminalSystemPackageUploadingService {

    /**
     *
     * 判断是否正在上传刷机包
     *
     * @param terminalType 终端类型
     * @return 上传文件结果
     */
    @NoRollback
    boolean isUpgradeFileUploading(CbbTerminalTypeEnums terminalType);

    /**
     *
     * @param request 请求参数
     * @param terminalType 终端类型
     * @throws BusinessException 业务异常
     */
    @NoRollback
    void uploadUpgradePackage(CbbTerminalUpgradePackageUploadRequest request, CbbTerminalTypeEnums terminalType) throws BusinessException;
}
