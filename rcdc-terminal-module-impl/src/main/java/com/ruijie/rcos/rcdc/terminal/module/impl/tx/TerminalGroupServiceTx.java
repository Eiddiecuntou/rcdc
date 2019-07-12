package com.ruijie.rcos.rcdc.terminal.module.impl.tx;

import java.util.UUID;

import org.springframework.lang.Nullable;

import com.ruijie.rcos.sk.base.exception.BusinessException;

/**
 * 
 * Description: 终端分组存在事物的操作
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年07月08日
 * 
 * @author nt
 */
public interface TerminalGroupServiceTx {

    /**
     * 删除终端分组
     * @param id 分组id
     * @param moveGroupId 子分组及终端移动分组id
     * @throws BusinessException 业务异常
     */
    void deleteGroup(UUID id, @Nullable UUID moveGroupId) throws BusinessException;
}
