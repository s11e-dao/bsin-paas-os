package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.SysLogOperate;

import java.util.Map;

/**
* @author bolei
* @description 针对表【sys_log_oper(操作日志记录)】的数据库操作Service
* @createDate 2024-12-25
*/

public interface LogOperateService {

    /**
     * 添加
     */
    public SysLogOperate add(Map<String, Object> requestMap);

    /**
     * 分页查询合约
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);

}
