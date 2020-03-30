package com.ruijie.rcos.rcdc.terminal.module.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalGroupMgmtAPI;
import java.util.UUID;

/**
 * Description: 常量
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/31
 *
 * @author Jarman
 */
public interface Constants {

    String SYSTEM_TYPE = "rcdc";

    /**
     * 终端管理员密码aes加密key值
     */
    String TERMINAL_ADMIN_PASSWORD_AES_KEY = "ADMINPASSWORDKEY";

    /**
     * samba刷机aes加密key值
     */
    String TERMINAL_PXE_SAMBA_PASSWORD_AES_KEY = "PXESAMBAPASSWORD";

    /**
     * 存放终端日志的目录
     */
    String STORE_TERMINAL_LOG_PATH = "/opt/ftp/terminal/log/";

    /**
     * 终端检测带宽正常标准值,小于等于20Mb为异常
     */
    double TERMINAL_DETECT_BINDWIDTH_NORM = 20;

    /**
     * 终端检测延时正常标准值，大于等于20ms为异常
     */
    double TERMINAL_DETECT_DELAY_NORM = 20;

    /**
     * 丢包率正常标准值(该数值是实际比率乘以100的数值)，大于等于0.1时为异常
     */
    double TERMINAL_DETECT_PACKET_LOSS_RATE = 0.1;

    int TERMINAL_DETECT_ABNORMAL_COMMON_CODE = -1;

    /** 终端管理员密码全局参数表key */
    String RCDC_TERMINAL_ADMIN_PWD_GLOBAL_PARAMETER_KEY = "terminal_pwd";


    /** rcdc服务器密码全局参数表key */
    String RCDC_CLUSTER_VIRTUAL_IP_GLOBAL_PARAMETER_KEY = "cluster_virtual_ip";

    /**
     * 存在终端系统刷机包的根路径
     */
    String TERMINAL_UPGRADE_PACKAGE_PATH = "/opt/upgrade/";

    /**
     * 存放终端系统vdi刷机包路径
     */
    String TERMINAL_UPGRADE_ISO_PATH_VDI = "/opt/upgrade/linux_vdi/";


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
    String PXE_SAMBA_LINUX_VDI_UPGRADE_BEGIN_FILE_PATH = "/opt/samba/pxeuser/linux_vdi/mac_begin/";

    /**
     * 系统刷机成功状态文件路径
     */
    String PXE_SAMBA_LINUX_VDI_UPGRADE_SUCCESS_FILE_PATH = "/opt/samba/pxeuser/linux_vdi/mac_end/";

    /**
     * linux vdi刷机ISO文件路径
     */
    String PXE_SAMBA_LINUX_VDI_ISO_PATH = "/opt/samba/pxeuser/linux_vdi/";

    /**
     *  linux vdi刷机ISO文件路径
     */

    String PXE_SAMBA_PACKAGE_PATH = "/opt/samba/pxeuser/";

    /**
     *  linux vdi刷机samba相对路径
     */
    String PXE_ISO_SAMBA_LINUX_VDI_RELATE_PATH = "/linux_vdi/";

    /**
     * 系统镜像挂载指令
     */
    String SYSTEM_CMD_MOUNT_UPGRADE_ISO = "mount %s %s";

    /**
     * 系统镜像解除挂载指令
     */
    String SYSTEM_CMD_UMOUNT_UPGRADE_ISO = "umount %s";

    /**
     * ISO升级包MD5校验
     */
    String SYSTEM_CMD_CHECK_ISO_MD5 = "checkisomd5 %s";

    /**
     * 系统刷机包版本文件属性-包类型
     */
    String TERMINAL_UPGRADE_ISO_VERSION_FILE_KEY_PACKAGE_TYPE = "plat";

    /**
     * 系统刷机包版本文件属性-外部版本号
     */
    String TERMINAL_UPGRADE_ISO_VERSION_FILE_KEY_VERSION = "version";

    /**
     * OTA 包的MD5值
     */
    String TERMINAL_UPGRADE_OTA_VERSION_FILE_KEY_PACKAGE_MD5 = "MD5";

    /**
     * OTA包平台类型
     */
    String TERMINAL_UPGRADE_OTA_PLATFORM_TYPE = "RK3188";

    /**
     * OTA包版本号
     */
    String TERMINAL_UPGRADE_OTA_VERSION_FILE_KEY_PACKAGE_VERSION = "VER";

    /**
     * OTA包版本号
     */
    String TERMINAL_UPGRADE_OTA_VERSION_FILE_KEY_PACKAGE_PLAT = "PLAT";

    /**
     * 出厂OTA包存放目录
     */
    String TERMINAL_UPGRADE_OTA = "/data/web/terminal/ota/android_vdi";

    /**
     * 解压后OTA包存放目录
     */
    String TERMINAL_UPGRADE_OTA_PACKAGE = "/opt/upgrade/ota/package/";

    /**
     * OTA包中version存放目录
     */
    String TERMINAL_UPGRADE_OTA_PACKAGE_VERSION = "/opt/upgrade/ota/package/version";

    /**
     * OTA包中zip包存放目录
     */
    String TERMINAL_UPGRADE_OTA_PACKAGE_ZIP = "/opt/upgrade/ota/package/Rainrcd.zip";

    /**
     * OTA包种子文件保存目录
     */
    String TERMINAL_UPGRADE_OTA_SEED_FILE = "/opt/ftp/terminal/ota/seed/";

    /**
     * 终端组件升级包路径
     */
    String LINUX_VDI_TERMINAL_TERMINAL_COMPONET_UPGRADE_TEMP_PATH = "/opt/upgrade/app/terminal_component/terminal_vdi_linux/temp";

    /**
     * Android VDI 终端组件升级包路径
     */
    String ANDROID_VDI_TERMINAL_TERMINAL_COMPONET_UPGRADE_TEMP_PATH = "/opt/upgrade/app/terminal_component/terminal_vdi_android/temp";

    /**
     * Linux IDV 终端组件升级包路径
     */
    String LINUX_IDV_TERMINAL_TERMINAL_COMPONET_UPGRADE_TEMP_PATH = "/opt/upgrade/app/terminal_component/terminal_idv_linux/temp";


    /**
     * 终端检测超时时间（单位:秒）
     */
    int TERMINAL_DETECT_TIMEOUT = 120;

    /**
     * 文件后缀名分隔符
     */
    String FILE_SUFFIX_DOT = ".";

    /** 终端默认分组UUID */
    UUID DEFAULT_TERMINAL_GROUP_UUID = CbbTerminalGroupMgmtAPI.DEFAULT_TERMINAL_GROUP_ID;

    /** 分组最大层级 */
    int TERMINAL_GROUP_MAX_LEVEL = 9;

    /** 终端组最大数量 */
    int TERMINAL_GROUP_MAX_GROUP_NUM = 2000;

    /** 终端组最大子分组数量 */
    int TERMINAL_GROUP_MAX_SUB_GROUP_NUM = 200;

}
