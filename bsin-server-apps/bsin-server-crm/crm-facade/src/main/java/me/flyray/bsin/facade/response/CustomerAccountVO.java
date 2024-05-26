package me.flyray.bsin.facade.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author bolei
 * @date 2023/10/4
 * @desc
 */

@Data
public class CustomerAccountVO {

    /**
     * 法币余额
     */
    private BigDecimal cnyBalance;

    /**
     * 劳动价值捕获联合曲线积分余额
     */
    private BigDecimal bondingCurveBalance;

    private BigDecimal bondingCurveAccumulatedBalance;

    private String bondingCurveName;

    private String bondingCurveSymbol;
    // 占比
    private BigDecimal bondingCurveProportion;

    // 流通量
    private BigDecimal bondingCurveCirculation;
    // 总供应量
    private BigDecimal bondingCurveSupply;

    /**
     * 品牌数字积分余额
     */
    private BigDecimal digitalPointsBalance;

    private String digitalPointsName;
    // 符号
    private String digitalPointsSymbol;
    // 占比
    private BigDecimal digitalPointsProportion;
    // 流通量
    private BigDecimal digitalPointsCirculation;
    // 总供应量
    private BigDecimal digitalPointsSupply;

    /**
     * 品牌passCard 的tbaAddress
     */
    private String tbaAddress;


    /**
     * profile地址
     */
    private String profileAddress;

}
