package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.MerchantBusinessType;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.MerchantBusinessTypeService;
import me.flyray.bsin.infrastructure.mapper.MerchantBusinessTypeMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@ShenyuDubboService(path = "/merchantBusinessType", timeout = 6000)
@ApiModule(value = "merchantBusinessType")
@Service
public class MerchantBusinessTypeServiceImpl implements MerchantBusinessTypeService {

    @Autowired
    private MerchantBusinessTypeMapper merchantBusinessTypeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @ShenyuDubboClient(value = "/add")
    public boolean add(Map<String, Object> requestMap) {
        MerchantBusinessType businessType = BsinServiceContext.getReqBodyDto(MerchantBusinessType.class, requestMap);
        return this.saveOrUpdateBusinessType(businessType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @ShenyuDubboClient("/edit")
    public boolean edit(Map<String, Object> requestMap) {
        MerchantBusinessType businessType = BsinServiceContext.getReqBodyDto(MerchantBusinessType.class, requestMap);
        return this.saveOrUpdateBusinessType(businessType);
    }

    /**
     * 保存/编辑
     *
     * @param businessType
     * @return
     */
    private boolean saveOrUpdateBusinessType(MerchantBusinessType businessType) {
        // 校验
        this.saveBeforeValid(businessType);
        boolean isUpdate = businessType.getSerialNo() != null;
        
        if (isUpdate) {
            MerchantBusinessType existingType = merchantBusinessTypeMapper.selectById(businessType.getSerialNo());
            if (existingType == null) {
                throw new BusinessException(ResponseCode.DATA_NOT_EXIST);
            }
            return merchantBusinessTypeMapper.updateById(businessType) > 0;
        } else {
            // 新增时设置默认值
            businessType.setSerialNo(BsinSnowflake.getId());
            businessType.setStatus("1"); // 默认启用
            // 如果前端没传parentNo，则设置默认值
            if (StringUtils.isEmpty(businessType.getParentNo())) {
                businessType.setParentNo("-1");
            }
            businessType.setTenantId(LoginInfoContextHelper.getTenantId());
            return merchantBusinessTypeMapper.insert(businessType) > 0;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @ShenyuDubboClient("/delete")
    public boolean delete(String categoryId) {
        // 检查是否有子分类
        boolean hasChildren = merchantBusinessTypeMapper.selectCount(new LambdaQueryWrapper<>(MerchantBusinessType.class)
                .eq(MerchantBusinessType::getParentNo, categoryId)) > 0;
        if (hasChildren) {
            throw new BusinessException("BUSINESS_TYPE_HAD_CHILD", "该业态下存在子分类，无法删除");
        }
        
        return merchantBusinessTypeMapper.deleteById(categoryId) > 0;
    }

    @Override
    @ShenyuDubboClient("/getList")
    public List<Tree<String>> getList(Map<String, Object> requestMap) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        
        LambdaQueryWrapper<MerchantBusinessType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MerchantBusinessType::getTenantId, tenantId)
               .eq(StringUtils.isNotEmpty(MapUtils.getString(requestMap, "name")), 
                   MerchantBusinessType::getName, MapUtils.getString(requestMap, "name"))
               .eq(StringUtils.isNotEmpty(MapUtils.getString(requestMap, "status")), 
                   MerchantBusinessType::getStatus, MapUtils.getString(requestMap, "status"))
               .orderByAsc(MerchantBusinessType::getParentNo)
               .orderByAsc(MerchantBusinessType::getSort);
        
        List<MerchantBusinessType> businessTypeList = merchantBusinessTypeMapper.selectList(wrapper);
        
        // 构建树形结构
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        treeNodeConfig.setIdKey("serialNo");
        treeNodeConfig.setParentIdKey("parentNo");
        treeNodeConfig.setWeightKey("sort");
        treeNodeConfig.setNameKey("name");
        
        List<Tree<String>> list = TreeUtil.build(businessTypeList, "-1", treeNodeConfig, (businessType, treeNode) -> {
            treeNode.setId(businessType.getSerialNo());
            treeNode.setName(businessType.getName());
            treeNode.setParentId(businessType.getParentNo());
            treeNode.setWeight(businessType.getSort());
            treeNode.putExtra("icon", businessType.getIcon());
            treeNode.putExtra("status", businessType.getStatus());
        });
        
        return Optional.ofNullable(list).orElseGet(Collections::emptyList);
    }

    @Override
    @ShenyuDubboClient("/admin/getList")
    public List<Tree<String>> getAdminList(Map<String, Object> requestMap) {
        return getList(requestMap);
    }

    /**
     * 保存前校验
     *
     * @param businessType
     */
    private void saveBeforeValid(MerchantBusinessType businessType) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        
        // 校验同级业态下是否存在重复名称
        long count = merchantBusinessTypeMapper.selectCount(new LambdaQueryWrapper<MerchantBusinessType>()
                .eq(MerchantBusinessType::getTenantId, tenantId)
                .eq(StringUtils.isNotEmpty(businessType.getParentNo()), 
                    MerchantBusinessType::getParentNo, businessType.getParentNo())
                .eq(MerchantBusinessType::getName, businessType.getName())
                .ne(StringUtils.isNotEmpty(businessType.getSerialNo()), 
                    MerchantBusinessType::getSerialNo, businessType.getSerialNo()));
        
        if (count > 0) {
            throw new BusinessException("BUSINESS_TYPE_EXIST", "同级业态下已存在相同名称");
        }
    }

}
