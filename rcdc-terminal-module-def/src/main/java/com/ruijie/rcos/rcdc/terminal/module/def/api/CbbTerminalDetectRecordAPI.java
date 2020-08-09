package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbDetectDateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectPageRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbDetectResultResponse;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageResponse;


/**
 * Description: 检测记录API
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/14
 *
 * @author nt
 */
public interface CbbTerminalDetectRecordAPI {

    /**
     * 获取检测记录分页列表
     *
     * @param request 分页请求参数
     * @return 检测记录分页列表
     * @throws BusinessException 业务异常
     */
    
    DefaultPageResponse<CbbTerminalDetectDTO> listDetect(CbbTerminalDetectPageRequest request) throws BusinessException;

    /**
     * 获取终端最后检测记录
     *
     * @param terminalId 请求参数
     * @return 检测记录信息
     * @throws BusinessException 业务异常
     */

    CbbTerminalDetectDTO getRecentDetect(String terminalId) throws BusinessException;

    /**
     * 获取终端检测记录结果
     *
     * @param detectDate 检测结果请求参数
     * @return 终端检测结果
     */
    
    CbbDetectResultResponse getDetectResult(CbbDetectDateEnums detectDate);
}
