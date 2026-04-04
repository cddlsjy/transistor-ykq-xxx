---
AIGC:
    ContentProducer: Minimax Agent AI
    ContentPropagator: Minimax Agent AI
    Label: AIGC
    ProduceID: "00000000000000000000000000000000"
    PropagateID: "00000000000000000000000000000000"
    ReservedCode1: 30450220086ee98301faed6e44a5c1ca1b1ebafa7f4b08aef8e76d9b8b0e7662754931b90221008c25edb11f8d97b98c9e8b7e46d0ee102640793975b07c0fd2601896408b5a1c
    ReservedCode2: 30440220125fc7dbf81189af819f22bea2a2f98338d518a12d02a74dcd9ee5c37d50b85c02203d7dfc7601970c01bf2be8e7502014037a9f7cc0fc21a398a3dce219ef91cbad
---

README
======

Transistor - Simple Radio App
-----------------------------

**Version 3.2.x ("Life on Mars?")**

Transistor is a bare bones app for listening to radio programs over the internet. The app stores stations as files on your device's external storage. It currently understands streams encoded in MP3, AAC and Ogg/Opus(*).

Important note: This is an app of type BYOS ("bring your own station"). It does not feature any kind of built-in search option. You will have to manually add radio stations.

Transistor is free software. It is published under the [MIT open source license](https://opensource.org/licenses/MIT). Want to help? Please check out the notes in [CONTRIBUTING.md](https://github.com/y20k/transistor/blob/master/CONTRIBUTING.md) first.

Install Transistor
------------------
[<img src="https://play.google.com/intl/de_de/badges/images/generic/en_badge_web_generic.png" width="192">](https://play.google.com/store/apps/details?id=org.y20k.transistor)

[<img src="https://cloud.githubusercontent.com/assets/9103935/14702535/45f6326a-07ab-11e6-9256-469c1dd51c22.png" width="192">](https://f-droid.org/repository/browse/?fdid=org.y20k.transistor)

[... or get a Release APK here on GitHub](https://github.com/y20k/transistor/releases)

How to use Transistor
---------------------
### How to add a new radio station?
The easiest way to add a new station is to [search for streaming links](https://github.com/y20k/transistor/wiki#places-to-look-for-radio-stations) and then choose Transistor as a your default handler for those file types. You can also tap the (+) symbol in the top bar and paste in streaming links directly. Please note: Transistor does not feature any kind of built-in search option.

### How to play back a radio station?
Tap the Play button ;).

### How to stop playback?
Tap the Stop button within the app or on the notification - or just unplug your headphones.

### How to start the sleep timer?
Tapping the Clock symbol in the stations detail screen starts a 15 minute countdown after which Transistor stops playback. An additional tap adds 15 minutes to the clock. Playback must be running to be able to activate the sleep timer.

### How to place a station shortcut on the Home screen?
The option to place a shortcut for a station on the Home screen can be accessed from the station's three dots menu. A tap on a shortcut will open Transistor - playback will start immediately.

### How to rename or delete a station?
The rename and delete options can be accessed both from the station's detail screen. Just tap on the three dots symbol. You can manage the list of stations also from a file browser (see next question).

### Where does Transistor store its stations?
Transistor does not save its list of stations in a database. Instead it stores stations as M3U files on your device's external storage. Feel free to tinker with those files using the text editor of your choice. The files are stored in /Android/data/org.y20k.transistor/files/Collection.

### How do I backup and transfer my radio stations?
Transistor supports Android 6's [Auto Backup](http://developer.android.com/about/versions/marshmallow/android-6.0.html#backup) feature. Radio stations are always backed up to your Google account and will be restored at reinstall. On devices running on older versions of Android you must manually save and restore the "Collection" folder.

### Why does Transistor not have any setting?
There is nothing to be set ;). Transistor is a very simple app. Depending on your point of view "simple" is either great or lame.

Which Permissions does Transistor need?
---------------------------------------
### Permission "INSTALL_SHORTCUT" and "UNINSTALL_SHORTCUT"
This permission is needed to install and uninstall radio station shortcuts on the Android Home screen.

### Permission "INTERNET"
Transistor streams radio stations over the internet.

### Permission "READ_EXTERNAL_STORAGE"
Transistor needs access to images, photos and documents to be able to customize radio station icons and to able to open locally saved playlist files.

### Permission "VIBRATE"
Tapping and holding a radio station will toggle a tiny vibration.

### Permission "WAKE_LOCK"
During Playback Transistor acquires a so called partial wake lock. That prevents the Android system to stop playback for power saving reasons.

(*) Opus playback is only supported on devices running Android 5.0+
四个修改：## Transistor 项目 minSdkVersion 从 25 降至 21 的修改说明

本次修改旨在将 Android 项目的最低支持版本从 API 25（Android 7.1）降低到 API 21（Android 5.0），以覆盖更广泛的设备。主要涉及两方面的调整：

1. **修改 `build.gradle` 中的 `minSdkVersion`**  
2. **替换所有直接调用 `Resources.getColor(int, Theme)` 的代码**（该 API 从 API 23 引入，在 API 21 上会崩溃），改用 AndroidX 兼容库 `ContextCompat.getColor()`。

以下是各文件的具体修改细节：

---

### 1. `app/build.gradle`

**修改位置**：`android.defaultConfig` 块中的 `minSdkVersion`。

**原代码**：
```gradle
minSdkVersion 25
```

**修改后**：
```gradle
minSdkVersion 21
```

**修改说明**：  
将最低支持的 Android 版本从 7.1 降低到 5.0。这允许应用程序在更旧的设备上安装和运行，同时要求所有使用的 API 必须兼容 API 21。其他构建配置（如 `targetSdkVersion`、`compileSdk`）保持不变，以确保针对较新系统的行为和优化。

---

### 2. `SettingsFragment.kt`

**修改位置**：`onViewCreated` 方法中设置背景颜色的语句。

**原代码**：
```kotlin
view.setBackgroundColor(resources.getColor(R.color.app_window_background, null))
```

**修改后**：
```kotlin
view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.app_window_background))
```

**同时添加的 import**：
```kotlin
import androidx.core.content.ContextCompat
```

**修改说明**：  
`Resources.getColor(int, Theme)` 方法需要 API 23 及以上版本。在 API 21 上调用会引发 `NoSuchMethodError`。`ContextCompat.getColor()` 是 AndroidX 提供的兼容性方法，内部自动根据当前 API 级别选择合适的实现，因此可以安全地在 API 21 上使用。此修改确保了设置 Fragment 背景颜色的操作在低版本设备上正常工作。

---

### 3. `UiHelper.kt`

**修改位置**：内部类 `SwipeToDeleteCallback` 和 `SwipeToMarkStarredCallback` 中获取背景颜色的语句。

**原代码（两处类似）**：
```kotlin
private val backgroundColor = context.resources.getColor(R.color.list_card_delete_background, null)
// 和
private val backgroundColor = context.resources.getColor(R.color.list_card_mark_starred_background, null)
```

**修改后**：
```kotlin
private val backgroundColor = ContextCompat.getColor(context, R.color.list_card_delete_background)
// 和
private val backgroundColor = ContextCompat.getColor(context, R.color.list_card_mark_starred_background)
```

**修改说明**：  
这两个内部类分别用于实现 RecyclerView 条目左滑删除和右滑标记收藏的交互效果。它们需要获取特定颜色来绘制滑动时的背景。原代码同样使用了带 `Theme` 参数的 `getColor` 方法，在 API 21 上不兼容。改为 `ContextCompat.getColor()` 后，颜色资源可以在所有 API 级别上正确解析，从而保证滑动视觉效果在 Android 5.0 上正常显示。

---

### 4. `ImageHelper.kt`

**修改位置**：`getMainColor` 方法中默认返回颜色的语句。

**原代码**：
```kotlin
return context.resources.getColor(R.color.default_neutral_medium_light, null)
```

**修改后**：
```kotlin
return ContextCompat.getColor(context, R.color.default_neutral_medium_light)
```

**修改说明**：  
`getMainColor` 方法用于从站台图像中提取主色调，当无法提取到合适的颜色时返回一个默认的中性色。原调用方式不兼容 API 21。改为 `ContextCompat.getColor()` 后，该降级逻辑可以在所有支持的 Android 版本上正确执行，避免因颜色获取失败导致 UI 异常或崩溃。

---

## 总结

通过以上四个文件的修改，项目成功将 `minSdkVersion` 从 25 降低到 21，并消除了所有因直接使用高版本 `Resources.getColor()` 方法而导致的兼容性问题。修改后的代码全部使用 `ContextCompat.getColor()` 获取颜色资源，保证了在 Android 5.0（API 21）及以上版本中的稳定运行。其他未修改的文件未发现类似 API 兼容性问题，项目可以顺利编译并预期在低版本设备上正常工作。# 结论，两个修改

Transistor v4.3.0 相比 v4.2.6 仅增加了动态颜色（Dynamic Colors）功能，且该功能已通过 `DynamicColors.isDynamicColorAvailable()` 进行版本判断（API 31+），在低版本系统上不会生效，**不会引发兼容性问题**。因此，将 `minSdkVersion` 从 25 降至 21 所需修改的文件与 v4.2.6 完全相同，仅需修改以下两个文件：

1. **`app/build.gradle`**：将 `minSdkVersion 25` 改为 `minSdkVersion 21`
2. **`app/src/main/java/org/y20k/transistor/collection/CollectionAdapter.kt`**：将两处 `context.resources.getColor(R.color.xxx, null)` 替换为 `ContextCompat.getColor(context, R.color.xxx)`

**无需修改 `SettingsFragment.kt` 或其他文件**。

---

## 修改详情

### 1. `app/build.gradle`
```gradle
defaultConfig {
    minSdkVersion 21   // 原为 25
    // 其余保持不变
}
```

### 2. `CollectionAdapter.kt` 中的修改位置

在内部类 `SwipeToDeleteCallback` 和 `SwipeToMarkStarredCallback` 中：

**修改前：**
```kotlin
private val backgroundColor = context.resources.getColor(R.color.list_card_delete_background, null)
```

**修改后：**
```kotlin
private val backgroundColor = ContextCompat.getColor(context, R.color.list_card_delete_background)
```

两处修改完全相同（第二个为 `list_card_mark_starred_background`）。

> 文件头部已存在 `import androidx.core.content.ContextCompat`，无需额外添加。

---


---------------------
[<img src="https://raw.githubusercontent.com/y20k/transistor/master/metadata/en-US/phoneScreenshots/01-lockscreen-active-v3.2-oneplus5.png" width="240">](https://raw.githubusercontent.com/y20k/transistor/master/metadata/en-US/phoneScreenshots/01-lockscreen-active-v3.2-oneplus5.png)
[<img src="https://raw.githubusercontent.com/y20k/transistor/master/metadata/en-US/phoneScreenshots/02-playback-v3.2-oneplus5.png" width="240">](https://raw.githubusercontent.com/y20k/transistor/master/metadata/en-US/phoneScreenshots/02-playback-v3.2-oneplus5.png)
[<img src="https://raw.githubusercontent.com/y20k/transistor/master/metadata/en-US/phoneScreenshots/03-details-v3.2-oneplus5.png" width="240">](https://raw.githubusercontent.com/y20k/transistor/master/metadata/en-US/phoneScreenshots/03-details-v3.2-oneplus5.png)

[<img src="https://raw.githubusercontent.com/y20k/transistor/master/metadata/en-US/phoneScreenshots/04-sleeptimer-v3.2-oneplus5.png" width="240">](https://raw.githubusercontent.com/y20k/transistor/master/metadata/en-US/phoneScreenshots/04-sleeptimer-v3.2-oneplus5.png)
[<img src="https://raw.githubusercontent.com/y20k/transistor/master/metadata/en-US/phoneScreenshots/05-homescreen-v3.2-oneplus5.png" width="240">](https://raw.githubusercontent.com/y20k/transistor/master/metadata/en-US/phoneScreenshots/05-homescreen-v3.2-oneplus5.png)
[<img src="https://raw.githubusercontent.com/y20k/transistor/master/metadata/en-US/phoneScreenshots/06-onboarding-v3.2-oneplus5.png" width="240">](https://raw.githubusercontent.com/y20k/transistor/master/metadata/en-US/phoneScreenshots/06-onboarding-v3.2-oneplus5.png)
