package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.hciadapter.module.def.api.CloudPlatformMgmtAPI;
import com.ruijie.rcos.rcdc.hciadapter.module.def.dto.ClusterVirtualIpDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.SambaInfoDTO;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultRequest;
import com.ruijie.rcos.sk.modulekit.api.comm.DtoResponse;

/**
 *
 * Description: samba信息缓存
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年12月30日
 *
 * @author nt
 */
@Service
public class SambaInfoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SambaInfoService.class);

    @Value("${samba.pxe.username}")
    private String sambaPxeUserName;

    @Value("${samba.pxe.password}")
    private String sambaPxePassword;

    @Value("${samba.pxe.port}")
    private String sambaPxePort;

    @Value("${samba.pxe.server_dir}")
    private String sambaPxeDir;

    @Autowired
    private CloudPlatformMgmtAPI cloudPlatformMgmtAPI;

    /**
     *  获取pxe刷机samba信息
     *
     * @return SambaInfoDTO samba信息
     * @throws BusinessException 业务异常
     */
    public SambaInfoDTO getPxeSambaInfo() throws BusinessException {
        SambaInfoDTO pxeSambaInfoDTO = new SambaInfoDTO();
        pxeSambaInfoDTO.setUserName(sambaPxeUserName);
        pxeSambaInfoDTO.setPassword(sambaPxePassword);
        pxeSambaInfoDTO.setIp(obtainVirtualIp());
        pxeSambaInfoDTO.setPort(sambaPxePort);
        pxeSambaInfoDTO.setFilePath(sambaPxeDir);

        return pxeSambaInfoDTO;
    }

    private String obtainVirtualIp() throws BusinessException {
        DtoResponse<ClusterVirtualIpDTO> response = cloudPlatformMgmtAPI.getClusterVirtualIp(new DefaultRequest());
        Assert.notNull(response, "response can not be null");
        Assert.notNull(response.getDto(), "ClusterVirtualIpDTO can not be null");

        return response.getDto().getClusterVirtualIpIp();
    }

}
