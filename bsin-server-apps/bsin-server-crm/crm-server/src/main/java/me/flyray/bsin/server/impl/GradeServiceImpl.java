package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.*;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.response.GradeVO;
import me.flyray.bsin.facade.service.GradeService;
import me.flyray.bsin.facade.service.TokenParamService;
import me.flyray.bsin.infrastructure.mapper.*;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.biz.AccountBiz;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static me.flyray.bsin.constants.ResponseCode.*;

/**
 * @author bolei
 * @description 针对表【crm_grade(客户等级划分配置)】的数据库操作Service实现
 * @createDate 2023-09-19 23:06:17
 */

@Slf4j
@ShenyuDubboService(path = "/grade", timeout = 6000)
@ApiModule(value = "grade")
@Service
public class GradeServiceImpl implements GradeService {

    @Autowired
    private GradeMapper gradeMapper;
    @Autowired
    private MemberGradeMapper memberGradeMapper;
    @Autowired
    private ConditionRelationMapper conditionRelationshipMapper;
    @Autowired
    private EquityRelationMapper equityRelationshipMapper;
    @Autowired
    private EquityMapper equityMapper;
    @Autowired
    private ConditionMapper conditionMapper;
    @Autowired
    private AccountBiz customerAccountBiz;

    @DubboReference(version = "${dubbo.provider.version}")
    private TokenParamService tokenParamService;

    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Override
    public Grade add(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        Grade grade = BsinServiceContext.getReqBodyDto(Grade.class, requestMap);
        grade.setTenantId(loginUser.getTenantId());
        grade.setMerchantNo(loginUser.getMerchantNo());
        grade.setSerialNo(BsinSnowflake.getId());
        gradeMapper.insert(grade);
        return grade;
    }

    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public void delete(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        if (gradeMapper.deleteById(serialNo) == 0) {
            throw new BusinessException(GRADE_NOT_EXISTS);
        }
    }

    @ApiDoc(desc = "edit")
    @ShenyuDubboClient("/edit")
    @Override
    public Grade edit(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        Grade grade = BsinServiceContext.getReqBodyDto(Grade.class, requestMap);
        grade.setTenantId(loginUser.getTenantId());
        grade.setMerchantNo(loginUser.getMerchantNo());
        if (gradeMapper.updateById(grade) == 0) {
            throw new BusinessException(GRADE_NOT_EXISTS);
        }
        return grade;
    }

