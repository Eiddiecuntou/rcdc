package com.ruijie.rcos.rcdc.terminal.module.impl.message;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/2/12
 *
 * @author Jarman
 */
public class SyncServerTimeResponse {

    private int code;

    private Content content;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    /**
     * 消息体
     */
    static class Content {
        private Long serverTime;

        public Long getServerTime() {
            return serverTime;
        }

        public void setServerTime(Long serverTime) {
            this.serverTime = serverTime;
        }
    }

    /**
     * 构建消息对象
     * @return 返回SyncServerTimeResponse
     */
    public static SyncServerTimeResponse build() {
        SyncServerTimeResponse response = new SyncServerTimeResponse();
        response.setCode(ShineResponseCode.SUCCESS);
        Content content = new Content();
        long currentTime = System.currentTimeMillis();
        content.setServerTime(currentTime);
        response.setContent(content);
        return response;
    }
}
