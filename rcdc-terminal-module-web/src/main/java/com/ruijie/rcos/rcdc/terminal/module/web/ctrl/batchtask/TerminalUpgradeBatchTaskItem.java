package com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask;

import java.util.UUID;
import org.springframework.util.Assert;
import com.ruijie.rcos.sk.base.batch.BatchTaskItem;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月28日
 * 
 * @author nt
 */
public class TerminalUpgradeBatchTaskItem implements BatchTaskItem {

    private UUID itemId;

    private String itemName;

    private UUID upgradeTaskId;

    public TerminalUpgradeBatchTaskItem(UUID itemId, String itemName, UUID upgradeTaskId) {
        Assert.notNull(itemId, "itemId can not be null");
        Assert.hasText(itemName, "itemName can not be null");
        Assert.notNull(upgradeTaskId, "upgradeTaskId can not be null");

        this.itemId = itemId;
        this.itemName = itemName;
        this.upgradeTaskId = upgradeTaskId;
    }

    @Override
    public UUID getItemID() {
        return itemId;
    }

    @Override
    public String getItemName() {
        return itemName;
    }

    public UUID getUpgradeTaskId() {
        return upgradeTaskId;
    }

}
