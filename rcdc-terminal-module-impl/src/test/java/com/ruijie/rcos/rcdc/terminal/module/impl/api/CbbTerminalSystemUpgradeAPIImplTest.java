package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradePackageAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbAddSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.QuerySystemUpgradeListService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.QuerySystemUpgradeTerminalListService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.QueryUpgradeableTerminalListService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.TerminalSystemUpgradeSupportService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalSystemUpgradeServiceTx;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: 终端系统升级api测试类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月28日
 * 
 * @author nt
 */
@RunWith(JMockit.class)
public class CbbTerminalSystemUpgradeAPIImplTest {

    @Tested
    private CbbTerminalSystemUpgradeAPIImpl cbbTerminalSystemUpgradeAPIImpl;

    @Injectable
    private TerminalSystemUpgradePackageDAO terminalSystemUpgradePackageDAO;

    @Injectable
    private TerminalSystemUpgradeService terminalSystemUpgradeService;
    
    @Injectable
    private TerminalSystemUpgradeSupportService terminalSystemUpgradeSupportService;

    @Injectable
    private TerminalSystemUpgradeServiceTx terminalSystemUpgradeServiceTx;

    @Injectable
    private TerminalBasicInfoDAO basicInfoDAO;

    @Injectable
    private QuerySystemUpgradeListService querySystemUpgradeListService;
    
    @Injectable
    private QuerySystemUpgradeTerminalListService querySystemUpgradeTerminalListService;

    @Injectable
    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;
    
    @Injectable
    private CbbTerminalSystemUpgradePackageAPI systemUpgradePackageAPI;
    
    @Injectable
    private QueryUpgradeableTerminalListService queryUpgradeableTerminalListDAO;

    /**
     * 测试升级包上传
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testAddSystemUpgradeTaskRequestIsNull() throws BusinessException {
        
        CbbAddSystemUpgradeTaskRequest request = null;
        
        try {
            cbbTerminalSystemUpgradeAPIImpl.addSystemUpgradeTask(request);
            fail();
        } catch (Exception e) {
            assertEquals(e.getMessage(), "request can not be null");
        }
    }

}
