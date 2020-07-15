package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import org.springframework.lang.Nullable;

import java.util.UUID;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/12/7
 *
 * @author Jarman
 */
public class TerminalPlatformRequest {

    @Nullable
    private UUID[] groupIdArr;

    @Nullable
    public UUID[] getGroupIdArr() {
        return groupIdArr;
    }

    public void setGroupIdArr(@Nullable UUID[] groupIdArr) {
        this.groupIdArr = groupIdArr;
    }
}
