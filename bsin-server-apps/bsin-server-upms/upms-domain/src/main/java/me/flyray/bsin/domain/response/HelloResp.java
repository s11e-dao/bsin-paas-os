package me.flyray.bsin.domain.response;

import java.io.Serializable;

/**
 * @author ：bolei
 * @date ：Created in 2021/11/30 16:30
 * @description：hello world 返回类
 * @modified By：
 */

public class HelloResp implements Serializable {

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
