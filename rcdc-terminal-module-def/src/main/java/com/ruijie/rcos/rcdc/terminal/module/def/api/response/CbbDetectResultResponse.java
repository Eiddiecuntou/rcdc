package com.ruijie.rcos.rcdc.terminal.module.def.api.response;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectStatisticsDTO;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * 
 * Description: 终端检测结果响应
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月28日
 * 
 * @author nt
 */
public class CbbDetectResultResponse extends DefaultResponse {

    private CbbTerminalDetectStatisticsDTO result;



    public CbbDetectResultResponse() {
        
    }

    public CbbDetectResultResponse(CbbTerminalDetectStatisticsDTO result) {
        this.result = result;
    }

    public CbbTerminalDetectStatisticsDTO getResult() {
        return result;
    }

    public void setResult(CbbTerminalDetectStatisticsDTO result) {
        this.result = result;
    }

}
