package com.ruijie.rcos.rcdc.terminal.module.impl.connect;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月7日
 * 
 * @author ls
 */
public class TerminalInfoTest {

    /**
     * 测试TerminalInfo
     */
    @Test
    public void testContrustor() {
        TerminalInfo terminalInfo = new TerminalInfo();
        terminalInfo.setTerminalId("sss");
        terminalInfo.setTerminalIp("192.168.1.2");
        assertEquals("sss", terminalInfo.getTerminalId());
        assertEquals("192.168.1.2", terminalInfo.getTerminalIp());
        
        TerminalInfo terminalInfo2 = new TerminalInfo("fff", "11.12.13.12");
        assertEquals("fff", terminalInfo2.getTerminalId());
        assertEquals("11.12.13.12", terminalInfo2.getTerminalIp());
    }

}
