# 小明机器人框架：xiaoming-bot 
小明机器人是一款插件化、便于上手、简单小巧的 QQ 机器人框架。

* QQ群：`1028582500`
* 作者：椽子
* 组织：太学

**请遵循 `Apache-2.0` 开源协议使用小明机器人框架**。

## 组件介绍
本项目有三个组件，api、core 和 host。
`api` 是一组小明调用标准，`core` 是对 api 的一种规范实现，`host` 是小明本体的启动器。

## 前置知识
* `Java` 基础知识：必须
* `Maven` 用法：必须
* `Git`：锦上添花

## 快速开始
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
package com.chuanwise.xiaoming.example;

import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.core.plugin.XiaomingPluginImpl;

/**
 * 插件主类示例
 * @author Chuanwise
 */
public class ExamplePlugin extends XiaomingPluginImpl {
}
```
这个插件已经能被小明加载了，但还没有任何功能。我们先将其打包运行吧！

## 打包运行
请在资源文件夹 `resources` 中创建 `plugin.json`，内容如下：
```json
{
  "name": "example-plugin-name",
  "main": "com.chuanwise.xiaoming.example.ExamplePlugin",
  "author": "Chuanwise",
  "version": "1.0.TEST",
  "fronts": [ "lexicons" ]
}
```
### `name`：插件名
选填，默认值为 `jar` 文件名。建议自行设计一个名字。

### `main`：插件主类名
必填，值为插件主类名。

### `version`：版本
选填，默认值为 `(unknown)`

### `fronts`：前置插件名列表
选填，默认值为空。小明只会在加载完全部 `fronts` 里的插件后加载本插件。

除上述内容外你还可以增加其他的键，可以在插件主类中获得他们的值。

将此插件打包为 `jar` 文件后放在 `小明根目录/plugins`，重新启动小明或执行小明指令 `#加载 <你的插件名>`（此热加载功能尚在实现中）即可加载本插件。

## 聊天消息处理
小明的聊天消息可分三类：群聊、私聊和群临时会话。它们都可以通过下述方式处理：

### 单句聊天消息
通过重写 `XiaomingPlugin` 类中的 `onMessage` 方法，可以实现对所有类型聊天消息的监听。例如：
```java
package com.chuanwise.xiaoming.example;

import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.core.plugin.XiaomingPluginImpl;

/**
 * 插件主类示例
 * @author Chuanwise
 */
public class ExamplePlugin extends XiaomingPluginImpl {
    @Override
    public boolean onMessage(XiaomingUser user) {
        if (user.inGroup()) {
            user.sendMessage(user.getMessage());
            return true;
        } else {
            return false;
        }
    }
}
```
上述例子展示了给用户发消息的方法 `user.sendMessage(...)`、判断用户当前位置的方法 `user.inGroup()` 和获得当前用户输入的方法：`user.getMessage()`。其功能是复读所有小明所在的群消息。

### 功能更强大的监听方式
小明支持上下文相关的消息交互。例如：用户输入`小明在吗`，小明回答`在，啥事`，用户继续输入`没事`或其他内容，小明视具体回答回复。

要实现这个功能，需要一个继承自 `com.chuanwise.xiaoming.core.interactor.message.MessageInteractorImpl` 的类作为交互器。在其中使用`com.chuanwise.xiaoming.api.annotation.Filter`注解该方法的触发信息。例如：
```java
package com.chuanwise.xiaoming.example.interactor;

import com.chuanwise.xiaoming.api.annotation.Filter;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.core.interactor.message.MessageInteractorImpl;

import java.util.Objects;

/**
 * 消息交互器示例
 * @author Chuanwise 
 */
public class MessageInteractorTest extends MessageInteractorImpl {
    // 收到 "小明在吗" 的消息时，该方法响应
    @Filter("小明在吗")
    public void onMessage(XiaomingUser user) {
        user.sendMessage("在，啥事");
        
        final String nextInput = user.nextInput();
        if (Objects.equals(nextInput, "没事")) {
            user.sendMessage("彳亍吧，下次有事记得叫我哦");
        } else {
            // 有事时的操作
        }
    }
}
```
这类响应聊天消息的方法，统称`交互方法`。存在交互方法的类，都是`交互器`。在插件启动时，你需要注册该交互器的实例。例如：
```java
package com.chuanwise.xiaoming.example;

import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.core.plugin.XiaomingPluginImpl;
import com.chuanwise.xiaoming.example.interactor.MessageInteractorTest;

/**
 * 插件主类示例
 * @author Chuanwise
 */
public class ExamplePlugin extends XiaomingPluginImpl {
    @Override
    public void onEnable() {
        // 注册一个交互器
        getXiaomingBot().getInteractorManager().register(new MessageInteractorTest(), this);
    }
}
```
交互方法起码要有一个 `Filter`（过滤器）注解。它有两种形式：
`@Filter("文本信息")`
`@Filter(value = "文本信息", pattern = FilterPattern.EQUALS)`


小明不喜欢那种无条件触发的交互方法，这可能会让小明所在的群非常吵，所以交互方法至少要有一个 `Filter`。但如果你仍然希望该交互方法被无条件触发，只需要使用`@Filter(value = "", pattern = FilterPattern.STARTS_WITH)`。或直接监听聊天消息。

