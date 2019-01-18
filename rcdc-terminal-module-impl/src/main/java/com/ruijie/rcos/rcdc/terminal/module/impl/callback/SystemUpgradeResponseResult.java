package com.ruijie.rcos.rcdc.terminal.module.impl.callback;

/**
 * 
 * Description: 系统升级响应结果
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月18日
 * 
 * @author nt
 */
public class SystemUpgradeResponseResult {
    
    public static final int SUCCESS = 0;
    
    public static final int UNSUPPORTED = -1;
    
    public static final int FAILURE = -99;

    /**
     * 响应结果
     */
    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
    
}
