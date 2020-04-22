package com.ruijie.rcos.rcdc.terminal.module.impl.spi.response;

import com.ruijie.rcos.sk.base.annotation.NotNull;

/**
 * Description: ftp配置信息
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/4/7 10:21 下午
 *
 * @author zhouhuan
 */
public class FtpConfigInfo {

    @NotNull
    private Integer ftpPort;

    @NotNull
    private String ftpUserName;

    @NotNull
    private String ftpUserPassword;

    @NotNull
    private String ftpPath;

    @NotNull
    private String fileDir;

    public Integer getFtpPort() {
        return ftpPort;
    }

    public void setFtpPort(Integer ftpPort) {
        this.ftpPort = ftpPort;
    }

    public String getFtpUserName() {
        return ftpUserName;
    }

    public void setFtpUserName(String ftpUserName) {
        this.ftpUserName = ftpUserName;
    }

    public String getFtpUserPassword() {
        return ftpUserPassword;
    }

    public void setFtpUserPassword(String ftpUserPassword) {
        this.ftpUserPassword = ftpUserPassword;
    }

    public String getFtpPath() {
        return ftpPath;
    }

    public void setFtpPath(String ftpPath) {
        this.ftpPath = ftpPath;
    }

    public String getFileDir() {
        return fileDir;
    }

    public void setFileDir(String fileDir) {
        this.fileDir = fileDir;
    }
}
