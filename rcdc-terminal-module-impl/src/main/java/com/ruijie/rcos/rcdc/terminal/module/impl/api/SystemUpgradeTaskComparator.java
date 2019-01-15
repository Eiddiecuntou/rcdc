package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import java.util.Comparator;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.SystemUpgradeTask;

/**
 * 
 * Description: 终端系统升级对象比较器
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月3日
 * 
 * @author nt
 */
public class SystemUpgradeTaskComparator implements Comparator<SystemUpgradeTask> {

    @Override
    public int compare(SystemUpgradeTask o1, SystemUpgradeTask o2) {
        if (o1.getState() == o2.getState()) {
            if (o1.getStartTime() < o2.getStartTime()) {
                return 1;
            }
        } else {
            if (o1.getState() == CbbSystemUpgradeStateEnums.UPGRADING) {
                return 1;
            }
        }
        return -1;
    }

}
