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

    WINDOWS("/opt/ftp/terminal/terminal_component/windows_app/component/",
            "/opt/ftp/terminal/terminal_component/windows_app/update.list",
            "/terminal_component/windows_app/component/"),

    KYLIN("/opt/ftp/terminal/terminal_component/linux_kylin_app/component/",
        "/opt/ftp/terminal/terminal_component/linux_kylin_app/update.list",
        "/terminal_component/linux_kylin_app/component/"),

    NEOKYLIN("/opt/ftp/terminal/terminal_component/linux_neokylin_app/component/",
            "/opt/ftp/terminal/terminal_component/linux_neokylin_app/update.list",
            "/terminal_component/linux_neokylin_app/component/"),

    UOS("/opt/ftp/terminal/terminal_component/linux_uos_app/component/",
            "/opt/ftp/terminal/terminal_component/linux_uos_app/update.list",
            "/terminal_component/linux_uos_app/component/");

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
