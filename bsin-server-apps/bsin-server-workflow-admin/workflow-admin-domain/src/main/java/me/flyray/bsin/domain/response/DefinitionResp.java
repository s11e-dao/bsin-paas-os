package me.flyray.bsin.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


/**
 * @author huangzh
 * @ClassName DefinitionResp
 * @DATE 2020/8/24 19:36
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefinitionResp implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总条数
     */
    private Long total;

    /**
     * 页面条数
     */
    private int pageSize;

    /**
     * 页码
     */
    private int pageNum;

    /**
     * 返回满足条件的列表
     */
    List DeployModels;




}
