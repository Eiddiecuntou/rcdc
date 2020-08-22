package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectStatisticsDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectThresholdDTO;

/**
 * 
 * Description: 终端检测结果响应
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月28日
 * 
 * @author nt
 */
public class CbbDetectResultDTO {

    private CbbTerminalDetectStatisticsDTO result;

    private CbbTerminalDetectThresholdDTO threshold;


    public CbbDetectResultDTO() {
        
    }

    public CbbDetectResultDTO(CbbTerminalDetectStatisticsDTO result, CbbTerminalDetectThresholdDTO threshold) {
        this.result = result;
        this.threshold = threshold;
    }

    public CbbTerminalDetectStatisticsDTO getResult() {
        return result;
    }

    public void setResult(CbbTerminalDetectStatisticsDTO result) {
        this.result = result;
    }

    public CbbTerminalDetectThresholdDTO getThreshold() {
        return threshold;
    }

    public void setThreshold(CbbTerminalDetectThresholdDTO threshold) {
        this.threshold = threshold;
    }
}
