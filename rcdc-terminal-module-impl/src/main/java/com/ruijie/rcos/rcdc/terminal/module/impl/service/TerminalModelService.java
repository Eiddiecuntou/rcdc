package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.CbbTerminalModelDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.sk.base.exception.BusinessException;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/12/10
 *
 * @author nt
 */
public interface TerminalModelService {

    /**
     *  查询所有终端类型
     *
     * @param platform 平台类型
     * @return CbbTerminalModelDTO数组
     */
    CbbTerminalModelDTO[] queryTerminalModelByPlatform(CbbTerminalPlatformEnums platform);

    /**
     *  根据终端类型查询支持的cpu型号
     *
     * @param productId 终端型号id
     * @return CbbTerminalModelDTO
     * @throws BusinessException 业务异常
     */
    CbbTerminalModelDTO queryByProductId(String productId) throws BusinessException;

}
