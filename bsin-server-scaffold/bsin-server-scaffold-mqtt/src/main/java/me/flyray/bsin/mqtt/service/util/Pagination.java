package me.flyray.bsin.mqtt.service.util;

import lombok.Data;

/**
 * @author ：bolei
 * @date ：Created in 2022/1/14 18:31
 * @description：分类对象
 * @modified By：
 */

@Data
public class Pagination {

    /**
     * 当前页码
     */
    private int pageNum;
    /**
     * 每页数量
     */
    private int pageSize;
    /**
     * 记录总数
     */
    private long totalSize;
    /**
     * 页码总数
     */
    private int totalPages;

}
