package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.vo;

import java.util.List;
import java.util.UUID;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/3/24
 *
 * @author Jarman
 */
public class TreeNode {

    private UUID id;

    private List<TreeNode> childList;

    public TreeNode(UUID id) {
        this.id = id;
    }

    public TreeNode(UUID id, List<TreeNode> childList) {
        this.id = id;
        this.childList = childList;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<TreeNode> getChildList() {
        return childList;
    }

    public void setChildList(List<TreeNode> childList) {
        this.childList = childList;
    }

    /**
     * 获取最大层级数
     * @param root 根节点
     * @return 最大层级数
     */
    public int maxDepth(TreeNode root) {
        int max = 0;
        if (root == null) {
            return 0;
        }
        for (TreeNode node : root.getChildList()) {
            max = Math.max(max, maxDepth(node));
        }
        return max + 1;
    }
}
