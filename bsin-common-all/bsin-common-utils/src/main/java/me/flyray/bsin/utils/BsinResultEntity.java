package me.flyray.bsin.utils;



import com.baomidou.mybatisplus.core.metadata.IPage;

import org.apache.shenyu.plugin.api.result.DefaultShenyuEntity;

import java.io.Serializable;

import lombok.Data;

/**
 * @author HLW
 **/
public class BsinResultEntity<T> extends DefaultShenyuEntity {

    /**
     * 成功
     */
    public static final int SUCCESS = 0;

    /**
     * 失败
     */
    public static final int FAIL = 1;


    /**
     * 分页信息
     */
    private Pagination pagination;


    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public BsinResultEntity(Integer code, String message, Object data) {
        super(code, message, data);
    }


    public static <T> BsinResultEntity<T> ok() {
        return restResult(null, SUCCESS, "操作成功");
    }

    public static <T> BsinResultEntity<T> ok(T data) {
        return restResult(data, SUCCESS, "操作成功");
    }

    public static <T> BsinResultEntity<T> ok(String msg) {
        return restResult(null, SUCCESS, msg);
    }

    public static <T> BsinResultEntity<T> ok(String msg, T data) {
        return restResult(data, SUCCESS, msg);
    }

    public static <T> BsinResultEntity<T> fail() {
        return restResult(null, FAIL, "操作失败");
    }

    public static <T> BsinResultEntity<T> fail(String msg) {
        return restResult(null, FAIL, msg);
    }

    public static <T> BsinResultEntity<T> fail(T data) {
        return restResult(data, FAIL, "操作失败");
    }

    public static <T> BsinResultEntity<T> fail(String msg, T data) {
        return restResult(data, FAIL, msg);
    }

    public static <T> BsinResultEntity<T> fail(int code, String msg) {
        return restResult(null, code, msg);
    }

    private static <T> BsinResultEntity<T> restResult(T data, int code, String msg) {
        return new BsinResultEntity<>(code, msg, data);
    }

    public static <T> Boolean isError(BsinResultEntity<T> ret) {
        return !isSuccess(ret);
    }

    public static <T> Boolean isSuccess(BsinResultEntity<T> ret) {
        return BsinResultEntity.SUCCESS == ret.getCode();
    }


    public static <T> BsinResultEntity<T> ok(IPage<?> page){
        BsinResultEntity<T> ret = new BsinResultEntity<>(SUCCESS,"操作成功",page.getRecords());
        Pagination pagination = new Pagination();
        pagination.setPageNum(page.getCurrent());
        pagination.setPageSize(page.getPages());
        pagination.setTotalSize(page.getTotal());
        ret.setPagination(pagination);
        return ret;
    }


    @Data
    private static class Pagination implements Serializable {

        /**
         * 当前页码
         */
        private long pageNum = 1;
        /**
         * 每页数量
         */
        private long pageSize = 10;
        /**
         * 记录总数
         */
        private long totalSize;
    }

}
