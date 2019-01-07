package com.ruijie.rcos.rcdc.terminal.module.web.ctrl.vo;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectResultDTO;

/**
 * 
 * Description: 终端检测列表响应内容
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月28日
 * 
 * @author nt
 */
public class TerminalDetectListContentVO {
    
    private CbbTerminalDetectDTO[] itemArr;
    
    private long total;
    
    private CbbTerminalDetectResultDTO result;

    public CbbTerminalDetectDTO[] getItemArr() {
        return itemArr;
    }

    public void setItemArr(CbbTerminalDetectDTO[] itemArr) {
        this.itemArr = itemArr;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public CbbTerminalDetectResultDTO getResult() {
        return result;
    }

    public void setResult(CbbTerminalDetectResultDTO result) {
        this.result = result;
    }
    
}
