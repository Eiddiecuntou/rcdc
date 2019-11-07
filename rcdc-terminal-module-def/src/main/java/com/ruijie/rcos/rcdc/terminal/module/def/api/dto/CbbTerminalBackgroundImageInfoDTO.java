package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time:  2019/11/6
 *
 * @author songxiang
 */
public class CbbTerminalBackgroundImageInfoDTO {

    private String imagePath;

    private String imageName;

    private String suffix;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
