package me.flyray.bsin.domain.request;

import java.io.Serializable;

/**
 * @author ：bolei
 * @date ：Created in 2021/11/30 16:28
 * @description：hello world 请求类
 * @modified By：
 */

public class HelloReq implements Serializable {

    /**
     * 根据雪花算法生成唯一ID
     */
    public String id;

    /**
     * 名称
     */
    public String name;

    /**
     * 描述
     */
    public String desc;

}
