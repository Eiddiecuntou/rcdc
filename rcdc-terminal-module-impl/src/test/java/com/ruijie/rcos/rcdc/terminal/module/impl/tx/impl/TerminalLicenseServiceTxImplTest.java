package com.ruijie.rcos.rcdc.terminal.module.impl.tx.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dao.TerminalAuthorizeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.entity.TerminalAuthorizeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:TerminalLicenseServiceTxImpl测试类
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/11/27 11:25 上午
 *
 * @author zhouhuan
 */
@RunWith(SkyEngineRunner.class)
public class TerminalLicenseServiceTxImplTest {

    @Tested
    TerminalLicenseServiceTxImpl terminalLicenseServiceTx;

    @Injectable
    private TerminalBasicInfoDAO basicInfoDAO;

    @Injectable
    private TerminalAuthorizeDAO terminalAuthorizeDAO;


    /**
     * 测试updateAllIDVTerminalAuthedAndUnlimitIDVTerminalAuth方法
     */
    @Test
    public void testUpdateAllIDVTerminalAuthedAndUnlimitIDVTerminalAuth() {
        List<TerminalEntity> terminalEntityList = new ArrayList<>();
        TerminalEntity entity = new TerminalEntity();
        entity.setTerminalId("123");
        entity.setAuthed(Boolean.FALSE);
        terminalEntityList.add(entity);

        new Expectations() {
            {
                basicInfoDAO.findTerminalEntitiesByAuthModeAndAuthed(CbbTerminalPlatformEnums.IDV, Boolean.FALSE);
                result = terminalEntityList;
                basicInfoDAO.save(entity);
            }
        };

        try {
            terminalLicenseServiceTx.updateTerminalAuthedAndUnlimitTerminalAuth(CbbTerminalPlatformEnums.IDV, Constants.TEMINAL_LICENSE_NUM);
        } catch (Exception e) {
            Assert.fail();
        }
        new Verifications() {
            {
                terminalAuthorizeDAO.save((TerminalAuthorizeEntity) any);
                times = 1;
                basicInfoDAO.save(entity);
                times = 1;
            }
        };
    }
}
