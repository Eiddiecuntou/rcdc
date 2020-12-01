package com.ruijie.rcos.rcdc.terminal.module.impl.dto;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalResetEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalWorkModeEnums;

/**
 * 
 * Description: VDI终端组件通用升级版本信息
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月21日
 * 
 * @author nt
 */
public class CommonComponentVersionInfoDTO extends BaseComponentVersionInfoDTO {

    /**
     * 组件文件名
     */
    private String completePackageName;

    /**
     * 组件文件相对路径（相对于安装包）
     */
    private String completePackageNameRelativePath;


    /**
     * 差异包文件名
     */
    private String incrementalPackageName;

    /**
     * 差异包文件相对路径（相对于安装包）
     */
    private String incrementalPackageRelativePath;

    /**
     * 完整组件文件种子下载路径
     */
    private String completeTorrentUrl;

    /**
     * 完整组件文件种子md5
     */
    private String completeTorrentMd5;

    /**
     * 差异包文件种子下载路径
     */
    private String incrementalTorrentUrl;

    /**
     * 差异包文件种子md5
     */
    private String incrementalTorrentMd5;

    /**
     * 差异包文件md5
     */
    private String incrementalPackageMd5;

    /**
     * 差异升级基线版本包名称
     */
    private String basePackageName;

    /**
     * 差异升级基线版本包md5值
     */
    private String basePackageMd5;

    /**
     * 重启标识(NOW/LATER/NOT)
     */
    private CbbTerminalResetEnums restartFlag;

    private CbbTerminalWorkModeEnums[] workModeArr;

    public String getCompletePackageName() {
        return completePackageName;
    }

    public void setCompletePackageName(String completePackageName) {
        this.completePackageName = completePackageName;
    }

    public String getIncrementalPackageName() {
        return incrementalPackageName;
    }

    public void setIncrementalPackageName(String incrementalPackageName) {
        this.incrementalPackageName = incrementalPackageName;
    }

    public String getCompleteTorrentUrl() {
        return completeTorrentUrl;
    }

    public void setCompleteTorrentUrl(String completeTorrentUrl) {
        this.completeTorrentUrl = completeTorrentUrl;
    }

    public String getCompleteTorrentMd5() {
        return completeTorrentMd5;
    }

    public void setCompleteTorrentMd5(String completeTorrentMd5) {
        this.completeTorrentMd5 = completeTorrentMd5;
    }

    public String getIncrementalTorrentUrl() {
        return incrementalTorrentUrl;
    }

    public void setIncrementalTorrentUrl(String incrementalTorrentUrl) {
        this.incrementalTorrentUrl = incrementalTorrentUrl;
    }

    public String getIncrementalTorrentMd5() {
        return incrementalTorrentMd5;
    }

    public void setIncrementalTorrentMd5(String incrementalTorrentMd5) {
        this.incrementalTorrentMd5 = incrementalTorrentMd5;
    }

    public String getIncrementalPackageMd5() {
        return incrementalPackageMd5;
    }

    public void setIncrementalPackageMd5(String incrementalPackageMd5) {
        this.incrementalPackageMd5 = incrementalPackageMd5;
    }

    public CbbTerminalResetEnums getRestartFlag() {
        return restartFlag;
    }

    public void setRestartFlag(CbbTerminalResetEnums restartFlag) {
        this.restartFlag = restartFlag;
    }

    public String getBasePackageName() {
        return basePackageName;
    }

    public void setBasePackageName(String basePackageName) {
        this.basePackageName = basePackageName;
    }

    public String getBasePackageMd5() {
        return basePackageMd5;
    }

    public void setBasePackageMd5(String basePackageMd5) {
        this.basePackageMd5 = basePackageMd5;
    }

    public String getCompletePackageNameRelativePath() {
        return completePackageNameRelativePath;
    }

    public void setCompletePackageNameRelativePath(String completePackageNameRelativePath) {
        this.completePackageNameRelativePath = completePackageNameRelativePath;
    }

    public String getIncrementalPackageRelativePath() {
        return incrementalPackageRelativePath;
    }

    public void setIncrementalPackageRelativePath(String incrementalPackageRelativePath) {
        this.incrementalPackageRelativePath = incrementalPackageRelativePath;
    }

    public CbbTerminalWorkModeEnums[] getWorkModeArr() {
        return workModeArr;
    }

    public void setWorkModeArr(CbbTerminalWorkModeEnums[] workModeArr) {
        this.workModeArr = workModeArr;
    }
}
