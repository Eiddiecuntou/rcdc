package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import static org.junit.Assert.assertTrue;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
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
public class LinuxVDISystemUpgradeFileClearHandlerTest {

    @Tested
    private LinuxVDISystemUpgradeFileClearHandler handler;

    /**
     * 测试clear，刷机任务无刷机终端
     * 
     * @param fileOperateUtil 文件工具类
     */
    @Test
    public void testClear(@Mocked FileOperateUtil fileOperateUtil) {
        handler.clear();

        new Verifications() {
            {
                FileOperateUtil.deleteFile((File) any);
                times = 1;
            }
        };
    }

}
