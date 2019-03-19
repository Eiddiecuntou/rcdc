package com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request;

import java.util.UUID;
import org.springframework.lang.Nullable;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.webmvc.api.request.WebRequest;

/**
 * 
 * Description: 取消等待中的刷机终端
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月18日
 * 
 * @author nt
 */
public class CancelTerminalSystemUpgradeWebRequest implements WebRequest {

    /**
     * 终端id数组
     */
    @Nullable
    private String[] terminalIdArr;

    /**
     * 终端刷机任务id
     */
    @NotNull
    private UUID upgradeTaskId;

    @NotNull
    private Boolean isCancelAll;

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

    public Boolean getIsCancelAll() {
        return isCancelAll;
    }

    public void setIsCancelAll(Boolean isCancelAll) {
        this.isCancelAll = isCancelAll;
    }

}
