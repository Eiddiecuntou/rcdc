package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import java.util.Comparator;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.SystemUpgradeTask;

public class SystemUpgradeTaskComparator implements Comparator<SystemUpgradeTask> {

    @Override
    public int compare(SystemUpgradeTask o1, SystemUpgradeTask o2) {
        if (o1.getState() == o2.getState()) {
            if (o1.getStartTime() < o2.getStartTime()) {
                return 1;
            }
        } else {
            if (o1.getState() == CbbSystemUpgradeStateEnums.DOING) {
                return 1;
            }
        }
        return -1;
    }

}
