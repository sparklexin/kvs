# kvs
Key-Value存储框架，核心特性：
1. 支持观察value变化
2. 支持设置key-value数据有效期
3. 支持删除过期数据
4. 支持类型：int/boolean/float/long/String/json，不同类型的key可以相同。

默认基于Room数据库实现，读写性能同room。
## 1. gradle配置
### 1.1 添加依赖
项目级`build.gradle`添加maven url
```groovy
allprojects {
	 repositories {
			...
			maven { url 'https://jitpack.io' }
	 }
}
```
模块`build.gradle`添加依赖
```groovy
dependencies {
	  implementation 'com.github.sparklexin:kvs:1.0.0'
}
```
### 1.2 java8配置
参考https://developer.android.com/studio/write/java8-support
```groovy
android {
  defaultConfig {
    // Required when setting minSdkVersion to 20 or lower
    multiDexEnabled true
  }

  compileOptions {
    // Flag to enable support for the new language APIs
    coreLibraryDesugaringEnabled true
    // Sets Java compatibility to Java 8
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
  }
}

dependencies {
  coreLibraryDesugaring 'com.android.tools:desugar_jdk_libs:1.0.9'
}
```
## 2. 使用
### 2.1 初始化KVS
在Application中初始化：
```java
KVS.initialize(KeyValueStoreFactory.create(application));
```
### 2.2 读写操作
支持类型：int、float、boolean、long、String、json
每种类型均有5种操作，以int为例：
| 方法                                                         | 描述                   |
| ------------------------------------------------------------ | ---------------------- |
| int getInt(String key, int defaultValue)                     | 获取数据               |
| LiveData<Optional<Integer>> getIntAsLiveData(String key)     | 监测数据变化           |
| void putInt(String key, int value)                           | 写入数据               |
| void putInt(String key, int value, long validDuration, TimeUnit timeUnit) | 写入数据并设置有效时长 |
| void removeInt(String key)                                   | 移除数据               |
```java
KeyValueStore kvs = KVS.defaultKeyValueStore();

// 写入 
kvs.putInt("key_age", 10);

// 写入并设置数据有效期：1天
kvs.putInt("key_name", "bob", 1, TimeUnit.DAYS);

// 读取数据
int age = kvs.getInt("key_age", 0);

// 监听数据
kvs.getIntAsLiveData("key_age").observe(optionalInt -> {
    ...
});

// 移除数据
kvs.removeInt("key_age");
```
### 2.3 清理数据
可配合定时任务(比如：WorkManager)进行清理数据
```java
KeyValueStore kvs = KVS.defaultKeyValueStore();
kvs.clearExpired();   // 清理过期数据
```