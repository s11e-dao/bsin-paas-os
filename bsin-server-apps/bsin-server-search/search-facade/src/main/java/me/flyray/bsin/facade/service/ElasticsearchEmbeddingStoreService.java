package me.flyray.bsin.facade.service;

import me.flyray.bsin.domain.entity.DubboTest;

public interface ElasticsearchEmbeddingStoreService {

    /**
     * find by id.
     * body：{"id":"1223"}
     *
     * @param id id
     * @return DubboTest dubbo test
     */
    DubboTest findById(String id);

}
