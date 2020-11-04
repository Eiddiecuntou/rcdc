package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.GetSetTester;
import junit.framework.TestCase;
import org.hibernate.annotations.UpdateTimestamp;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.UUID;

@RunWith(SkyEngineRunner.class)
public class CbbUpgradeableTerminalListDTOTest {

    /**
     * 测试getAndSet方法
     */
    @Test
    public void testSetAndGet() {
        CbbUpgradeableTerminalListDTO cbbUpgradeableTerminalListDTO = new CbbUpgradeableTerminalListDTO();
        String id = UUID.randomUUID().toString();
        cbbUpgradeableTerminalListDTO.setId(id);
        String templateName = "templatename";
        cbbUpgradeableTerminalListDTO.setTerminalName(templateName);
        String ip = "ip";
        cbbUpgradeableTerminalListDTO.setIp(ip);
        String mac = "mac";
        cbbUpgradeableTerminalListDTO.setMac(mac);
        cbbUpgradeableTerminalListDTO.setTerminalState(CbbTerminalStateEnums.ONLINE);
        String productType = "productType";
        cbbUpgradeableTerminalListDTO.setProductType(productType);
        Date lastUpgradeTime = new Date();
        cbbUpgradeableTerminalListDTO.setLastUpgradeTime(lastUpgradeTime);
        UUID groupId = UUID.randomUUID();
        cbbUpgradeableTerminalListDTO.setGroupId(groupId);

        Assert.assertEquals(cbbUpgradeableTerminalListDTO.getId(), id);
        Assert.assertEquals(cbbUpgradeableTerminalListDTO.getTerminalName(), templateName);
        Assert.assertEquals(cbbUpgradeableTerminalListDTO.getIp(), ip);
        Assert.assertEquals(cbbUpgradeableTerminalListDTO.getMac(), mac);
        Assert.assertEquals(cbbUpgradeableTerminalListDTO.getTerminalState(), CbbTerminalStateEnums.ONLINE);
        Assert.assertEquals(cbbUpgradeableTerminalListDTO.getProductType(), productType);
        Assert.assertEquals(cbbUpgradeableTerminalListDTO.getLastUpgradeTime(), lastUpgradeTime);
        Assert.assertEquals(cbbUpgradeableTerminalListDTO.getGroupId(), groupId);
    }
}