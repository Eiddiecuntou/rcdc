package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.CbbTerminalModelDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbItemArrResponse;
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
public interface CbbTerminalModelAPI {

    /**
     *  查询终端类型列表
     *
     * @param platformArr 请求参数
     * @return 终端类型列表
     */
    
    CbbItemArrResponse<CbbTerminalModelDTO> listTerminalModel(CbbTerminalPlatformEnums[] platformArr);


    /**
     *  根据终端型号id查询终端型号
     *
     * @param productId 请求参数
     * @return 终端型号信息
     * @throws BusinessException 业务异常
     */
    
    CbbTerminalModelDTO queryByProductId(String productId) throws BusinessException;


    /**
     * 查询终端运行平台类型
     * @param platformArr 终端平台类型
     * @return 终端运行平台类型
     */
    List<String> listTerminalOsType(CbbTerminalPlatformEnums[] platformArr);
}
