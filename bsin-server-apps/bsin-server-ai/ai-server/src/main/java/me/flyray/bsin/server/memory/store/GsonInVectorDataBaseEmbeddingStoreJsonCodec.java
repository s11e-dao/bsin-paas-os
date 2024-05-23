package me.flyray.bsin.server.memory.store;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dev.langchain4j.data.segment.TextSegment;
import java.lang.reflect.Type;

public class GsonInVectorDataBaseEmbeddingStoreJsonCodec
    implements InVectorDataBaseEmbeddingStoreJsonCodec {

  @Override
  public InVectorDataBaseEmbeddingStore<TextSegment> fromJson(String json) {
    Type type =
        new TypeToken<
            dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore<
                TextSegment>>() {}.getType();
    return new Gson().fromJson(json, type);
  }

  @Override
  public String toJson(InVectorDataBaseEmbeddingStore<?> store) {
    return new Gson().toJson(store);
  }
}
