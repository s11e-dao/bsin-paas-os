package me.flyray.bsin.infrastructure.mapper;

import me.flyray.bsin.domain.entity.Hello;

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
public interface HelloMapper {

    int insert(Hello hello);

    List<Hello> listPage();

}
