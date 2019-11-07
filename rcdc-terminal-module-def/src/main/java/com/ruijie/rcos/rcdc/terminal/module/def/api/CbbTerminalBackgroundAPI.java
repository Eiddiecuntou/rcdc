package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalBackgroundImageInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalBackGroundUploadRequest;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.DtoResponse;
import com.ruijie.rcos.sk.modulekit.api.tx.NoRollback;

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
     * 上传终端背景图
     * @param request
     * @throws BusinessException 业务异常
     */
    @NoRollback
    void upload(CbbTerminalBackGroundUploadRequest request) throws BusinessException;

    /**
     * 获取终端背景图的路径
     * @return DefaultResponse
     * @throws BusinessException 业务异常
     */
    @NoRollback
    DtoResponse<CbbTerminalBackgroundImageInfoDTO> getBackgroundImageInfo() throws BusinessException;

    /**
     * 初始化背景图
     * @return DefaultResponse
     * @throws BusinessException 业务异常
     */
    @NoRollback
    DefaultResponse initBackgroundImage() throws BusinessException;
}
