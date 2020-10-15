package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalUpgradePackageUploadDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradePackageHandlerFactory;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

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

    /**
     * testUploadUpgradePackage
     */
    @Test
    public void testUploadUpgradePackageSuccess() throws Exception {
        CbbTerminalUpgradePackageUploadDTO request = new CbbTerminalUpgradePackageUploadDTO();
        request.setFileName("test");
        request.setFilePath("/test");
        request.setTerminalType(CbbTerminalTypeEnums.VDI_LINUX);
        request.setFileMD5("xxxx");

        CbbTerminalTypeEnums vdiLinux = CbbTerminalTypeEnums.VDI_LINUX;

        impl.uploadUpgradePackage(request, vdiLinux);

        new Verifications() {
            {
                handlerFactory.getHandler(vdiLinux);
                times = 1;
            }
        };
    }

}
