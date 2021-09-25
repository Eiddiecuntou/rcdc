package com.ruijie.rcos.rcdc.terminal.module.impl.dao;

import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalAuthorizationWhitelistEntity;
import com.ruijie.rcos.sk.modulekit.api.ds.SkyEngineJpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Description:
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/9/16
 *
 * @author zhangsiming
 */
public interface TerminalAuthorizationWhitelistDAO extends SkyEngineJpaRepository<TerminalAuthorizationWhitelistEntity, UUID> {
    /**
     * 返回终端授权白名单表中所有的型号，并按照优先级的大小降序排列
     *
     * @return 返回白名单列表
     */
    List<TerminalAuthorizationWhitelistEntity> findAllByOrderByPriorityDesc();

    /**
     * @param productType 产品型号
     * @return 找到匹配型号的授权白名单记录
     */
    TerminalAuthorizationWhitelistEntity findByProductType(String productType);
}
