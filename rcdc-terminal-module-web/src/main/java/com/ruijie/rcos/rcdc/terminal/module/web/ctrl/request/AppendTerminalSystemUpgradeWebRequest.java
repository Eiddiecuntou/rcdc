package com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request;

import java.util.UUID;
import com.ruijie.rcos.sk.base.annotation.NotEmpty;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.webmvc.api.request.WebRequest;

/**
 * 
 * Description: 追加终端刷机请求
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月14日
 * 
 * @author nt
 */
public class AppendTerminalSystemUpgradeWebRequest implements WebRequest {

    /**
     * 终端id数组
     */
    @NotEmpty
    private String[] terminalIdArr;

    /**
     * 终端刷机任务id
     */
    @NotNull
    private UUID upgradeTaskId;

    public String[] getTerminalIdArr() {
        return terminalIdArr;
    }

    public void setTerminalIdArr(String[] terminalIdArr) {
        this.terminalIdArr = terminalIdArr;
    }

    public UUID getUpgradeTaskId() {
        return upgradeTaskId;
    }

    public void setUpgradeTaskId(UUID upgradeTaskId) {
        this.upgradeTaskId = upgradeTaskId;
    }

}
