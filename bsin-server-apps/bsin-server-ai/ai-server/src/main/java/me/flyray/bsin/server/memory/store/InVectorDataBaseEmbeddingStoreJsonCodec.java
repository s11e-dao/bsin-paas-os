package me.flyray.bsin.server.memory.store;

import dev.langchain4j.data.segment.TextSegment;

public interface InVectorDataBaseEmbeddingStoreJsonCodec {
  InVectorDataBaseEmbeddingStore<TextSegment> fromJson(String json);

  String toJson(InVectorDataBaseEmbeddingStore<?> store);
}
