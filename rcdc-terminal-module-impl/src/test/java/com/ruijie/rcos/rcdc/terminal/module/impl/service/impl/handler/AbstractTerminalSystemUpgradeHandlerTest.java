package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import com.google.common.io.Files;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/15
 *
 * @author hs
 */
@RunWith(SkyEngineRunner.class)
public class AbstractTerminalSystemUpgradeHandlerTest {

    @Tested
    private AbstractTerminalSystemUpgradeHandler handler;

    /**
     * 测试 moveUpgradePackage，参数为null
     *
     * @throws Exception 异常
     */
    @Test
    public void testMoveUpgradePackageArgIsNull() throws Exception {
        TestedTerminalSystemUpgradeHandler handler = new TestedTerminalSystemUpgradeHandler();
        ThrowExceptionTester.throwIllegalArgumentException(() -> handler.moveUpgradePackage(null, "fromPath"), "toPath can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> handler.moveUpgradePackage("toPath", null), "fromPath can not be null");
        Assert.assertTrue(true);

    }

    /**
     * 测试 moveUpgradePackage，失败，磁盘空间不足
     *
     * @throws BusinessException 异常
     */
    @Test
    public void testMoveUpgradePackageSpaceNotEnough() throws BusinessException {
        String toPath = "toPath";
        String fromPath = "fromPath";

        new MockUp<File>() {

            @Mock
            public long length() {
                return 1000;
            }

            @Mock
            public long getUsableSpace() {
                return 100;
            }
        };

        try {
            handler.moveUpgradePackage(fromPath, toPath);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_UPGRADE_PACKAGE_DISK_SPACE_NOT_ENOUGH, e.getKey());
        }

    }

    /**
     * 测试 moveUpgradePackage，上传失败
     *
     * @throws Exception 异常
     */
    @Test
    public void testMoveUpgradePackageFail() throws Exception {
        String toPath = "toPath";
        String fromPath = "fromPath";
        new MockUp<File>() {

            @Mock
            public long length() {
                return 100;
            }

            @Mock
            public long getUsableSpace() {
                return 1000;
            }

        };

        new Expectations(Files.class) {
            {
                Files.move((File) any, (File) any);
                result = new Exception();
            }
        };

        try {
            handler.moveUpgradePackage(fromPath, toPath);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_FAIL, e.getKey());
        }

        new Verifications() {
            {
                Files.move((File) any, (File) any);
                times = 1;
            }
        };

    }

    /**
     * 测试 moveUpgradePackage，成功
     *
     * @throws Exception 异常
     */
    @Test
    public void testMoveUpgradePackageSuccess() throws Exception {
        String toPath = "toPath";
        String fromPath = "fromPath";
        new MockUp<File>() {

            @Mock
            public long length() {
                return 100;
            }

            @Mock
            public long getUsableSpace() {
                return 1000;
            }

        };

        new Expectations(Files.class) {
            {
                Files.move((File) any, (File) any);
            }
        };

        handler.moveUpgradePackage(fromPath, toPath);

        new Verifications() {
            {
                Files.move((File) any, (File) any);
                times = 1;
            }
        };

    }


    /**
     * 测试类
     */
    private class TestedTerminalSystemUpgradeHandler extends AbstractTerminalSystemUpgradeHandler {
        @Override
        public void uploadUpgradePackage(CbbTerminalUpgradePackageUploadRequest request) throws BusinessException {

        }
    }
}
