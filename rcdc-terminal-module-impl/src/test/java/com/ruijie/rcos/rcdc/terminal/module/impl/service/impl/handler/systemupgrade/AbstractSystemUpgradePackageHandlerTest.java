package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/3/27 14:21
 *
 * @author zhangyichi
 */
@RunWith(SkyEngineRunner.class)
public class AbstractSystemUpgradePackageHandlerTest {

    @Mocked
    private TerminalSystemUpgradePackageService terminalSystemUpgradePackageService;

    /**
     * 校验MD5，执行异常
     * @throws BusinessException 异常
     */
    @Test
    public void testCheckISOMd5IoException() throws BusinessException {
        new MockUp<DefaultExecutor>() {
            @Mock
            int execute(CommandLine command) throws ExecuteException, IOException {
                throw new IOException("xxx");
            }
        };

        new MockUp<ByteArrayOutputStream>() {
            @Mock
            void close() throws IOException {
                throw new IOException("xxx");
            }
        };

        AbstractSystemUpgradePackageHandler systemUpgradePackageHandler = new MockSystemUpgradePackageHandler();
        try {
            systemUpgradePackageHandler.checkISOMd5("filePath");
            Assert.fail();
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_UPGRADE_PACKAGE_FILE_ILLEGAL, e.getKey());
        }
    }

    private class MockSystemUpgradePackageHandler extends AbstractSystemUpgradePackageHandler {
        @Override
        protected TerminalUpgradeVersionFileInfo getPackageInfo(String fileName, String filePath) throws BusinessException {
            return new TerminalUpgradeVersionFileInfo();
        }

        @Override
        protected TerminalSystemUpgradePackageService getSystemUpgradePackageService() {
            return terminalSystemUpgradePackageService;
        }

        @Override
        public void preUploadPackage() {

        }

        @Override
        public void postUploadPackage() {

        }
    }
}