package com.ruijie.rcos.rcdc.terminal.module.impl.message;

import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.Tested;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/6/16
 *
 * @author hs
 */
@RunWith(SkyEngineRunner.class)
public class SyncTerminalLogoRequestTest {

    /**
     * 测试get、set
     */
    @Test
    public void testGetAndSet() {
        SyncTerminalLogoRequest request = new SyncTerminalLogoRequest("", "");
        request.setLogoPath("logo");
        request.setMd5("123456");
        assertEquals("logo", request.getLogoPath());
        assertEquals("123456", request.getMd5());

    }
}
