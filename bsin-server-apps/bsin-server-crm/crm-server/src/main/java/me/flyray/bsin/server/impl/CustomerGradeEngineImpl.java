package me.flyray.bsin.server.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.domain.entity.Grade;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.engine.GradeEngine;
import me.flyray.bsin.facade.service.GradeService;
import me.flyray.bsin.facade.service.MemberService;

/**
 * @author bolei
 * @date 2023/6/28 16:36
 * @desc
 */


@Slf4j
@ShenyuDubboService(path = "/gradeEngine", timeout = 6000)
@ApiModule(value = "gradeEngine")
@Service
public class CustomerGradeEngineImpl implements GradeEngine {

    @DubboReference(version = "${dubbo.provider.version}")
    private MemberService memberService;

    @DubboReference(version = "${dubbo.provider.version}")
    private GradeService gradeService;

    @ApiDoc(desc = "execute")
    @ShenyuDubboClient("/execute")
    @Override
    public Map<String, Object> execute(Map<String, Object> requestMap) {
        return null;
    }

    @ApiDoc(desc = "verifyGrade")
    @ShenyuDubboClient("/verifyGrade")
    public Map<String, Object>  verifyGrade(Map<String, Object> requestMap) {
        Grade grade = memberService.getMemberGradeDetail(requestMap);
        String customerGradeNum = new String("");
        if (grade != null) {
            customerGradeNum = grade.getGradeNum();
        }
        if (customerGradeNum.isEmpty()) {
            throw new BusinessException(ResponseCode.TASK_NON_CLAIM_CONDITION_GRADE);
        }
        // gradwNo
        Grade conditionGrade = gradeService.getGradeDetail(requestMap);
        if (new BigDecimal(conditionGrade.getGradeNum()).compareTo(new BigDecimal(customerGradeNum))
                    <= 0) {
                throw new BusinessException(ResponseCode.TASK_NON_CLAIM_CONDITION_GRADE);
        }
        return null;
    }
}
