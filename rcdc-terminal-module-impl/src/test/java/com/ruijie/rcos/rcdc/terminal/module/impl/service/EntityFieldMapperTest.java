package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Map;
import org.junit.Test;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.Tested;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月4日
 * 
 * @author ls
 */
public class EntityFieldMapperTest {

    @Tested
    private EntityFieldMapper mapper;

    /**
     * 测试mapping，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testMappingArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> mapper.mapping("", "ds"), "paramField不能为空");
        ThrowExceptionTester.throwIllegalArgumentException(() -> mapper.mapping("sd", ""), "entityField不能为空");
        assertTrue(true);
    }

    /**
     * 测试mapping
     */
    @Test
    public void testMapping() {
        Map<String, String> map = mapper.getMapper();
        map.clear();
        mapper.mapping("sds", "sss");
        assertEquals("sss", map.get("sds"));
        map.clear();
    }

}
