# 小明机器人框架：xiaoming-bot 
小明机器人是一款基于 Mirai 的插件化、便于上手、简单小巧的通用 QQ 机器人框架。

* QQ群：`1028959718`
* 作者：`椽子`

**请遵循 `Apache-2.0` 开源协议使用小明机器人框架**。

## 组件介绍
本项目有三个组件，`api`、`core` 和 `host`。`api` 是一组小明调用标准，`core` 是对 api 的一种规范实现，`host` 是小明本体的启动器。

## 前置知识
* `Java` 基础知识：必须
* `Maven` 用法：必须
* `Git`：锦上添花

## 快速开始
你可以下载最新的 `core` `RELEASE`，将之添加在项目的库中，或使用 `mvn` 安装 `core` 后，在 `pom.xml` 中添加依赖：
```xml
<dependency>
    <groupId>com.chuanwise</groupId>
    <artifactId>xiaoming-core</artifactId>
    <version>1.0</version>
</dependency>
```
小明通过插件主类加载插件，所以我们需要先创建一个插件主类。插件主类继承自`com.chuanwise.xiaoming.core.plugin.XiaomingPluginImpl`，例如：

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
这个文件中这些属性会有特殊的作用：

类型|属性名|是否必填|说明
---|---|---|---
字符串|`name`|否|建议填上，否则小明会将 `jar` 文件名作为插件名
字符串|`main`|是|插件主类名
字符串|`version`|否|建议填上，否则默认为`unknown`
字符串列表|`fronts`|否|前置插件名列表

除上述内容外你还可以增加其他的键，可以在插件主类中获得他们的值。

将此插件打包为 `jar` 文件后放在 `小明根目录/plugins`，重新启动小明或执行小明指令 `刷新插件` 和 `加载 <你的插件名>` 即可加载本插件。

## 聊天消息处理
小明的聊天消息可分三类：群聊、私聊和群临时会话。它们都可以通过下述方式处理：

