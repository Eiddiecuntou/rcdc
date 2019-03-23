package com.ruijie.rcos.rcdc.terminal.module.impl;

/**
 * Description: 常量
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/31
 *
 * @author Jarman
 */
public interface Constants {
    
    int SUCCESS = 0;
    

    String SYSTEM_TYPE = "rcdc";
    
    String TERMINAL_COMPONENT_UPGRADE_DIRECTION_NAME = "origin";

    /**
     * 终端管理员密码aes加密key值
     */
    String TERMINAL_ADMIN_PASSWORD_AES_KEY = "ADMINPASSWORDKEY";
    
    /**
     * 系统默认编码
     */
    String RCDC_DEFAULT_ENCODE = "UTF-8";

    /**
     * 存放终端日志的目录
     */
    String STORE_TERMINAL_LOG_PATH = "/opt/ftp/terminal/log/";

    /**
     * 终端检测带宽正常标准值,小于等于20Mb为异常
     */
    double TERMINAL_DETECT_BINDWIDTH_NORM = 20;

    /**
     * 终端检测延时正常标准值，大于等于50ms为异常
     */
    int TERMINAL_DETECT_DELAY_NORM = 20;

    /**
     * 丢包率正常标准值(该数值是实际比率乘以100的数值)，大于等于0.1时为异常
     */
    double TERMINAL_DETECT_PACKET_LOSS_RATE = 0.1;

    /** 终端管理员密码全局参数表key */
    String RCDC_TERMINAL_ADMIN_PWD_GLOBAL_PARAMETER_KEY = "terminal_pwd";
    
    
    /** rcdc服务器密码全局参数表key */
    String RCDC_SERVER_IP_GLOBAL_PARAMETER_KEY = "rcdc_server_ip";

    /**
     * 存放终端系统vdi刷机包路径
     */
    String TERMINAL_UPGRADE_ISO_PATH_VDI = "/opt/upgrade/linux_vdi/";
    
    /**
     * 刷机时刷机包mount相对路径
     */
    String MOUNT_RELATE_DIR = "mount_dir/";
    
    /**
     * 存放终端系统idv刷机包路径
     */
    String TERMINAL_UPGRADE_ISO_PATH_IDV = "/opt/upgrade/linux_idv/";

    /**
     * 存放终端系统ota刷机包路径
     */
    String TERMINAL_UPGRADE_ISO_PATH_OTA = "/opt/upgrade/ota/";

    /**
     * 系统刷机包挂载路径
     */
    String TERMINAL_UPGRADE_ISO_MOUNT_PATH = "/opt/upgrade/mount_dir/";

    /**
     * 系统刷机包版本文件路径
     */
    String TERMINAL_UPGRADE_ISO_VERSION_FILE_PATH = "/home/partimag/rainos-img/version";

    /**
     * 系统刷机包镜像文件路径
     */
    String TERMINAL_UPGRADE_ISO_IMG_FILE_PATH = "/home/partimag/";
    
    /**
     * 系统刷机开始状态文件路径
     */
    String TERMINAL_UPGRADE_START_SATTUS_FILE_PATH = "/opt/pxeuser/mac_begin/";
    
    /**
     * 系统刷机成功状态文件路径
     */
    String TERMINAL_UPGRADE_END_SATTUS_FILE_PATH = "/opt/pxeuser/mac_end/";

    /**
     * 系统镜像挂载指令
     */
    String SYSTEM_CMD_MOUNT_UPGRADE_ISO = "mount %s %s";

    /**
     * 系统镜像解除挂载指令
     */
    String SYSTEM_CMD_UMOUNT_UPGRADE_ISO = "umount %s";

    /**
     * 系统刷机包版本文件属性-包类型
     */
    String TERMINAL_UPGRADE_ISO_VERSION_FILE_KEY_PACKAGE_TYPE = "plat";

    /**
     * 系统刷机包版本文件属性-外部版本号
     */
    String TERMINAL_UPGRADE_ISO_VERSION_FILE_KEY_VERSION = "version";

    /**
     * 终端组件升级包路径 
     */
    String TERMINAL_TERMINAL_COMPONET_UPGRADE_PATH = "/opt/upgrade/app/terminal_component/";

    /**
     * 终端组件升级包updatelist文件名
     */
    String TERMINAL_COMPONET_ORIGIN_UPDATE_LIST_FILE_NAME = "/origin/update.list";

    /**
     * 终端检测超时时间（单位:秒）
     */
    int TERMINAL_DETECT_TIMEOUT = 120;
    
    /**
     * 文件后缀名分隔符
     */
    String FILE_SUFFIX_DOT = ".";
    
    /**
     * 终端刷机nfs服务开放目录
     */
    String TERMINAL_SYSTEM_UPGRADE_ISO_NFS_DIR = "/opt/pxeuser";
    
    /**
     * 刷机镜像刷机文件存放路径
     */
    String ISO_IMG_MOUNT_PATH = "/opt/pxeuser/";

}
