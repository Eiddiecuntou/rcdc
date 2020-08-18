package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalStatisticsAPI;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.TerminalStatisticsDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalStatisticsDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.TerminalStatisticsItem;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.ViewTerminalStatDAO;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.util.HibernateUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/7/15
 *
 * @author jarman
 */
public class CbbTerminalStatisticsAPIImpl implements CbbTerminalStatisticsAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(CbbTerminalStatisticsAPIImpl.class);

    @Autowired
    private ViewTerminalStatDAO viewTerminalStatDAO;

    @Override
    public CbbTerminalStatisticsDTO statisticsTerminal(UUID[] groupIdArr) {
        Assert.notNull(groupIdArr, "groupIdArr");
        CbbTerminalStatisticsDTO response = new CbbTerminalStatisticsDTO();
        //统计各类型终端在线情况
        TerminalStatisticsItem itemVDI = buildTerminalStatisticsItem(CbbTerminalPlatformEnums.VDI, groupIdArr);
        response.setVdi(itemVDI);
        TerminalStatisticsItem itemIDV = buildTerminalStatisticsItem(CbbTerminalPlatformEnums.IDV, groupIdArr);
        response.setIdv(itemIDV);
        TerminalStatisticsItem itemAPP = buildTerminalStatisticsItem(CbbTerminalPlatformEnums.APP, groupIdArr);
        response.setApp(itemAPP);
        //统计所有终端在线情况
        return response;
    }

    private TerminalStatisticsItem buildTerminalStatisticsItem(CbbTerminalPlatformEnums terminalPlatform, UUID[] groupIdArr) {
        List<TerminalStatisticsDTO> resultList;
        Long neverLoginCount = 0L;
        if (ArrayUtils.isEmpty(groupIdArr)) {
            resultList = viewTerminalStatDAO.statisticsByTerminalState(terminalPlatform);
        } else {
            List<UUID> terminalGroupIdList = HibernateUtil
                    .handleQueryIncludeList(Arrays.asList(groupIdArr));
            resultList = viewTerminalStatDAO.statisticsByTerminalStateAndGroupId(terminalPlatform,
                    terminalGroupIdList);
        }
        if (CollectionUtils.isEmpty(resultList)) {
            LOGGER.debug("没有终端类型为[{}]的数据", terminalPlatform);
            return new TerminalStatisticsItem();
        }
        TerminalStatisticsItem item = buildTerminalStatisticsItem(resultList, neverLoginCount);
        return item;
    }

    private TerminalStatisticsItem buildTerminalStatisticsItem(List<TerminalStatisticsDTO> list, Long neverLoginCount) {
        AtomicInteger online = new AtomicInteger();
        AtomicInteger offline = new AtomicInteger();
        AtomicInteger other = new AtomicInteger();
        list.forEach((item -> {
            CbbTerminalStateEnums state = CbbTerminalStateEnums.valueOf(item.getState());
            int count = item.getCount().intValue();
            switch (state) {
                case UPGRADING:
                case ONLINE:
                    online.addAndGet(count);
                    break;
                case OFFLINE:
                    offline.addAndGet(count);
                    break;
                default:
                    other.addAndGet(count);
                    break;
            }
        }));
        TerminalStatisticsItem item = new TerminalStatisticsItem();
        item.setOnline(online.get());
        item.setOffline(offline.get());
        item.setNeverLogin(neverLoginCount.intValue());
        item.setTotal(online.get() + offline.get() + other.get());
        return item;
    }
}
