package com.ruijie.rcos.rcdc.terminal.module.impl.tx.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalDetectionDAO;
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
 * Create Time: 2019年2月26日
 * 
 * @author ls
 */
public class TerminalBasicInfoServiceTxImplTest {

    @Tested
    private TerminalBasicInfoServiceTxImpl serviceTxImpl;
    
    @Injectable
    private TerminalDetectionDAO detectionDAO;

    @Injectable
    private TerminalBasicInfoDAO basicInfoDAO;
    
    /**
     * 测试deleteTerminal，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testDeleteTerminalArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> serviceTxImpl.deleteTerminal(""),
                "terminalId can not be blank");
        assertTrue(true);
    }
    
    /**
     * 测试deleteTerminal，删除失败
     */
    @Test
    public void testDeleteTerminalFail() {
        new Expectations() {
            {
                basicInfoDAO.deleteByTerminalId("1");
                result = 0;
            }
        };
        try {
            serviceTxImpl.deleteTerminal("1");
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL, e.getKey());
        }
        
        new Verifications() {
            {
                basicInfoDAO.deleteByTerminalId("1");
                times = 1;
                detectionDAO.deleteByTerminalId("1");
                times = 0;
            }
        };
    }
    
    /**
     * 测试deleteTerminal，删除失败
     * @throws BusinessException 异常
     */
    @Test
    public void testDeleteTerminal() throws BusinessException {
        new Expectations() {
            {
                basicInfoDAO.deleteByTerminalId("1");
                result = 1;
            }
        };
        serviceTxImpl.deleteTerminal("1");
        new Verifications() {
            {
                basicInfoDAO.deleteByTerminalId("1");
                times = 1;
                detectionDAO.deleteByTerminalId("1");
                times = 1;
            }
        };
    }
}
