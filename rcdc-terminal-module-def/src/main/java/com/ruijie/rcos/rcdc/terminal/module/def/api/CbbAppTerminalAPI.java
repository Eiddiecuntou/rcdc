package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.sk.base.exception.BusinessException;


/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/15
 *
 * @author nt
 */
public interface CbbAppTerminalAPI {

    /**
     * 获取windows软终端全量包下载路径
     *
     * @return 下载路径
     * @throws BusinessException 业务异常
     */

    String getWindowsAppDownloadUrl() throws BusinessException;
}
