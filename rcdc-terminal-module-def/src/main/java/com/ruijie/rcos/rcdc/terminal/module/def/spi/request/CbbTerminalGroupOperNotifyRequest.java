package com.ruijie.rcos.rcdc.terminal.module.def.spi.request;

import com.ruijie.rcos.sk.base.annotation.NotNull;
import org.springframework.lang.Nullable;

import java.util.Set;
import java.util.UUID;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020年1月7日
 * 
 * @author wjp
 */
public class CbbTerminalGroupOperNotifyRequest {

    @NotNull
    private UUID id;

    @NotNull
    private Set<UUID> deleteIdSet;

    @Nullable
    private UUID moveGroupId;


    public UUID getMoveGroupId() {
        return moveGroupId;
    }

    public void setMoveGroupId(UUID moveGroupId) {
        this.moveGroupId = moveGroupId;
    }

    public Set<UUID> getDeleteIdSet() {
        return deleteIdSet;
    }

    public void setDeleteIdSet(Set<UUID> deleteIdSet) {
        this.deleteIdSet = deleteIdSet;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
