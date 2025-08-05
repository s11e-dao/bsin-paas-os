package me.flyray.bsin.facade.service;

import cn.hutool.core.lang.tree.Tree;
import jakarta.validation.constraints.NotBlank;
import me.flyray.bsin.domain.entity.MerchantBusinessType;
import me.flyray.bsin.validate.AddGroup;
import me.flyray.bsin.validate.EditGroup;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

public interface MerchantBusinessTypeService {

    /**
     * 新增
     * @param requestMap 商品分类信息
     * @return
     */
    @Validated({AddGroup.class})
    boolean add(Map<String, Object> requestMap);


    /**
     * 编辑
     * @param requestMap 商品分类信息
     * @return
     */
    @Validated({EditGroup.class})
    boolean edit(Map<String, Object> requestMap);


    /**
     * 根据ID删除
     * @param categoryId 商品分类ID
     * @return
     */
    boolean delete(@NotBlank(message = "分类ID不能为空") String categoryId);


    /**
     * 获取分类列表
     * @param requestMap
     * @return
     */
    List<Tree<String>> getTree(Map<String, Object> requestMap);


    /**
     * 获取分类列表
     * @param requestMap
     * @return
     */
    List<MerchantBusinessType> getList(Map<String, Object> requestMap);

    /**
     *  获取分类列表 -管理端
     * @param requestMap
     * @return
     */
    List<Tree<String>> getAdminList(Map<String, Object> requestMap);

}
