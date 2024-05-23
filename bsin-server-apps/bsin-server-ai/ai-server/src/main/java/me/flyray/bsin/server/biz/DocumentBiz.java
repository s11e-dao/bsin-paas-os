package me.flyray.bsin.server.biz;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.loader.UrlDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.domain.domain.KnowledgeBaseFile;
import me.flyray.bsin.domain.enums.FileLoadType;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.server.document.parser.DocumentParserFactory;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author leonard
 * @description
 * @createDate 2024/2/5 /14/01
 */
@Component
@Slf4j
public class DocumentBiz {

  @Autowired private DocumentParserFactory documentParserFactory;

  public Document loadDocument(KnowledgeBaseFile knowledgeBaseFile) {
    // 1.非结构化文档加载
    Document document = null;
    // url
    if (FileLoadType.URL.getCode().equals(knowledgeBaseFile.getType())) {
      if (StringUtils.isEmpty(knowledgeBaseFile.getFileUri())) {
        throw new BusinessException("100000", "知识库文件URI为空！！！");
      }
      document = UrlDocumentLoader.load(knowledgeBaseFile.getFileUri(), new TextDocumentParser());
    }
    // path
    else if (FileLoadType.FILE.getCode().equals(knowledgeBaseFile.getType())) {
      //      // 从本地路径加载
      //      document =
      //              FileSystemDocumentLoader.loadDocument(
      //                      knowledgeBaseFile.getLocalPath(),
      //
      // documentParserFactory.getDocumentParserByFileType(knowledgeBaseFile.getFileType()));
      // TODO: 由于网关上传的文件在服务器A，AI服务在服务器B，导致不能共享本地文件
      document =
          UrlDocumentLoader.load(
              knowledgeBaseFile.getFileUri(),
              documentParserFactory.getDocumentParserByFileType(knowledgeBaseFile.getFileType()));
    }
    // path
    else if (FileLoadType.FILE_PATH.getCode().equals(knowledgeBaseFile.getType())) {
      if (StringUtils.isEmpty(knowledgeBaseFile.getFileUri())) {
        throw new BusinessException("100000", "知识库文件路径为空！！！");
      }
      document =
          FileSystemDocumentLoader.loadDocument(
              knowledgeBaseFile.getFileUri(), new TextDocumentParser());
    } else if (FileLoadType.WECHAT_MP.getCode().equals(knowledgeBaseFile.getType())) {
      throw new BusinessException("100000", "不支持的知识库加载方式！！！");
    } else {
    }
    return document;
  }
}
