package com.ruijie.rcos.rcdc.terminal.module.impl.connect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月23日
 * 
 * @author ls
 */
@RunWith(JMockit.class)
public class TerminalBusinessInitializingBeanTest {

    @Tested
    private TerminalBusinessInitializingBean initializingBean;
    
    @Injectable
    private TerminalBasicInfoDAO terminalBasicInfoDAO;

    @Injectable
    private TerminalBasicInfoService terminalBasicInfoService;


    /**
     * 测试afterPropertiesSet,terminalList为空
     * @throws Exception 异常
     */
    @Test
    public void testAfterPropertiesSetTerminalListIsEmpty() throws Exception {
        new Expectations() {
            {
                terminalBasicInfoDAO.findTerminalEntitiesByState(CbbTerminalStateEnums.ONLINE);
                result = Collections.emptyList();
            }
        };
        initializingBean.afterPropertiesSet();
        new Verifications() {
            {
                terminalBasicInfoService.modifyTerminalStateToOffline(anyString);
                times = 0;
            }
        };
    }
    
    /**
     * 测试afterPropertiesSet,terminalList不为空
     * @throws Exception 异常
     */
    @Test
    public void testAfterPropertiesSetTerminalListIsNotEmpty() throws Exception {
        List<TerminalEntity> terminalList = new ArrayList<>();
        TerminalEntity entity = new TerminalEntity();
        TerminalEntity entity1 = new TerminalEntity();
        terminalList.add(entity);
        terminalList.add(entity1);
        new Expectations() {
            {
                terminalBasicInfoDAO.findTerminalEntitiesByState(CbbTerminalStateEnums.ONLINE);
                result = terminalList;
            }
        };
        initializingBean.afterPropertiesSet();
        new Verifications() {
            {
                terminalBasicInfoService.modifyTerminalStateToOffline(anyString);
                times = 2;
            }
        };
    }

}
