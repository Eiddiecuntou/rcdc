package com.ruijie.rcos.rcdc.terminal.module.def.api.response;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectDTO;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * 
 * Description: 终端检测信息响应
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月19日
 * 
 * @author nt
 */
public class CbbDetectInfoResponse extends DefaultResponse {

    private CbbTerminalDetectDTO detectInfo;

    public CbbTerminalDetectDTO getDetectInfo() {
        return detectInfo;
    }

    public void setDetectInfo(CbbTerminalDetectDTO detectInfo) {
        this.detectInfo = detectInfo;
    }

}
