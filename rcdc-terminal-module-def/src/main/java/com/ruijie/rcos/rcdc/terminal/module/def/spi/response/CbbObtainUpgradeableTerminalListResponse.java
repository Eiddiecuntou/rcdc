package com.ruijie.rcos.rcdc.terminal.module.def.spi.response;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.UpgradeableTerminalListDTO;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * Description: 可刷机终端列表响应
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/6/17
 *
 * @author nt
 */
public class CbbObtainUpgradeableTerminalListResponse extends DefaultResponse {

    private UpgradeableTerminalListDTO[] terminalArr;

    private int pageSize;

    private int totalCount;

    public UpgradeableTerminalListDTO[] getTerminalArr() {
        return terminalArr;
    }

    public void setTerminalArr(UpgradeableTerminalListDTO[] terminalArr) {
        this.terminalArr = terminalArr;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
