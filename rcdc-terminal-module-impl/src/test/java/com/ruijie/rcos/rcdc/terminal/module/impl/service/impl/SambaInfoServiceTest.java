package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.ruijie.rcos.rcdc.hciadapter.module.def.api.CloudPlatformMgmtAPI;
import com.ruijie.rcos.rcdc.hciadapter.module.def.dto.ClusterVirtualIpDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.SambaInfoDTO;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultRequest;
import com.ruijie.rcos.sk.modulekit.api.comm.DtoResponse;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;

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
    private CloudPlatformMgmtAPI cloudPlatformMgmtAPI;

    @Injectable
    private String sambaPxeUserName = "userName";

    @Injectable
    private String sambaPxePassword = "pwd";

    @Injectable
    private String sambaPxePort = "123";

    @Injectable
    private String sambaPxeDir = "/aa/bb";

    @Test
    public void testGetPxeSambaInfo() throws BusinessException {
        ClusterVirtualIpDTO vipDTO = new ClusterVirtualIpDTO();
        vipDTO.setClusterVirtualIpIp("172.1.1.0");
        DtoResponse<ClusterVirtualIpDTO> response = DtoResponse.success(vipDTO);

        new Expectations() {
            {
                cloudPlatformMgmtAPI.getClusterVirtualIp((DefaultRequest) any);
                result = response;
            }
        };

        SambaInfoDTO pxeSambaInfo = sambaInfoService.getPxeSambaInfo();

        SambaInfoDTO expectedDTO = new SambaInfoDTO();
        expectedDTO.setUserName("userName");
        expectedDTO.setPassword("pwd");
        expectedDTO.setIp("172.1.1.0");
        expectedDTO.setPort("123");
        expectedDTO.setFilePath("/aa/bb");

        assertEquals(expectedDTO, pxeSambaInfo);
    }

}
