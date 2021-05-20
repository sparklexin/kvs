package xin.sparkle.kvs;

import java.util.Objects;

import xin.sparkle.kvs.core.KeyValueStore;

public final class KVS {
    private static KeyValueStore delegate;
    
    public static void initialize(KeyValueStore keyValueStore) {
        delegate = keyValueStore;
    }

    public static KeyValueStore defaultKVS() {
        return Objects.requireNonNull(delegate, "Please call KVS#initialize first!");
    }
}
