package com.ruijie.rcos.rcdc.terminal.module.impl.entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.*;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalGroupDetailDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalGroupTreeNodeDTO;

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
@Table(name = "t_cbb_terminal_group")
public class TerminalGroupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID parentId;

    private String name;

    private Date createTime;

    @Version
    private int version;

    /**
     * 实体对象转成DTO对象
     * 
     * @param terminalGroupDTO 页面呈现的VO对象
     */
    public void converToDTO(CbbTerminalGroupDetailDTO terminalGroupDTO) {
        Assert.notNull(terminalGroupDTO, "terminalGroupDTO can not be null");

        terminalGroupDTO.setId(id);
        terminalGroupDTO.setGroupName(name);
        terminalGroupDTO.setParentGroupId(parentId);
    }

    /**
     * 实体对象转成树形DTO对象
     * 
     * @param treeNodeDTO 页面呈现的树形对象
     */
    public void converToDTO(CbbTerminalGroupTreeNodeDTO treeNodeDTO) {
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
