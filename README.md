# 让 Salt Player for Windows 在播放时系统不会息屏

如果你的 Windows 系统设置了屏幕超时，但在播放音乐时想要一直让屏幕亮着，而你又不想频繁更改设置，那就试试这个插件吧！

启用插件后，如果处于播放状态，Windows就不会息屏，暂停后，Windows 就会遵守你的屏幕超时设置。

# 注意
此插件使用了win32 api，所以只能在 Windows 系统上启用。

对于非 Windows 系统，将不会有任何效果。

# 构建
前置条件：
+ Java 25
+ Gradle 9.6.1

```powershell
git clone https://github.com/Balinhui/SPW-Stay-Awake.git
cd .\SPW-Stay-Awake\
.\gradlew.bat plugin
```
