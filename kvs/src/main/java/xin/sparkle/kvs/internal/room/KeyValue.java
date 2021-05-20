package xin.sparkle.kvs.internal.room;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.room.Entity;

@Entity(tableName = "key_value", primaryKeys = {"key", "type"})
@Keep
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class KeyValue {
    @NonNull
    public String key;
    @NonNull
    public String type;
    public String value;
    public long updateTime;
    public long validDurationMillis;

    public KeyValue(@NonNull String key, @NonNull String type, String value, long updateTime, long validDurationMillis) {
        this.key = key;
        this.type = type;
        this.value = value;
        this.updateTime = updateTime;
        this.validDurationMillis = validDurationMillis;
    }
}
