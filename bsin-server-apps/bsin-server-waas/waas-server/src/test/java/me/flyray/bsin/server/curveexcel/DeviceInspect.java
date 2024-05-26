package me.flyray.bsin.server.curveexcel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author leonard
 * @date 2023/07/17 16:19
 */
@Data
public class DeviceInspect {

    @ExcelProperty("资产编号")
    private String iceCuberNumber;
    @ExcelProperty("巡检时间")
    private String inspectionDate;

}
