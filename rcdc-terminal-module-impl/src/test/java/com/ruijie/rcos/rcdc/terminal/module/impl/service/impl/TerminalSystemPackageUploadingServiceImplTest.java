package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradePackageHandlerFactory;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

/**
 * test
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020年4月10日
 * 
 * @author lin
 */
@RunWith(JMockit.class)
public class TerminalSystemPackageUploadingServiceImplTest {

    @Tested
    private TerminalSystemPackageUploadingServiceImpl impl;
    
    @Injectable
    private TerminalSystemUpgradePackageHandlerFactory handlerFactory;
    
    @Test
    public void testisUpgradeFileUploading() {
        CbbTerminalTypeEnums terminalType = CbbTerminalTypeEnums.APP_ANDROID;
        assertEquals(false,impl.isUpgradeFileUploading(terminalType));
    }
}