### 单句聊天消息
通过重写 `XiaomingPlugin` 类中的 `onMessage` 方法，可以实现对所有类型聊天消息的监听。例如：
```java
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
其功能是复读所有小明所在的群消息。

`onMessage` 方法的参数是当前的小明使用者，返回值含义为插件是否与用户交互。如果为 `true`，将会记录一次交互并触发一次 `PluginResponseEvent`。

### 功能更强大的监听方式
小明支持上下文相关的消息交互。例如：用户输入`小明在吗`，小明回答`在，啥事`，用户继续输入`没事`或其他内容，小明视具体回答回复。

要实现这个功能，需要一个继承自 `com.chuanwise.xiaoming.core.interactor.message.MessageInteractorImpl` 的类作为交互器。在其中使用`com.chuanwise.xiaoming.api.annotation.Filter`注解该方法的触发信息。例如：
```java
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
public class ExamplePlugin extends XiaomingPluginImpl {
    @Override
    public void onEnable() {
        // 注册一个交互器
        getXiaomingBot().getInteractorManager().register(new MessageInteractorTest(), this);
    }
}
```
交互方法起码要有一个 `Filter`（过滤器）注解。它有两种形式：
`@Filter("文本信息")`<br>
`@Filter(value = "文本信息", pattern = FilterPattern.EQUALS)`

小明不喜欢那种无条件触发的交互方法，这可能会让小明所在的群非常吵，所以交互方法至少要有一个 `Filter`。但如果你仍然希望该交互方法被无条件触发，只需要使用`@Filter(value = "", pattern = FilterPattern.STARTS_WITH)`。或直接监听聊天消息。

`pattern` 取值等详细信息见 [交互器：`Interactor`](#交互器：`Interactor`)。

## 核心
小明由本体和各个组件组成。

### 小明本体：`XiaomingBot`
几乎在所有的类下，你都可以通过 `getXiaomingBot()` 获得该类所属的小明机器人本体。本体提供了大量组件的获取方法，方便你进行各类操作。

小明本体的方法主要是各类组件的引用，即以 `get` 开头的大部分无参方法。

方法名|组件说明
---|---
`getUserCallLimitManager()`|用户调用限制器
`getTextManager()`|说明文本管理器
`getRegularPreserveManager()`|文件定时保存线程
`getEventListenerManager()`|监听器管理器
`getResponseGroupManager()`|响应群管理器
`getFilePreservableFactory()`|文件加载器
`getReceptionistManager()`|接待线程管理器
`getLicenseManager()`|强制验证管理器
`getPluginManager()`|插件管理器
`getInteractorManager()`|交互器管理器
`getConsoleXiaomingUser()`|控制台小明用户
`getStatistician()`|统计数据管理器
`getAccountManager()`|账户管理器
`getWordManager()`|提示语管理器
`getPermissionManager()`|权限管理器
`getService()`|小明线程池
`getConfig()`|小明配置信息

这些组件将在下文逐一介绍。

此外还有一些功能性方法：

方法名|组件说明
---|---
`load()`|重新加载所有小明组件
`load(String name)`|重新加载指定的小明组件
`setMiraiBot()`|设置小明核心的 `mirai` 机器人
`getMiraiBot()`|获得小明核心的 `mirai` 机器人
`isStop()`|判断小明是否停机
`start()`|启动小明
`setConsoleXiaomingUser()`|设置小明的控制台用户
`execute(Thread thread)`|执行一个线程
`execute(Runnable runnable)`|执行一个线程
`stop()`|关闭小明

你的线程应该实现 `XiaomingThread` 接口。该接口只有两个方法：`void stop()` 和 `void forceStop()`。在关闭小明时，小明会尝试先后调用两个方法。如果仍然不能关闭线程，会尝试强制关闭。这可能对你的数据有所损伤。

请不要直接使用类似 `new Thread(runnable).start();` 的方式执行线程。请采用 `getXiaomingBot().execute(runnable)` 的方式。只有通过这种方式启动的线程才会收到小明的关闭通知。

### 小明使用者：`XiaomingUser`
每一个小明的使用者都是该类的对象，就连控制台也不例外。

`XiaomingUser` 类有非常多实用方法，主要有三类：发送消息类、接收消息类和其他类。

#### 发送消息类
发送消息类方法有很多的重载形式，返回值皆为 `boolean` ，表示消息是否被发送成功。倒数两个参数一般是 `String` 和 `Object...`。前者是消息内容，其中可以存在 `{}`，将会被按顺序替换为 `Object...` 中的参数。例如：`user.sendPrivateMessage("小明不能帮你做这件事哦，因为你缺少权限：{}", permissionNode)`，等同于 `user.sendPrivateMessage("小明不能帮你做这件事哦，因为你缺少权限：" + permissionNode)`。

方法原型|说明
---|---
`sendError(String, Object...)`|给当前用户发送错误消息
`sendWarn(String, Object...)`|给当前用户发送警告消息
`sendMessage(String, Object...)`|给当前用户发送普通消息
`sendPrivateError(String, Object...)`|给当前用户私发错误消息
`sendPrivateWarn(String, Object...)`|给当前用户私发警告消息
`sendPrivateMessage(String, Object...)`|给当前用户私发普通消息

上述方法是通常使用的发送消息的方法。此外你还可以使用下列方法：

方法原型|说明
---|---
`sendGroupMessage(String, Object...)`|如果小明为群聊或群临时会话用户，则向其对应的群中发送消息
`sendGroupMessage(long, String, Object...)`|在指定的群中发消息
`sendGroupAtMessage(String, Object...)`|先 @ 用户，再给用户发消息
`sendGroupAtMessage(long, long, String, Object...)`|在指定的群中先 @ 特定用户，再给其发消息
`sendPrivateMessage(String, Object...)`|给当前小明用户发送私聊消息
`sendPrivateMessage(long, String, Object...)`|给指定的用户发送私聊消息
`sendPrivateMessage(long, long, String, Object...)`|给指定的群中的用户发送私聊消息。第一个 `long` 为群号，第二个为 `QQ` 号。

#### 接收消息类
接收消息类的方法都以 `next` 开头。它们最后几个参数 `long`, `Function` 的含义是最长等待时间和超时后执行的方法。其余参数随具体情况而定：

前四个方法是最常用的方法，它会自动判断当前的位置（在群里、私聊还是临时会话？）自动选择需要调用的方法。

返回类型|方法原型|说明
---|---|---
`String`|`nextInput(long)`|获得用户的下一次输入。最久等待指定时间（单位：毫秒）
`String`|`nextInput(Function)`|获得用户的下一次输入。等待十分钟后执行方法
`String`|`nextInput()`|获得用户的下一次输入，等待十分钟后退出当前交互方法
`String`|`nextInput(long, Function)`|获得用户的下一次输入。最久等待指定时间（单位：毫秒）后执行特定的超时方法

获得用户在来自指定群的临时会话中的下一次输入。第一个参数为群号。

返回类型|方法原型
---|---
`String`|`nextTempInput(long, long)`
`String`|`nextTempInput(long)`
`String`|`nextTempInput(long, long, Function)`
`String`|`nextTempInput(long, Function)`

获得用户在群的临时会话中的下一次输入。第一个参数为群号。

返回类型|方法原型
---|---
`String`|`nextGroupInput(long, long)`
`String`|`nextGroupInput(long)`
`String`|`nextGroupInput(long, Function)`
`String`|`nextGroupInput(long, long, Function)`

获得用户在私聊中的下一次输入。第一个参数为群号。

返回类型|方法原型
---|---
`String`|`nextPrivateInput(long, Function)`
`String`|`nextPrivateInput()`
`String`|`nextPrivateInput(long)`
`String`|`nextPrivateInput(Function)`

默认超时后会退出当前交互器。你可以通过捕捉 `InteractorTimeoutException` 异常以阻止超时退出。

#### 其他
在发送较长的消息时，使用 `StringBuffer` 构造字符串存在换行的麻烦。小明内也集成了一个 `StringBuffer`，用来收集若干次 `sendMessage` 类消息的输出。与之相关的方法有：

返回类型|方法原型|说明
---|---|---
`StringBuffer`|`getBuffer()`|获得当前的消息缓冲区
`void`|`appendBuffer(String)`|在当前消息缓冲区中增加一行文字
`void`|`enableBuffer()`|接下来让小明将消息存放在缓冲区中
`void`|`setUsingBuffer(boolean)`|启动或关闭缓冲区
`void`|`clearBuffer()`|清除缓冲区信息
`String`|`getBufferAndClear()`|提取缓冲区消息，并清除后关闭缓冲区
`boolean`|`isUsingBuffer()`|判断当前是否正在使用缓冲区

你可以通过缓冲区机制实现多次输出的合并。例如小明有一个指令是 `批处理<remain>`，你可以通过类似下面的方法避免频繁发送每次指令执行时的输出：

```java
package com.chuanwise.xiaoming.core.interactor.core;

