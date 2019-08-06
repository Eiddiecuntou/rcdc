package com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbCommonUpdatelistDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.sk.base.filesystem.common.FileUtils;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/1
 *
 * @author nt
 */
public abstract class AbstractUpdatelistCacheInitTemplate<T extends CbbCommonUpdatelistDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractUpdatelistCacheInitTemplate.class);

    public final void init() {
        String filePath = getUpdateListPath();
        File updateListFile = new File(filePath);
        if (!updateListFile.isFile()) {
            LOGGER.debug("updatelist file not exist or not a file, the file path is {}", filePath);
            return;
        }

        String updateListContent = readUpdateListContent(updateListFile);
        if (StringUtils.isBlank(updateListContent)) {
            LOGGER.debug("获取updatelist信息失败，请检查updatelist文件是否正确，updatelist文件路径：{}", filePath);
            return;
        }

        T updatelist = JSON.parseObject(updateListContent, getTClass());
        putInCache(updatelist);

        // 完成初始化后将updatelist缓存状态更新为false
        cacheInitFinished();
        LOGGER.info("finish init updatelist...");


    }

    /**
     * 读取updatelist的内容
     *
     * @param updateListFile updatelist文件
     * @return
     */
    private String readUpdateListContent(File updateListFile) {
        String updatelistStr = "";
        try {
            updatelistStr = FileUtils.readFileToString(updateListFile, Charset.forName("UTF-8"));
        } catch (IOException e) {
            LOGGER.error("read updatelist file error", e);
        }

        return updatelistStr;
    }

    /**
     * 获取泛型对相应的Class对象
     *
     * @return
     */
    public Class<T> getTClass() {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        return (Class) type.getActualTypeArguments()[0];
    }

    /**
     * 获取updatelist文件路径
     *
     * @return
     */
    protected abstract String getUpdateListPath();

    /**
     * 将组件信息存入缓存
     *
     * @param updatelist 组件信息
     */
    protected void putInCache(T updatelist) {
        if (updatelist == null || CollectionUtils.isEmpty(updatelist.getComponentList())) {
            LOGGER.debug("updatelist is null, upgrade file is incorrect");
            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("updatelist : " + JSON.toJSONString(updatelist));
        }

        fillUpdateList(updatelist);
        updatelist.setComponentSize(updatelist.getComponentList().size());

        // 将组件升级updatelist按终端类型，存入缓存中
        Map<CbbTerminalTypeEnums, T> updatelistCache = getUpdateListCacheManager();
        updatelistCache.put(getTerminalType(), updatelist);
    }

    /**
     * 初始化updatelist缓存完成
     */
    protected abstract void cacheInitPre();

    /**
     * 初始化updatelist缓存完成
     */
    protected abstract void cacheInitFinished();

    protected abstract Map<CbbTerminalTypeEnums, T> getUpdateListCacheManager();

    protected abstract void fillUpdateList(T updatelist);

    protected abstract CbbTerminalTypeEnums getTerminalType();

}
