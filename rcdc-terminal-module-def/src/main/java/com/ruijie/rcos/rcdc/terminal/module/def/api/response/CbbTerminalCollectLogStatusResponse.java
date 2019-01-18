package com.ruijie.rcos.rcdc.terminal.module.def.api.response;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CollectLogStateEnums;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * 
 * Description: 终端收集日志状态响应
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月18日
 * 
 * @author nt
 */
public class CbbTerminalCollectLogStatusResponse extends DefaultResponse{

    
    private CollectLogStateEnums state;
    
    private String logName;

    public CollectLogStateEnums getState() {
        return state;
    }

    public void setState(CollectLogStateEnums state) {
        this.state = state;
    }

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }
    
    
}
