package xin.sparkle.kvs.demo;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.TimeUnit;

import xin.sparkle.kvs.KVS;
import xin.sparkle.kvs.KeyValueStoreFactory;

public class KVSInit {

    public static void initialize(@NonNull Application application) {
        initKVS(application);
        initPeriodCleanExpired(application);
    }

    private static void initKVS(@NonNull Application application) {
        KVS.initialize(KeyValueStoreFactory.create(application));
    }

    private static void initPeriodCleanExpired(@NonNull Application application) {
        WorkManager.getInstance(application).enqueueUniquePeriodicWork("kvs_expired_clean", ExistingPeriodicWorkPolicy.KEEP, provideExpiredCleanRequest());
    }

    private static PeriodicWorkRequest provideExpiredCleanRequest() {
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build();
        return new PeriodicWorkRequest.Builder(CleanWorker.class, 1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build();
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public static class CleanWorker extends Worker {

        public CleanWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
        }

        @NonNull
        @Override
        public Result doWork() {
            KVS.defaultKVS().clearExpired();
            return Result.success();
        }
    }
}
