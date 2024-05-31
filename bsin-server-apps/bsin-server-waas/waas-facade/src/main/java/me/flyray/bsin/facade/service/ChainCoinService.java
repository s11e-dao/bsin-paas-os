package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.flyray.bsin.domain.entity.ChainCoin;
import me.flyray.bsin.domain.request.ChainCoinDTO;

import java.util.List;

/**
* @author Admin
* @description 针对表【crm_coin(币;)】的数据库操作Service
* @createDate 2024-04-24 20:36:46
*/
public interface ChainCoinService {

    /**
     * 添加链上货币
     * 1、币种的重复性校验
     * 2、保存币种信息
     * 3、监听智能合约上该链上货币的交易
     * @param chainCoinDTO
     * @return
     */
    void add(ChainCoinDTO chainCoinDTO);

    /**
     * 编辑币种信息
     * @param chainCoinDTO
     * @return
     */
    void edit(ChainCoinDTO chainCoinDTO);

    /**
     * 删除币种
     * @param chainCoinDTO
     * @return
     */
    void delete(ChainCoinDTO chainCoinDTO);

    /**
     * 分页查询币种列表
     * @param coinDTO
     * @return
     */
    Page<ChainCoin> getPageList(ChainCoinDTO coinDTO);

    /**
     * 查询币种列表
     * @param coinDTO
     * @return
     */
    List<ChainCoin> getList(ChainCoinDTO coinDTO);

    /**
     * coin下拉
     */
    List<String> coinDropDown();

    /**
     * chain下拉
     */
    List<String> chainDropDown();
}
