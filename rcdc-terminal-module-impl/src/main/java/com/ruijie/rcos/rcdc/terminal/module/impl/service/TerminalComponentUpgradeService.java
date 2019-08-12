package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalVersionResultDTO;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import org.springframework.lang.Nullable;

/**
 * 
 * Description: 终端组件升级
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月3日
 * 
 * @author nt
 */
public interface TerminalComponentUpgradeService {

    /**
     * 获取终端组件升级版本信息
     * 
     * @param terminalEntity 终端信息
     * @param validateMd5 组件包校验md5值
     * @return 组件升级版本信息
     * @throws BusinessException 业务异常
     */
    TerminalVersionResultDTO getVersion(TerminalEntity terminalEntity, @Nullable String validateMd5) throws BusinessException;
}
