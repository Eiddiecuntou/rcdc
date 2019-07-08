package com.ruijie.rcos.rcdc.terminal.module.def.api.request.terminal;

import java.util.UUID;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;
import org.springframework.lang.Nullable;


/**
 *
 * Description: 删除终端分组请求参数
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年10月30日
 * 
 * @author chenzj
 */
public class DeleteTerminalGroupRequest implements Request {

    /**
     * 分组id
     */
    @NotNull
    private UUID id;

    @Nullable
    private UUID moveGroupId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Nullable
    public UUID getMoveGroupId() {
        return moveGroupId;
    }

    public void setMoveGroupId(@Nullable UUID moveGroupId) {
        this.moveGroupId = moveGroupId;
    }
}
