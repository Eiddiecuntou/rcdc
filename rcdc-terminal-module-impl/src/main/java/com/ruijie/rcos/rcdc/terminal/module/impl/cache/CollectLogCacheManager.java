package com.ruijie.rcos.rcdc.terminal.module.impl.cache;

import com.ruijie.rcos.rcdc.terminal.module.impl.enums.CollectLogStateEnums;
import org.springframework.util.Assert;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: 缓存收集日志过程的状态，
 * 前端下载请求时，根据状态来显示界面和操作逻辑
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/6
 *
 * @author Jarman
 */
@Service
public class CollectLogCacheManager {

    private static final Map<String, CollectLogCache> COLLECT_LOG_CACHE_MAP = new ConcurrentHashMap<>();

    /**
     * 添加缓存，接收到请求后覆盖原来的缓存信息
     * 状态标识为正在收集中
     *
     * @param terminalId 终端id
     * @return 返回对应终端缓存对象
     */
    public CollectLogCache addCache(String terminalId) {
        Assert.hasText(terminalId, "terminalId不能为空");
        CollectLogCache cache = new CollectLogCache();
        cache.setState(CollectLogStateEnums.DOING);
        COLLECT_LOG_CACHE_MAP.put(terminalId, cache);
        return cache;
    }

    /**
     * 移除缓存
     *
     * @param terminalId 终端id
     */
    public void removeCache(String terminalId) {
        Assert.hasText(terminalId, "terminalId不能为空");
        COLLECT_LOG_CACHE_MAP.remove(terminalId);
    }

    /**
     * 更新缓存
     *
     * @param terminalId 终端id
     * @param state      缓存转态
     */
    public void updateState(String terminalId, CollectLogStateEnums state) {
        Assert.hasText(terminalId, "terminalId不能为空");
        Assert.notNull(state, "CollectLogStateEnums不能为null");
        updateState(terminalId, state, "");
    }

    /**
     * 更新缓存
     *
     * @param terminalId  终端id
     * @param state       终端状态
     * @param logFileName 日志上传成功后记录日志文件名称
     */
    public void updateState(String terminalId, CollectLogStateEnums state, String logFileName) {
        Assert.hasText(terminalId, "terminalId不能为空");
        Assert.notNull(state, "CollectLogStateEnums不能为null");
        Assert.hasText(logFileName, "logFileName不能为空");
        CollectLogCache cache = COLLECT_LOG_CACHE_MAP.get(terminalId);
        if (cache == null) {
            cache = new CollectLogCache();
        }
        cache.setState(state);
        cache.setLogFileName(logFileName);
        COLLECT_LOG_CACHE_MAP.put(terminalId, cache);
    }

    /**
     * 获取对应终端缓存
     *
     * @param terminalId 终端id
     * @return 返回对应终端缓存对象
     */
    public CollectLogCache getCache(String terminalId) {
        Assert.hasText(terminalId, "terminalId不能为空");
        return COLLECT_LOG_CACHE_MAP.get(terminalId);
    }

    /**
     * 获取缓存集合
     *
     * @return 返回集合对象
     */
    public Map<String, CollectLogCache> getCollectLogCaches() {
        return COLLECT_LOG_CACHE_MAP;
    }

}
