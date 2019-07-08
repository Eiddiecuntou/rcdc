package com.ruijie.rcos.rcdc.terminal.module.def.api.response.terminal;

import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * 
 * Description: 检验分组名称是否同级唯一响应
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月19日
 * 
 * @author nt
 */
public class CheckGroupNameDuplicationResponse extends DefaultResponse {

    /**
     * 是否唯一 true:唯一;  false:不唯一
     */
    private Boolean hasDuplication;
    
    public CheckGroupNameDuplicationResponse() {
    }

    public CheckGroupNameDuplicationResponse(Boolean hasDuplication) {
        this.setStatus(Status.SUCCESS);
        this.hasDuplication = hasDuplication;
    }

    public Boolean getHasDuplication() {
        return hasDuplication;
    }

    public void setHasDuplication(Boolean hasDuplication) {
        this.hasDuplication = hasDuplication;
    }

}
