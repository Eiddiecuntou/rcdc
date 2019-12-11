package com.ruijie.rcos.rcdc.terminal.module.def.api.response;

import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import org.springframework.util.Assert;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/12/10
 *
 * @author nt
 */
public class CbbTerminalCpuTypeResponse extends DefaultResponse {

    private String cpuType;

    public CbbTerminalCpuTypeResponse() {
    }

    public CbbTerminalCpuTypeResponse(String cpuType) {
        Assert.hasText(cpuType, "cpuType can not be blank");
        this.cpuType = cpuType;
    }

    public String getCpuType() {
        return cpuType;
    }

    public void setCpuType(String cpuType) {
        this.cpuType = cpuType;
    }
}
