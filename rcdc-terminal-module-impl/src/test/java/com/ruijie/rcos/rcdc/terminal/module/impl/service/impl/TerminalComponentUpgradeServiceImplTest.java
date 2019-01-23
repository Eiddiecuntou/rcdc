package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.ComponentUpdateListCacheManager;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月23日
 * 
 * @author ls
 */
@RunWith(JMockit.class)
public class TerminalComponentUpgradeServiceImplTest {

    @Tested
    private TerminalComponentUpgradeServiceImpl serviceImpl;
    
    
    @Injectable
    private ComponentUpdateListCacheManager cacheManager;
    
    /**
     * 测试getVersion,参数为空
     * @throws Exception 异常
     */
    @Test
    public void testGetVersion() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> serviceImpl.getVersion("", TerminalPlatformEnums.VDI),
                "rainOsVersion can not be blank");
        ThrowExceptionTester.throwIllegalArgumentException(() -> serviceImpl.getVersion("123", null),
                "platform can not be null");
        assertTrue(true);
    }

}
