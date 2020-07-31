package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalBackgroundImageInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalBackgroundSaveRequest;
import com.ruijie.rcos.sk.base.exception.BusinessException;

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
     * 上传终端背景图,保存背景图的相关配置，并且向在线终端同步背景图
     * @param request 文件信息
     * @throws BusinessException 业务异常
     */
    void saveBackgroundImageConfig(CbbTerminalBackgroundSaveRequest request) throws BusinessException;

    /**
     * 获取终端背景图的路径
     * @return DefaultResponse
     * @throws BusinessException 业务异常
     */
    CbbTerminalBackgroundImageInfoDTO getBackgroundImageInfo() throws BusinessException;

    /**
     * 初始化背景图
     * @throws BusinessException 业务异常
     */
    void initBackgroundImage() throws BusinessException;
}
