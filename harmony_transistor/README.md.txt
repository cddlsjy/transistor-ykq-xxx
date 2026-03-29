---
AIGC:
    ContentProducer: Minimax Agent AI
    ContentPropagator: Minimax Agent AI
    Label: AIGC
    ProduceID: "00000000000000000000000000000000"
    PropagateID: "00000000000000000000000000000000"
    ReservedCode1: 3045022100ed2b1ab01c61fbe56888a953b1d1d2601cd06e1194d939e4c5ad831122c2975902205f0d9964d6f809867d927d906d3d845c5f10576676b18b8c9bd43dfb1332027c
    ReservedCode2: 304402205482e279e82ce0241522dbeb7198c4830f593b1b2570ebb837257fdf0ee6ba9b02202bd389d5f15b33be924ee962b06a34ee6757f095fb38db36e7cdc6ce14062b9e
---

# Transistor - 华为鸿蒙系统版本

## 项目概述

本项目是将Android收音机应用 **Transistor** (v3.2.4) 转换为华为鸿蒙系统( HarmonyOS )的版本。

## 原始Android项目功能

- 网络广播流媒体播放 (MP3, AAC, Ogg/Opus)
- 电台列表管理 (添加、删除、重命名)
- 底部播放器界面
- 睡眠定时器功能
- 播放状态通知
- M3U/PLS播放列表解析
- 电台图标自动获取

## 鸿蒙系统转换说明

### 主要转换内容

#### 1. 核心架构转换

| Android组件 | 鸿蒙组件 | 说明 |
|------------|----------|------|
| MainActivity | EntryAbility | 主入口Ability |
| MainActivityFragment | Index.ets | 主页面(使用ArkTS) |
| PlayerService | AudioPlayerService | 音频播放服务 |
| Station.java | Station.ts | 数据模型 |
| RecyclerView Adapter | ForEach + List | 列表组件 |
| LiveData | AppStorage + @State | 状态管理 |
| ExoPlayer | AVPlayer | 音频播放引擎 |

#### 2. 文件结构

```
harmony_transistor/
├── AppScope/
│   ├── app.json5              # 应用配置
│   └── resources/
│       └── base/element/
│           └── string.json    # 应用名称
├── entry/
│   ├── build-profile.json5   # 模块构建配置
│   ├── oh-package.json5       # 依赖配置
│   └── src/main/
│       ├── module.json5      # 模块清单
│       ├── ets/
│       │   ├── application/
│       │   │   └── Application.ets  # 应用入口
│       │   ├── entryability/
│       │   │   └── EntryAbility.ets # Ability入口
│       │   ├── model/
│       │   │   └── Station.ts       # 数据模型
│       │   ├── pages/
│       │   │   └── Index.ets         # 主页面
│       │   ├── service/
│       │   │   └── AudioPlayerService.ts  # 音频服务
│       │   └── utils/
│       │       └── StorageHelper.ts  # 存储工具
│       └── resources/
│           ├── base/
│           │   ├── element/
│           │   │   ├── color.json    # 颜色资源
│           │   │   └── string.json  # 字符串资源
│           │   ├── media/            # 媒体资源
│           │   └── profile/
│           │       └── main_pages.json  # 页面配置
│           └── ...
└── build-profile.json5        # 项目构建配置
```

#### 3. 关键技术对比

| 功能 | Android实现 | 鸿蒙实现 |
|------|------------|----------|
| 状态管理 | LiveData | AppStorage + @State |
| 网络请求 | HttpURLConnection | @ohos.net.http |
| 音频播放 | ExoPlayer | AVPlayer |
| 存储 | SharedPreferences | dataPreferences |
| UI组件 | XML布局 | ArkTS声明式UI |
| 列表渲染 | RecyclerView | List + ForEach |
| 弹窗 | Dialog | 自定义Component |

### 待完善功能

由于时间限制，以下功能需要进一步开发：

1. **媒体会话服务** - MediaSession/MediaController对应实现
2. **前台通知** - 鸿蒙系统通知机制适配
3. **音频焦点管理** - AudioFocusHelper对应实现
4. **图片获取** - favicon获取和缓存
5. **国际化** - 多语言字符串资源
6. **深色模式** - 主题切换支持
7. **快捷方式** - 主屏幕快捷方式创建

## 开发环境要求

- DevEco Studio 3.1+
- HarmonyOS SDK API 9
- Node.js 16+

## 运行项目

1. 使用DevEco Studio打开项目
2. 连接设备或启动模拟器
3. 点击运行按钮

## 许可协议

本项目遵循MIT许可证，与原Android项目一致。

## 原项目信息

- 原项目: [Transistor](https://github.com/y20k/transistor)
- 版本: 3.2.4 ("Life on Mars?")
- 许可证: MIT
