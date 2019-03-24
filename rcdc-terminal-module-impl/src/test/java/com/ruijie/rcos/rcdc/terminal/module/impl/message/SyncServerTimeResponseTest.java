package com.ruijie.rcos.rcdc.terminal.module.impl.message;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月6日
 * 
 * @author ls
 */
public class SyncServerTimeResponseTest {

    /**
     * 测试build
     */
    @Test
    public void testBuild() {
        SyncServerTimeResponse response = SyncServerTimeResponse.build();
        SyncServerTimeResponse.Content content = new SyncServerTimeResponse.Content();
        Long time = 100L;
        content.setServerTime(time);
        response.setContent(content);
        assertEquals(0, response.getCode());
        assertEquals(content, response.getContent());
        assertEquals(time, response.getContent().getServerTime());
    }

}
