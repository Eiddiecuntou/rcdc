package com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist;

/**
 * 
 * Description: linuxVDI终端组件升级版本信息
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月14日
 * 
 * @author nt
 */
public class CbbWinAppUpdateListDTO extends CbbCommonUpdatelistDTO<CbbWinAppComponentVersionInfoDTO> {

    public CbbWinAppUpdateListDTO() {
    }

    public CbbWinAppUpdateListDTO(String version, Integer componentSize) {
        super(version, componentSize);
    }

}
