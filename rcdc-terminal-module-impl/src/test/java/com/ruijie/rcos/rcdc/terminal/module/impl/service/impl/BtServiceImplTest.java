package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.SeedFileInfo;
import com.ruijie.rcos.sk.base.shell.ShellCommandRunner;
import mockit.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ruijie.rcos.rcdc.hciadapter.module.def.api.CloudPlatformMgmtAPI;
import com.ruijie.rcos.rcdc.hciadapter.module.def.dto.ClusterVirtualIpDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.SambaInfoDTO;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultRequest;
import com.ruijie.rcos.sk.modulekit.api.comm.DtoResponse;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/1/8
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class BtServiceImplTest {

    @Tested
    private BtServiceImpl btService;

    /**
     * 测试制作bt种子
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testMakeBtSeed() throws BusinessException {

        new MockUp<ShellCommandRunner>() {
            @Mock
            public String execute() {
                SeedFileInfo seedFileInfo = new SeedFileInfo("123", "456");
                return JSON.toJSONString(seedFileInfo);
            }
        };

        SeedFileInfo seedFileInfo = btService.makeBtSeed("/aaa/a", "/bbb", "172.21.1.1");

        SeedFileInfo expected = new SeedFileInfo("123", "456");
        assertEquals(expected, seedFileInfo);
    }

    /**
     * 测试制作bt种子 - bt脚本执行失败
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testMakeBtSeedFail() throws BusinessException {

        new MockUp<ShellCommandRunner>() {
            @Mock
            public String execute() {
                SeedFileInfo seedFileInfo = new SeedFileInfo("123", "456");
                return "fail ";
            }
        };

        try {
            btService.makeBtSeed("/aaa/a", "/bbb", "172.21.1.1");
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_OTA_UPGRADE_MAKE_SEED_FILE_FAIL, e.getKey());
        }
    }

    /**
     * 测试制作bt种子 - bt脚本执行失败
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testStartBtShare() throws BusinessException {

        new MockUp<ShellCommandRunner>() {
            @Mock
            public String execute() {
                return "success111";
            }
        };

        btService.startBtShare("/aaa/a", "/bbb");

        new Verifications() {
            {
                String.format("python %s %s", "/data/web/rcdc/shell/start_ota_bt_share.py", "/aaa/a", "/bbb");
                times = 1;
            }
        };
    }

    /**
     * 测试制作bt种子 - bt脚本执行失败
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testStopBtShare() throws BusinessException {

        new MockUp<ShellCommandRunner>() {
            @Mock
            public String execute() {
                return "success111";
            }
        };

        btService.stopBtShare("/aaa/a");

        new Verifications() {
            {
                String.format("python %s %s", "/data/web/rcdc/shell/stop_ota_bt_share.py", "/aaa/a");
                times = 1;
            }
        };
    }

}
