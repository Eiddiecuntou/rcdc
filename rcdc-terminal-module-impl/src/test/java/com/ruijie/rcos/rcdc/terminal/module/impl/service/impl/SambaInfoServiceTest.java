package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.base.sysmanage.module.def.api.SambaServiceAPI;
import com.ruijie.rcos.base.sysmanage.module.def.dto.SambaConfigDTO;
import com.ruijie.rcos.base.sysmanage.module.def.enums.SambaMountState;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.SambaInfoDTO;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/1/8
 *
 * @author Jarman
 */
@RunWith(SkyEngineRunner.class)
public class SambaInfoServiceTest {

    @Tested
    private SambaInfoService sambaInfoService;

    @Injectable
    private SambaServiceAPI sambaServiceAPI;

    @Test
    public void testGetPxeSambaInfo() throws BusinessException {
        SambaConfigDTO sambaConfigDTO = new SambaConfigDTO();
        sambaConfigDTO.setWriter(new SambaConfigDTO.SambaUser("pxe_writer", "123455"));
        sambaConfigDTO.setIp("172.0.0.1");
        sambaConfigDTO.setPort(445);
        sambaConfigDTO.setServerPath("pxe");
        sambaConfigDTO.setLocalPath("/external_share/pxe");
        sambaConfigDTO.setState(SambaMountState.MOUNTED);
        new Expectations() {
            {
                sambaServiceAPI.getSambaConfig("pxe");
                result = sambaConfigDTO;
            }
        };

        SambaInfoDTO pxeSambaInfo = sambaInfoService.getPxeSambaInfo();
        Assert.assertEquals(pxeSambaInfo.getUserName(), "pxe_writer");
        Assert.assertEquals(pxeSambaInfo.getPassword(), "123455");
        Assert.assertEquals(pxeSambaInfo.getIp(), "172.0.0.1");
        Assert.assertEquals(pxeSambaInfo.getPort(), "445");
        Assert.assertEquals(pxeSambaInfo.getFilePath(), "pxe");
    }

    @Test
    public void testGetPxeSambaInfoWithUnmount() throws BusinessException {
        SambaConfigDTO sambaConfigDTO = new SambaConfigDTO();
        sambaConfigDTO.setWriter(new SambaConfigDTO.SambaUser("pxe_writer", "123455"));
        sambaConfigDTO.setIp("172.0.0.1");
        sambaConfigDTO.setPort(445);
        sambaConfigDTO.setServerPath("pxe");
        sambaConfigDTO.setLocalPath("/external_share/pxe");
        sambaConfigDTO.setState(SambaMountState.UNMOUNT);
        new Expectations() {
            {
                sambaServiceAPI.getSambaConfig("pxe");
                result = sambaConfigDTO;
            }
        };

        try {
            sambaInfoService.getPxeSambaInfo();
        } catch (BusinessException e) {
            Assert.assertEquals(e.getKey(), BusinessKey.RCDC_TERMINAL_SAMBA_UNMOUNT);
        }

    }

}