    @ApiDoc(desc = "getList")
    @ShenyuDubboClient("/getList")
    @Override
    public List<Grade> getList(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        Grade grade = BsinServiceContext.getReqBodyDto(Grade.class, requestMap);
        LambdaQueryWrapper<Grade> warapper = new LambdaQueryWrapper<>();
        warapper.orderByDesc(Grade::getCreateTime);
        warapper.eq(Grade::getTenantId, loginUser.getTenantId());
        warapper.eq(StringUtils.isNotEmpty(loginUser.getMerchantNo()), Grade::getMerchantNo, loginUser.getMerchantNo());
        List<Grade> gradeList = gradeMapper.selectList(warapper);
        return gradeList;
    }

    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<?> getPageList(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        String bizRoleType = MapUtils.getString(requestMap, "bizRoleType");
        Object paginationObj = requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj, pagination);
        Page<Grade> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        Grade grade = BsinServiceContext.getReqBodyDto(Grade.class, requestMap);
        LambdaQueryWrapper<Grade> warapper = new LambdaQueryWrapper<>();
        warapper.orderByDesc(Grade::getCreateTime);
        warapper.eq(Grade::getTenantId, loginUser.getTenantId());
        warapper.eq(StringUtils.isNotEmpty(loginUser.getMerchantNo()), Grade::getMerchantNo, loginUser.getMerchantNo());
        warapper.eq(StringUtils.isNotEmpty(bizRoleType), Grade::getBizRoleType, bizRoleType);
        warapper.eq(
                StringUtils.isNotEmpty(grade.getGradeNum()),
                Grade::getGradeNum,
                grade.getGradeNum());
        IPage<Grade> pageList = gradeMapper.selectPage(page, warapper);
        return pageList;
    }


    /**
     * 等级条件和权益详情
     *
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "getDetail")
    @ShenyuDubboClient("/getDetail")
    @Override
    public GradeVO getDetail(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        Grade grade = gradeMapper.selectById(serialNo);
        // 查询等级条件和权益
        // 1.权益分类编号：关联等级 任务 活动的编号
        String categoryNo = grade.getSerialNo();
        List<Condition> conditionList = conditionMapper.getConditionList(categoryNo);
        // 2.权益分类编号：关联等级 任务 活动的编号
        List<Equity> equityList = equityMapper.getEquityList(categoryNo);

        // 3.该等级下的会员member
        GradeVO gradeVO = new GradeVO();
//        BeanUtil.copyProperties(grade,gradeVO);
        gradeVO.setGrade(grade);
        gradeVO.setConditionList(conditionList);
        gradeVO.setEquityList(equityList);
        return gradeVO;
    }

    /**
     * 等级详情
     *
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "getGradeDetail")
    @ShenyuDubboClient("/getGradeDetail")
    @Override
    public Grade getGradeDetail(Map<String, Object> requestMap) {
        String gradeNo = MapUtils.getString(requestMap, "gradeNo");
        if (gradeNo == null) {
            gradeNo = MapUtils.getString(requestMap, "serialNo");
        }
        Grade grade = gradeMapper.selectById(gradeNo);
        return grade;
    }

    @Override
    public List<Grade> getGradeListByIds(Map<String, Object> requestMap) {
        List<String> gradeNos = (List<String>) requestMap.get("gradeNos");
        if (gradeNos.size() < 1) {
            throw new BusinessException("200000", "请求参数不能为空！");
        }
        List<Grade> gradeList = gradeMapper.selectList(new LambdaQueryWrapper<Grade>()
                .in(Grade::getSerialNo, gradeNos));
        return gradeList;
    }

    /**
     * 查询商户下的会员等级详情
     * 等级列表
     * 等级的权益
     * 等级的条件
     * 等级的会员
     *
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "getGradeAndMemberList")
    @ShenyuDubboClient("/getGradeAndMemberList")
    @Override
    public List<?> getGradeAndMemberList(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        String tenantId = MapUtils.getString(requestMap, "tenantId");
        if (tenantId == null) {
            tenantId = loginUser.getTenantId();
            if (tenantId == null) {
                throw new BusinessException(TENANT_ID_NOT_ISNULL);
            }
        }
        String merchantNo = MapUtils.getString(requestMap, "merchantNo");
        if (merchantNo == null) {
            merchantNo = loginUser.getMerchantNo();
            requestMap.put("merchantNo", merchantNo);
            if (merchantNo == null) {
                merchantNo = loginUser.getTenantMerchantNo();
            }
        }
        String gradeNum = MapUtils.getString(requestMap, "gradeNum");
        String name = MapUtils.getString(requestMap, "name");
        LambdaQueryWrapper<Grade> warapper = new LambdaQueryWrapper<>();
        warapper.orderByDesc(Grade::getCreateTime);
        warapper.eq(Grade::getTenantId, tenantId);
        warapper.eq(Grade::getMerchantNo, merchantNo);
        warapper.eq(StringUtils.isNotEmpty(gradeNum), Grade::getGradeNum, gradeNum);
        warapper.eq(StringUtils.isNotEmpty(name), Grade::getGradeNum, name);
        List<GradeVO> gradeVOS = new ArrayList<>();

        List<Grade> gradeList = gradeMapper.selectList(warapper);

        // 1.商户发行的数字积分(查询tokenParam)
        TokenParam tokenParamMap = tokenParamService.getDetailByMerchantNo(requestMap);
        Integer decimals = Integer.valueOf(2);
        String ccy = tokenParamMap.getSymbol();
        decimals = tokenParamMap.getDecimals();

        for (Grade grade : gradeList) {
            GradeVO gradeVO = new GradeVO();
            gradeVO.setGrade(grade);
            gradeVO.setDecimals(decimals);
            // 查询该等级下的会员
            List<CustomerBase> memberList = memberGradeMapper.selectMemberListByGrade(grade.getSerialNo(), ccy);
            gradeVO.setMemberList(memberList);
            gradeVOS.add(gradeVO);
        }
        return gradeVOS;
    }

}




