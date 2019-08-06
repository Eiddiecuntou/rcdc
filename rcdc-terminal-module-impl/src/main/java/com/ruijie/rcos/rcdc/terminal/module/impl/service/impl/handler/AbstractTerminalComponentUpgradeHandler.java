package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/2
 *
 * @author nt
 */
public abstract class AbstractTerminalComponentUpgradeHandler implements TerminalComponentUpgradeHandler {

    /**
     * 转换版本号为数字
     *
     * @param version 版本信息
     * @return 数字版本号
     */
    public Integer getVersionFromVerStr(String version) {
        /*
         * 版本号格式： 1.0.0.1
         * 版本号约定：4位数，是否升级判断用前3位即可
         * 第4位用于场景标记（1-云办公，2-云课堂）
         */
        int lastIndexOf = version.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return 0;
        }
        return Integer.valueOf(version.substring(0, lastIndexOf).replace(".", ""));
    }

    /**
     * 比较版本
     *
     * @param firstVersion 版本1
     * @param secondVersion 版本2
     * @return 比较结果
     */
    public boolean compareVersion(String firstVersion, String secondVersion) {
        int v1 = getVersionFromVerStr(firstVersion);
        int v2 = getVersionFromVerStr(secondVersion);
        return v1 > v2;
    }

}
