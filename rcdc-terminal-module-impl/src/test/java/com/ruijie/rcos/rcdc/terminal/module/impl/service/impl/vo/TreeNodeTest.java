package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.vo;

import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/6/8
 *
 * @author XiaoJiaXin
 */
@RunWith(SkyEngineRunner.class)
public class TreeNodeTest {

    /**
     * test
     */
    @Test
    public void testMaxDepthRootNull() {
        TreeNode node = new TreeNode(UUID.randomUUID(), new ArrayList<>());
        int depth = node.maxDepth(null);
        Assert.assertTrue(depth == 0);
    }

    /**
     * test
     */
    @Test
    public void testGetAndSet() {
        UUID id = UUID.randomUUID();
        TreeNode node = new TreeNode(id);
        node.setId(id);
        Assert.assertEquals(id, node.getId());
        ArrayList<TreeNode> childList = new ArrayList<>();
        node.setChildList(childList);
        Assert.assertEquals(childList, node.getChildList());

    }
}