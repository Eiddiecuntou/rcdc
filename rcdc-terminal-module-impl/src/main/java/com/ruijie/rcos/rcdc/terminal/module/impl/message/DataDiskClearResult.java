package com.ruijie.rcos.rcdc.terminal.module.impl.message;

/**
 * Description:
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/12/8 14:41
 *
 * @author conghaifeng
 */
public class DataDiskClearResult {

    /**
     * 数据盘清空结果，"0”表示成功，“1”表示出现异常未能成功清空数据盘
     */
    private Integer result;

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }
}
