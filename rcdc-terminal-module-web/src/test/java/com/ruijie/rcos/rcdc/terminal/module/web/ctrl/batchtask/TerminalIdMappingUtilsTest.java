package com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月24日
 * 
 * @author ls
 */
@RunWith(JMockit.class)
public class TerminalIdMappingUtilsTest {

    /**
     * 测试mapping，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testMappingArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> TerminalIdMappingUtils.mapping(null), "terminalIdArr不能为null");
        ThrowExceptionTester.throwIllegalStateException(() -> TerminalIdMappingUtils.mapping(new String[0]), "terminalIdArr大小不能为0");
        assertTrue(true);
    }
    
    /**
     * 测试mapping，
     */
    @Test
    public void testMapping() {
        String[] terminalIdArr = new String[2];
        terminalIdArr[0] = "0";
        terminalIdArr[1] = "1";
        Map<UUID, String> map = TerminalIdMappingUtils.mapping(terminalIdArr);
        assertEquals(2, map.size());
        assertTrue(map.containsValue("0"));
        assertTrue(map.containsValue("1"));
    }

    /**
     * 测试extractUUID，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testExtractUUIDArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> TerminalIdMappingUtils.extractUUID(null), "idMap不能为null");
        assertTrue(true);
    }
    
    /**
     * 测试extractUUID，参数为空
     */
    @Test
    public void testExtractUUID() {
        Map<UUID, String> map = new HashMap<>();
        UUID uuid0 = UUID.randomUUID();
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        map.put(uuid0, "0");
        map.put(uuid1, "1");
        map.put(uuid2, "2");
        UUID[] uuidArr = TerminalIdMappingUtils.extractUUID(map);
        assertEquals(3, uuidArr.length);
        assertTrue(Arrays.asList(uuidArr).contains(uuid0));
        assertTrue(Arrays.asList(uuidArr).contains(uuid1));
        assertTrue(Arrays.asList(uuidArr).contains(uuid2));
    }
}
