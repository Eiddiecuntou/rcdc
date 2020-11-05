package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbGetNetworkModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbNetworkModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.UUID;

@RunWith(SkyEngineRunner.class)
public class CbbTerminalBasicInfoDTOTest {

    /**
     * 测试getAndSet方法
     */
    @Test
    public void testSetAndGet() {
        CbbTerminalBasicInfoDTO cbbTerminalBasicInfoDTO = new CbbTerminalBasicInfoDTO();


        UUID id = UUID.randomUUID();
        cbbTerminalBasicInfoDTO.setId(id);
        String terminalName = "";
        cbbTerminalBasicInfoDTO.setTerminalName(terminalName);
        cbbTerminalBasicInfoDTO.setTerminalId(terminalName);
        cbbTerminalBasicInfoDTO.setMacAddr(terminalName);
        cbbTerminalBasicInfoDTO.setIp(terminalName);
        cbbTerminalBasicInfoDTO.setSubnetMask(terminalName);
        cbbTerminalBasicInfoDTO.setGateway(terminalName);
        cbbTerminalBasicInfoDTO.setMainDns(terminalName);
        cbbTerminalBasicInfoDTO.setSecondDns(terminalName);
        cbbTerminalBasicInfoDTO.setGetIpMode(CbbGetNetworkModeEnums.AUTO);
        cbbTerminalBasicInfoDTO.setGetDnsMode(CbbGetNetworkModeEnums.AUTO);
        cbbTerminalBasicInfoDTO.setProductType(terminalName);
        cbbTerminalBasicInfoDTO.setTerminalPlatform(CbbTerminalPlatformEnums.IDV);
        cbbTerminalBasicInfoDTO.setSerialNumber(terminalName);
        cbbTerminalBasicInfoDTO.setCpuType(terminalName);
        cbbTerminalBasicInfoDTO.setMemorySize(0L);
        cbbTerminalBasicInfoDTO.setDiskSize(0L);
        cbbTerminalBasicInfoDTO.setTerminalOsType(terminalName);
        cbbTerminalBasicInfoDTO.setTerminalOsVersion(terminalName);
        cbbTerminalBasicInfoDTO.setRainOsVersion(terminalName);
        cbbTerminalBasicInfoDTO.setRainUpgradeVersion(terminalName);
        cbbTerminalBasicInfoDTO.setHardwareVersion(terminalName);
        cbbTerminalBasicInfoDTO.setNetworkAccessMode(CbbNetworkModeEnums.WIRED);
        Date current = new Date();
        cbbTerminalBasicInfoDTO.setCreateTime(current);
        cbbTerminalBasicInfoDTO.setLastOnlineTime(current);
        cbbTerminalBasicInfoDTO.setLastOfflineTime(current);
        cbbTerminalBasicInfoDTO.setVersion(0);
        cbbTerminalBasicInfoDTO.setState(CbbTerminalStateEnums.ONLINE);
        CbbTerminalNetworkInfoDTO[] networkInfoArr = {};
        cbbTerminalBasicInfoDTO.setNetworkInfoArr(networkInfoArr);
        CbbTerminalDiskInfoDTO[] diskInfoArr = {};
        cbbTerminalBasicInfoDTO.setDiskInfoArr(diskInfoArr);
        cbbTerminalBasicInfoDTO.setWirelessNetCardNum(0);
        cbbTerminalBasicInfoDTO.setEthernetNetCardNum(0);

        Assert.assertEquals(cbbTerminalBasicInfoDTO.getId(), id);
        Assert.assertEquals(cbbTerminalBasicInfoDTO.getTerminalName(), terminalName);
        Assert.assertEquals(cbbTerminalBasicInfoDTO.getTerminalId(), terminalName);
        Assert.assertEquals(cbbTerminalBasicInfoDTO.getMacAddr(), terminalName);
        Assert.assertEquals(cbbTerminalBasicInfoDTO.getIp(), terminalName);
        Assert.assertEquals(cbbTerminalBasicInfoDTO.getSubnetMask(), terminalName);
        Assert.assertEquals(cbbTerminalBasicInfoDTO.getGateway(), terminalName);
        Assert.assertEquals(cbbTerminalBasicInfoDTO.getMainDns(), terminalName);
        Assert.assertEquals(cbbTerminalBasicInfoDTO.getSecondDns(), terminalName);
        Assert.assertEquals(cbbTerminalBasicInfoDTO.getGetIpMode(), CbbGetNetworkModeEnums.AUTO);
        Assert.assertEquals(cbbTerminalBasicInfoDTO.getGetDnsMode(), CbbGetNetworkModeEnums.AUTO);
        Assert.assertEquals(cbbTerminalBasicInfoDTO.getProductType(), terminalName);
        Assert.assertEquals(cbbTerminalBasicInfoDTO.getTerminalPlatform(), CbbTerminalPlatformEnums.IDV);
        Assert.assertEquals(cbbTerminalBasicInfoDTO.getSerialNumber(), terminalName);
        Assert.assertEquals(cbbTerminalBasicInfoDTO.getCpuType(), terminalName);
        Assert.assertEquals(cbbTerminalBasicInfoDTO.getMemorySize(), new Long(0));
        Assert.assertEquals(cbbTerminalBasicInfoDTO.getDiskSize(), new Long(0));
        Assert.assertEquals(cbbTerminalBasicInfoDTO.getTerminalOsType(), terminalName);
        Assert.assertEquals(cbbTerminalBasicInfoDTO.getTerminalOsVersion(), terminalName);
        Assert.assertEquals(cbbTerminalBasicInfoDTO.getRainOsVersion(), terminalName);
        Assert.assertEquals(cbbTerminalBasicInfoDTO.getRainUpgradeVersion(), terminalName);
        Assert.assertEquals(cbbTerminalBasicInfoDTO.getHardwareVersion(), terminalName);
        Assert.assertEquals(cbbTerminalBasicInfoDTO.getNetworkAccessMode(), CbbNetworkModeEnums.WIRED);
        Assert.assertEquals(cbbTerminalBasicInfoDTO.getCreateTime(), current);
        Assert.assertEquals(cbbTerminalBasicInfoDTO.getLastOnlineTime(), current);
        Assert.assertEquals(cbbTerminalBasicInfoDTO.getLastOfflineTime(), current);
        Assert.assertEquals(cbbTerminalBasicInfoDTO.getVersion(), new Integer(0));
        Assert.assertEquals(cbbTerminalBasicInfoDTO.getState(), CbbTerminalStateEnums.ONLINE);
        Assert.assertEquals(cbbTerminalBasicInfoDTO.getNetworkInfoArr(), networkInfoArr);
        Assert.assertEquals(cbbTerminalBasicInfoDTO.getDiskInfoArr(), diskInfoArr);
        Assert.assertEquals(cbbTerminalBasicInfoDTO.getWirelessNetCardNum(), new Integer(0));
        Assert.assertEquals(cbbTerminalBasicInfoDTO.getEthernetNetCardNum(), new Integer(0));

    }
}