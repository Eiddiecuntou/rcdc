package com.ruijie.rcos.rcdc.terminal.module.impl.enums;

import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/9/17
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class UpgradeFileTypeEnumsTest {

    /**
     * 测试contains参数为空
     * @throws Exception 异常
     */
    @Test
    public void testContainsArgmentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> UpgradeFileTypeEnums.contains(null), "fileType can not be empty");
        assertTrue(true);
    }

    /**
     * 测试contains - 文件类型未知
     * @throws Exception 异常
     */
    @Test
    public void testContainsFileTypeIsUnknown() throws Exception {
        String fileType = "sss";

        boolean contains = UpgradeFileTypeEnums.contains(fileType);
        assertTrue(!contains);
    }

    /**
     * 测试contains
     * @throws Exception 异常
     */
    @Test
    public void testContains() throws Exception {
        String fileType = "iso";

        boolean contains = UpgradeFileTypeEnums.contains(fileType);
        assertTrue(contains);
    }
}
