package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import me.flyray.bsin.domain.entity.SysLogLogin;

import java.util.List;
import java.util.Map;

/**
* @author bolei
* @description 针对表【sys_log_login(系统访问记录)】的数据库操作Service
* @createDate 2024-12-25
*/

public interface LogLoginService {

    /**
     * 添加
     */
    public SysLogLogin add(Map<String, Object> requestMap);

    /**
     * 分页查询
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);

}
