package com.ruijie.rcos.rcdc.terminal.module.def.api.enums;

/**
 * Description:
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/7/21 16:45
 *
 * @author conghaifeng
 */
public enum AppTerminalOsTypeEnums {

    WINDOWS("/opt/ftp/terminal/terminal_component/windows/component/",
            "/opt/ftp/terminal/terminal_component/windows/update.list",
            "/terminal_component/windows/component/"),

    NEOKYLIN("/opt/ftp/terminal/terminal_component/neokylin/component/",
            "/opt/ftp/terminal/terminal_component/neokylin/update.list",
            "/terminal_component/neokylin/component/"),

    UOS("/opt/ftp/terminal/terminal_component/uos/component/",
            "/opt/ftp/terminal/terminal_component/uos/update.list",
            "/terminal_component/uos/component/");

    /**
     * 软终端下载路径
     */
    private String componentDir;

    /**
     * 软终端升级list位置
     */
    private String updateListPath;

    /**
     * 软终端升级组件
     */
    private String componentPackageDownloadUrlPre;

    AppTerminalOsTypeEnums(String componentDir, String updateListPath, String componentPackageDownloadUrlPre) {
        this.componentDir = componentDir;
        this.updateListPath = updateListPath;
        this.componentPackageDownloadUrlPre = componentPackageDownloadUrlPre;
    }

    public String getComponentDir() {
        return componentDir;
    }

    public String getUpdateListPath() {
        return updateListPath;
    }

    public String getComponentPackageDownloadUrlPre() {
        return componentPackageDownloadUrlPre;
    }

}
