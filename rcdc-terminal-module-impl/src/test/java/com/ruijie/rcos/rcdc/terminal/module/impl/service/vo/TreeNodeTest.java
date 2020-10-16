package com.ruijie.rcos.rcdc.terminal.module.impl.service.vo;

import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.vo.TreeNode;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/9/28
 *
 * @author hs
 */
@RunWith(SkyEngineRunner.class)
public class TreeNodeTest {

    /**
     * 测试GetterAndSetter
     */
    @Test
    public void testGetterAndSetter() {
        UUID id = UUID.randomUUID();
        TreeNode treeNode = new TreeNode(id);
        Assert.assertEquals(id, treeNode.getId());
    }
}
