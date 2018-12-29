package com.ruijie.rcos.rcdc.terminal.module.impl;

/**
 * Description: 常量接口
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/31
 *
 * @author Jarman
 */
public interface Constants {

    String SYSTEM_TYPE = "rcdc";

    /**
     * 存放终端日志的目录
     */
    String STORE_TERMINAL_LOG_PATH = "/opt/ftp/terminal/log/";

    /**
     * 终端检测带宽正常标准值,小于等于20Mb为异常
     */
    int TERMINAL_DETECT_BINDWIDTH_NORM = 20;
    
    /**
     * 终端检测延时正常标准值，大于等于50ms为异常
     */
    int TERMINAL_DETECT_DELAY_NORM = 50;
    
    /**
     * 丢包率正常标准值(该数值是实际比率乘以100的数值)，大于等于0.1时为异常
     */
    double TERMINAL_DETECT_PACKET_LOSS_RATE = 0.1;
}
