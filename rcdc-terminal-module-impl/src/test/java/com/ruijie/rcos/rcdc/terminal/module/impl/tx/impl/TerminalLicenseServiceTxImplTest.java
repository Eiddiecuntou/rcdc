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
                basicInfoDAO.findTerminalEntitiesByPlatformAndAuthed(CbbTerminalPlatformEnums.IDV, Boolean.FALSE);
                result = terminalEntityList;
                basicInfoDAO.save(entity);
            }
        };

        try {
            terminalLicenseServiceTx.updateAllIDVTerminalAuthedAndUnlimitIDVTerminalAuth();
        } catch (Exception e) {
            Assert.fail();
        }
        new Verifications() {
            {
                globalParameterAPI.updateParameter(Constants.TEMINAL_LICENSE_NUM, anyString);
                times = 1;
                basicInfoDAO.save(entity);
                times = 1;
            }
        };
    }

    /**
     * 测试updateAllIDVTerminalAuthedAndUnlimitIDVTerminalAuth方法，更新终端授权状态重试失败
     */
    @Test
    public void testUpdateAllIDVTerminalAuthedAndUnlimitIDVTerminalAuthUpdateTermianlAuthStateFail() {
        List<TerminalEntity> terminalEntityList = new ArrayList<>();
        TerminalEntity entity = new TerminalEntity();
        entity.setTerminalId("123");
        entity.setVersion(1);
        entity.setAuthed(Boolean.FALSE);
        terminalEntityList.add(entity);

        new Expectations(entity) {
            {
                basicInfoDAO.findTerminalEntitiesByPlatformAndAuthed(CbbTerminalPlatformEnums.IDV, Boolean.FALSE);
                result = terminalEntityList;
                basicInfoDAO.save(entity);
                result = new Exception("xx");
                entity.setAuthed(Boolean.TRUE);
                basicInfoDAO.findTerminalEntityByTerminalId(withEqual("123"));
                result = entity;
            }
        };
        try {
            terminalLicenseServiceTx.updateAllIDVTerminalAuthedAndUnlimitIDVTerminalAuth();
        } catch (Exception e) {
            Assert.fail();
        }
        new Verifications() {
            {
                globalParameterAPI.updateParameter(Constants.TEMINAL_LICENSE_NUM, anyString);
                times = 1;
                basicInfoDAO.save(entity);
                times = 4;
            }
        };
    }

    /**
     * 测试updateAllIDVTerminalAuthedAndUnlimitIDVTerminalAuth方法，在第2次重试时才成功更新授权状态成功
     */
    @Test
    public void testUpdateAllIDVTerminalAuthedAndUnlimitIDVTerminalAuthUpdateAuthStateSuccessInTheSecondTime() {
        List<TerminalEntity> terminalEntityList = new ArrayList<>();
        TerminalEntity entity = new TerminalEntity();
        entity.setTerminalId("123");
        entity.setVersion(1);
        entity.setAuthed(Boolean.FALSE);
        terminalEntityList.add(entity);

        new Expectations(entity) {
            {
                basicInfoDAO.findTerminalEntitiesByPlatformAndAuthed(CbbTerminalPlatformEnums.IDV, Boolean.FALSE);
                result = terminalEntityList;
                basicInfoDAO.save(entity);
                result = new Exception("xx");
                result = new Exception("xx");
                result = new TerminalEntity();
                entity.setAuthed(Boolean.TRUE);
                basicInfoDAO.findTerminalEntityByTerminalId(withEqual("123"));
                result = entity;
            }
        };
        try {
            terminalLicenseServiceTx.updateAllIDVTerminalAuthedAndUnlimitIDVTerminalAuth();
        } catch (Exception e) {
            Assert.fail();
        }
        new Verifications() {
            {
                globalParameterAPI.updateParameter(Constants.TEMINAL_LICENSE_NUM, anyString);
                times = 1;
                basicInfoDAO.save(entity);
                times = 3;
            }
        };
    }

    @Test
    public void testUpdateAllIDVTerminalUnauthedAndUpdateLicenseNum() {
        List<TerminalEntity> terminalEntityList = new ArrayList<>();
        TerminalEntity entity = new TerminalEntity();
        entity.setTerminalId("123");
        entity.setVersion(1);
        entity.setAuthed(Boolean.FALSE);
        terminalEntityList.add(entity);
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntitiesByPlatformAndAuthed(CbbTerminalPlatformEnums.IDV, Boolean.TRUE);
                result = terminalEntityList;

                globalParameterAPI.updateParameter(Constants.TEMINAL_LICENSE_NUM, "111");
            }
        };

        terminalLicenseServiceTx.updateAllIDVTerminalUnauthedAndUpdateLicenseNum(111);

        new Verifications() {
            {
                basicInfoDAO.findTerminalEntitiesByPlatformAndAuthed(CbbTerminalPlatformEnums.IDV, Boolean.TRUE);
                times = 1;

                globalParameterAPI.updateParameter(Constants.TEMINAL_LICENSE_NUM, "111");
                times = 1;
            }
        };

    }
}