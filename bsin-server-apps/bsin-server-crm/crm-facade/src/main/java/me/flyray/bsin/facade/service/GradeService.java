package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.Grade;
import me.flyray.bsin.facade.response.GradeVO;

import java.util.List;
import java.util.Map;

/**
* @author bolei
* @description 针对表【crm_grade(客户等级划分配置)】的数据库操作Service
* @createDate 2023-09-19 23:06:17
*/

public interface GradeService {

    /**
     * 添加
     */
    public Grade add(Map<String, Object> requestMap);

    /**
     * 删除
     */
    public void delete(Map<String, Object> requestMap);


    /**
     * 编辑
     */
    public Grade edit(Map<String, Object> requestMap);


    /**
     * 商户下所有
     */
    public List<Grade> getList(Map<String, Object> requestMap);

    /**
     * 商户下分页所有
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);
    
    /**
     * 查询等级、权益、条件详情
     */
    public GradeVO getDetail(Map<String, Object> requestMap);

    /**
     * 查询等级详情
     */
    public Grade getGradeDetail(Map<String, Object> requestMap);

    public List<Grade> getGradeListByIds(Map<String, Object> requestMap);

    public List<?> getGradeAndMemberList(Map<String, Object> requestMap);

}
