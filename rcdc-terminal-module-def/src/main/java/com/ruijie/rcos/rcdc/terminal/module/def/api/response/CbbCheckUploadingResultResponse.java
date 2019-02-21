package com.ruijie.rcos.rcdc.terminal.module.def.api.response;

import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * 
 * Description: 检查刷机包是否正在上传中结果响应
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月21日
 * 
 * @author nt
 */
public class CbbCheckUploadingResultResponse extends DefaultResponse {

    private boolean hasLoading;

    public boolean isHasLoading() {
        return hasLoading;
    }

    public void setHasLoading(boolean hasLoading) {
        this.hasLoading = hasLoading;
    }

}
