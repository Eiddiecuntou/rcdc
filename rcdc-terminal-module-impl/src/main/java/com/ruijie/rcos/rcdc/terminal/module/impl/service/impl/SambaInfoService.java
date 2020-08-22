package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.base.sysmanage.module.def.api.SambaServiceAPI;
import com.ruijie.rcos.base.sysmanage.module.def.dto.SambaConfigDTO;
import com.ruijie.rcos.base.sysmanage.module.def.enums.SambaMountState;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.SambaInfoDTO;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    private static final String PXE_SHARE_NAME = "pxe";

    @Autowired
    private SambaServiceAPI sambaServiceAPI;

    /**
     * 获取pxe刷机samba信息
     *
     * @return SambaInfoDTO samba信息
     * @throws BusinessException 业务异常
     */
    public SambaInfoDTO getPxeSambaInfo() throws BusinessException {

        final SambaConfigDTO sambaConfigDTO = sambaServiceAPI.getSambaConfig(PXE_SHARE_NAME);

        if (sambaConfigDTO.getState() == SambaMountState.UNMOUNT) {
            LOGGER.error("samba共享目录[{}]未挂载", PXE_SHARE_NAME);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SAMBA_UNMOUNT, PXE_SHARE_NAME);
        }

        SambaInfoDTO pxeSambaInfoDTO = new SambaInfoDTO();
        pxeSambaInfoDTO.setUserName(sambaConfigDTO.getWriter().getUsername());
        pxeSambaInfoDTO.setPassword(sambaConfigDTO.getWriter().getPassword());
        pxeSambaInfoDTO.setIp(sambaConfigDTO.getIp());
        pxeSambaInfoDTO.setPort(sambaConfigDTO.getPort().toString());
        pxeSambaInfoDTO.setFilePath(sambaConfigDTO.getServerPath());

        return pxeSambaInfoDTO;
    }
}
