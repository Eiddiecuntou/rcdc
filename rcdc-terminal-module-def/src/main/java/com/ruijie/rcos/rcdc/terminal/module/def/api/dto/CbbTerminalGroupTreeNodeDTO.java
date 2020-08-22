package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

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
public class CbbTerminalGroupTreeNodeDTO {
    
    private UUID id;
    
    private String label;

    private Boolean enableDefault;
    
    /**
     * 父级节点id，用于组装树形结构，序列化时忽略该属性
     */
    @JSONField(serialize = false)
    private UUID parentId;
    
    @SuppressWarnings("PMD.ArrayOrListPropertyNamingRule")
    private CbbTerminalGroupTreeNodeDTO[] children;
    
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

    public CbbTerminalGroupTreeNodeDTO[] getChildren() {
        return children;
    }

    public void setChildren(CbbTerminalGroupTreeNodeDTO[] children) {
        this.children = children;
    }

    public Boolean getEnableDefault() {
        return enableDefault;
    }

    public void setEnableDefault(Boolean enableDefault) {
        this.enableDefault = enableDefault;
    }
}
