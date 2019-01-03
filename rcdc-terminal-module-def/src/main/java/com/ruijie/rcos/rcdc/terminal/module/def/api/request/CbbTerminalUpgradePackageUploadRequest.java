package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;
import com.ruijie.rcos.sk.webmvc.api.request.ChunkUploadFile;

/**
 * 
 * Description: 终端系统升级包上传请求
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月4日
 * 
 * @author nt
 */
public class CbbTerminalUpgradePackageUploadRequest implements Request {

    /**
     * 升级包上传信息
     */
    @NotNull
    private ChunkUploadFile file;

    public ChunkUploadFile getFile() {
        return file;
    }

    public void setFile(ChunkUploadFile file) {
        this.file = file;
    }
    
}
