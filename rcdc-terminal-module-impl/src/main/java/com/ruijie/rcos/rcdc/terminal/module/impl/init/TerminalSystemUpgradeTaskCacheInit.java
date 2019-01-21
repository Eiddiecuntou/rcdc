package com.ruijie.rcos.rcdc.terminal.module.impl.init;

import com.ruijie.rcos.sk.modulekit.api.bootstrap.SafetySingletonInitializer;

/**
 * 
 * Description: 终端系统升级任务初始化
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月3日
 * 
 * @author nt
 */
public class TerminalSystemUpgradeTaskCacheInit implements SafetySingletonInitializer {

    @Override
    public void safeInit() {
        // TODO 1. 从服务器上对应文件中初始化任务队列  2.队列初始化完成后，判断队列是否为空,若为空，关闭NFS服务 ，读取升级文件中的升级信息，文件路径后福，忠进的描述不一致，待确认
        
    }

}
