package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalModelDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.sk.base.exception.BusinessException;

import java.util.List;

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
     * @param platformArr 平台类型
     * @return CbbTerminalModelDTO数组
     */
    CbbTerminalModelDTO[] queryTerminalModelByPlatform(CbbTerminalPlatformEnums[] platformArr);

    /**
     *  根据终端类型查询支持的cpu型号
     *
     * @param productId 终端型号id
     * @return CbbTerminalModelDTO
     * @throws BusinessException 业务异常
     */
    CbbTerminalModelDTO queryByProductId(String productId) throws BusinessException;

    /**
     * 查询所有终端运行平台类型
     * @param platformArr 终端平台类型
     * @return List<String>
     */
    List<String> queryTerminalOsTypeByPlatform(CbbTerminalPlatformEnums[] platformArr);
}
