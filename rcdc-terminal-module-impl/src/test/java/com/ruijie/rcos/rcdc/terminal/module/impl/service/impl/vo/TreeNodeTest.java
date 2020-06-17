package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.vo;

import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
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

    @Test
    public void testMaxDepthRootNull() {
        TreeNode node = new TreeNode(UUID.randomUUID(), new ArrayList<>());
        int depth = node.maxDepth(null);
        Assert.assertTrue(depth == 0);
    }
}