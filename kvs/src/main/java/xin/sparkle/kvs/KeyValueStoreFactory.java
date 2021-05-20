package xin.sparkle.kvs;

import android.app.Application;

import androidx.room.Room;

import java.lang.reflect.Type;
import java.util.concurrent.Executor;

import xin.sparkle.kvs.internal.converter.GsonConverter;
import xin.sparkle.kvs.core.KeyValueStore;
import xin.sparkle.kvs.internal.room.KeyValueDatabase;
import xin.sparkle.kvs.internal.room.RoomKeyValueStore;

public class KeyValueStoreFactory {

    public static KeyValueStore create(Application application) {
        return create(application, new Config());
    }

    public static KeyValueStore create(Application application, Config config) {
        KeyValueDatabase database = Room.databaseBuilder(application,
                KeyValueDatabase.class, config.databaseName())
                .allowMainThreadQueries()
                .build();
        return new RoomKeyValueStore(database.keyValueDao(), config);
    }

    public static class Config {
        // 数据库名
        protected String databaseName() {
            return "kvs_room";
        }

        // 对于putXxx(String key, Xxx value)方式添加数据，设置有效期。
        public long defaultValidDurationMillisOfPut() {
            return Long.MAX_VALUE;
        }

        // 当前时间戳：默认为当前系统时间戳
        public long currentTimeMillis() {
            return System.currentTimeMillis();
        }

        // Json str <=> model之间的转换: 默认为Gson, 在调用线程执行。
        public JsonConverter createJsonConverter() {
            return new GsonConverter();
        }

        public interface JsonConverter {
            Executor defaultExecutor = Runnable::run;

            String toJson(Object obj);

            <T> T fromJson(String json, Type type);

            default Executor fromJsonExecutor() {
                return defaultExecutor;
            }

            default Executor toJsonExecutor() {
                return defaultExecutor;
            }
        }
    }
}
