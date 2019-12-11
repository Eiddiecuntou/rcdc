package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.CbbTerminalModelDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalPlatformRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalProductIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbItemArrResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalCpuTypeResponse;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.tx.NoRollback;

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
     * @param request 请求参数
     * @return 终端类型列表
     */
    @NoRollback
    CbbItemArrResponse<CbbTerminalModelDTO> listTerminalModel(CbbTerminalPlatformRequest request);


    /**
     *  根据终端类型查询支持的cpu型号
     *
     * @param request 请求参数
     * @return cpu型号列表
     * @throws BusinessException 业务异常
     */
    @NoRollback
    CbbTerminalCpuTypeResponse queryCpuTypeByProductId(CbbTerminalProductIdRequest request) throws BusinessException;
}
