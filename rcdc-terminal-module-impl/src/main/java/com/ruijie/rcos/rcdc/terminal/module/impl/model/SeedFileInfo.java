package com.ruijie.rcos.rcdc.terminal.module.impl.model;

import org.springframework.util.Assert;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/14
 *
 * @author hs
 */
public class SeedFileInfo {

    private String seedFilePath;

    private String seedFileMD5;

    public SeedFileInfo(String seedFilePath, String seedFileMD5) {
        Assert.notNull(seedFileMD5, "seedFileMD5 can not be null");
        Assert.notNull(seedFileMD5, "seedFileMD5 can not be null");
        this.seedFilePath = seedFilePath;
        this.seedFileMD5 = seedFileMD5;
    }

    public String getSeedFilePath() {
        return seedFilePath;
    }

    public void setSeedFilePath(String seedFilePath) {
        this.seedFilePath = seedFilePath;
    }

    public String getSeedFileMD5() {
        return seedFileMD5;
    }

    public void setSeedFileMD5(String seedFileMD5) {
        this.seedFileMD5 = seedFileMD5;
    }
}
