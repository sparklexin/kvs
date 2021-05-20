package xin.sparkle.kvs.internal.room;

import androidx.annotation.RestrictTo;
import androidx.room.Database;
import androidx.room.RoomDatabase;

@RestrictTo(RestrictTo.Scope.LIBRARY)
@Database(entities = {KeyValue.class}, version = 1, exportSchema = false)
public abstract class KeyValueDatabase extends RoomDatabase {
    public abstract KeyValueDao keyValueDao();
}
