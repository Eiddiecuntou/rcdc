package com.ruijie.rcos.rcdc.terminal.module.def.spi;

import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbTerminalOcsAuthChangeRequest;

/**
 * Description: 终端OCS授权变化通知
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/9/26
 *
 * @author zhangsiming
 */
public interface CbbTerminalOcsAuthChangeSPI {

    /**
     * @param changeRequest ocs授权变更通知
     */
    void notifyOcsAuthChange(CbbTerminalOcsAuthChangeRequest changeRequest);
}
