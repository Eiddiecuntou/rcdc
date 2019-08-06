package com.ruijie.rcos.rcdc.terminal.module.impl.util;

import java.io.*;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/3
 *
 * @author nt
 */
public class DeepCopyUtil {

    /**
     * 深拷贝
     *
     * @param src 来源对象
     * @param <T>
     * @return
     */
    public static <T extends Serializable> T deepCopy(T src) {

        ObjectInputStream in;
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray())) {
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(src);
            in = new ObjectInputStream(byteIn);
            T dest = (T) in.readObject();
            return dest;
        } catch (Exception e) {
            throw new IllegalStateException("深拷贝对象发生异常", e);
        }
    }
}
