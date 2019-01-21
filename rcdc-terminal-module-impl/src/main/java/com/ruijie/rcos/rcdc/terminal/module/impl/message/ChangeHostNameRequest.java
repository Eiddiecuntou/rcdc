package com.ruijie.rcos.rcdc.terminal.module.impl.message;

/**
 * 
 * Description: 修改终端名称
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月17日
 * 
 * @author nt
 */
public class ChangeHostNameRequest {

    private String hostName;
    
    public ChangeHostNameRequest(String hostName) {
        this.hostName = hostName;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
    
}
