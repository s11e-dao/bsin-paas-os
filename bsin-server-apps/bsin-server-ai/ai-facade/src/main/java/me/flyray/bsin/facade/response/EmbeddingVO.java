package me.flyray.bsin.facade.response;


import java.io.Serializable;
import dev.langchain4j.data.document.Metadata;

import lombok.Data;
import me.flyray.bsin.domain.entity.EmbeddingModel;

@Data
public class EmbeddingVO implements Serializable {

    /**
     * 模型编号
     */
    private String serialNo;

    /**
     * 模型
     */
    private EmbeddingModel embeddingModel;

    /**
     * 客户ID
     */
    private String customerNo;

    /**
     * AI编号： copilot_no|agent_no|品牌馆_no|customer_no|knowledge_base_no
     */
    private String aiNo;

    /**
     * 知识库文件ID
     */
    private String knowledgeBaseFileNo;

    /**
     * 知识库文件chunkId
     */
    private String chunkNo;

    /**
     * 提示词模版ID
     */
    private String promptTemplateNo;


    /**
     * 大语言模型ID
     */
    private String llmNo;


    /**
     * 相似度得分
     */
    private double score;

    /**
     * id
     */
    private String embeddingId;

    /**
     * 分段的文本
     */
    private String text;

    /**
     * 1.index:索引
     * 2.url:知识库文件类型
     * 3.document_type:知识库文件类型
     */
    private Metadata metadata;

    /**
     * 向量文件
     */
    private Object embeddingVector;



}