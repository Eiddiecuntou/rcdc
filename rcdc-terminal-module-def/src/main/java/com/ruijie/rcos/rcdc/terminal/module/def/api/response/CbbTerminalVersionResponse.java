package com.ruijie.rcos.rcdc.terminal.module.def.api.response;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalComponentUpdateListDTO;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * 
 * Description: 请求终端版本响应
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月14日
 * 
 * @author nt
 */
public class CbbTerminalVersionResponse extends DefaultResponse {
    
    private Integer result;

    private CbbTerminalComponentUpdateListDTO updatelist;
    
    public CbbTerminalVersionResponse() {
        super();
    }

    public CbbTerminalVersionResponse(Integer result, CbbTerminalComponentUpdateListDTO updatelist) {
        super();
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
