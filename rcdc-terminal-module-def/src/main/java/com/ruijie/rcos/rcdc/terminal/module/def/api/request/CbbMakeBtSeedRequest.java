package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.sk.modulekit.api.comm.Request;
import org.springframework.util.Assert;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/11
 *
 * @author hs
 */
public class CbbMakeBtSeedRequest implements Request {

    private String ip;

    private String filePath;

    private String seedSavePath;

    public CbbMakeBtSeedRequest(String ip, String filePath, String seedSavePath) {
        Assert.hasText(ip, "ip can not be blank");
        Assert.hasText(filePath, "filePath can not be blank");
        Assert.hasText(seedSavePath, "seedSavePath can not be blank");
        this.ip = ip;
        this.filePath = filePath;
        this.seedSavePath = seedSavePath;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getSeedSavePath() {
        return seedSavePath;
    }

    public void setSeedSavePath(String seedSavePath) {
        this.seedSavePath = seedSavePath;
    }
}
