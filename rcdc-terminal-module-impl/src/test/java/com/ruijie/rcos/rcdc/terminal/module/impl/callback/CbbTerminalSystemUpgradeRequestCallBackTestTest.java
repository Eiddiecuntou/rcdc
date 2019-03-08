package com.ruijie.rcos.rcdc.terminal.module.impl.callback;

import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbShineMessageResponse;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalSystemUpgradeServiceTx;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月4日
 * 
 * @author ls
 */
public class CbbTerminalSystemUpgradeRequestCallBackTestTest {

    @Tested
    private CbbTerminalSystemUpgradeRequestCallBack callBack;
    
    @Injectable
    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;

    @Injectable
    private TerminalSystemUpgradeServiceTx terminalSystemUpgradeServiceTx;
    
    /**
     * 测试success，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testSuccessArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> callBack.success("", new CbbShineMessageResponse<>()),
                "terminalId 不能为空");
        ThrowExceptionTester.throwIllegalArgumentException(() -> callBack.success("1", null),
                "TerminalSystemUpgradeRequest 不能为空");
        assertTrue(true);
    }
    
    /**
     * 测试success，终端不支持升级,并且没有正在升级中的终端
     * @throws BusinessException 异常
     */
    @Test
    public void testSuccessIsUnsupportAndNotUpgradingTerminal() throws BusinessException {
        String terminalId = "1";
        CbbShineMessageResponse<Object> msg = new CbbShineMessageResponse<>();
        msg.setCode(CbbTerminalSystemUpgradeRequestCallBack.UNSUPPORTED);
        
        new Expectations() {
            {
                systemUpgradeTerminalDAO.findByTerminalIdAndState(terminalId, CbbSystemUpgradeStateEnums.UPGRADING);
                result = new ArrayList<>();
            }
        };
        callBack.success(terminalId, msg);
        
        new Verifications() {
            {
                systemUpgradeTerminalDAO.findByTerminalIdAndState(terminalId, CbbSystemUpgradeStateEnums.UPGRADING);
                times = 1;
                terminalSystemUpgradeServiceTx.modifySystemUpgradeTerminalState((UUID) any, terminalId, CbbSystemUpgradeStateEnums.UNSUPPORTED);
                times = 0;
            }
        };
    }
    
    /**
     * 测试success，终端升级失败,并且有正在升级中的终端
     * @throws BusinessException 异常
     */
    @Test
    public void testSuccessIsFailAndHasUpgradingTerminal() throws BusinessException {
        String terminalId = "1";
        CbbShineMessageResponse<Object> msg = new CbbShineMessageResponse<>();
        msg.setCode(CbbTerminalSystemUpgradeRequestCallBack.FAILURE);
        List<TerminalSystemUpgradeTerminalEntity> upgradingTerminalList = new ArrayList<>();
        TerminalSystemUpgradeTerminalEntity terminalEntity = new TerminalSystemUpgradeTerminalEntity();
        upgradingTerminalList.add(terminalEntity);
        new Expectations() {
            {
                systemUpgradeTerminalDAO.findByTerminalIdAndState(terminalId, CbbSystemUpgradeStateEnums.UPGRADING);
                result = upgradingTerminalList;
            }
        };
        callBack.success(terminalId, msg);
        
        new Verifications() {
            {
                systemUpgradeTerminalDAO.findByTerminalIdAndState(terminalId, CbbSystemUpgradeStateEnums.UPGRADING);
                times = 1;
                terminalSystemUpgradeServiceTx.modifySystemUpgradeTerminalState(
                        terminalEntity.getSysUpgradeId(), terminalId, CbbSystemUpgradeStateEnums.FAIL);
                times = 1;
            }
        };
    }
    
    /**
     * 测试success，终端升级失败,并且修改刷机终端状态异常
     * @throws BusinessException 异常
     */
    @Test
    public void testSuccessIsFailAndHasBusinessException() throws BusinessException {
        String terminalId = "1";
        CbbShineMessageResponse<Object> msg = new CbbShineMessageResponse<>();
        msg.setCode(CbbTerminalSystemUpgradeRequestCallBack.FAILURE);
        List<TerminalSystemUpgradeTerminalEntity> upgradingTerminalList = new ArrayList<>();
        TerminalSystemUpgradeTerminalEntity terminalEntity = new TerminalSystemUpgradeTerminalEntity();
        upgradingTerminalList.add(terminalEntity);
        new Expectations() {
            {
                systemUpgradeTerminalDAO.findByTerminalIdAndState(terminalId, CbbSystemUpgradeStateEnums.UPGRADING);
                result = upgradingTerminalList;
                terminalSystemUpgradeServiceTx.modifySystemUpgradeTerminalState(
                        terminalEntity.getSysUpgradeId(), terminalId, CbbSystemUpgradeStateEnums.FAIL);
                result = new BusinessException("key");
            }
        };
        callBack.success(terminalId, msg);
        
        new Verifications() {
            {
                systemUpgradeTerminalDAO.findByTerminalIdAndState(terminalId, CbbSystemUpgradeStateEnums.UPGRADING);
                times = 1;
                terminalSystemUpgradeServiceTx.modifySystemUpgradeTerminalState(
                        terminalEntity.getSysUpgradeId(), terminalId, CbbSystemUpgradeStateEnums.FAIL);
                times = 1;
            }
        };
    }

    /**
     * 测试timeout，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testTimeoutArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> callBack.timeout(""),
                "terminalId 不能为空");
        assertTrue(true);
    }
}
