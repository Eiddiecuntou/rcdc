package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.sk.base.annotation.NotBlank;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;
import org.springframework.lang.Nullable;

import java.util.UUID;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/1/9
 *
 * @author chen zj
 */
public class CbbTerminalGroupNameDuplicationRequest implements Request {

    @Nullable
    private UUID id;

    @Nullable
    private UUID parentId;

    @NotBlank
    private String groupName;

    public CbbTerminalGroupNameDuplicationRequest(UUID id, UUID parentId, String groupName) {
        this.id = id;
        this.parentId = parentId;
        this.groupName = groupName;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}
