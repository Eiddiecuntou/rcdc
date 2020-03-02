package com.ruijie.rcos.rcdc.terminal.module.def.api.response.group;

import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/1/9
 *
 * @author chen zj
 */
public class CbbCheckGroupNameDuplicationResponse extends DefaultResponse {

    private boolean hasDuplication;

    public CbbCheckGroupNameDuplicationResponse(boolean hasDuplication) {
        this.hasDuplication = hasDuplication;
    }

    public boolean isHasDuplication() {
        return hasDuplication;
    }

    public void setHasDuplication(boolean hasDuplication) {
        this.hasDuplication = hasDuplication;
    }
}




