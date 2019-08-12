package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;

import mockit.*;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月26日
 * 
 * @author ls
 */
@RunWith(SkyEngineRunner.class)
public class LinuxVDIComponentUpgradeHandlerTest {

    @Tested
    private LinuxVDIComponentUpgradeHandler handler;


    @Test
    public void testGetVersion() {
        handler.getVersion(new GetVersionRequest());
        assertTrue(true);
    }
}
