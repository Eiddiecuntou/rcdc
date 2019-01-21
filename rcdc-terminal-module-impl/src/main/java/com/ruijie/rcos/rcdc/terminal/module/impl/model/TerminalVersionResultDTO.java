package com.ruijie.rcos.rcdc.terminal.module.impl.model;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalComponentUpdateListDTO;

/**
 * 
 * Description: 终端组件升级请求结果信息
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月3日
 * 
 * @author nt
 */
public class TerminalVersionResultDTO {

    private Integer result;

    private CbbTerminalComponentUpdateListDTO updatelist;
    
    public TerminalVersionResultDTO() {
    }

    public TerminalVersionResultDTO(Integer result, CbbTerminalComponentUpdateListDTO updatelist) {
        this.result = result;
        this.updatelist = updatelist;
    }
    
    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public CbbTerminalComponentUpdateListDTO getUpdatelist() {
        return updatelist;
    }

    public void setUpdatelist(CbbTerminalComponentUpdateListDTO updatelist) {
        this.updatelist = updatelist;
    }
}
