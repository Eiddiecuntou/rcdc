package com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal;

import java.util.UUID;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * 
 * Description: 终端分组树节点
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月7日
 * 
 * @author nt
 */
public class TerminalGroupTreeNodeDTO {
    
    private UUID id;
    
    private String label;
    
    /**
     * 父级节点id，用于组装树形结构，序列化时忽略该属性
     */
    @JSONField(serialize = false)
    private UUID parentId;
    
    @SuppressWarnings("PMD.ArrayOrListPropertyNamingRule")
    private TerminalGroupTreeNodeDTO[] children;
    
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public TerminalGroupTreeNodeDTO[] getChildren() {
        return children;
    }

    public void setChildren(TerminalGroupTreeNodeDTO[] children) {
        this.children = children;
    }
    
}
