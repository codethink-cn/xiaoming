### 小明机器人框架 xiaoming-bot
### 开发文档
# 插件开发文档
> 你当前的位置：[项目首页](https://github.com/Chuanwise/xiaoming-bot) -> [开发文档](https://github.com/Chuanwise/xiaoming-bot/tree/main/docs/Development.md) -> 插件开发文档

欢迎你使用小明框架开发插件。目前仅支持使用 `Java` 开发小明插件，也欢迎你基于此开发新的 `SDK`！

下面示范的插件工程可以在 [插件范例](https://github.com/chuanwise/xiaoming-example) 处下载到。欢迎 `clone` 这个仓库对照研究。

马上开始你的插件编写之旅吧！

## 插件组成
小明插件由插件主类、交互器等其他组件组成。

**插件主类**是小明启动插件的唯一途径，其必须是小明插件类的子类。你可以重写该类的一些方法，使得加载插件时，小明得以利用多态性调用你编写的函数，以达到启动你的插件的效果。

**交互器**是和用户消息交互的强大组件。

### 插件主类
|关键类|类名|
|---|---|
|小明插件 `API`|`XiaomingBot`|
|插件类的内核实现|`XiaomingBotImpl`|

只要一个类实现了插件 `API`，都能被小明作为插件启动。你可以直接继承内核的相关实现省去类内细节，例如：
```java
package cn.chuanwise.xiaoming.example;

import XiaomingPluginImpl;
import cn.chuanwise.xiaoming.example.interactor.MessageInteractorTest;

/**
 * 插件主类示例
 * @author Chuanwise
 */
public class ExamplePlugin extends XiaomingPluginImpl {}
```
这个插件会被加载，但没有任何作用。可以通过重写一些函数，在关键时刻进行一些操作。

|返回类型|原型|异常类型|说明|
|---|---|---|---|
|`void`|`onLoad()`||插件加载时调用|
|`void`|`onEnable()`||插件启动时调用|
|`void`|`onDisable()`||插件关闭时调用|
|`void`|`onUnload()`||插件卸载时调用|

`load` 和 `enable` 的区别主要在于，小明会在启动插件之前把所有插件都 `load` 一遍，便于让需要协同工作的插件先处理相关信息，再按照前置插件依赖顺序 `enable` 所有的插件（如果该插件所需前置插件没有都正常启动，则本插件不会被启动）。

例如这是一个插件启动时就输出一段插件信息的插件：
```java
package cn.chuanwise.xiaoming.example;

import XiaomingPluginImpl;

/**
 * 插件主类示例
 * @author Chuanwise
 */
public class ExamplePlugin extends XiaomingPluginImpl {
    public static ExamplePlugin INSTANCE;

    @Override
    public void onLoad() {
        INSTANCE = this;
        getDataFolder().mkdirs();
    }

    @Override
    public void onEnable() {
        getLog().info("小明范例插件加载成功 (๑•̀ㅂ•́)و✧");
    }
}
```

写好了插件的功能后，在资源文件夹中创建文件 `plugin.json`（插件属性文件，供小明读取插件相关信息），其内容为：
```json
{
  "name": "example-plugin",
  "main": "cn.chuanwise.xiaoming.example.ExamplePlugin",
  "author": "Chuanwise",
  "version": "1.0.TEST",
  "fronts": [ "java.util.ArrayList", ["lexicons"]]
}
```
这个文件中这些属性会有特殊的作用：

类型|属性名|是否必填|说明
---|---|---|---
字符串|`name`|否|建议填上，否则小明会将 `jar` 文件名作为插件名
字符串|`main`|是|插件主类名
字符串|`version`|否|建议填上，否则默认为`unknown`
字符串列表|`fronts`|否|前置插件名列表

除上述内容外你还可以增加其他的键，可以在插件主类中获得他们的值。但不建议将这类信息放在 `plugin.json` 中。

将此插件打包为 `jar` 文件后放在 `小明根目录/plugins`，重新启动小明即可加载本插件。

### 交互器
作为一款插件，响应特定的 `QQ` 消息几乎是必备的功能。

|关键类|类名|
|---|---|
|交互器 `API`|`Interactor`|
|交互器的内核实现|`InteractorImpl`|

交互器必须实现 `API`。你可以直接继承其内核实现。

交互器通过过滤器响应消息。例如，下面的交互器在群聊、私聊和临时会话中回应「你会打羽毛球吗？」消息。
```java
package cn.chuanwise.xiaoming.example.interactor;

import Filter;
import XiaomingUser;
import InteractorImpl;

/**
 * 消息交互器示例
 * @author Chuanwise
 */
public class MessageInteractorTest extends InteractorImpl {
    @Filter("你会打羽毛球吗？")
    public void onCanYouPlayBadminton(XiaomingUser user) {
        user.sendMessage("我会打羽毛球！");
    }
}
```
在插件启动时，你需要注册该交互器的实例，让小明注意到你的交互器。例如：
```java
package cn.chuanwise.xiaoming.example;

import XiaomingPluginImpl;
import cn.chuanwise.xiaoming.example.interactor.MessageInteractorTest;

/**
 * 插件主类示例
 * @author Chuanwise
 */
public class ExamplePlugin extends XiaomingPluginImpl {
    @Override
    public void onEnable() {
        getXiaomingBot().getInteractorManager().register(new MessageInteractorTest(), this);
    }
}
```
这样这个交互器就会真正起作用了。

#### 交互方法与过滤器
完成交互所用的方法称为**交互方法**，至少要有一个 `Filter`（过滤器）注解。上例中的 `onCanYouPlayBadminton(XiaomingUser)` 就是一个交互方法。

过滤器用于判断该方法是否需要响应当前消息。只能用在方法上，有四个参数：
参数名|类型|含义|默认值
---|---|---|---
`value`|`String`|由下一个参数而定|（无默认值，为必填项）
`pattern`|`FilterPattern`|过滤方式|`FilterPattern.PARAMETER`
`enableUsabe`|`boolean`|是否需要在「指令格式」列表中显示|`true`
`usage`|`String`|指令格式。如果为空串，则默认转换前面的 `value` 值。|`""`

`FilterPattern` 是过滤方式，是一个枚举类型，其所有可能的取值有：

取值|触发交互方法的时机
---|---
`EQUALS`|消息等于 `value` 时
`EQUALS_IGNORE_CASE`|消息等于 `value` （忽略大小写）时
`STARTS_WITH`|消息以 `value` 开头时
`ENDS_WITH`|消息以 `value` 结尾时
`STARTS_REGEX`|消息开头匹配正则表达式 `value` 时
`ENDS_REGEX`|消息结尾匹配正则表达式 `value` 时
`MATCHES`|消息匹配正则表达式 `value` 时
`PARAMETER`|消息匹配提取参数的正则表达式 `value` 时

小明不喜欢那种无条件触发的交互方法，这可能会让机器人非常吵，所以交互方法至少要有一个 `Filter`。但如果你仍然希望该交互方法被无条件触发，只需要使用 `@Filter(value = "", pattern = FilterPattern.STARTS_WITH)`。

平时使用时，仅需在 `@Filter` 的参数处写上该消息匹配的正则表达式（不能使用 `{0}` 之类的形式）即可。例如，下面的指令获取数字：
```java
package cn.chuanwise.xiaoming.example.interactor;

import Filter;
import XiaomingUser;
import InteractorImpl;

/**
 * 消息交互器示例
 * @author Chuanwise
 */
public class MessageInteractorTest extends InteractorImpl {
    @Filter("\\d+")
    public void onNumber(XiaomingUser user) {
        user.sendMessage("你输入了一个数字 o(*￣▽￣*)ブ");
    }
}
```

#### 提取过滤器中的参数
一些指令需要提取消息中的参数。这个工作在交互方法内进行有些麻烦，有讨论参数数组的长度等繁琐过程。小明可以帮你做这些操作，只需要在过滤器内使用 `{parameter}` 定义变量，并在交互方法的参数中使用 **过滤器参数注解** `@FilterParameter(...)` 引用它们就可以了。

例如：
```java
package cn.chuanwise.xiaoming.example.interactor;

import Filter;
import FilterParameter;
import XiaomingUser;
import InteractorImpl;

/**
 * 消息交互器示例
 * @author Chuanwise
 */
public class MessageInteractorTest extends InteractorImpl {
    @Filter("禁止禁止{what}")
    public void onMultipleProhibited(XiaomingUser user, @FilterParameter("what") String what) {
        user.sendMessage("禁止禁止禁止{what}");
    }
}

```
这个方法的参数除了当前的 `XiaomingUser`，还有一个使用 `@FilterParameter(...)` 注解的参数。

小明会检查当前输入能否匹配 `禁止禁止<what>` 的形式，如果可以，则将参数 `{what}` 对应位置处的值提取到带有 `@FilterParameter("what")` 注解的参数处。

一个方法可以带多个 `@Filter`，其容器是 `@Filters`。在一些情况下，你会使用到默认参数。例如，发送 `禁言  {subject}` 时禁言某用户，单独发送 `禁言` 则禁言某个特定的用户。可以将其写成两个指令，但更棒的办法是使用参数默认值。

过滤器参数注解还有一种形式：`@FilterParameter(value = "what", defaultValue = "芜湖")`。其作用是当当前 `@Filter` 中的字符串没有出现 `{what}` ，但是这个交互方法的其他过滤器能匹配当前输入时，使用 `"芜湖"` 作为该变量的默认值。`defaultValue` 的默认值为空串 `""`。

例如：
```java
package cn.chuanwise.xiaoming.example.interactor;

import Filter;
import FilterParameter;
import GroupContact;
import MemberContact;
import GroupXiaomingUser;
import XiaomingUser;
import InteractorImpl;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 消息交互器示例
 * @author Chuanwise
 */
public class MessageInteractorTest extends InteractorImpl {
    @Filter("禁言")
    @Filter("禁言 {qq}")
    public void onMute(GroupXiaomingUser user, @FilterParameter(value = "qq", defaultValue = "1437100907") long qq) {
        final GroupContact contact = user.getContact();
        final MemberContact member = contact.getMember(qq);
        if (Objects.isNull(member)) {
            user.sendMessage("「{qq}」不在本群哦");
        } else {
            try {
                member.mute(TimeUnit.DAYS.toMillis(1));
                user.sendMessage("成功禁言「{qq}」1 天");
            } catch (Exception exception) {
                user.sendMessage("我好像没有足够的权限呢");
            }
        }
    }
}
```
上面的交互方法实现在群内发送 `禁言` 和 `禁言 <QQ>` 都能达到禁言本群一个用户 1 天的效果。如果不带参数，则默认禁言小明框架作者 `1437100907` Σ(っ °Д °;)っ。

我们也可以通过上面的例子得知如果希望一个交互方法仅响应群聊时的做法（用户类型写 `GroupXiaomingUser` 即可）。

#### 交互方法的参数
上述变化可以看出，交互方法的参数非常灵活。小明在调用交互方法时，会根据参数类型和相关注解综合判断需要在此处填入的值。

根据参数类型填充的参数有：
|参数类型|自动填充的值|说明|
|---|---|---|
|`XiaomingUser`|当前消息的发出者|虽然 `XiaomingUser` 是泛型类型，但使用时无需关注其泛型参数|
|`GroupXiaomingUser`|当前消息在群聊中的发送者|如果当前消息不是群聊中的，则该交互方法不会被触发|
|`PrivateXiaomingUser`|当前消息在私聊中的发送者|如果当前消息不是私聊中的，则该交互方法不会被触发|
|`MemberXiaomingUser`|当前消息在临时会话中的发送者|如果当前消息不是临时会话中的，则该交互方法不会被触发|
|`Message`|当前消息||
|`GroupMessage`|当前群聊消息|如果不是群聊消息，则该交互方法不会被触发|
|`PrivateMessage`|当前私聊消息|如果不是私聊消息，则该交互方法不会被触发|
|`MemberMessage`|当前临时会话消息|如果不是临时会话消息，则该交互方法不会被触发|
|`CommandFormat`|当前交互方法的指令格式|包含当前交互方法的所有格式、所需权限等信息。不建议修改其中的值，虽然这可能确实能达到运行时修改指令信息的作用。

根据参数类型和注解填充的参数有：
|参数类型|注解信息|自动填充的值|说明|
|---|---|---|---|
|`String`|`@FilterParameter("参数名")`|当前消息对应过滤器中 `{参数名}` 位置的值|如果匹配当前消息的过滤器中没有 `{参数名}` ，则填充默认值。
|`String[]`|`@FilterParameter("arguments")`|当前消息划分为参数后的数组|默认用空格划分参数。但使用 `""` 作界符的，带有空格的部分会被认为是一个完整的参数，例如 `"argument with spaces"`
|`long`|`@FilterParameter("qq")`|当前消息 `{qq}` 处的值对应的 `QQ`|如果此处的值非法，小明会警告`「xxx」不是一个合理的 QQ`。群聊 `@群聊成员` 也会被正确识别。
|`long`|`@FilterParameter("time")`|一段时间长度对应的毫秒数|例如此处的值为 `1秒` 或 `1s` 时，该参数将被填充为 `1000`。
|`long`|`@FilterParameter("group")`|群号|只解析数字，不验证小明是否在该群中。
|`int`|`@FilterParameter("index")`|索引|必须是大于等于 `0` 的整数。

#### 自定义参数填充策略
虽然自动提取参数已经省去了手动处理输入的麻烦，但仍然有很多情况需要自行处理特殊参数类型。例如，学生管理系统中经常需要通过学号确定学生，但每次都在交互方法中解析学号格式、查找并反馈学生是否存在是非常麻烦的。

小明支持自定义填充的方式。只需要重写 `parseParameter` 方法。该方法的信息如下：

返回类型|返回含义
---|---
`T`|泛型类型。含义为此处应该填入的参数，如果为 `null`，当前方法不会与用户交互。

参数|含义
---|---
`XiaomingUser user`|当前调用者
`Class<T> clazz`|当前参数的类型
`String parameterName`|参数在 `@FilterParameter("parameterName")` 中的名字
`String currentValue`|参数当前在消息中对应的值
`String defaultValue`|参数在 `@FilterParameter(...)` 中的默认值

```java
package cn.chuanwise.xiaoming.example.interactor;

import Filter;
import FilterParameter;
import XiaomingUser;
import InteractorImpl;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 消息交互器示例
 * @author Chuanwise
 */
public class MessageInteractorTest extends InteractorImpl {
    @Filter("学生信息 {student}")
    public void onStudentMessage(XiaomingUser user, @FilterParameter("student") Student student) {
        user.sendMessage("学生名：" + student.getName() + " ...");
    }

    @Override
    public <T> T parseParameter(XiaomingUser user, Class<T> clazz, String parameterName, String currentValue, String defaultValue) {
        T superResult = super.parseParameter(user, clazz, parameterName, currentValue, defaultValue);
        if (Objects.nonNull(superResult)) {
            return superResult;
        }

        if (clazz.isAssignableFrom(Student.class) && Objects.equals(parameterName, "student")) {
            if (currentValue.matches("\\d+")) {
                final long studentCode = Long.parseLong(currentValue);
                final Student student = /* 查找该学生 */;

                if (Objects.isNull(student)) {
                    user.sendError("找不到学号为「{student}」的学生呢");
                    return null;
                } else {
                    return (T) student;
                }
            } else {
                user.sendError("「{student}」并不是一个合理的学号哦");
            }
        }

        return null;
    }
}
```
上述交互器，通过重写 `onParameter`，免去了 `onStudent` 交互方法中处理学号的麻烦。该交互器其他所有使用 `@FilterParameter("student")` 修饰的 `Student` 类型参数都将被填充为对应的学生。

#### 交互方法的注解
小明定义了一些用于交互方法的注解，它们可以影响交互方法的作用域、响应消息的格式等。目前其所有有效的注解有：

注解类型|参数|说明|
---|---|---|
`@Filter`|`value, pattern`|`value` 为相关信息，`pattern` 为匹配方式
`@Permission`|所需权限|如果用户没有该权限，将警告 `小明不能帮你做这件事哦，因为你缺少权限：{permission}`。其中可以包含 `@Filter` 的变量，例如 `@Filter("禁言 {qq}")` 和 `@Permission("mute.{qq}")`
`@WhenExternal`||允许交互方法在任何地方响应
`@WhenQuiet`||允许交互方法在安静模式下仍然响应
`@NonNext`||阻断式响应，即若当前交互方法响应，则不再继续寻找本类中的交互方法。
`@RequireAccountTag("tag")`|账户标记|只有具备该标记的账户才能触发该方法
`@RequireGroupTag("tag")`|群聊标记|只有在具备该标记的群聊中才能触发该方法

需要注意的是，交互方法的返回值一般都是 `void`。默认情况下小明都会认为该方法交互了，会产生一次调用记录。但如果返回 `boolean` 型变量，则只有 `true` 时小明才会认为该方法交互了。

#### 处理交互方法抛出的异常
交互方法抛出的异常会被递送至交互器的 `onThrowable` 方法中，通过重写该方法自定义处理异常的方式。该方法的参数有：

参数类型|值|说明
---|---|---
`XiaomingUser`|触发异常的用户|
`Message`|用户当前的输入|
`Map<String, String>`|通过 `@Filter` 解析获得的参数|如果使用的不是 `PARAMETER` 类型，则此项为 `null`
`Method`|交互方法|
`List<Object>`|调用上述交互方法的实际参数|
`CommandFormat`|该交互方法的相关信息|
`Throwable`|直接从交互方法中抛出的异常|

该方法返回 `boolean` 型变量，表示是否处理过异常。为 `false` 时，异常将继续向外抛出，小明将记录此异常并在日志群中发送错误信息，随后提示用户 `小明遇到了一些错误，这个问题已经上报了，期待更好的小明吧`。

<!-- #### 运行时注册交互方法
你也许希望通过配置文件读取指令格式，在运行时注册指令。可以通过调用交互器的 `register` 方法注册。该方法有两种重载形式，分别为： -->


### 你可能需要的其他组件
未完待续 (๑•̀ㅂ•́)و✧

> **声明**
> 
> 到此你已经阅读结束小明的开发文档有关**插件开发**的介绍，赶快去写几个插件试试吧！<br>
> **小明及相关插件的技术交流 / 用户 QQ 群**：`1028959718`
>
> 返回[开发文档](https://github.com/Chuanwise/xiaoming-bot/tree/main/docs/Development.md)<br>
> 查看[插件中心](https://github.com/Chuanwise/xiaoming-bot/tree/main/docs/PluginCenter.md)<br>
> 返回[项目首页](https://github.com/Chuanwise/xiaoming-bot/)<br>
> 
> |本文作者|最后更新时间|对应版本号|
> |---|---|---|
> |`Chuanwise`|`2021年6月18日`|`1.0`