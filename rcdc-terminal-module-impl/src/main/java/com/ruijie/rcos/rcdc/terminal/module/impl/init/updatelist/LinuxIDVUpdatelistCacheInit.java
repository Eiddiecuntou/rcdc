package com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist;

import com.ruijie.rcos.rcdc.terminal.module.impl.dto.CommonUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import org.springframework.stereotype.Service;

/**
 * Description: linux idv终端updatelist缓存初始化
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/30
 *
 * @author hs
 */
@Service
public class LinuxIDVUpdatelistCacheInit extends AbstractUpdatelistCacheInitTemplate<CommonUpdateListDTO> {

    private static final String UPDATE_LIST_PATH = "/opt/upgrade/app/terminal_component/terminal_idv_linux/origin/update.list";

    /**
     * 获取updatelist文件路径
     *
     * @return updatelist文件路径
     */
    @Override
    protected String getUpdateListPath() {
        return UPDATE_LIST_PATH;
    }

    /**
     * 补充updatelist信息
     *
     * @param updatelist updatelist信息
     */
    @Override
    protected void fillUpdateList(CommonUpdateListDTO updatelist) {

    }

    /**
     * 获取终端类型
     *
     * @return 终端类型
     */
    @Override
    protected CbbTerminalTypeEnums getTerminalType() {
        return CbbTerminalTypeEnums.IDV_LINUX;
    }
}
