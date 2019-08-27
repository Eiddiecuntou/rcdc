package com.ruijie.rcos.rcdc.terminal.module.impl.util;

import java.io.*;

import org.springframework.util.Assert;

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
     * @param <T> 对象类型
     * @return 拷贝对象
     */
    public static <T extends Serializable> T deepCopy(T src) {
        Assert.notNull(src, "src can not be null");

        T cloneObj = null;
        try {
            // 写入字节流
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(src);
            oos.close();

            // 分配内存,写入原始对象,生成新对象
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            // 获取上面的输出字节流
            ObjectInputStream ois = new ObjectInputStream(bais);

            // 返回生成的新对象
            cloneObj = (T) ois.readObject();
            ois.close();
        } catch (Exception e) {
            throw new IllegalStateException("深拷贝对象异常", e);
        }
        return cloneObj;
    }

}
