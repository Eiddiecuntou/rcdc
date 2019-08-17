package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalDetectRecordAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectStatisticsDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectPageRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectResultRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbDetectInfoResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbDetectResultResponse;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalDetectService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Stream;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/14
 *
 * @author nt
 */
public class CbbTerminalDetectRecordAPIImpl implements CbbTerminalDetectRecordAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(CbbTerminalOperatorAPIImpl.class);

    @Autowired
    private TerminalDetectService detectService;

    @Autowired
    private TerminalBasicInfoDAO terminalBasicInfoDAO;


    @Override
    public DefaultPageResponse<CbbTerminalDetectDTO> listDetect(CbbTerminalDetectPageRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        Page<TerminalDetectionEntity> page = detectService.pageQuery(request);
        if (page.getNumberOfElements() == 0) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("detect page query returns 0 element, param: ", JSON.toJSONString(request));
            }
            return buildEmptyResponse(page.getTotalElements());
        }

        List<TerminalDetectionEntity> detectionList = page.getContent();
        int size = detectionList.size();
        CbbTerminalDetectDTO[] detectDTOArr = new CbbTerminalDetectDTO[size];

        Stream.iterate(0, i -> i + 1).limit(size).forEach(i -> {
            CbbTerminalDetectDTO detectDTO = new CbbTerminalDetectDTO();
            TerminalDetectionEntity detectionEntity = detectionList.get(i);
            detectionEntity.convertTo(detectDTO);
            setThreshold(detectDTO);
            TerminalEntity terminalEntity = terminalBasicInfoDAO.findTerminalEntityByTerminalId(detectionEntity.getTerminalId());
            if (terminalEntity != null) {
                detectDTO.setIp(terminalEntity.getIp());
                detectDTO.setTerminalName(terminalEntity.getTerminalName());
                detectDTO.setMac(terminalEntity.getMacAddr());
            }
            detectDTOArr[i] = detectDTO;
        });

        return DefaultPageResponse.Builder.success(page.getSize(), (int) page.getTotalElements(), detectDTOArr);
    }

    @Override
    public CbbDetectInfoResponse getRecentDetect(CbbTerminalIdRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        CbbTerminalDetectDTO detectInfo = detectService.getRecentDetect(request.getTerminalId());
        CbbDetectInfoResponse response = new CbbDetectInfoResponse();
        response.setDetectInfo(detectInfo);
        return response;
    }

    private void setThreshold(CbbTerminalDetectDTO detectDTO) {
        detectDTO.setBandwidthThreshold(Constants.TERMINAL_DETECT_BINDWIDTH_NORM);
        detectDTO.setPacketLossRateThreshold(Constants.TERMINAL_DETECT_PACKET_LOSS_RATE);
        detectDTO.setDelayThreshold(Constants.TERMINAL_DETECT_DELAY_NORM);
    }

    /**
     * 构建空列表返回参数
     *
     * @param total 总数
     * @return 空列表响应
     */
    private DefaultPageResponse<CbbTerminalDetectDTO> buildEmptyResponse(long total) {
        DefaultPageResponse<CbbTerminalDetectDTO> emptyResp = new DefaultPageResponse<CbbTerminalDetectDTO>();
        emptyResp.setItemArr(new CbbTerminalDetectDTO[0]);
        emptyResp.setTotal(total);
        emptyResp.setStatus(Response.Status.SUCCESS);
        return emptyResp;
    }

    @Override
    public CbbDetectResultResponse getDetectResult(CbbTerminalDetectResultRequest request) {
        Assert.notNull(request, "request can not be null");

        CbbTerminalDetectStatisticsDTO result = detectService.getDetectResult(request.getDetectDate());
        CbbDetectResultResponse resp = new CbbDetectResultResponse(result);
        resp.setStatus(Response.Status.SUCCESS);
        return resp;
    }
}
