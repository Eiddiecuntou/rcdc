package com.ruijie.rcos.rcdc.terminal.module.web.ctrl;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradeAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradePackageAPI;
import com.ruijie.rcos.sk.base.batch.BatchTaskBuilder;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.webmvc.api.optlog.ProgrammaticOptLogRecorder;
import com.ruijie.rcos.sk.webmvc.api.request.ChunkUploadFile;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月25日
 * 
 * @author ls
 */
@RunWith(JMockit.class)
public class TerminalSystemUpgradeControllerTest {

    @Tested
    private TerminalSystemUpgradeController controller;

    @Injectable
    private CbbTerminalSystemUpgradeAPI cbbTerminalUpgradeAPI;

    @Injectable
    private CbbTerminalSystemUpgradePackageAPI cbbTerminalUpgradePackageAPI;

    @Mocked
    private BatchTaskBuilder builder;

    /**
     * 测试uploadPackage，参数为空
     * 
     * @param optLogRecorder mock日志记录对象
     * @throws Exception 异常
     */
    @Test
    public void testUploadPackageArgumentIsNull(@Mocked ProgrammaticOptLogRecorder optLogRecorder) throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.uploadPackage(null, optLogRecorder),
                "file can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.uploadPackage(new ChunkUploadFile(), null),
                "optLogRecorder can not be null");
        assertTrue(true);
    }

}
