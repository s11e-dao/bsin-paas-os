package me.flyray.bsin.server.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.domain.domain.Grade;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.CustomerGradeEngine;
import me.flyray.bsin.facade.service.GradeService;
import me.flyray.bsin.facade.service.MemberService;

/**
 * @author bolei
 * @date 2023/6/28 16:36
 * @desc
 */


@Slf4j
@ShenyuDubboService(path = "/customerGradeEngine", timeout = 6000)
@ApiModule(value = "customerGradeEngine")
@Service
public class CustomerGradeEngineImpl implements CustomerGradeEngine {

    @DubboReference(version = "dev")
    private MemberService memberService;

    @DubboReference(version = "dev")
    private GradeService gradeService;
    @Override
    public Map<String, Object> execute(Map<String, Object> requestMap) {
        return null;
    }


    public Map<String, Object>  verifyGrade(Map<String, Object> requestMap) {
        Map resMap = memberService.getMemberGradeDetail(requestMap);
        String customerGradeNum = new String("");
        if (resMap.get("data") != null && !((String) (resMap.get("data"))).isEmpty()) {
            Grade memberGrade = (Grade) resMap.get("data");
            customerGradeNum = memberGrade.getGradeNum();
        }
        if (customerGradeNum.isEmpty()) {
            throw new BusinessException(ResponseCode.TASK_NON_CLAIM_CONDITION_GRADE);
        }
        // gradwNo
        resMap = gradeService.getGradeDetail(requestMap);
        Grade conditionGrade = (Grade) resMap.get("data");
        if (new BigDecimal(conditionGrade.getGradeNum()).compareTo(new BigDecimal(customerGradeNum))
                    <= 0) {
                throw new BusinessException(ResponseCode.TASK_NON_CLAIM_CONDITION_GRADE);
        }
        return null;
    }
}
