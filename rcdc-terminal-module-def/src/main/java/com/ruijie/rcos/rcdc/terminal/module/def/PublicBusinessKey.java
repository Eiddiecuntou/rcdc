package com.ruijie.rcos.rcdc.terminal.module.def;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020年03月25日
 *
 * @author xiejian
 */
public interface PublicBusinessKey {

    String RCDC_TERMINALGROUP_GROUP_NUM_EXCEED_LIMIT = "rcdc_terminalgroup_group_num_exceed_limit";

    String RCDC_TERMINALGROUP_SUB_GROUP_NUM_EXCEED_LIMIT = "rcdc_terminalgroup_sub_group_num_exceed_limit";

    String RCDC_TERMINALGROUP_GROUP_NAME_DUPLICATE = "rcdc_terminalgroup_group_name_duplicate";

    String RCDC_DELETE_TERMINAL_GROUP_SUB_GROUP_HAS_DUPLICATION_WITH_MOVE_GROUP =
            "rcdc_delete_terminal_group_sub_group_has_duplication_with_move_group";

    /**
     * 终端断开连接，处于离线状态
     */
    String RCDC_TERMINAL_OFFLINE = "rcdc_terminal_offline";

    /** 终端离线状态不能进行关闭操作 */
    String RCDC_TERMINAL_OFFLINE_CANNOT_SHUTDOWN = "rcdc_terminal_offline_cannot_shutdown";
}
