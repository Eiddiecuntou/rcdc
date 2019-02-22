package com.ruijie.rcos.rcdc.terminal.module.impl.callback;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbShineMessageResponse;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalSystemUpgradeServiceTx;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月23日
 * 
 * @author ls
 */
@RunWith(JMockit.class)
public class CbbTerminalSystemUpgradeRequestCallBackTest {

    @Tested
    private CbbTerminalSystemUpgradeRequestCallBack callBack;
    
    @Injectable
    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;
    
    @Injectable
    private TerminalSystemUpgradeServiceTx terminalSystemUpgradeServiceTx;
    
    /**
     * 测试success,参数为空
     * @throws Exception 异常
     */
    @Test
    public void testSuccessArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> callBack.success("", new CbbShineMessageResponse()),
                "terminalId 不能为空");
        ThrowExceptionTester.throwIllegalArgumentException(() -> callBack.success("ss", null),
                "TerminalSystemUpgradeRequest 不能为空");
        assertTrue(true);
    }
}
