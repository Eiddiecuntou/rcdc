package com.ruijie.rcos.rcdc.terminal.module.impl.cache;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCollectLogStateEnums;

/**
 * Description: 收集日志缓存对象
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/6
 *
 * @author Jarman
 */
public class CollectLogCache {

    public CollectLogCache() {
        
    }

    public CollectLogCache(CbbCollectLogStateEnums state) {
        this.state = state;
    }

    /**
     * 状态
     */
    private CbbCollectLogStateEnums state;

    /**
     * 只有状态为已完成时才有值
     */
    private String logFileName;


    public CbbCollectLogStateEnums getState() {
        return state;
    }

    public void setState(CbbCollectLogStateEnums state) {
        this.state = state;
    }

    public String getLogFileName() {
        return logFileName;
    }

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

}
