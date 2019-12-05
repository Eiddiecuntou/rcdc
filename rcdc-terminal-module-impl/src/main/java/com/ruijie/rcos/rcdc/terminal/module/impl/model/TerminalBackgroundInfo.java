package com.ruijie.rcos.rcdc.terminal.module.impl.model;

/**
 * Description: 请求背景图片的回应
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/11/6
 *
 * @author songxiang
 */
public class TerminalBackgroundInfo {

    private TerminalBackgroundDetailInfo detailInfo;

    private Boolean isDefaultImage;

    public TerminalBackgroundDetailInfo getDetailInfo() {
        return detailInfo;
    }

    public void setDetailInfo(TerminalBackgroundDetailInfo detailInfo) {
        this.detailInfo = detailInfo;
    }

    public Boolean getIsDefaultImage() {
        return isDefaultImage;
    }

    public void setIsDefaultImage(Boolean isDefaultImage) {
        this.isDefaultImage = isDefaultImage;
    }

    public class TerminalBackgroundDetailInfo {

        private String md5;

        private String ftpPath;

        private String imageName;

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public String getFtpPath() {
            return ftpPath;
        }

        public void setFtpPath(String ftpPath) {
            this.ftpPath = ftpPath;
        }

        public String getImageName() {
            return imageName;
        }

        public void setImageName(String imageName) {
            this.imageName = imageName;
        }
    }
}
