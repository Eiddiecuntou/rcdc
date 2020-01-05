package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.componentupgrade;

import org.springframework.util.Assert;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/2
 *
 * @author nt
 */
public abstract class AbstractTerminalComponentUpgradeHandler implements TerminalComponentUpgradeHandler {

    private static final String VERSION_SPLIT = "\\.";

    /**
     * 比较是否第一个版本号大于第二个版本号
     *
     * @param firstVersion 版本1
     * @param secondVersion 版本2
     * @return 比较结果
     */
    public boolean isVersionNotLess(String firstVersion, String secondVersion) {
        Assert.hasText(firstVersion, "firstVersion can not be blank");
        Assert.hasText(secondVersion, "secondVersion can not be blank");

        String[] firstVerArr = firstVersion.split(VERSION_SPLIT);
        String[] secondVerArr = secondVersion.split(VERSION_SPLIT);

        Assert.isTrue(firstVerArr.length == secondVerArr.length, "版本格式不一致，无法进行比较");

        for (int i = 0; i < firstVerArr.length; i++) {
            int intVer1 = Integer.parseInt(firstVerArr[i]);
            int intVer2 = Integer.parseInt(secondVerArr[i]);
            if (intVer1 == intVer2) {
                continue;
            }

            if (intVer1 > intVer2) {
                return true;
            }

            if (intVer1 < intVer2) {
                return false;
            }
        }

        return true;
    }

}
