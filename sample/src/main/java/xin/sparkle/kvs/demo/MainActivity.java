package xin.sparkle.kvs.demo;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import xin.sparkle.kvs.KVS;
import xin.sparkle.kvs.core.KeyValueStore;

public class MainActivity extends AppCompatActivity {

    private LiveData<Optional<Integer>> optionalLiveData;
    private Observer<Optional<Integer>> observer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        KeyValueStore kvs = KVS.defaultKVS();
        findViewById(R.id.getInt).setOnClickListener(v -> {
            String key = getKeyFromEditText();
            int value = kvs.getInt(key, -1);
            toast(String.format("key:{%s}, value: {%s}", key, value));
        });

        findViewById(R.id.putInt).setOnClickListener(v -> {
            String key = getKeyFromEditText();
            int value = getValueFromEditText();
            kvs.putInt(key, value);
        });

        findViewById(R.id.putIntExpired).setOnClickListener(v -> {
            String key = getKeyFromEditText();
            int value = getValueFromEditText();
            kvs.putInt(key, value, 10, TimeUnit.SECONDS);
        });

        findViewById(R.id.getIntAsLiveData).setOnClickListener(v -> {
            String key = getKeyFromEditText();
            if (optionalLiveData != null) {
                optionalLiveData.removeObserver(observer);
            }

            optionalLiveData = kvs.getIntAsLiveData(key);
            observer = optionalInt -> {
                if (optionalInt.isPresent()) {
                    int value = optionalInt.get();
                    toast(String.format("getAsLiveData() key:{%s}, new value: {%s}", key, value));
                } else {
                    toast(String.format("getAsLiveData() key:{%s} is not exist!", key));
                }
            };
            optionalLiveData.observe(this, observer);
        });

        findViewById(R.id.removeInt).setOnClickListener(v -> {
            String key = getKeyFromEditText();
            kvs.removeInt(key);
        });

        findViewById(R.id.clearExpired).setOnClickListener(v -> {
            kvs.clearExpired();
        });

        findViewById(R.id.clearAll).setOnClickListener(v -> {
            kvs.clearAll();
        });
    }

    private String getKeyFromEditText() {
        EditText keyEditText = findViewById(R.id.key);
        return keyEditText.getText().toString();
    }

    private int getValueFromEditText() {
        EditText valueEditText = findViewById(R.id.value);
        String value = valueEditText.getText().toString();
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return -1;
        }
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}