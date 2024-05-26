package me.flyray.bsin.server.curveexcel;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @author leonard
 * @date 2023/07/17 16:19
 */
@Data
public class CurveData {
    @ExcelProperty("序号")
    private String num;
    @ExcelProperty("铸造时间")
    private Date date;
    @ExcelProperty("当前供应量")
    private BigDecimal currentSupply;
    @ExcelProperty("当前价格")
    private Double currentPrice;
    @ExcelProperty("mint金额")
    private BigDecimal mintAmount;
    @ExcelProperty("mint积分数量")
    private BigDecimal mintReturn;
    @ExcelProperty("赎回数量")
    private BigDecimal burnAmount;
    @ExcelProperty("赎回金额")
    private Double burnReturn;
    /**
     * 忽略这个字段
     */
    @ExcelIgnore
    private String ignore;
}

