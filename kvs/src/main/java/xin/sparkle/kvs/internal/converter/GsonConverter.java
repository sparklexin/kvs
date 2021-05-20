package xin.sparkle.kvs.internal.converter;

import androidx.annotation.RestrictTo;

import com.google.gson.Gson;

import java.lang.reflect.Type;

import xin.sparkle.kvs.KeyValueStoreFactory;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class GsonConverter implements KeyValueStoreFactory.Config.JsonConverter {
    Gson gson = new Gson();

    @Override
    public String toJson(Object obj) {
        return gson.toJson(obj);
    }

    @Override
    public <T> T fromJson(String json, Type type) {
        return gson.fromJson(json, type);
    }
}