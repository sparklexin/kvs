package xin.sparkle.kvs.internal.room;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import xin.sparkle.kvs.KeyValueStoreFactory;
import xin.sparkle.kvs.core.KeyValueStore;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class RoomKeyValueStore implements KeyValueStore {
    private static final String TYPE_STRING = "String";
    private static final String TYPE_INT = "int";
    private static final String TYPE_BOOLEAN = "boolean";
    private static final String TYPE_FLOAT = "float";
    private static final String TYPE_LONG = "long";
    private static final String TYPE_JSON = "json";

    final KeyValueDao dao;
    final KeyValueStoreFactory.Config config;
    final KeyValueStoreFactory.Config.JsonConverter jsonConverter;

    public RoomKeyValueStore(@NonNull KeyValueDao keyValueDao, @NonNull KeyValueStoreFactory.Config config) {
        this.dao = keyValueDao;
        this.config = config;
        this.jsonConverter = config.createJsonConverter();
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return get(key, defaultValue, TYPE_INT, Integer::parseInt);
    }

    @Override
    public LiveData<Optional<Integer>> getIntAsLiveData(String key) {
        return getAsLiveData(key, TYPE_INT, Integer::parseInt);
    }

    @Override
    public void putInt(String key, int value) {
        put(key, value, TYPE_INT);
    }

    @Override
    public void putInt(String key, int value, long validDuration, TimeUnit timeUnit) {
        put(key, value, validDuration, timeUnit, TYPE_INT);
    }

    @Override
    public void removeInt(String key) {
        remove(key, TYPE_INT);
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        return get(key, defaultValue, TYPE_FLOAT, Float::parseFloat);
    }

    @Override
    public LiveData<Optional<Float>> getFloatAsLiveData(String key) {
        return getAsLiveData(key, TYPE_FLOAT, Float::parseFloat);
    }

    @Override
    public void putFloat(String key, float value) {
        put(key, value, TYPE_FLOAT);
    }

    @Override
    public void putFloat(String key, float value, long validDuration, TimeUnit timeUnit) {
        put(key, value, validDuration, timeUnit, TYPE_FLOAT);
    }

    @Override
    public void removeFloat(String key) {
        remove(key, TYPE_FLOAT);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return get(key, defaultValue, TYPE_BOOLEAN, Boolean::valueOf);
    }

    @Override
    public LiveData<Optional<Boolean>> getBooleanAsLiveData(String key) {
        return getAsLiveData(key, TYPE_BOOLEAN, Boolean::valueOf);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        put(key, value, TYPE_BOOLEAN);
    }

    @Override
    public void putBoolean(String key, boolean value, long validDuration, TimeUnit timeUnit) {
        put(key, value, validDuration, timeUnit, TYPE_BOOLEAN);
    }

    @Override
    public void removeBoolean(String key) {
        remove(key, TYPE_BOOLEAN);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return get(key, defaultValue, TYPE_LONG, Long::parseLong);
    }

    @Override
    public LiveData<Optional<Long>> getLongAsLiveData(String key) {
        return getAsLiveData(key, TYPE_LONG, Long::parseLong);
    }

    @Override
    public void putLong(String key, long value) {
        put(key, value, TYPE_LONG);
    }

    @Override
    public void putLong(String key, long value, long validDuration, TimeUnit timeUnit) {
        put(key, value, validDuration, timeUnit, TYPE_LONG);
    }

    @Override
    public void removeLong(String key) {
        remove(key, TYPE_LONG);
    }

    @Override
    public String getString(String key, String defaultValue) {
        return get(key, null, TYPE_STRING, value -> value);
    }

    @Override
    public LiveData<Optional<String>> getStringAsLiveData(String key) {
        return getAsLiveData(key, TYPE_STRING, value -> value);
    }

    @Override
    public void putString(String key, String value) {
        put(key, value, String.class.getSimpleName());
    }

    @Override
    public void putString(String key, String value, long validDuration, TimeUnit timeUnit) {
        put(key, value, validDuration, timeUnit, TYPE_STRING);
    }

    @Override
    public void removeString(String key) {
        remove(key, TYPE_STRING);
    }

    @Override
    public <T> T getFromJson(String key, @NonNull Type typeOfT) {
        return get(key, null, TYPE_JSON, str -> jsonConverter.fromJson(str, typeOfT));
    }

    @Override
    public <T> LiveData<Optional<T>> getFromJsonAsLiveData(String key, Type typeOfT) {
        return getAsLiveData(key, TYPE_JSON, str -> jsonConverter.fromJson(str, typeOfT), jsonConverter.fromJsonExecutor());
    }

    @Override
    public void putToJson(String key, Object value) {
        Runnable runnable = () -> put(key, jsonConverter.toJson(value), TYPE_JSON);
        Executor executor = jsonConverter.toJsonExecutor();
        safeExecute(executor, runnable);
    }

    @Override
    public void putToJson(String key, Object value, long validDuration, TimeUnit timeUnit) {
        Runnable runnable = () -> put(key, jsonConverter.toJson(value), validDuration, timeUnit, TYPE_JSON);
        Executor executor = jsonConverter.toJsonExecutor();
        safeExecute(executor, runnable);
    }

    @Override
    public void removeJson(String key) {
        remove(key, TYPE_JSON);
    }

    private void safeExecute(Executor executor, Runnable runnable) {
        if (executor == null) {
            runnable.run();
        } else {
            executor.execute(runnable);
        }
    }

    private <T> T get(String key, T defaultValue, String type, Function<String, T> mapper) {
        KeyValue keyValue = dao.get(key, type);
        if (keyValue == null) {
            return defaultValue;
        }

        if (!isValid(keyValue)) {
            return defaultValue;
        }
        try {
            return mapper.apply(keyValue.value);
        } catch (Exception exception) {
            return defaultValue;
        }
    }

    private <T> LiveData<Optional<T>> getAsLiveData(String key, String type, Function<String, T> mapper) {
        return getAsLiveData(key, type, mapper, null);
    }

    private <T> LiveData<Optional<T>> getAsLiveData(String key, String type, Function<String, T> mapper, Executor mapExecutor) {
        MediatorLiveData<Optional<T>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(dao.getAsLivaData(key, type), keyValueList -> {
            KeyValue keyValue = keyValueList == null || keyValueList.isEmpty() ? null : keyValueList.get(0);
            if (isValid(keyValue)) {
                Runnable runnable = () -> {
                    T value = mapper.apply(keyValue.value);
                    mediatorLiveData.postValue(Optional.ofNullable(value));
                };
                safeExecute(mapExecutor, runnable);
            } else {
                mediatorLiveData.postValue(Optional.empty());
            }
        });
        return mediatorLiveData;
    }

    private boolean isValid(KeyValue keyValue) {
        return keyValue != null && config.currentTimeMillis() - keyValue.updateTime < keyValue.validDurationMillis;
    }

    private <T> void put(String key, T value, String type) {
        long updateTime = config.currentTimeMillis();
        long duration = config.defaultValidDurationMillisOfPut();
        KeyValue keyValue = new KeyValue(key, type, String.valueOf(value), updateTime, duration);
        dao.put(keyValue);
    }

    private <T> void put(String key, T value, long validDuration, TimeUnit timeUnit, String type) {
        long updateTime = config.currentTimeMillis();
        long duration = TimeUnit.MILLISECONDS.convert(validDuration, timeUnit);
        KeyValue keyValue = new KeyValue(key, type, String.valueOf(value), updateTime, duration);
        dao.put(keyValue);
    }

    private void remove(String key, String type) {
        KeyValue keyValue = new KeyValue(key, type, null, 0, 0);
        dao.remove(keyValue);
    }

    @Override
    public void clearAll() {
        dao.clearAll();
    }

    @Override
    public void clearExpired() {
        dao.clear(config.currentTimeMillis());
    }
}
