package me.flyray.bsin.infrastructure.biz;


import me.flyray.bsin.domain.entity.ModelType;
import me.flyray.bsin.domain.response.ModelTypeTree;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class ModelTypeBiz {
    /**
     * 递归
     *
     * @param root
     * @param all
     * @return
     */
    public List<ModelTypeTree> getModelTypeTree(ModelType root, List<ModelType> all) {
        List<ModelTypeTree> children = all.stream().filter(m -> {
            return m.getParentId().equals(root.getId());
        }).map(m -> {
            ModelTypeTree childMenu = new ModelTypeTree(m.getId(), m.getTypeCode(), m.getTypeName(),
                    m.getParentId(), m.getDescription(),m.getDelFlag(),m.getCreateTime(),m.getUpdateTime(),
                    getModelTypeTree(m, all)); //递归
            return childMenu;
        }).collect(Collectors.toList());

        return children;
    }
}
