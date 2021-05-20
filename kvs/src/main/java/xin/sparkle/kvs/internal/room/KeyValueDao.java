package xin.sparkle.kvs.internal.room;

import androidx.annotation.RestrictTo;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface KeyValueDao {
    @Query("SELECT * FROM key_value WHERE `key` = :key and `type` = :type")
    KeyValue get(String key, String type);

    @Query("SELECT * FROM key_value WHERE `key` = :key and `type` = :type")
    LiveData<List<KeyValue>> getAsLivaData(String key, String type);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void put(KeyValue keyValue);

    @Delete
    void remove(KeyValue keyValue);

    @Query("DELETE FROM key_value where :endTime - updateTime > validDurationMillis")
    void clear(long endTime);

    @Query("DELETE FROM key_value")
    void clearAll();
}
