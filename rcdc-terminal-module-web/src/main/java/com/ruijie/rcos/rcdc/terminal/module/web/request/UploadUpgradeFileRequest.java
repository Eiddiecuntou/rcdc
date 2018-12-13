package com.ruijie.rcos.rcdc.terminal.module.web.request;

import javax.validation.constraints.NotNull;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;
import com.ruijie.rcos.sk.webmvc.api.request.ChunkUploadFile;

/**
 *
 * Description: 升级文件上传请求
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月20日
 * 
 * @author nt
 */
public class UploadUpgradeFileRequest implements Request {
    
    /**
     * 上传的文件信息
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
