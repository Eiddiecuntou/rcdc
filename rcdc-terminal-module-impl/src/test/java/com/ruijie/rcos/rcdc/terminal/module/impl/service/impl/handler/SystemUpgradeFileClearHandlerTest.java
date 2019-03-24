package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import static org.junit.Assert.assertTrue;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Invocation;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月26日
 * 
 * @author ls
 */
public class SystemUpgradeFileClearHandlerTest {
    
    @Tested
    private SystemUpgradeFileClearHandler handler;

    @Injectable
    private TerminalSystemUpgradePackageService terminalSystemUpgradePackageService;

    @Injectable
    private TerminalSystemUpgradeTerminalDAO terminalSystemUpgradeTerminalDAO;
    
    /**
     * 测试clear，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testClearArgumentIsNull() throws Exception {
        TerminalSystemUpgradeEntity packageIdNullEntity = new TerminalSystemUpgradeEntity();
        packageIdNullEntity.setId(UUID.randomUUID());
        ThrowExceptionTester.throwIllegalArgumentException(() -> handler.clear
                (null),"systemUpgradeEntity can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> handler.clear
                (packageIdNullEntity),"upgradePackageId can not be null");
        assertTrue(true);
    }
    
    /**
     * 测试clear，刷机任务无刷机终端
     * @param fileOperateUtil 
     * @throws BusinessException 异常
     */
    @Test
    public void testClearNoUpgradeTerminal(@Mocked FileOperateUtil fileOperateUtil) throws BusinessException {
        TerminalSystemUpgradeEntity entity = new TerminalSystemUpgradeEntity();
        entity.setUpgradePackageId(UUID.randomUUID());
        handler.clear(entity);
        
        new Verifications() {
            {
                terminalSystemUpgradePackageService.getSystemUpgradePackage((UUID) any);
                times = 1;
                FileOperateUtil.deleteFile((File) any);
                times = 1;
            }
        };
    }
    
    /**
     * 测试clear，
     * @param fileOperateUtil 
     * @throws BusinessException 异常
     */
    @Test
    public void testClear(@Mocked FileOperateUtil fileOperateUtil) throws BusinessException {
        TerminalSystemUpgradeEntity entity = new TerminalSystemUpgradeEntity();
        entity.setUpgradePackageId(UUID.randomUUID());
        
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = new ArrayList<>();
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminal.setTerminalId("1");
        upgradeTerminalList.add(upgradeTerminal);
        new MockUp<File>() {
            @Mock
            public boolean exists(Invocation invocation) {
                Assert.notNull(invocation, "invocation can not be null");
                File file = (File)invocation.getInvokedInstance(); 
                if (file.getAbsolutePath().contains("mac_end")) {
                    return true;
                }
                return false;
            }
            
            @Mock
            public boolean delete() {
                return true;
            }
        };
        handler.clear(entity);
        
        new Verifications() {
            {
                terminalSystemUpgradePackageService.getSystemUpgradePackage((UUID) any);
                times = 1;
            }
        };
    }

}
