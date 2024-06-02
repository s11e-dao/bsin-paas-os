package me.flyray.bsin.facade.response;

import java.util.List;

import lombok.Data;
import me.flyray.bsin.domain.domain.*;
import me.flyray.bsin.domain.entity.Condition;
import me.flyray.bsin.domain.entity.CustomerBase;
import me.flyray.bsin.domain.entity.Equity;
import me.flyray.bsin.domain.entity.Grade;

/**
 * @author bolei
 * @date 2023/10/1
 * @desc
 */

@Data
public class GradeVO {

    private Grade grade;

    private Integer decimals;

    private List<CustomerBase> memberList;

    private List<Equity> equityList;

    private List<Condition> conditionList;
}
