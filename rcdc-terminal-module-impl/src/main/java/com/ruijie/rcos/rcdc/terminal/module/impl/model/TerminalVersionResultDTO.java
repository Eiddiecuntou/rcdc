package com.ruijie.rcos.rcdc.terminal.module.impl.model;

/**
 * 
 * Description: 终端组件升级请求结果信息
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月3日
 * 
 * @author nt
 */
public class TerminalVersionResultDTO<T> {

    private Integer result;

    private T updatelist;

    public TerminalVersionResultDTO() {
        
    }

    public TerminalVersionResultDTO(Integer result, T updatelist) {
        this.result = result;
        this.updatelist = updatelist;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public T getUpdatelist() {
        return updatelist;
    }

    public void setUpdatelist(T updatelist) {
        this.updatelist = updatelist;
    }
}
