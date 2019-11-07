package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbCommonComponentVersionInfoDTO;
import org.springframework.util.Assert;

import java.util.List;

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
    public boolean isVersionBigger(String firstVersion, String secondVersion) {
        Assert.hasText(firstVersion, "firstVersion can not be blank");
        Assert.hasText(secondVersion, "secondVersion can not be blank");

        String[] v1 = firstVersion.split(VERSION_SPLIT);
        String[] v2 = secondVersion.split(VERSION_SPLIT);

        if (v1.length > v2.length) {
            return true;
        }

        for (int i = 0; i< v1.length; i ++) {
            if (Integer.parseInt(v1[i]) > Integer.parseInt(v2[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * VDI终端：清除差异升级信息
     *
     * @param componentList 组件升级信息
     */
    protected void clearDifferenceUpgradeInfo(List<CbbCommonComponentVersionInfoDTO> componentList) {
        Assert.notNull(componentList, "componentList cannot be null");
        for (CbbCommonComponentVersionInfoDTO componentInfo : componentList) {
            componentInfo.setIncrementalPackageMd5(null);
            componentInfo.setIncrementalPackageName(null);
            componentInfo.setIncrementalTorrentMd5(null);
            componentInfo.setIncrementalTorrentUrl(null);
            componentInfo.setBasePackageName(null);
            componentInfo.setBasePackageMd5(null);
        }
    }
}
