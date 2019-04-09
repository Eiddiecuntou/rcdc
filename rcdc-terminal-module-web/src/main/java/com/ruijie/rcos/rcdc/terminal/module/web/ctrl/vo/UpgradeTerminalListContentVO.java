package com.ruijie.rcos.rcdc.terminal.module.web.ctrl.vo;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbSystemUpgradeTaskDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbSystemUpgradeTaskTerminalDTO;

/**
 * 
 * Description: 升级任务终端列表VO
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月19日
 * 
 * @author nt
 */
public class UpgradeTerminalListContentVO {

    private CbbSystemUpgradeTaskTerminalDTO[] itemArr;

    private long total;

    private int waitNum;

    private int upgradingNum;

    private int successNum;

    private int failNum;

    private int unsupportNum;

    private int undoNum;

    private CbbSystemUpgradeTaskDTO upgradeTask;

    public CbbSystemUpgradeTaskTerminalDTO[] getItemArr() {
        return itemArr;
    }

    public void setItemArr(CbbSystemUpgradeTaskTerminalDTO[] itemArr) {
        this.itemArr = itemArr;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public CbbSystemUpgradeTaskDTO getUpgradeTask() {
        return upgradeTask;
    }

    public void setUpgradeTask(CbbSystemUpgradeTaskDTO upgradeTask) {
        this.upgradeTask = upgradeTask;
    }

    public int getWaitNum() {
        return waitNum;
    }

    public void setWaitNum(int waitNum) {
        this.waitNum = waitNum;
    }

    public int getUpgradingNum() {
        return upgradingNum;
    }

    public void setUpgradingNum(int upgradingNum) {
        this.upgradingNum = upgradingNum;
    }

    public int getSuccessNum() {
        return successNum;
    }

    public void setSuccessNum(int successNum) {
        this.successNum = successNum;
    }

    public int getFailNum() {
        return failNum;
    }

    public void setFailNum(int failNum) {
        this.failNum = failNum;
    }

    public int getUnsupportNum() {
        return unsupportNum;
    }

    public void setUnsupportNum(int unsupportNum) {
        this.unsupportNum = unsupportNum;
    }

    public int getUndoNum() {
        return undoNum;
    }

    public void setUndoNum(int undoNum) {
        this.undoNum = undoNum;
    }

}
