package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbDownLoadUrlResponse;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultRequest;
import com.ruijie.rcos.sk.modulekit.api.tx.NoRollback;

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
     * @param  request 请求参数
     * @return 下载路径
     * @throws BusinessException 业务异常
     */
    @NoRollback
    CbbDownLoadUrlResponse getWindowsAppDownloadUrl(DefaultRequest request) throws BusinessException;
}
