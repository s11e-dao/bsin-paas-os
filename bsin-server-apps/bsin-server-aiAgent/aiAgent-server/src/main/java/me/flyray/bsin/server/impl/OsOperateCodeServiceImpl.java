package me.flyray.bsin.server.impl;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.domain.entity.LLMParam;
import me.flyray.bsin.facade.service.OsOperateCodeService;
import me.flyray.bsin.server.milvus.BsinMilvusEmbeddingStore;
import me.flyray.bsin.server.milvus.BsinOsOperateCodeEmbeddingStore;
import me.flyray.bsin.server.milvus.BsinOsOperateCodeSegment;
import me.flyray.bsin.server.milvus.BsinTextSegment;
import org.apache.commons.collections4.MapUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@ShenyuDubboService(path = "/osOperateCode", timeout = 6000)
@ApiModule(value = "osOperateCode")
@Service
public class OsOperateCodeServiceImpl implements OsOperateCodeService {

    @Autowired
    EmbeddingModel embeddingModel;

    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Override
    public void add(Map<String, Object> requestMap) {
        String descripiton = MapUtils.getString(requestMap, "descripiton");
        String opCode = MapUtils.getString(requestMap, "opCode");
        String params = MapUtils.getString(requestMap, "params");
        String scope = MapUtils.getString(requestMap, "scope");

        BsinOsOperateCodeSegment bsinOsOperateCodeSegment = BsinOsOperateCodeSegment.fromBsin(descripiton, opCode, params, scope, null);
        System.out.println("TextSegment-metadata: \n\n" + bsinOsOperateCodeSegment.metadata());
        System.out.println("TextSegment-text: \n\n" + bsinOsOperateCodeSegment.text());
        Embedding embedding = embeddingModel.embed(bsinOsOperateCodeSegment).content();
        BsinOsOperateCodeEmbeddingStore embeddingStore =
                BsinOsOperateCodeEmbeddingStore.builder().host("localhost").port(19530).collectionName("osOperateCode").dimension(512).build();
        embeddingStore.add(embedding, bsinOsOperateCodeSegment);

    }

    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public void delete(Map<String, Object> requestMap) throws Exception {
        String opCode = MapUtils.getString(requestMap, "opCode");
        BsinOsOperateCodeEmbeddingStore embeddingStore =
                BsinOsOperateCodeEmbeddingStore.builder().host("localhost").port(19530).collectionName("osOperateCode").dimension(512).build();
        embeddingStore.delDataWithExp("opCode == '" + opCode + "'");

    }

    @ApiDoc(desc = "search")
    @ShenyuDubboClient("/search")
    @Override
    public Map<String, Object> search(Map<String, Object> requestMap) {
        String question = MapUtils.getString(requestMap, "question");
        BsinOsOperateCodeEmbeddingStore embeddingStore =
                BsinOsOperateCodeEmbeddingStore.builder().host("localhost").port(19530).collectionName("osOperateCode").dimension(512).build();
        Embedding queryEmbedding = embeddingModel.embed(question).content();
        List<EmbeddingMatch<BsinOsOperateCodeSegment>> relevant = embeddingStore.findRelevant(queryEmbedding, 1);
        EmbeddingMatch<BsinOsOperateCodeSegment> embeddingMatch = relevant.get(0);
        System.out.println(embeddingMatch.score()); // 0.8144287765026093
        System.out.println(embeddingMatch.embedded().text()); // I like football.
        Map<String, Object> result = new HashMap<>();
        result.put("answer",embeddingMatch.embedded().text());
        return result;

    }

}
