package com.ruijie.rcos.rcdc.terminal.module.impl.service;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/10/19
 *
 * @author nting
 */
public interface TerminalComponentInitService {

    /**
     * 初始化Linux VDI组件升级包
     */
    void initLinuxVDI();

    /**
     * 初始化Android VDI组件升级包
     */
    void initAndroidVDI();

    /**
     * 初始化Linux IDV组件升级包
     */
    void initLinuxIDV();

}