// import ...

/**
 * 全局指令处理器
 * @author Chuanwise
 */
public class GlobalCommandInteractor extends CommandInteractorImpl {
    public static final String BAT_REGEX = "(批处理|bat)";
    /**
     * 批处理指令
     * @param user 指令执行者
     * @param remain 指令
     */
    @Filter(BAT_REGEX + "{remain}")
    public void onMultipleCommands(XiaomingUser user, @FilterParameter("remain") String remain) {
        final String[] subCommands = remain.split(Pattern.quote("\\n"), 0);

        // 接下来使用小明缓冲区，将输出到这里
        user.enableBuffer();
        int commandNumber = 0;
        try {
            for (int i = 0; i < subCommands.length; i++) {
                String command = subCommands[i];
                if (command.isEmpty()) {
                    continue;
                }
                user.setMessage(command);
                if (getXiaomingBot().getInteractorManager().onCommand(user)) {
                    commandNumber++;
                } else {
                    user.sendError("无效的命令：{}，批处理任务被中断。", command);
                    break;
                }
            }
        } catch (Exception exception) {
            user.sendError("执行{}个指令时出现异常，批处理任务被中断。");
            exception.printStackTrace();
        }

        // 提取缓冲区信息，恢复正常输出
        final String bufferString = user.getBufferAndClear();
        user.sendPrivateMessage(bufferString);

        if (commandNumber == 0) {
            user.sendError("小明没能成功执行任何一个指令");
        } else {
            user.sendMessage("成功执行了 {} 个指令", commandNumber);
        }
    }
}
```

此外小明还有记录最近几条有效输入记录的功能。它主要用于异常报告，但也可供平时使用。与之相关的方法为：

返回类型|方法原型|说明
---|---|---
`List<String>`|`getRecentInputs()`|获得最近的几次有效输入
`void`|`clearRecentInputs()`|清除最近几次有效输入

判断和获取用户会话环境的方法：

返回类型|方法原型|说明
---|---|---
`boolean`|`inPrivate()`|判断用户当前是否在私聊
`boolean`|`inGroup()`|判断用户当前是否在群聊
`boolean`|`inTemp()`|判断用户当前是否在临时会话
`Group`|`getGroup()`|如果用户当前在临时会话或群聊，获得相关的群
`Friend`|`getAsPrivate()`|如果此时为私聊，获得私聊会话
`Member`|`getAsTempMember()`|如果此时为临时会话，获得临时会话
`Member`|`getAsGroupMember()`|如果此时为群聊，获得群聊会话
`ResponseGroup`|`getResponseGroup()`|如果此时为临时会话或群聊，获得对应的响应群（临时会话时可能会失败）
`long`|`getQQ()`|获取用户 `QQ`
`String`|`getMessage()`|获取用户输入
`void`|`setMessage(String)`|改变用户输入

其他相关方法：

返回类型|方法原型|说明
---|---|---
`String`|`getCompleteName()`|获取用户全名（包含群号等信息）
`Receptionist`|`getReceptionist()`|获得该用户的接待线程
`boolean`|`hasPermission(String[])`|判断用户是否有所需权限
`boolean`|`hasPermission(String)`|判断用户是否有所需权限
`boolean`|`requirePermission(String)`|当用户没有所需权限时提醒，并返回 `false`
`boolean`|`isBlockPlugin(String)`|判断用户是否屏蔽某插件
`Account`|`getAccount()`|获取该用户在小明这里的账户信息。如果此前无相关信息返回 `null`
`Account`|`getOrPutAccount()`|获取或新建该用户在小明这里的账户信息
`XiaomingBot`|`getXiaomingBot()`|获取小明本体

### 用户调用限制器：`UserCallLimitManager`
其主要的方法只有两个，分别为：
方法名|说明
---|---
`getPrivateCallLimiter()`|获得最近的私聊调用限制记录
`getGroupCallLimiter()`|获得最近的群聊调用限制记录

上述方法返回的都是 `UserCallLimiter` 类型的对象。该类型判断是否到调用限制所用的工具，其方法主要有：

方法名|说明
---|---
`isTooManySoUncallable(long qq)`|判断用户是否因为调用次数过多而达到限制
`addCallRecord(long qq)`|增加一条新的调用记录
`shouldNotice()`|判断是否应该提醒用户
`uncallable(long qq)`|判断用户是否能调用
`getCallRecords(long qq)`|获得某个用户最近的调用记录。如果无记录返回 `null`
`getOrPutCallRecords(long qq)`|获得或新增某个用户最近的调用记录
`isTooFastSoUncallable(long qq)`|判断用户是否因为调用过快而达到限制
`getConfig()`|获得当前的调用限制配置
`setConfig(CallLimitConfig config)`|设置新的调用限制配置

### 过滤器：`Filter`
过滤器是一个注解，只能标注在方法上。负责验证信息并用于判断触发的交互方法。

参数名|含义|默认值（如果有）
---|---|---
`value`|由下一个参数而定|
`pattern`|过滤方式|`FilterPattern.PARAMETER`

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
public class FilterTestInteractor extends CommandInteractorImpl {
    @Filter("禁止{what}")
    public void filterTest1(XiaomingUser user, @FilterParameter("what") String what) {
        user.sendMessage("禁止禁止{}", what);
    }
}
```
上述函数的第二个参数使用 `@FilterParameter("what")` 注解，小明将会把过滤器 `{what}` 处的值填自动填充到这里。例如当输入的消息是 `禁止复读` 时，`what` 的值会被设置成 `复读`。

