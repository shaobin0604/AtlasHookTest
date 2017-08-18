# AtlasHookTest

在 Android 中使用 DexClassLoader 加载一个 **未优化** 的 dex/apk/jar 文件会比较耗时。这在 Android 5.x 开始采用 ART 虚拟机后更加明显。消耗的时间主要是做 dex **优化**

- Dalvik 虚拟机: *dexopt*
- ART 虚拟机: *dex2oat*

插件框架需要通过 DexClassLoader 动态加载插件，首次加载插件需要耗费比较长的时间。

在阅读 [alibaba/atlas](https://github.com/alibaba/atlas) 时，发现其 `atlas-core/libs/armeabi/libdexinterpret.so` 和 `atlas-core/libs/android-art-interpret-3.0.0.jar` 可以通过 **禁用** dex2oat 达到加速 load dex 的效果

该项目用于测试 [alibaba/atlas](https://github.com/alibaba/atlas) 加速 load dex 的效果

- 测试设备 Nexus 4 CM13 Android 6.0.1 ART 虚拟机
- 模拟插件的 APK 为 assets 中的 `com.dijkstra.notely.apk` ，大小： 2.6M
  - 启用 *dex2oat* 加载该 APK 耗时：10 秒
  - 禁用 *dex2oat* 加载该 APK 耗时: 500 毫秒

<img src="AtlasHookTest/art/device.png" width="300px" />

# 相关项目

- [asLody/TurboDex](https://github.com/asLody/TurboDex)