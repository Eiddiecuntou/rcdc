package com.ruijie.rcos.rcdc.terminal.module.def.api.response.group;

import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * Description: 获取终端分组路径响应
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/7/15
 *
 * @author nt
 */
public class CbbObtainGroupNamePathResponse extends DefaultResponse {

    private String[] groupNameArr;

    public String[] getGroupNameArr() {
        return groupNameArr;
    }

    public void setGroupNameArr(String[] groupNameArr) {
        this.groupNameArr = groupNameArr;
    }
}
