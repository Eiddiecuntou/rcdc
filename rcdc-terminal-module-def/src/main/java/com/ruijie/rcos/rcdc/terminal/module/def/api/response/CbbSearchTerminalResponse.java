package com.ruijie.rcos.rcdc.terminal.module.def.api.response;

import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/12/4
 *
 * @author Jarman
 */
public class CbbSearchTerminalResponse extends DefaultResponse {

    private Long hitCount;

    private String[] searchResults;

    public CbbSearchTerminalResponse(Long hitCount, String[] searchResults) {
        this.hitCount = hitCount;
        this.searchResults = searchResults;
    }

    public Long getHitCount() {
        return hitCount;
    }

    public void setHitCount(Long hitCount) {
        this.hitCount = hitCount;
    }

    public String[] getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(String[] searchResults) {
        this.searchResults = searchResults;
    }
}
