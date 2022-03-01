package com.ruijie.rcos.rcdc.terminal.module.impl.auth;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dao.TerminalAuthorizeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.entity.TerminalAuthorizeEntity;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/8/16 15:31
 *
 * @author TING
 */
@Service
public class TerminalLicenseCommonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalLicenseCommonService.class);

    @Autowired
    private TerminalAuthorizeDAO terminalAuthorizeDAO;

    /**
     *  判断终端是否已授权
     *
     * @param terminalId 终端ID
     * @return true 已授权； false 未授权
     */
    public boolean isTerminalAuthed(String terminalId) {
        Assert.hasText(terminalId, "terminalId can not be blank");

        TerminalAuthorizeEntity authorizeEntity = terminalAuthorizeDAO.findByTerminalId(terminalId);
        LOGGER.info("终端[{}]的授权信息为：{}", terminalId, JSON.toJSONString(authorizeEntity));
        return authorizeEntity != null && authorizeEntity.getAuthed();
    }

    /**
     * 判断终端是否是云应用授权
     * @param terminalId 终端ID
     * @return true 已云应用授权；false 未云应用授权
     */
    public boolean isTerminalCvaAuthed(String terminalId) {
        Assert.hasText(terminalId, "terminalId can not be blank");

        TerminalAuthorizeEntity authorizeEntity = terminalAuthorizeDAO.findByTerminalId(terminalId);
        LOGGER.info("终端[{}]的授权信息为：{}", terminalId, JSON.toJSONString(authorizeEntity));
        return authorizeEntity != null && authorizeEntity.getCvaAuthed();
    }

}
