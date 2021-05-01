# 小明机器人框架：xiaoming-bot 
小明机器人是一款插件化、便于上手、简单小巧的 QQ 机器人框架。

* QQ群：`1028582500`
* 作者：椽子
* 组织：太学

**请遵循 `Apache-2.0` 开源协议使用小明机器人框架**。

## 组件介绍
本项目有三个组件，api、core 和 host。
`api` 是一组小明调用标准，`core` 是对 api 的一种规范实现，`host` 是小明本体的启动器。

## 开发文档
### 前置知识
* `Java` 基础知识：必须
* `Maven` 用法：必须
* `Git`：锦上添花

### 快速开始
使用 `mvn` 安装 `core` 后，便可以开始编写小明插件。首先在 `pom.xml` 中添加依赖：
```xml
<dependency>
    <groupId>com.taixue.xiaoming.bot</groupId>
    <artifactId>xiaoming-bot-core</artifactId>
    <version>1.0</version>
</dependency>
```
随后创建插件主类。插件主类必须实现 `com.taixue.xiaoming.bot.api.plugin.XiaomingPlugin` 接口。你可以选择继承自内核的实现 `com.taixue.xiaoming.bot.core.plugin.XiaomingPluginImpl`，例如：
```java
package com.taixue.xiaoming.bot.example;

import com.taixue.xiaoming.bot.core.plugin.XiaomingPluginImpl;

/**
 * 示例插件主类
 * @author Chuanwise
 */
public class ExamplePlugin extends XiaomingPluginImpl {
}
```
这个插件已经能被小明加载了，但还没有任何功能。我们先将其打包运行吧！

### 打包运行
请在资源文件夹 `resources` 中创建 `plugin.json`，内容如下：
```json
{
  "@class": "com.taixue.xiaoming.bot.core.plugin.PluginPropertyImpl",
  "name": "example-plugin-name",
  "main": "com.taixue.xiaoming.bot.example.ExamplePlugin",
  "author": "Chuanwise",
  "version": "1.0.TEST",
  "fronts": [ "lexicons" ]
}
```
#### @class
必须为 `com.taixue.xiaoming.bot.core.plugin.PluginPropertyImpl`

#### name：插件名
选填，默认值为 `jar` 文件名。建议自行设计一个名字。

#### main：插件主类名
必填，值为插件主类名。

#### version：版本
选填，默认值为 `(unknown)`

#### fronts：前置插件名列表
选填，默认值为空。小明只会在加载完全部 `fronts` 里的插件后加载本插件。

除上述内容外你还可以增加其他的键，可以在插件主类中获得他们的值。

将此插件打包为 `jar` 文件后放在 `小明根目录/plugins`，重新启动小明或执行小明指令 `#加载 <你的插件名>`（此热加载功能尚在实现中）即可加载本插件。

### 更进一步
尚未完工，敬请期待。

## 插件实例
* `xiaoming-bot-lexicons`：(词库插件)[https://github.com/Chuanwise/xiaoming-bot-lexicons/settings]，让你的小明回复自由起来，还有色图收集器的作用哦！
* `xiaoming-bot-toolkit`：(实用工具插件)[https://github.com/Chuanwise/xiaoming-bot-toolkit]：为你的小明增加一些鸡肋功能吧！
* `xiaoming-bot-nwu`：(西大插件)[https://github.com/Chuanwise/xiaoming-bot-nwu]，把你的小明推荐给你的老师吧！