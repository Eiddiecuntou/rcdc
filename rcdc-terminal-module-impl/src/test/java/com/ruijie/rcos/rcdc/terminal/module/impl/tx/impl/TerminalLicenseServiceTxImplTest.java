package com.ruijie.rcos.rcdc.terminal.module.impl.tx.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;
import java.util.ArrayList;
import java.util.List;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

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
    private GlobalParameterAPI globalParameterAPI;


    /**
     * 测试updateIDVTerminalAuthStateAndLicenseNum方法
     */
    @Test
    public void testUpdateIDVTerminalAuthStateAndLicenseNum() {
        List<TerminalEntity> terminalEntityList = new ArrayList<>();
        TerminalEntity entity = new TerminalEntity();
        entity.setTerminalId("123");
        entity.setVersion(1);
        terminalEntityList.add(entity);

        new Expectations() {
            {
                basicInfoDAO.findTerminalEntitiesByPlatformAndAuthed(CbbTerminalPlatformEnums.IDV, Boolean.TRUE);
                result = terminalEntityList;
                basicInfoDAO.modifyAuthed(withEqual("123"), anyInt, Boolean.FALSE);
                result = 1;
            }
        };
        try {
            terminalLicenseServiceTx.updateAllIDVTerminalUnauthedAndUpdateLicenseNum(5);
        } catch (Exception e) {
            Assert.fail();
        }
        new Verifications() {
            {
                globalParameterAPI.updateParameter(Constants.TEMINAL_LICENSE_NUM, anyString);
                times = 1;
                basicInfoDAO.modifyAuthed(withEqual("123"), 1, Boolean.FALSE);
                times = 1;
            }
        };
    }

    /**
     * 测试updateIDVTerminalAuthStateAndLicenseNum方法，更新终端授权状态重试失败
     */
    @Test
    public void testUpdateIDVTerminalAuthStateAndLicenseNumUpdateTermianlAuthStateFail() {
        List<TerminalEntity> terminalEntityList = new ArrayList<>();
        TerminalEntity entity = new TerminalEntity();
        entity.setTerminalId("123");
        entity.setVersion(1);
        terminalEntityList.add(entity);

        new Expectations() {
            {
                basicInfoDAO.findTerminalEntitiesByPlatformAndAuthed(CbbTerminalPlatformEnums.IDV, Boolean.TRUE);
                result = terminalEntityList;
                basicInfoDAO.modifyAuthed(withEqual("123"), anyInt, Boolean.FALSE);
                result = 0;
                basicInfoDAO.findTerminalEntityByTerminalId(withEqual("123"));
                result = entity;
            }
        };
        try {
            terminalLicenseServiceTx.updateAllIDVTerminalUnauthedAndUpdateLicenseNum(5);
        } catch (Exception e) {
            Assert.fail();
        }
        new Verifications() {
            {
                globalParameterAPI.updateParameter(Constants.TEMINAL_LICENSE_NUM, anyString);
                times = 1;
                basicInfoDAO.modifyAuthed(withEqual("123"), 1, Boolean.FALSE);
                times = 4;
            }
        };
    }

    /**
     * 测试updateIDVTerminalAuthStateAndLicenseNum方法，在第2次重试时才成功更新授权状态成功
     */
    @Test
    public void testUpdateIDVTerminalAuthStateAndLicenseNumUpdateAuthStateSuccessInTheSecondTime() {
        List<TerminalEntity> terminalEntityList = new ArrayList<>();
        TerminalEntity entity = new TerminalEntity();
        entity.setTerminalId("123");
        entity.setVersion(1);
        terminalEntityList.add(entity);

        new Expectations() {
            {
                basicInfoDAO.findTerminalEntitiesByPlatformAndAuthed(CbbTerminalPlatformEnums.IDV, Boolean.TRUE);
                result = terminalEntityList;
                basicInfoDAO.modifyAuthed(withEqual("123"), anyInt, Boolean.FALSE);
                result = 0;
                result = 0;
                result = 1;
                basicInfoDAO.findTerminalEntityByTerminalId(withEqual("123"));
                result = entity;
            }
        };
        try {
            terminalLicenseServiceTx.updateAllIDVTerminalUnauthedAndUpdateLicenseNum(5);
        } catch (Exception e) {
            Assert.fail();
        }
        new Verifications() {
            {
                globalParameterAPI.updateParameter(Constants.TEMINAL_LICENSE_NUM, anyString);
                times = 1;
                basicInfoDAO.modifyAuthed(withEqual("123"), 1, Boolean.FALSE);
                times = 3;
            }
        };
    }
}