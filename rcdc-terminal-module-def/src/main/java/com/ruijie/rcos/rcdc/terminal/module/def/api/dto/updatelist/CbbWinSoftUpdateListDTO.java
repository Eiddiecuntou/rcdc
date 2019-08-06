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
public class CbbWinSoftUpdateListDTO extends CbbCommonUpdatelistDTO<CbbWinSoftComponentVersionInfoDTO> {

    public CbbWinSoftUpdateListDTO() {
    }

    public CbbWinSoftUpdateListDTO(String version, Integer componentSize) {
        super(version, componentSize);
    }

}
