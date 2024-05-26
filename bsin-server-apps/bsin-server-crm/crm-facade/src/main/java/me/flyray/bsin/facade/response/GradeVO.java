package me.flyray.bsin.facade.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;
import me.flyray.bsin.domain.domain.*;

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
