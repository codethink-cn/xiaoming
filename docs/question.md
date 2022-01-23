# 常见问题
请依次检查下面的问题下表格中的项目。如果所有项目无误但仍存在问题，请进入**技术指导 / 用户群**：[`1028959718`](https://jq.qq.com/?_wv=1027&k=sjBXo6xh) 询问。

## 普通用户问题
### 无法启动机器人怎么办？
#### 要求输入 QQ 时出现异常
你启动小明启动器的平台可能不允许接收标准输入。请在自己的设备启动小明后将`小明根目录/launcher/launcher.json` 复制到服务器上。你也可以新建文本文档，将其命名为 `launcher.json`，并写入以下内容：
```json
{
    "account": {
        "qq": 123456789,
        "md5": null,
        "password": "your-password"
    },
    "protocol": "ANDROID_PHONE",
    "autoReconnectOnForceOffline": false,
    "enableDeviceInfo": true
}
```
随后将其复制到服务器上`小明根目录/launcher`中，随后重启小明启动器。

#### 出现 `WrongPasswordException` 异常
如果是，可能是你的密码错误或你同时启动了多个相同账号的小明。若关闭其他小明后仍出现此问题，请删除 `launcher/launcher.json` 后再试。

#### 机器人启动器闪退怎么办？
**请严格按顺序检查下列项目：**

1. 设备上是否具有 `Java` 环境。打开控制台，输入 `java -version`，如果能看到 `Java` 版本，说明设备上具有 `Java` 环境，否则请下载并安装 `JVM` 或 `JRE`。版本要求 `Java 8` （也叫 `Java 1.8`）以上。
1. 启动脚本是否语法正确。`-jar` `-Dmirai.slider.captcha.supported` 和 `xiaoming-host-xxxx.jar` 之间都必须带有至少一个空格。
1. 检查 `launcher/launcher.json` 是否正确。下面是一个范例，**`password` 和 `protocol` 必须用英文双引号 `"` 包围**。
```json
{
    "account": {
        "qq": 123456789,
        "md5": null,
        "password": "password"
    },
    "protocol": "ANDROID_PHONE",
    "autoReconnectOnForceOffline": false
}
```


#### 显示「设备异常，禁止登陆」怎么办？
请删除 `小明根目录/launcher/device.json` 重新进行安全验证。

如果还不行，参照 [为什么机器人只处理私聊消息，不处理群聊消息？](#为什么机器人只处理私聊消息，不处理群聊消息？)。

#### 设备锁验证界面打不开
请点击蓝色的设备锁验证按钮，将打开的网址中的 `verify` 改为 `qrcode` 后刷新。

### 为什么机器人不响应消息？
#### 机器人启动了吗？
私聊发送 `call`。正常情况下机器人应该回复至今的调用次数。如果没有回复，请检查机器人是否启动、账号是否被封（一般封两三次后就不会再被封了）。

若启动遇到问题，请移步[无法启动机器人怎么办？](#无法启动机器人怎么办？)。

#### 为什么机器人只处理私聊消息，不处理群聊消息？
这是腾讯风控你的机器人导致，新机器人偶尔出现该问题。使用机器人账号登录一下[QQ安全中心](https://aq.qq.com/cn2/index)即可。

## 开发者问题
### 为什么我的插件抛出 `NoSuchMethodError` 或 `ClassNotFoundError`，且缺少的类类名以 `cn.chuanwise.xiaoming` 开头？
请更新 `core`、`api` 和 `host` 为最新版本。或至少同一版本。