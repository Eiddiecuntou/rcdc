package com.ruijie.rcos.rcdc.terminal.module.impl.enums;

/**
 * Description: RCDC发送消息事件给终端（Shine）
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/1
 *
 * @author Jarman
 */
public enum SendTerminalEventEnums {

    /**
     * 关闭终端
     */
    SHUTDOWN_TERMINAL("shutdown_terminal"),

    /**
     * 重启终端
     */
    RESTART_TERMINAL("restart_terminal"),

    /**
     * 修改终端管理员密码
     */
    CHANGE_TERMINAL_PASSWORD("change_terminal_password"),

    /**
     * 终端检测
     */
    DETECT_TERMINAL("detect"),

    /**
     * 收集终端日志
     */
    COLLECT_TERMINAL_LOG("collect_terminal_log"),

    /**
     * 修改终端名称
     */
    MODIFY_TERMINAL_NAME("modify_name"),

    /**
     * 修改终端网络配置
     */
    MODIFY_TERMINAL_NETWORK_CONFIG("modify_network_config"),

    /**
     * 升级终端
     */
    UPGRADE_TERMINAL_SYSTEM("upgrade_system"),

    /**
     * 同步终端Logo
     */
    CHANGE_TERMINAL_LOGO("change_terminal_logo"),

    /**
     * 解除故障
     */
    RELIEVE_FAULT("relieve_fault"),

    /**
     * 清空idv终端数据盘
     */
    CLEAR_DATA("clear_data"),

    /**
     * 同步终端背景
     */
    CHANGE_TERMINAL_BACKGROUND("change_terminal_background"),

    /**
     * 更新离线登录设置
     */
    CHANGE_TERMINAL_OFFLINE_LOGIN_CONFIG("change_offline_login_config");

    private String name;

    SendTerminalEventEnums(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
