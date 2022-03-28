package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.TerminalFtpConfigInfo;

/**
 * Description: 终端ftp信息API
 * Copyright: Copyright (c) 2022
 * Company: RuiJie Co., Ltd.
 * Create Time: 2022/3/28 9:41 上午
 *
 * @author zhouhuan
 */
public interface CbbTerminalFtpAPI {

    /**
     * 获取终端ftp配置信息
     * @return ftp配置信息
     */
    TerminalFtpConfigInfo getTerminalFtpConfigInfo();
}
