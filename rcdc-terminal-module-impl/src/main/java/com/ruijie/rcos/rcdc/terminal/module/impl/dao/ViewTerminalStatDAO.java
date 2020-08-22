package com.ruijie.rcos.rcdc.terminal.module.impl.dao;

import com.ruijie.rcos.rcdc.terminal.module.impl.dto.TerminalStatisticsDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.ViewTerminalStatEntity;
import com.ruijie.rcos.sk.modulekit.api.ds.SkyEngineJpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/12/6
 *
 * @author Jarman
 */
public interface ViewTerminalStatDAO extends SkyEngineJpaRepository<ViewTerminalStatEntity, UUID> {

    /**
     * 按状态分组查询统计
     *
     * @param terminalType 终端类型
     * @return 返回终端统计结果
     */
    @Query("select new com.ruijie.rcos.rcdc.terminal.module.impl.dto.TerminalStatisticsDTO(count(state)," + "state) " + "from "
            + "ViewTerminalStatEntity where platform=?1 group by state")
    List<TerminalStatisticsDTO> statisticsByTerminalState(CbbTerminalPlatformEnums terminalType);

    /**
     * 按状态分组查询统计
     *
     * @param terminalType 终端类型
     * @param groupIdList  组Id列表
     * @return 返回终端统计结果
     */
    @Query("select new com.ruijie.rcos.rcdc.terminal.module.impl.dto.TerminalStatisticsDTO(count(state), state) "
            + "from ViewTerminalStatEntity "
            + "where platform=?1 and groupId in (?2) group by state")
    List<TerminalStatisticsDTO> statisticsByTerminalStateAndGroupId(CbbTerminalPlatformEnums terminalType, List<UUID> groupIdList);

}
