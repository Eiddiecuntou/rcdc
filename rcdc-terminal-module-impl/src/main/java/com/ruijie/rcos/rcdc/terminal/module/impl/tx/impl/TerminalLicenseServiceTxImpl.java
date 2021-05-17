package com.ruijie.rcos.rcdc.terminal.module.impl.tx.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalLicenseServiceTx;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;

/**
 * Description: TerminalLicenseServiceTx接口实现类
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/11/27 10:32 上午
 *
 * @author zhouhuan
 */
@Service
public class TerminalLicenseServiceTxImpl implements TerminalLicenseServiceTx {

    private static Logger LOGGER = LoggerFactory.getLogger(TerminalLicenseServiceTxImpl.class);

    private static final int FAIL_TRY_COUNT = 3;

    @Autowired
    private TerminalBasicInfoDAO terminalBasicInfoDAO;

    @Autowired
    private GlobalParameterAPI globalParameterAPI;

    @Override
    public void updateTerminalAuthedAndUnlimitTerminalAuth(CbbTerminalPlatformEnums platform, String licenseKey) {
        Assert.notNull(platform, "platform can not be empty");
        Assert.hasText(licenseKey, "licenseKey can not be empty");
        List<TerminalEntity> terminalEntityList = terminalBasicInfoDAO.findTerminalEntitiesByAuthModeAndAuthed(platform, Boolean.FALSE);
        updateTerminalAuthState(terminalEntityList, Boolean.TRUE);

        globalParameterAPI.updateParameter(licenseKey, String.valueOf(Constants.TERMINAL_AUTH_DEFAULT_NUM));
    }

    @Override
    public void updateTerminalUnauthedAndUpdateLicenseNum(CbbTerminalPlatformEnums platform, String licenseKey, Integer licenseNum) {
        Assert.notNull(platform, "platform can not be empty");
        Assert.hasText(licenseKey, "licenseKey can not be empty");
        Assert.notNull(licenseNum, "licenseNum can not null");
        List<TerminalEntity> terminalEntityList = terminalBasicInfoDAO.findTerminalEntitiesByAuthModeAndAuthed(platform, Boolean.TRUE);
        updateTerminalAuthState(terminalEntityList, Boolean.FALSE);

        globalParameterAPI.updateParameter(licenseKey, String.valueOf(licenseNum));
    }

    private void updateTerminalAuthState(List<TerminalEntity> terminalEntityList, Boolean authed) {
        terminalEntityList.stream().forEach(terminalEntity -> {
            if (authed.equals(terminalEntity.getAuthed())) {
                LOGGER.info("更新终端授权状态为{}时，发现终端此时的授权状态已经是{}。无须更新终端状态", authed, authed);
                return;
            }
            terminalEntity.setAuthed(authed);
            try {
                terminalBasicInfoDAO.save(terminalEntity);
            } catch (Exception e) {
                LOGGER.error("更新终端[" + terminalEntity.getTerminalId() + "]的授权状态为" + authed + "失败！", e);
                retryUpdateAuthed(terminalEntity.getTerminalId(), authed);
            }
        });
    }

    /**
     * 重试更新终端授权状态
     * 
     * @param terminalId 终端id
     * @param authed 是否授权
     */
    private void retryUpdateAuthed(String terminalId, Boolean authed) {
        int retry = 0;

        while (retry++ < FAIL_TRY_COUNT) {
            LOGGER.warn("第{}次重试更新终端[{}]的授权状态为{}", retry, terminalId, authed);
            TerminalEntity entity = terminalBasicInfoDAO.findTerminalEntityByTerminalId(terminalId);
            if (authed.equals(entity.getAuthed())) {
                LOGGER.info("重试更新终端授权状态为{}时，发现终端此时的授权状态已经是{}。无须重试更新终端状态", authed, authed);
                return;
            }
            entity.setAuthed(authed);
            try {
                terminalBasicInfoDAO.save(entity);
            } catch (Exception e) {
                LOGGER.error("第" + retry + "次重试更新终端[" + terminalId + "]的授权状态为" + authed + "失败！", e);
                continue;
            }
            LOGGER.info("成功通过重试更新终端[{}]的授权状态为：{}", terminalId, authed);
            return;
        }

        LOGGER.error("重试{}次，仍然无法成功将终端[{}]的授权状态更新为：{}", FAIL_TRY_COUNT, terminalId, authed);
    }
}
