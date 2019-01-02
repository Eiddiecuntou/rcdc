package com.ruijie.rcos.rcdc.terminal.module.impl.connect;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Description: 初始化业务
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/1/2
 *
 * @author Jarman
 */
@Service
public class TerminalBusinessInitializingBean implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalBusinessInitializingBean.class);

    @Autowired
    private TerminalBasicInfoDAO terminalBasicInfoDAO;

    @Override
    public void afterPropertiesSet() throws Exception {
        initTerminalState();
    }

    /**
     * 把在线状态初始化为离线状态
     */
    private void initTerminalState() {

        List<TerminalEntity> list = terminalBasicInfoDAO.findTerminalEntitiesByState(CbbTerminalStateEnums.ONLINE.name());
        if (CollectionUtils.isEmpty(list)) {
            LOGGER.debug("没有需要初始化的终端状态");
            return;
        }
        LOGGER.debug("存在异常关机导致终端状态不一致的情况，总共有{}台终端状态需要初始化", list.size());
        list.forEach(item ->
                terminalBasicInfoDAO.modifyTerminalState(item.getTerminalId(), item.getVersion(), CbbTerminalStateEnums.OFFLINE)
        );
    }
}

