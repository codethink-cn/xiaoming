# 开发文档
欢迎阅读开发文档！本文讲解配置小明工程的方式、小明各 `API` 的功能等知识。**请确保在阅读之前，你已经熟悉[用户手册](/manual)。**

> 本文面向**具备编程基础的开发人员**。如果你是用户，请阅读[用户手册](/manual)。

如果你遇到了问题或希望小明具有新的功能，欢迎加入**小明及相关插件的技术交流 / 用户 QQ 群**：`1028959718`。

## 配置环境
> 小明的所有工程都是 `maven` 工程，这是很好用的 `Java` 项目的管理和构建工具。我们推荐使用 `Jetbrains` 公司开发的 `IDEA` 作为你的 `IDE`，这是令程序员感到舒适的集成开发环境。

小明为开发人员提供 `api` 和 `core` 两种依赖。`api` 是小明内部的所有接口的定义，`core` 则是它们的实现。下面以添加 `core` 为依赖为例。

### 将依赖 `jar` 放在工程中
请在**小明 QQ 群**中，或在 `Github` 中下载最新的 `xiaoming-core` 的 `jar` 文件，例如 `xiaoming-core-3.2-experimental.jar`。在工程下新建 `lib` 文件夹用于存放依赖，将其放入。再在 `pom.xml` 中的依赖中添加：
```xml
<dependency>
    <groupId>cn.chuanwise</groupId>
    <artifactId>xiaoming-core</artifactId>
    <version>3.2</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/lib/xiaoming-core-3.2-experimental.jar</systemPath>
</dependency>
```
重新载入 `maven` 工程即可开始使用小明内核。

### 让 `maven` 自动下载依赖
**这个方法可能会失败**

请在 `pom.xml` 的仓库中添加：
```xml
<repositories>
    <!--小明内核仓库-->
    <repository>
        <id>xiaoming-core</id>
        <url>https://www.github.com/Chuanwise/xiaoming-bot</url>
    </repository>
</repositories>
```
随后在依赖中添加
```xml
<dependency>
    <groupId>cn.chuanwise</groupId>
    <artifactId>xiaoming-core</artifactId>
    <version>3.2</version>
    <!-- 如果你的程序运行时具备小明 core，则可以取消下一行注释以降低 jar 大小 -->
    <!-- <scope>provided</scope> -->
</dependency>
```

### 直接使用示例插件
从 [插件范例](https://github.com/chuanwise/xiaoming-example) 处可以下载插件的示例工程，在此基础上修改也是很好的配置方式。

## 使用小明开发的方式
欢迎开发小明插件（小明调用你的功能），或让小明作为你的组件（你调用小明的功能）。

这两种开发方式事实上只有启动小明的区别。

### 开发小明插件
**请阅读[插件开发文档](/development/plugin)**

### 将小明作为组件
此方式需要通过代码启动小明机器人。请在[这里](https://github.com/Chuanwise/xiaoming-bot/releases)下载最新的小明内核和 `API`，导入项目后通过 `XiaomingBot` 类的 `start()` 方法启动小明：
```java
package cn.chuanwise.xiaoming.example;

import XiaomingBot;
import XiaomingBotImpl;

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