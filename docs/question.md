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

#### 显示「无法完成滑块验证」怎么办？
请将 `launcher/launcher.json` 中的登陆方式 `protocol` 改为手机登录：`ANDROID_PHONE`，再为启动脚本添加启动参数 `Dmirai.slider.captcha.supported`，也就是把机器人的启动脚本改成类似这样：
```bash
java -Dfile.encoding=UTF-8 -Dmirai.slider.captcha.supported -jar xiaoming-host-xxxx.jar
```
之后重新启动该脚本。在群内，或[这里](https://github.com/mzdluo123/TxCaptchaHelper)下载滑块验证助手，并将其安装在**安卓手机**上，将启动机器人时显示的弹框内容复制到滑块验证助手中，再将获得的一串文字复制回弹框下方的白色输入框（**这行白色的框不是装饰**）后关闭弹框即可。

#### 显示「设备异常，禁止登陆」怎么办？
将 `xiaoming-host-xxx` 复制到自己的设备上登录小明后，找到 `launcher/device.json`。将该文件替换服务器那边的 `launcher/device.json`，随后重新启动小明。


### 为什么机器人不响应消息？
#### 机器人启动了吗？
私聊发送 `call`。正常情况下机器人应该回复至今的调用次数。如果没有回复，请检查机器人是否启动、账号是否被封（一般封两三次后就不会再被封了）。

若启动遇到问题，请移步[无法启动机器人怎么办？](#无法启动机器人怎么办？)。

#### 本群启动小明了吗？


#### 为什么机器人只处理私聊消息，不处理群聊消息？

#### 
|检查项|做法|此项正常时|此项异常应|
|---|---|---|---|
|机器人是否启动|私聊机器人 `call`|机器人显示||
|检查该群是否启动了明确调用|私聊机器人 `明确调用`|机器人提示「明确调用尚未被启动」|检查是否相应群聊中启动了明确调用（群聊中发送 `本群标记`），如果带有 `clear-call` 标记或刚才小明提到的标记，则本群启动了明确调用，只有以特定的一串字符开头的消息才会被小明注意。可能是
|检查该群是否是响应群|在该群中发送 `本群启动小明`|

未完待续

## 开发者问题
