package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCollectLogStateEnums;

/**
 * 
 * Description: 终端收集日志状态响应
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月18日
 * 
 * @author nt
 */
public class CbbTerminalCollectLogStatusDTO {


    private CbbCollectLogStateEnums state;

    private String logName;

    public CbbCollectLogStateEnums getState() {
        return state;
    }

    public void setState(CbbCollectLogStateEnums state) {
        this.state = state;
    }

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }

    public static void main(String[] args) {
        String str = "20210512160816_172.20.94.231_300D9E37E46C_shine.zip";
        str = str.substring(0,str.lastIndexOf("."));
        System.out.println(str);
    }
}