<!-- ## 重要的类
### XiaomingUser
所有小明的用户，都是一个 `XiaomingUser` 的实例，就连控制台也不例外。本类将同一 `QQ` 用户在不同区域内的状态统一起来，以便进行上下文相关交互。

该类的方法有：

#### 发送消息类
boolean **sendMessage**(Object message, Object... arguments)

项目|详情
---|---
返回值类型|`boolean`
返回值含义|消息是否成功被发送
Object message|消息对象
Object... arguments|`message.toString()` 中包含的参数

小明会执行 `message.toString()`，并将其中的 `{}` 按顺序替换为 `arguments` 内的 `argument.toString()` 值。例如：
```java
String permissionNode = "test.node";
if (!user.hasPermission(permissionNode)) {
    user.sendMessage("小明不能帮你做这件事哦，因为你还缺少权限：{}", permissionNode);
}
``` -->


sendMessage

## 核心
### 过滤器：`Filter`
过滤器是一个注解，只能标注在方法上。负责验证信息并用于判断触发的交互方法。

参数名|含义|默认值（如果有）
---|---|---
value|由下一个参数而定|
pattern|过滤方式|`FilterPattern.PARAMETER`

`FilterPattern` 是过滤方式，是一个枚举类型，其所有可能的取值有：

值|触发交互方法的时机
---|---
`EQUALS`|消息等于 `value` 时
`EQUALS_IGNORE_CASE`|消息等于 `value` （忽略大小写）时
`STARTS_WITH`|消息以 `value` 开头时
`ENDS_WITH`|消息以 `value` 结尾时
`STARTS_REGEX`|消息开头匹配正则表达式 `value` 时
`ENDS_REGEX`|消息结尾匹配正则表达式 `value` 时
`MATCHES`|消息匹配正则表达式 `value` 时
`PARAMETER`|消息匹配提取参数的正则表达式 `value` 时

在使用 `PARAMETER` 作为过滤方式的过滤器的交互方法中，可以使用 `@FilterParameter("...")` 注解提取参数。例如：

```java
@Filter("禁止{what}")
public void filterTest1(XiaomingUser user, @FilterParameter("what") String what) {
    user.sendMessage("禁止禁止{}", what);
}
```
上述函数的第二个参数使用 `@FilterParameter("what")` 注解，小明将会把过滤器 `{what}` 处的值填自动填充到这里。例如当输入的消息是 `禁止复读` 时，`what` 的值会被设置成 `复读`。

`@FilterParameter` 注解还有另一种形式：`@FilterParameter(value = "what", defaultValue = "芜湖")`。其作用是当 `@Filter` 中的字符串没有出现 `{what}` 时，使用 `"芜湖"` 作为该变量的默认值。`defaultValue` 的默认值为空串 `""`。

值得一提的时，使用 `@FilterParameter` 的注解不一定必须是 `String` 类型。例如：
```java
@Filter("{qq}可爱吗")
public void filterTest3(XiaomingUser user, @FilterParameter("qq") long who) {
    if (who == 1437100907) {
        user.sendMessage("当然可爱呀，毕竟是我爸嗷 _(:з」∠)_");
    } else {
        user.sendMessage("也可爱，但没我爸可爱");
    }
}
```
实际输入时，在 `{qq}` 的位置可以输入 `QQ` 号或直接 `@` 相关用户。小明会提取对应的 `QQ` 号，并放在 `who` 变量中。

你可以通过重写当前交互器类的 `onParameter` 方法自由地处理此处的参数。该方法的信息为：

返回类型|返回含义
---|---
`Object`|此处应该填入的参数，如果为 `null` 则匹配不成功

如果为 `null`，小明会抛出异常并退出该交互器。

参数|含义
---|---
XiaomingUser user|当前调用者
Class<T> clazz|当前参数类
String parameterName|参数在 `@FilterParameter("...")` 中的名字
String currentValue|参数当前值
String defaultValue|参数默认值

### 交互器：`Interactor`
交互器通过交互方法和用户交互。是小明的主要组件之一。交互器分为两类：指令交互器和消息交互器，其区别在指令交互器多一个自动生成指令格式说明的功能。

交互器内部使用过滤器 `@Filter` 注解的方法被称为交互方法，是直接和用户交互的工具。在上述讲解过滤器的例子中我们已经了解了一些简单的交互方法，实际上交互方法还可以有除了 `XiaomingUser` 和使用 `@FilterParameter` 注解的参数之外的参数：

参数类型|自动填充内容
---|---
`FilterMatcher`|与当前输入匹配的当前交互方法的一个过滤验证器
`InteractorMethodDetail`|当前交互方法的一些细节

除此之外，你还可以通过重写 `onParameter` 的另一个实例以自动填充此处的参数。该方法的信息为：

返回类型|返回含义
---|---
`Object`|此处应该填入的参数，如果为 `null` 则匹配不成功

如果为 `null`，小明会抛出异常并退出该交互器。

参数|含义
---|---
XiaomingUser user|当前调用者
Parameter parameter|当前参数

## 示例插件
* `xiaoming-example`：(插件示例)[https://github.com/Chuanwise/xiaoming-example]