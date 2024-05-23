package me.flyray.bsin.server.spi.store.embedding.inmemory;

import me.flyray.bsin.server.memory.store.InVectorDataBaseEmbeddingStoreJsonCodec;

public interface InVectorDataBaseEmbeddingStoreJsonCodecFactory {

  InVectorDataBaseEmbeddingStoreJsonCodec create();
}
