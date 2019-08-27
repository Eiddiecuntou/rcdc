package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectStatisticsDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbDetectDateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectPageRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalDetectResult;
import org.springframework.data.domain.Page;

import java.util.UUID;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/4/26
 *
 * @author nt
 */
public interface TerminalDetectService {

    /**
     * 更新检测信息
     *
     * @param terminalId 终端id
     * @param detectResult 检测结果数据对象
     */
    void updateTerminalDetect(String terminalId, TerminalDetectResult detectResult);

    /**
     * 检测失败
     *
     * @param terminalId 终端id
     */
    void detectFailure(String terminalId);

    /**
     * 保存终端检测记录
     *
     * @param terminalId 终端id
     * @return 终端检测记录
     */
    TerminalDetectionEntity save(String terminalId);

    /**
     * 删除检测记录
     *
     * @param id 检测记录id
     */
    void delete(UUID id);

    /**
     * 获取终端当天的检测记录
     *
     * @param terminalId 终端id
     * @return 当天的终端检测记录，无记录返回null
     */
    TerminalDetectionEntity findInCurrentDate(String terminalId);

    /**
     * 终端检测记录分页查询
     *
     * @param request 分页查询请求参数
     * @return 分页列表
     */
    Page<TerminalDetectionEntity> pageQuery(CbbTerminalDetectPageRequest request);

    /**
     * 获取检测结果
     *
     * @param detectDate 日期
     * @return 检测结果
     */
    CbbTerminalDetectStatisticsDTO getDetectResult(CbbDetectDateEnums detectDate);

    /**
     * 获取最近的终端检测记录
     *
     * @param terminalId 终端id
     * @return 终端检测记录
     */
    CbbTerminalDetectDTO getRecentDetect(String terminalId);

}
