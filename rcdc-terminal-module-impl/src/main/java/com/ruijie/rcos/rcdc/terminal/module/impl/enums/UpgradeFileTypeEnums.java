package com.ruijie.rcos.rcdc.terminal.module.impl.enums;

import org.springframework.util.Assert;

/**
 * 
 * Description: 终端系统升级上传文件类型
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月19日
 * 
 * @author nt
 */
public enum UpgradeFileTypeEnums {

    /**
     * 文件类型iso
     */
    ISO("iso"),

    /**
     * 文件类型zip
     */
    ZIP("zip");


    private String fileType;


    UpgradeFileTypeEnums(String fileType) {
        this.fileType = fileType;
    }

    public String getFileType() {
        return fileType;
    }

    /**
     * 
     * 判断传入字符串是否为枚举成员
     * 
     * @param fileType 文件类型字符串
     * @return 校验结果
     */
    public static boolean contains(final String fileType) {
        Assert.hasText(fileType, "fileType can not be empty");
        for (UpgradeFileTypeEnums typeEnum : UpgradeFileTypeEnums.values()) {
            if (typeEnum.getFileType().equals(fileType)) {
                return true;
            }
        }
        return false;
    }

}
