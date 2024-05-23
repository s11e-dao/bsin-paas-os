package  me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.domain.Hello;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ：bolei
 * @date ：Created in 2021/11/30 16:54
 * @description：hello数据访问
 */

@Repository
@Mapper
public interface HelloMapper extends BaseMapper<Hello> {

    List<Hello> listPage();

}
