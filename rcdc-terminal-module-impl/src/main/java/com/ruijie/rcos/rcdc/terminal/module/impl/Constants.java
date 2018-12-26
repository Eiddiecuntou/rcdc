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
     * 系统默认编码
     */
    String RCDC_DEFAULT_ENCODE = "UTF-8";

    /**
     * 存放终端日志的目录
     */
    String STORE_TERMINAL_LOG_PATH = "/opt/ftp/terminal/log/";
    
    /**
     * 存放终端系统vdi刷机包路径
     */
    String TERMINAL_UPGRADE_ISO_PATH_VDI = "/opt/upgrade/linux_vdi/";

    /**
     * 存放终端系统idv刷机包路径
     */
    String TERMINAL_UPGRADE_ISO_PATH_IDV = "/opt/upgrade/linux_idv/";

    /**
     * 存放终端系统ota刷机包路径
     */
    String TERMINAL_UPGRADE_ISO_PATH_OTA = "/opt/upgrade/ota/";
  
    /**
     * TODO 系统刷机包挂载路径  
     */
    String TERMINAL_UPGRADE_ISO_MOUNT_PATH = "";

    /**
     * TODO 系统刷机包版本文件路径   
     */
    String TERMINAL_UPGRADE_ISO_VERSION_FILE_PATH = "";
    
    /**
     * TODO 系统刷机包镜像文件路径   
     */
    String TERMINAL_UPGRADE_ISO_IMG_FILE_PATH = "/home/partimag/";
    
    /**
     * 系统镜像挂载指令
     */
    String SYSTEM_CMD_MOUNT_UPGRADE_ISO = "mount %s %s";

    /**
     * 系统镜像解除挂载指令
     */
    String SYSTEM_CMD_UMOUNT_UPGRADE_ISO = "umount %s %s";
    
    /**
     * 系统刷机包版本文件属性-包类型
     */
    String TERMINAL_UPGRADE_ISO_VERSION_FILE_KEY_PACKAGE_TYPE = "plat";
    
    /**
     * 系统刷机包版本文件属性-外部版本号
     */
    String TERMINAL_UPGRADE_ISO_VERSION_FILE_KEY_VERSION = "version";
    
    /**
     * 终端组件升级包路径 TODO
     */
    String TERMINAL_COMPONENT_UPGRADE_PACKAGE_PATH = "";

    /**
     * 终端组件升级包updatelist文件名
     */
    String TERMINAL_COMPONET_UPDATE_LIST_FILE_NAME = "update.list";


}
