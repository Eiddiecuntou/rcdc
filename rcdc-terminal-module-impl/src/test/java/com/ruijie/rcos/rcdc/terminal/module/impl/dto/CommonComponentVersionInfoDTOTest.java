package com.ruijie.rcos.rcdc.terminal.module.impl.dto;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalResetEnums;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/9/4
 *
 * @author hs
 */
@RunWith(SkyEngineRunner.class)
public class CommonComponentVersionInfoDTOTest {

    /**
     * 测试GetterAndSetter
     */
    @Test
    public void testGetterAndSetter() {
        CommonComponentVersionInfoDTO dto = new CommonComponentVersionInfoDTO();
        dto.setBasePackageMd5("basePackageMd5");
        dto.setBasePackageName("basePackageName");
        dto.setCompletePackageName("completePackageName");
        dto.setCompleteTorrentUrl("completeTorrentUrl");
        dto.setCompleteTorrentMd5("completeTorrentMd5");
        dto.setIncrementalPackageName("incrementalPackageName");
        dto.setIncrementalPackageMd5("incrementalPackageMd5");
        dto.setIncrementalTorrentUrl("incrementalTorrentUrl");
        dto.setIncrementalTorrentMd5("incrementalTorrentMd5");
        dto.setRestartFlag(CbbTerminalResetEnums.LATER);
        assertEquals("basePackageMd5", dto.getBasePackageMd5());
        assertEquals("basePackageName", dto.getBasePackageName());
        assertEquals("completePackageName", dto.getCompletePackageName());
        assertEquals("completeTorrentUrl", dto.getCompleteTorrentUrl());
        assertEquals("completeTorrentMd5", dto.getCompleteTorrentMd5());
        assertEquals("incrementalPackageName", dto.getIncrementalPackageName());
        assertEquals("incrementalPackageMd5", dto.getIncrementalPackageMd5());
        assertEquals("incrementalTorrentUrl", dto.getIncrementalTorrentUrl());
        assertEquals("incrementalTorrentMd5", dto.getIncrementalTorrentMd5());

    }
}
