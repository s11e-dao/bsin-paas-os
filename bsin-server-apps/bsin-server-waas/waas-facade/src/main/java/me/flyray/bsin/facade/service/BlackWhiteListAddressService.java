package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.flyray.bsin.domain.entity.BlackWhiteListAddress;
import me.flyray.bsin.domain.request.BlackWhiteListAddressDTO;
import me.flyray.bsin.domain.response.BlackWhiteListAddressVO;
import me.flyray.bsin.utils.BsinResultEntity;

import java.util.List;

/**
* @author Admin
* @description 针对表【crm_address_black_white_list(地址黑白名单;)】的数据库操作Service
* @createDate 2024-04-24 20:37:18
*/
public interface BlackWhiteListAddressService {

    /**
     * 添加黑白名单地址
     * @param blackWhiteListAddressDTO
     * @return
     */
    void save(BlackWhiteListAddressDTO blackWhiteListAddressDTO);

    /**
     * 编辑黑白名单地址
     * @param blackWhiteListAddressDTO
     * @return
     */
    void edit(BlackWhiteListAddressDTO blackWhiteListAddressDTO);

    /**
     * 删除黑白名单地址
     * @param blackWhiteListAddressDTO
     * @return
     */
    void delete(BlackWhiteListAddressDTO blackWhiteListAddressDTO);

    /**
     * 查询黑白名单列表
     * @param address
     * @return
     */
     List<BlackWhiteListAddressVO> getList(BlackWhiteListAddressDTO address) ;

    /**
     * 分页查询黑白名单
     * @param address
     * @return
     */
     Page<BlackWhiteListAddressVO> pageList(BlackWhiteListAddressDTO address);
}
