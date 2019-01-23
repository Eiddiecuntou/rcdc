package com.ruijie.rcos.rcdc.terminal.module.impl.entity;

import javax.persistence.*;

import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.DetectStateEnums;
import com.ruijie.rcos.sk.base.i18n.LocaleI18nResolver;
import java.util.Date;
import java.util.UUID;

/**
 * Description: 终端检测实体类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/30
 *
 * @author Jarman
 */
@Entity
@Table(name = "t_cbb_terminal_detection")
public class TerminalDetectionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String terminalId;

    /**
     * ip冲突结果，0 不冲突，1 冲突，如果有冲突则ipConflictMac字段保存冲突的mac地址，否则为空值
     */
    private Integer ipConflict;

    /**
     * ip冲突的mac地址，未冲突时为空值
     */
    private String ipConflictMac;

    /**
     * 是否可访问外网，0不能访问，1可访问
     */
    private Integer accessInternet;

    /**
     * 带宽大小
     */
    private Double bandwidth;

    /**
     * 丢包率
     */
    private Double packetLossRate;

    /**
     * 网络时延
     */
    private Integer networkDelay;

    /**
     * 检测时间
     */
    private Date detectTime;

    /**
     * 终端检测状态
     */
    @Enumerated(EnumType.STRING)
    private DetectStateEnums detectState;

    /**
     * 检测失败原因
     */
    private String detectFailMsg;

    @Version
    private Integer version;

    /**
     *  对象转换
     * @param detectDTO 设值对象
     */
    public void convertTo(CbbTerminalDetectDTO detectDTO) {
        Assert.notNull(detectDTO, "detect dto can not be null");

        //状态信息需确认
        detectDTO.setTerminalId(terminalId);
        detectDTO.setAccessInternet(accessInternet);
        detectDTO.setBandwidth(bandwidth);
        detectDTO.setDelay(networkDelay);
        detectDTO.setIpConflict(ipConflict);
        detectDTO.setPacketLossRate(packetLossRate);
        detectDTO.setDetectTime(detectTime);
        CbbTerminalDetectDTO.DetectState state = detectDTO.getCheckState();;
        state.setState(detectState.name());
        state.setMessage(LocaleI18nResolver.resolve(detectState.getName()));
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public Integer getIpConflict() {
        return ipConflict;
    }

    public void setIpConflict(Integer ipConflict) {
        this.ipConflict = ipConflict;
    }

    public String getIpConflictMac() {
        return ipConflictMac;
    }

    public void setIpConflictMac(String ipConflictMac) {
        this.ipConflictMac = ipConflictMac;
    }

    public Integer getAccessInternet() {
        return accessInternet;
    }

    public void setAccessInternet(Integer accessInternet) {
        this.accessInternet = accessInternet;
    }

    public String getDetectFailMsg() {
        return detectFailMsg;
    }

    public void setDetectFailMsg(String detectFailMsg) {
        this.detectFailMsg = detectFailMsg;
    }

    public Double getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Double bandwidth) {
        this.bandwidth = bandwidth;
    }

    public Double getPacketLossRate() {
        return packetLossRate;
    }

    public void setPacketLossRate(Double packetLossRate) {
        this.packetLossRate = packetLossRate;
    }

    public Date getDetectTime() {
        return detectTime;
    }

    public void setDetectTime(Date detectTime) {
        this.detectTime = detectTime;
    }

    public Integer getNetworkDelay() {
        return networkDelay;
    }

    public void setNetworkDelay(Integer networkDelay) {
        this.networkDelay = networkDelay;
    }

    public DetectStateEnums getDetectState() {
        return detectState;
    }

    public void setDetectState(DetectStateEnums detectState) {
        this.detectState = detectState;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getVersion() {
        return version;
    }

}