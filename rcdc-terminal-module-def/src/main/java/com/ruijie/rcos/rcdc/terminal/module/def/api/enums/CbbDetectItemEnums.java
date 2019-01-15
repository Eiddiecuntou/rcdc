package com.ruijie.rcos.rcdc.terminal.module.def.api.enums;

import org.springframework.util.Assert;

/**
 * Description: 终端检测项
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/1
 *
 * @author Jarman
 */
public enum CbbDetectItemEnums {

    /**
     * ip冲突检测
     */
    IP_CONFILCT("ipConflict"),

    /**
     * 带宽检测
     */
    BANDWIDTH("bandwidth"),

    /**
     * 互联网连通检测
     */
    ACCESS_INTERNET("accessInternet"),

    /**
     * 丢包率
     */
    PACKET_LOSS_RATE("packetLossRate"),

    /**
     * 时延
     */
    DELAY("delay");

    private String name;

    CbbDetectItemEnums(String name) {
        this.name = name;
    }

    /**
     * 判断是否包含字符串匹配的枚举类型
     * 
     * @param name 名称
     * @return 是否包含
     */
    public static boolean contains(String name) {
        Assert.hasText(name, "name can not be null");

        CbbDetectItemEnums[] itemArr = values();
        for (CbbDetectItemEnums item : itemArr) {
            if (name.equals(item.getName())) {
                return true;
            }
        }
        return false;
    }


    public String getName() {
        return name;
    }

}
