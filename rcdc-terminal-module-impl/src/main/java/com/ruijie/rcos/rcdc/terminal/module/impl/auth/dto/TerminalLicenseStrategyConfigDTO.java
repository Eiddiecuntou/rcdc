package com.ruijie.rcos.rcdc.terminal.module.impl.auth.dto;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/7/16 16:20
 *
 * @author TING
 */
public class TerminalLicenseStrategyConfigDTO {

    @JSONField(name = "init")
    List<TerminalLicenseStrategyInitConfigDTO> initList;

    @JSONField(name = "update")
    List<TerminalLicenseStrategyUpdateConfigDTO> updateList;


    @JSONField(name = "allocate")
    List<TerminalLicenseStrategyAuthConfigDTO> allocateList;

    @JSONField(name = "recycle")
    List<TerminalLicenseStrategyAuthConfigDTO> recycleList;


    public List<TerminalLicenseStrategyInitConfigDTO> getInitList() {
        return initList;
    }

    public void setInitList(List<TerminalLicenseStrategyInitConfigDTO> initList) {
        this.initList = initList;
    }

    public List<TerminalLicenseStrategyUpdateConfigDTO> getUpdateList() {
        return updateList;
    }

    public void setUpdateList(List<TerminalLicenseStrategyUpdateConfigDTO> updateList) {
        this.updateList = updateList;
    }

    public List<TerminalLicenseStrategyAuthConfigDTO> getAllocateList() {
        return allocateList;
    }

    public void setAllocateList(List<TerminalLicenseStrategyAuthConfigDTO> allocateList) {
        this.allocateList = allocateList;
    }

    public List<TerminalLicenseStrategyAuthConfigDTO> getRecycleList() {
        return recycleList;
    }

    public void setRecycleList(List<TerminalLicenseStrategyAuthConfigDTO> recycleList) {
        this.recycleList = recycleList;
    }
}
