package com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask;

import org.springframework.util.Assert;
import java.util.*;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/1/18
 *
 * @author Jarman
 */
public class TerminalIdMappingUtils {

    /**
     * terminalId 映射UUID
     *
     * @param terminalIdArr terminalId数组
     * @return 返回映射Map
     */
    public static Map<UUID, String> mapping(String[] terminalIdArr) {
        Assert.notNull(terminalIdArr, "terminalIdArr不能为null");
        Assert.state(terminalIdArr.length > 0, "terminalIdArr大小不能为0");
        Map<UUID, String> idMap = new HashMap<>(terminalIdArr.length);
        for (String terminalId : terminalIdArr) {
            idMap.put(UUID.randomUUID(), terminalId);
        }
        return idMap;
    }

    /**
     * 抽取UUID
     *
     * @param idMap id映射对象
     * @return 返回UUID数组
     */
    public static UUID[] extractUUID(Map<UUID, String> idMap) {
        Assert.notNull(idMap, "idMap不能为null");
        List<UUID> uuidList = new ArrayList<>(idMap.size());
        idMap.forEach((k, v) -> {
            uuidList.add(k);
        });
        UUID[] idArr = new UUID[uuidList.size()];
        return uuidList.toArray(idArr);
    }
}
