package com.ruijie.rcos.rcdc.terminal.module.def.api.response;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectResultDTO;
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

    private CbbTerminalDetectResultDTO result;



    public CbbDetectResultResponse() {
        
    }

    public CbbDetectResultResponse(CbbTerminalDetectResultDTO result) {
        this.result = result;
    }

    public CbbTerminalDetectResultDTO getResult() {
        return result;
    }

    public void setResult(CbbTerminalDetectResultDTO result) {
        this.result = result;
    }

}
