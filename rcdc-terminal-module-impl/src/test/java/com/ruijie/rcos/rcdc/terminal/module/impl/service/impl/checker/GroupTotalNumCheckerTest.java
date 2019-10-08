package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.checker;

import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalGroupDAO;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/9/24
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class GroupTotalNumCheckerTest {

    @Tested
    private GroupTotalNumChecker checker;

    @Injectable
    private TerminalGroupDAO terminalGroupDAO;

    /**
     * 测试分组数超出限制
     */
    @Test
    public void testCheckGroupExceedLimit() {
        new Expectations() {
            {
                terminalGroupDAO.count();
                result = 2000;
            }
        };
        try {
            checker.check(1);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINALGROUP_GROUP_NUM_EXCEED_LIMIT, e.getKey());
        }

        new Verifications() {
            {
                terminalGroupDAO.count();
                times = 1;
            }
        };
    }

    /**
     * 测试分组数未超出限制
     */
    @Test
    public void testCheck() throws BusinessException {
        new Expectations() {
            {
                terminalGroupDAO.count();
                result = 1999;
            }
        };

        checker.check(1);

        new Verifications() {
            {
                terminalGroupDAO.count();
                times = 1;
            }
        };
    }

}
