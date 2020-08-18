package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.impl.dto.TerminalStatisticsDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalStatisticsDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.ViewTerminalStatDAO;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.util.HibernateUtil;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/8/11
 *
 * @author hs
 */
@RunWith(SkyEngineRunner.class)
public class CbbTerminalStatisticsAPIImplTest {

    @Tested
    private CbbTerminalStatisticsAPIImpl terminalStatisticsAPI;

    @Injectable
    private ViewTerminalStatDAO viewTerminalStatDAO;

    /**
     * 测试statisticsTerminal
     */
    @Test
    public void testStatisticsTerminal() {
        UUID[] groupIdArr = new UUID[]{UUID.randomUUID()};
        List<UUID> terminalGroupIdList = HibernateUtil
                .handleQueryIncludeList(Arrays.asList(groupIdArr));
        List<TerminalStatisticsDTO> dtoList = new ArrayList<>();
        TerminalStatisticsDTO terminalStatisticsDTO = new TerminalStatisticsDTO( 1L, CbbTerminalStateEnums.ONLINE.name());
        dtoList.add(terminalStatisticsDTO);
        new Expectations() {
            {
                viewTerminalStatDAO.statisticsByTerminalStateAndGroupId((CbbTerminalPlatformEnums) any, terminalGroupIdList);
                result = dtoList;

            }
        };

        CbbTerminalStatisticsDTO response = terminalStatisticsAPI.statisticsTerminal(groupIdArr);

        assertEquals((Integer) 1, response.getVdi().getOnline());
        assertEquals((Integer) 1, response.getApp().getOnline());
        assertEquals((Integer) 1, response.getIdv().getOnline());

    }


    /**
     * 测试statisticsTerminal, groupIdArr为Empty
     */
    @Test
    public void testStatisticsTerminalGroupIdArrEmpty() {
        UUID[] groupIdArr = new UUID[]{};

        List<TerminalStatisticsDTO> dtoList = new ArrayList<>();
        TerminalStatisticsDTO terminalStatisticsDTO = new TerminalStatisticsDTO( 1L, CbbTerminalStateEnums.OFFLINE.name());
        dtoList.add(terminalStatisticsDTO);
        new Expectations() {
            {
                viewTerminalStatDAO.statisticsByTerminalState((CbbTerminalPlatformEnums) any);
                result = dtoList;

            }
        };

        CbbTerminalStatisticsDTO response = terminalStatisticsAPI.statisticsTerminal(groupIdArr);

        assertEquals((Integer) 1, response.getVdi().getOffline());
        assertEquals((Integer) 1, response.getApp().getOffline());
        assertEquals((Integer) 1, response.getIdv().getOffline());

    }

    /**
     * 测试statisticsTerminal, groupIdArr为Empty
     */
    @Test
    public void testStatisticsTerminalTerminalListEmpty() {
        UUID[] groupIdArr = new UUID[]{};

        List<TerminalStatisticsDTO> dtoList = new ArrayList<>();

        new Expectations() {
            {
                viewTerminalStatDAO.statisticsByTerminalState((CbbTerminalPlatformEnums) any);
                result = dtoList;

            }
        };

        CbbTerminalStatisticsDTO response = terminalStatisticsAPI.statisticsTerminal(groupIdArr);

        assertEquals((Integer) 0, response.getVdi().getOnline());
        assertEquals((Integer) 0, response.getApp().getOnline());
        assertEquals((Integer) 0, response.getIdv().getOnline());


    }


}
