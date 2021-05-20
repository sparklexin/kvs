package xin.sparkle.kvs.core;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface KeyValueStore {

    int getInt(String key, int defaultValue);

    LiveData<Optional<Integer>> getIntAsLiveData(String key);

    void putInt(String key, int value);

    void putInt(String key, int value, long validDuration, TimeUnit timeUnit);

    void removeInt(String key);

    float getFloat(String key, float defaultValue);

    LiveData<Optional<Float>> getFloatAsLiveData(String key);

    void putFloat(String key, float value);

    void putFloat(String key, float value, long validDuration, TimeUnit timeUnit);

    void removeFloat(String key);

    boolean getBoolean(String key, boolean defaultValue);

    LiveData<Optional<Boolean>> getBooleanAsLiveData(String key);

    void putBoolean(String key, boolean value);

    void putBoolean(String key, boolean value, long validDuration, TimeUnit timeUnit);

    void removeBoolean(String key);

    long getLong(String key, long defaultValue);

    LiveData<Optional<Long>> getLongAsLiveData(String key);

    void putLong(String key, long value);

    void putLong(String key, long value, long validDuration, TimeUnit timeUnit);

    void removeLong(String key);

    String getString(String key, String defaultValue);

    LiveData<Optional<String>> getStringAsLiveData(String key);

    void putString(String key, String value);

    void putString(String key, String value, long validDuration, TimeUnit timeUnit);

    void removeString(String key);

    <T> T getFromJson(String key, @NonNull Type typeOfT);

    <T> LiveData<Optional<T>> getFromJsonAsLiveData(String key, Type typeOfT);

    void putToJson(String key, Object value);

    void putToJson(String key, Object value, long validDuration, TimeUnit timeUnit);

    void removeJson(String key);

    void clearExpired();

    void clearAll();
}