package com.ruijie.rcos.rcdc.terminal.module.impl.tx.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalLicenseServiceTx;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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
    public void updateIDVTerminalAuthStateAndLicenseNum(Integer licenseNum, Boolean expectAuthState, Boolean updateAuthState) {
        Assert.notNull(licenseNum, "licenseNum can not null");
        Assert.notNull(expectAuthState, "expectAuthState can not be null");
        Assert.notNull(updateAuthState, "updateAuthState can not be null");

        List<TerminalEntity> terminalEntityList = terminalBasicInfoDAO
            .findTerminalEntityByPlatformAndAuthed(CbbTerminalPlatformEnums.IDV, expectAuthState);
        terminalEntityList.stream().forEach(terminalEntity -> {
            int affectedRows = terminalBasicInfoDAO
                .modifyAuthed(terminalEntity.getTerminalId(), terminalEntity.getVersion(), updateAuthState);
            if (affectedRows == 0) {
                retryUpdateAuthed(terminalEntity.getTerminalId(), updateAuthState);
            }
        });

        globalParameterAPI.updateParameter(Constants.TEMINAL_LICENSE_NUM, String.valueOf(licenseNum));
    }

    /**
     * 重试更新终端授权状态
     * @param terminalId 终端id
     * @param authed 是否授权
     */
    private void retryUpdateAuthed(String terminalId, Boolean authed) {
        int retry = 0;

        while (retry++ < FAIL_TRY_COUNT) {
            LOGGER.warn("第{}次重试更新终端[{}]的授权状态为：{}", retry, terminalId, authed);
            TerminalEntity entity = terminalBasicInfoDAO.findTerminalEntityByTerminalId(terminalId);
            int affectedRows = terminalBasicInfoDAO.modifyAuthed(terminalId, entity.getVersion(), authed);
            if (affectedRows != 0) {
                LOGGER.info("成功通过重试更新终端[{}]的授权状态为：{}", terminalId, authed);
                return;
            }
        }

        LOGGER.error("重试{}次，仍然无法成功将终端[{}]的授权状态更新为：{}", FAIL_TRY_COUNT, terminalId, authed);
    }
}
