package me.flyray.bsin.server.impl;

import me.flyray.bsin.domain.entity.DubboTest;
import me.flyray.bsin.facade.service.ElasticsearchEmbeddingStoreService;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.apache.shenyu.common.utils.GsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DubboService
@ApiModule(value = "elasticsearchEmbeddingStoreService")
public class ElasticsearchEmbeddingStoreServiceImpl implements ElasticsearchEmbeddingStoreService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchEmbeddingStoreServiceImpl.class);

    @Override
    @ShenyuDubboClient("/findById")
    @ApiDoc(desc = "findById")
    // @GlobalTransactional
    public DubboTest findById(final String id) {
       /* if ("1".equals(id)) {
            throw new BusinessException(new I18eCode("exception.insert.data.to.db"));
        }*/
        LOGGER.info(GsonUtils.getInstance().toJson(RpcContext.getContext().getAttachments()));
        return new DubboTest(id, "hello world shenyu Apache, findById");
    }

}
