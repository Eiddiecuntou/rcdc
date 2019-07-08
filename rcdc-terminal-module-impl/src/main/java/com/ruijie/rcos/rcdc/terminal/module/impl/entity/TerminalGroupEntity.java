package com.ruijie.rcos.rcdc.terminal.module.impl.entity;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.TerminalGroupDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.TerminalGroupTreeNodeDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.TerminalTypeEnums;

/**
 * 终端组持久化实体
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年10月30日
 * 
 * @author chenzj
 */
@Entity
@Table(name = "t_rco_terminal_group")
public class TerminalGroupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID parentId;

    private String name;

    private Date createTime;

    @Enumerated(EnumType.STRING)
    private TerminalTypeEnums terminalType;

    @Version
    private int version;

    /**
     * 实体对象转成DTO对象
     * 
     * @param terminalGroupDTO 页面呈现的VO对象
     */
    public void converToDTO(TerminalGroupDTO terminalGroupDTO) {
        Assert.notNull(terminalGroupDTO, "terminalGroupDTO can not be null");

        terminalGroupDTO.setId(id);
        terminalGroupDTO.setGroupName(name);
        terminalGroupDTO.setParentGroupId(parentId);
        terminalGroupDTO.setTerminalType(terminalType);
    }

    /**
     * 实体对象转成树形DTO对象
     * 
     * @param treeNodeDTO 页面呈现的树形对象
     */
    public void converToDTO(TerminalGroupTreeNodeDTO treeNodeDTO) {
        Assert.notNull(treeNodeDTO, "terminal group treeNodeDTO can not be null");
        
        treeNodeDTO.setId(id);
        treeNodeDTO.setLabel(name);
        treeNodeDTO.setParentId(parentId);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public TerminalTypeEnums getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(TerminalTypeEnums terminalType) {
        this.terminalType = terminalType;
    }
    
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
