package me.flyray.bsin.facade.response;

import java.util.List;
import java.util.Map;

import lombok.Data;
import me.flyray.bsin.domain.entity.DigitalAssetsItem;

/**
 * @author bolei
 * @date 2023/7/25 14:31
 * @desc
 */

@Data
public class DigitalAssetsDetailRes {

    // 基础信息

    /**
     * 资产名称
     */
    private String name;

    private Object orderbook;

    private Object customerPassCard;

    private DigitalAssetsItem digitalAssetsItem;

    private Object digitalAssetsCollection;

    private Object contractProtocol;

    // 查询数字资产的权益和详情
    private List<Map> equityList;

    // 成交记录数据
    private List<Map> tradeHistoryList;

}