`@FilterParameter` 注解还有另一种形式：`@FilterParameter(value = "what", defaultValue = "芜湖")`。其作用是当 `@Filter` 中的字符串没有出现 `{what}` 时，使用 `"芜湖"` 作为该变量的默认值。`defaultValue` 的默认值为空串 `""`。

值得一提的时，使用 `@FilterParameter` 的注解不一定必须是 `String` 类型。例如：
```java
public class FilterTestInteractor extends CommandInteractorImpl {
    @Filter("{qq}可爱吗")
    public void filterTest3(XiaomingUser user, @FilterParameter("qq") long who) {
        if (who == 1437100907) {
            user.sendMessage("当然可爱呀，毕竟是我爸嗷 _(:з」∠)_");
        } else {
            user.sendMessage("也可爱，但没我爸可爱");
        }
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

如果为 `null`，则用户不会与当前方法交互。

参数|含义
---|---
`XiaomingUser user`|当前调用者
`Parameter parameter`|当前参数

例如在下面的例子里，通过重写 `onParameter` 实现自动填充禁言时长：
```java
public class AdminInteractor extends CommandInteractorImpl {
    /**
    * 通过重写 onParameter 实现自定义参数类型填充
    */
    @Override
    public <T> Object onParameter(XiaomingUser user, Class<T> clazz, String parameterName, String currentValue, String defaultValue) {
        // 需要先执行父类的 onParameter，再执行本类的逻辑
        Object result = super.onParameter(user, clazz, parameterName, currentValue, defaultValue);

        // 如果当前参数是 long 类型且 @FilterParameter 注解中的名字是 time
        if (long.class.isAssignableFrom(clazz) && Objects.equals(parameterName, "time")) {
            // long TimeUtil.parseTime(String string) 将文字描述的时间转换为对应的毫秒数
            // 例如「5天」、「10时」等。转换失败返回 -1
            final long timeMillis = TimeUtil.parseTime(currentValue);
            if (timeMillis == -1) {
                user.sendError("{}并不是一个合理的时间哦", currentValue);
                result = null;
            } else {
                result = timeMillis;
            }
        }
        return result;
    }

    /**
    * 本方法有一个参数是 long，但参数名不是 qq，默认解析失败
    * 但因为重写了 onParameter，所以这里的参数会被正确填充
    */
    @GroupInteractor
    @Filter("(禁言|mute)(我|me) {time}")
    @RequirePermission("admin.mute.me")
    public void onMuteMe(XiaomingUser user, @FilterParameter("time") long time) {
        // 相关代码
    }
}
```

## 示例插件
* `xiaoming-example`： [插件示例](https://github.com/Chuanwise/xiaoming-example)
* `xiaoming-lexicons`： [词库插件](https://github.com/Chuanwise/xiaoming-lexicons)