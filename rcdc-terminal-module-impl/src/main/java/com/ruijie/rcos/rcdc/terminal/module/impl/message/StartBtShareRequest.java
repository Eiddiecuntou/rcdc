package com.ruijie.rcos.rcdc.terminal.module.impl.message;

import org.springframework.util.Assert;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/18
 *
 * @author hs
 */
public class StartBtShareRequest {

    private String seedPath;
    
    private String filePath;

    public StartBtShareRequest(String seedPath, String filePath) {
        Assert.notNull(seedPath, "seedPath can not be null");
        Assert.notNull(filePath, "filePath can not be null");
        this.seedPath = seedPath;
        this.filePath = filePath;
    }

    public String getSeedPath() {
        return seedPath;
    }

    public void setSeedPath(String seedPath) {
        this.seedPath = seedPath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
