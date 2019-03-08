package com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask;

import static org.junit.Assert.assertEquals;
import java.util.UUID;
import org.junit.Test;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月7日
 * 
 * @author ls
 */
public class TerminalUpgradeBatchTaskItemTest {

    /**
     * 测试构造器，getter
     */
    @Test
    public void testConstructor() {
        UUID itemId = UUID.randomUUID();
        UUID upgradeTaskId = UUID.randomUUID();
        TerminalUpgradeBatchTaskItem taskItem = new TerminalUpgradeBatchTaskItem(itemId, "item", upgradeTaskId);
        assertEquals(itemId, taskItem.getItemID());
        assertEquals("item", taskItem.getItemName());
        assertEquals(upgradeTaskId, taskItem.getUpgradeTaskId());
    }

}
