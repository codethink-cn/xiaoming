### 小明机器人框架 xiaoming-bot
# 开发文档
> 你当前的位置：[项目 README](https://github.com/Chuanwise/xiaoming-bot) -> [文档](https://github.com/Chuanwise/xiaoming-bot/tree/main/docs) -> 开发文档

欢迎来到小明机器人框架的开发文档。

## 小明开发方式
将你编写的代码和小明结合起来，一共有两种方式：小明调用你的代码（开发小明插件）和你调用小明（小明作为你的组件）。它们的区别主要在启动小明。

## 小明内部的组件
### 小明本体
小明本体的 `API` 为 `com.chuanwise.xiaoming.api.bot.XiaomingBot`。几乎在小明的任何组件处，都能通过 `getXiaomingBot()` 获得该组件所属的机器人。小明本体有许多其他组件的访问器，可由此调用小明的其他组件。

除了上述访问其他组件的访问器，还有这些方法：
|      返回类型       |       方法名       |       说明         |
|--------------------|--------------------|-------------------|
|       `void`       |      `start()`     |      启动小明      |
|       `void`       |       `stop()`     |      关闭小明      |
|       `void`       |     `isStop()`     | 判断小明是否被关闭  |
|       `void`       |    `optimize()`    |    优化小明性能     |

### 各类插件
* 插件开发文档：[PluginDevelopment](https://github.com/Chuanwise/xiaoming-bot/tree/main/docs/PluginDevelopment.md)

### 其他组件
* 插件管理器：[PluginManager](https://github.com/Chuanwise/xiaoming-bot/tree/main/docs/PluginManager.md)
* 交互器管理器：[InteractorManager](https://github.com/Chuanwise/xiaoming-bot/tree/main/docs/InteractorManager.md)
* 调度器：[Scheduler](https://github.com/Chuanwise/xiaoming-bot/tree/main/docs/Scheduler.md)
* 未完待续


## 启动小明的方式
### 开发小明插件
使用小明启动你的插件时，需要具备 `Java` 环境及小明的启动器。我们提供了一款能满足基础需求的启动器：`xiaoming-host`。请在[这里](https://github.com/Chuanwise/xiaoming-bot/releases)下载最新的`xiaoming-host`。

使用 `Java` 启动该 `jar` 文件。你可以在同目录下创建脚本 `start.bat`，并输入以下内容保存后双击启动：
```bash
java -jar xiaoming-host-xxx.jar
```
若是初次在当前设备上登录，则涉及滑块验证等。

请在 `mirai` 运行时中添加 `JVM` 属性 `mirai.slider.captcha.supported` (添加参数 `-Dmirai.slider.captcha.supported`) ，例如：
```bash
java -Dmirai.slider.captcha.supported -jar xiaoming-host-xxx.jar
```
之后重新启动该脚本。下载[滑块验证助手](https://github.com/mzdluo123/TxCaptchaHelper)，将 `mirai` 显示的弹框内容复制到滑块验证助手中，再将获得的 `ticket` 复制回 `mirai` 显示的弹框后关闭即可。

如果上述方式仍无法正确通过滑块验证，请查阅[最新的 Mirai 滑块验证模块文档](https://github.com/project-mirai/mirai-login-solver-selenium)。

将插件 jar 文件放在小明目录的 plugins 文件夹中，重启小明，或执行以下指令：
```xiaoming
刷新插件
加载插件 <你的插件名>
```
即可加载你的插件。

### 将小明作为组件
此方式需要通过代码启动小明机器人。请在[这里](https://github.com/Chuanwise/xiaoming-bot/releases)下载最新的小明内核和 `API`，导入项目后通过 `XiaomingBot` 类的 `start()` 方法启动小明：
```java
package com.chuanwise.xiaoming.example;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.core.bot.XiaomingBotImpl;

/**
 * 程序中调用小明示例
 * @author Chuanwise
 */
public class EnableXiaomingExample {
    public static void main(String[] args) {
        // 第一个参数为 QQ，第二个为密码
        final long qq = 123456789;
        final String password = "password";

        XiaomingBot xiaomingBot = new XiaomingBotImpl(qq, password);
        xiaomingBot.start();

        // ...
        xiaomingBot.stop();
    }
}
```
如果你已经有现成的 `Mirai` 协议的 `Bot`，可以使用 `xiaomingBot.setMiraiBot(Bot miraiBot)` 直接设置现成的。