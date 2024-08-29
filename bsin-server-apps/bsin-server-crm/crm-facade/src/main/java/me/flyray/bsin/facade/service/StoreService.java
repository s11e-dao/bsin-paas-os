package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.Store;

import java.util.Map;

/**
 * @author bolei
 * @date 2023/6/27 19:48
 * @desc 店铺服务
 */

public interface StoreService {

    /**
     * 商户开通店铺
     */
    public void openStore(Map<String, Object> requestMap);

    /**
     * 删除店铺
     */
    public void delete(Map<String, Object> requestMap);

    /**
     * 修改店铺
     */
    public void edit(Map<String, Object> requestMap);


    /**
     * 分页查询店铺
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);

    /**
     * 查询店铺详情
     */
    public Store getDetail(Map<String, Object> requestMap);

}
