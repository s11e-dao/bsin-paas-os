package me.flyray.bsin.domain.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(name = "CarInfoRequestDTO", description = "车辆信息DTO")
public class CarInfoRequestDTO implements Serializable {

  @Schema(name = "vin", description = "车架号", required = true)
  private String vin;

  @Schema(name = "plateNo", description = "车牌号", required = true)
  private String plateNo;

  @Schema(name = "plateType", description = "号牌种类", required = true)
  private String plateType;

  @Schema(name = "vehicleName", description = "厂牌型号", required = true)
  private String vehicleName;

  @Schema(name = "orderId", description = "订单id", required = true)
  private String orderId;
}
