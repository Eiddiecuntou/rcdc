package com.ruijie.rcos.rcdc.terminal.module.web.request;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.sk.webmvc.api.request.WebRequest;

/**
 * 
 * Description: 终端分页列表请求
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月1日
 * 
 * @author nt
 */
public class ListTerminalRequest implements WebRequest {
    
    /**
     * 分页行数
     */
    private int pageSize;
    
    /**
     * 分页页数
     */
    private int currentPage;
    
    /**
     * 终端类型
     */
    private CbbTerminalTypeEnums terminalType;
    
    /**
     * 终端系统版本
     */
    private String terminalSystemVersion;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public CbbTerminalTypeEnums getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(CbbTerminalTypeEnums terminalType) {
        this.terminalType = terminalType;
    }

    public String getTerminalSystemVersion() {
        return terminalSystemVersion;
    }

    public void setTerminalSystemVersion(String terminalSystemVersion) {
        this.terminalSystemVersion = terminalSystemVersion;
    }
    
}
