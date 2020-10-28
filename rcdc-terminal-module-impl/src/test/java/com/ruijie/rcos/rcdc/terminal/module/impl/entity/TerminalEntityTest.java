package com.ruijie.rcos.rcdc.terminal.module.impl.entity;


import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDiskInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalNetworkInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.GetSetTester;
import mockit.Expectations;
import mockit.MockUp;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.fail;

/**
 * Description:
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/9/15 1:43 下午
 *
 * @author zhouhuan
 */
@RunWith(SkyEngineRunner.class)
public class TerminalEntityTest {

    /**
     * testGetAndSet
     */
    @Test
    public void testGetAndSet() {
        GetSetTester tester = new GetSetTester(TerminalEntity.class);
        tester.addIgnoreProperty("networkInfoArr");
        tester.addIgnoreProperty("diskInfoArr");
        tester.runTest();
        Assert.assertTrue(true);
    }

    /**
     * testGetNetworkInfo
     */
    @Test
    public void testGetNetworkInfo() throws BusinessException {
        TerminalEntity testEntity1 = new TerminalEntity();

        CbbTerminalNetworkInfoDTO[] networkInfo1Arr = testEntity1.getNetworkInfoArr();
        Assert.assertEquals(0, networkInfo1Arr.length);

        TerminalEntity testEntity2 = new TerminalEntity();
        testEntity2.setNetworkInfos("[{\"gateway\":\"172.20.113.1\",\"getDnsMode\":\"AUTO\"," +
                "\"getIpMode\":\"AUTO\",\"ip\":\"172.20.113.157\",\"macAddr\":\"58:69:6c:ff:3b:cc\"," +
                "\"mainDns\":\"172.30.44.20\",\"networkAccessMode\":\"WIRED\",\"secondDns\":\"192.168.5.28\",\"subnetMask\":\"255.255.255.0\"}]");

        CbbTerminalNetworkInfoDTO[] networkInfo2Arr = testEntity2.getNetworkInfoArr();
        Assert.assertEquals(1, networkInfo2Arr.length);
        Assert.assertEquals("58:69:6c:ff:3b:cc", networkInfo2Arr[0].getMacAddr());

        TerminalEntity testEntity3 = new TerminalEntity();
        testEntity3.setNetworkInfos("[]");

        CbbTerminalNetworkInfoDTO[] networkInfo3Arr = testEntity3.getNetworkInfoArr();
        Assert.assertEquals(0, networkInfo3Arr.length);
    }

    /**
     * testGetNetworkInfoArrParseJSONError
     */
    @Test
    public void testGetNetworkInfoArrParseJSONError() throws BusinessException {
        TerminalEntity testEntity1 = new TerminalEntity();
        testEntity1.setNetworkInfos("sdfassdf");
        try {
            testEntity1.getNetworkInfoArr();
            fail();
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_NETWORK_INFO_ERROR, e.getKey());
        }
    }

    /**
     * testGetDiskInfoArr
     */
    @Test
    public void testGetDiskInfoArr() throws BusinessException {
        TerminalEntity testEntity1 = new TerminalEntity();
        CbbTerminalDiskInfoDTO[] diskInfo1Arr = testEntity1.getDiskInfoArr();
        Assert.assertEquals(0, diskInfo1Arr.length);


        TerminalEntity testEntity2 = new TerminalEntity();
        CbbTerminalDiskInfoDTO dto2 = new CbbTerminalDiskInfoDTO();
        dto2.setDevName("123");
        testEntity2.setAllDiskInfo(JSON.toJSONString(Lists.newArrayList(dto2)));
        CbbTerminalDiskInfoDTO[] diskInfo2Arr = testEntity2.getDiskInfoArr();
        Assert.assertEquals(1, diskInfo2Arr.length);
        Assert.assertEquals("123", diskInfo2Arr[0].getDevName());

    }

    /**
     * testGetDiskInfoArrParseJSONError
     */
    @Test
    public void testGetDiskInfoArrParseJSONError() throws BusinessException {
        TerminalEntity testEntity1 = new TerminalEntity();
        testEntity1.setAllDiskInfo("sdfassdf");
        try {
            testEntity1.getDiskInfoArr();
            fail();
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_DISK_INFO_ERROR, e.getKey());
        }
    }

    /**
     * testGetDiskInfoArrIsEmpty
     */
    @Test
    public void testGetDiskInfoArrIsEmpty() throws BusinessException {
        TerminalEntity testEntity1 = new TerminalEntity();
        testEntity1.setAllDiskInfo("[]");

        CbbTerminalDiskInfoDTO[] diskInfoArr = testEntity1.getDiskInfoArr();
        Assert.assertEquals(0, diskInfoArr.length);
    }
}