# 插件开发文档
欢迎你使用小明框架开发插件。目前仅支持使用 `Java` 开发小明插件，也欢迎你基于此开发新的 `SDK`！

> 下面示范的插件工程可以在 [插件范例](https://github.com/chuanwise/xiaoming-example) 处下载到。欢迎 `clone` 这个仓库对照研究。如果你的 `IDE` 是 `IDEA`，`clone` 下来的工程无法编译，请参考[开发前的准备](/dev/)马上开始你的插件编写之旅吧！

小明插件由插件主类和其他组件组成。

> **插件主类**是小明启动插件的唯一途径，其必须是小明插件类的子类。你可以重写该类的一些方法，使得加载插件时，小明得以利用多态性调用你编写的函数，以达到启动你的插件的效果。其他组件由插件功能决定，例如**交互器**是和用户消息交互的强大组件。

## 插件主类
|关键类|类名|
|---|---|
|插件 `API`|`cn.chuanwise.xiaoming.plugin.Plugin`|
|插件类的内核实现|`cn.chuanwise.xiaoming.plugin.JavaPlugin`|

只要一个类实现了插件 `API`，都能被小明作为插件启动。建议继承内核的相关实现省去类内细节，例如：
```java
package cn.chuanwise.xiaoming.example;

import cn.chuanwise.xiaoming.plugin.JavaPlugin;

/** 示范小明插件主类 */
public class ExamplePlugin extends JavaPlugin {}
```
插件主类应该要么具备一个**无参构造函数**，要么具备一个访问 `public static` 修饰的、类型为插件主类，名为 `INSTANCE` 的属性。例如：

```java
package cn.chuanwise.xiaoming.example;

import cn.chuanwise.xiaoming.example.iterator.ExampleInteractors;
import cn.chuanwise.xiaoming.plugin.JavaPlugin;

/** 示范小明插件主类 */
public class ExamplePlugin extends JavaPlugin {
    public static final ExamplePlugin INSTANCE = new ExamplePlugin();

    /** 虽然不具备默认的无参构造函数，因为存在 INSTANCE，所以能够被成功加载 */
    private ExamplePlugin() {
        // ...
    }

    @Override
    public void onEnable() {
        getLogger().info("小明示范插件启动成功，芜湖！");
    }
}
```
推荐使用 `INSTANCE` 的方式，这可以避免重载插件时插件主类被重复构造。

通过第二个例子可知，我们可以通过重写一些函数，利用多态性让小明调用，以在关键时刻进行一些操作。

|返回类型|原型|说明|
|---|---|---|
|`void`|`onLoad()`|插件加载时调用|
|`void`|`onEnable()`|插件启动时调用|
|`void`|`onDisable()`|插件关闭时调用|
|`void`|`onUnload()`|插件卸载时调用|
|`void`|`onDisableSoftDepend(Plugin plugin)`|插件的软前置关闭前调用|

`load` 和 `enable` 的区别主要在于，小明会在启动插件之前把所有插件都并行地 `load` 一遍，便于让需要协同工作的插件先处理相关信息，再按照前置插件依赖顺序串行地 `enable` 所有的插件（如果该插件所需前置插件没有都正常启动，则本插件不会被启动）。卸载插件时则都是串行的。

写好了插件的功能后，在资源文件夹中创建文件 `plugin.json`（插件属性文件，供小明读取插件相关信息），内容可以是：
```json
{
  "name": "ExamplePlugin",
  "main": "cn.chuanwise.xiaoming.example.ExamplePlugin",
  "author": "Chuanwise",
  "version": "1.0-SNAPSHOT"
}
```
其中只有 `main` 是必填的，这是插件主类名。

|类型|属性名|是否必填|说明|
|---|---|---|---|
|字符串|`main`|**是**|插件主类名|
|字符串|`name`|否|建议填上，否则小明会将 `jar` 文件名作为插件名|
|字符串|`version`|否|建议填上，否则默认为`unknown`|
|字符串数组|`depends`|否|前置插件名列表。如果前置插件没有全部启动，则本插件不会被启动|
|字符串数组|`softDepends`|否|软前置插件名列表。在启动本插件前会尽可能启动所有软前置，但不保证这些一定都被启动|
|字符串|`author`|否|作者|
|字符串数组|`authors`|否|一些作者|

除上述内容外你还可以增加其他的键，可以在插件主类中通过 `getPluginHandler().get("xxx")` 获得这些值。

将此插件打包为 `jar` 文件后放在 `小明根目录/plugins`，重新启动小明即可加载本插件。

这是的 `ExamplePlugin` 启动时的日志：
```log
[2021-15-18 21:15:54] [pool-1-thread-4] [INFO] c.c.x.p.PluginManagerImpl : 正在加载插件：ExamplePlugin
[2021-15-18 21:15:54] [pool-1-thread-4] [INFO] c.c.x.p.PluginManagerImpl : 成功加载插件：ExamplePlugin
[2021-15-18 21:15:54] [main] [INFO]            c.c.x.p.PluginManagerImpl : 正在启动插件：ExamplePlugin
[2021-15-18 21:15:54] [main] [INFO]                       ExamplePlugin : 小明示范插件启动成功，芜湖！
[2021-15-18 21:15:54] [main] [INFO]            c.c.x.p.PluginManagerImpl : 成功启动插件：ExamplePlugin
```

## 小明本体
小明本体实现了 `XiaomingBot` 接口，具有所有组件的访问器和修改器，以及部分功能性方法。几乎在所有地方，你都可以通过 `getXiaomingBot()` 获得小明本体的引用。

小明本体的具有如下方法：

### 功能性方法
|返回类型|原型|说明|
|---|---|---|
|`Logger`|`getLogger()`|获得日志记录器|
|`Bot`|`getMiraiBot()`|获得 `mirai` 机器人引用|
|`void`|`start()`|启动小明|
|`boolean`|`isEnabled()`|判断小明是否启动|
|`void`|`stop()`|关闭小明|
|`boolean`|`isDisabled()`|判断小明是否关闭|
|`void`|`load()`|载入所有设置|
|`boolean`|`load(String string)`|载入指定名称的设置，返回载入是否成功|

### 状态相关方法
|返回类型|原型|异常类型|说明|
|---|---|---|---|
|`XiaomingBot.Status`|`getStatus()`||获得小明本体状态|
|`XiaomingBot.Status`|`nextStatusOrDefault(long l, Object object)`|`InterruptedException`|等待小明的下一个状态，或返回默认值|
|`XiaomingBot.Status`|`nextStatus(long l)`|`InterruptedException`|等待小明的下一个状态|
|`XiaomingBot.Status`|`nextStatusOrSupply(long l, Supplier supplier)`|`InterruptedException`|获得小明下一个状态|

### 组件访问器
|返回类型|原型|介绍|说明|
|---|---|---|---|
|`Statistician`|`getStatistician()`|统计数据|
|`AccountManager`|`getAccountManager()`|账号管理器|
|`LanguageManager`|`getLanguageManager()`|语言管理器|
|`Serializer`|`getSerializer()`|序列化器|
|`FileSaver`|`getFileSaver()`|[文件保存器](#文件保存)|通过此组件保存文件|
|`FileLoader`|`getFileLoader()`|[文件载入器](#文件载入)|通过此组件载入文件|
|`Serializer`|`getCoreSerializer()`|核心序列化器|是 `Json` 序列化器，设置**恒定**不变|
|`FileLoader`|`getCoreFileLoader()`|核心文件载入器|通过核心序列化器载入编码**恒定**为 `UTF-8` 的 `Json` 文件|
|`ResourceManager`|`getResourceManager()`|资源管理器|通过此组件保存图片|
|`ContactManager`|`getContactManager()`|会话管理器|
|`PermissionManager`|`getPermissionManager()`|权限管理器|
|`Optimizer`|`getOptimizer()`|性能优化器|
|`UserCallLimitManager`|`getUserCallLimitManager()`|用户调用限制器|
|`PluginManager`|`getPluginManager()`|插件管理器|
|`InteractorManager`|`getInteractorManager()`|[交互器管理器](#消息处理)|用于处理聊天消息|
|`Configuration`|`getConfiguration()`|配置信息|
|`ReceptionistManager`|`getReceptionistManager()`|接待员管理器|
|`CenterClientManager`|`getCenterClientManager()`|中心服务器客户端|
|`LicenseManager`|`getLicenseManager()`|使用协议管理器|
|`Scheduler`|`getScheduler()`|[调度器](#调度器)|通过此组件异步调用|
|`XiaomingClassLoader`|`getXiaomingClassLoader()`|类加载器|
|`GroupRecordManager`|`getGroupRecordManager()`|群记录管理器|
|`ReportMessageManager`|`getReportMessageManager()`|报告管理器|
|`ListenerManager`|`getListenerManager()`|[监听器管理器](#监听器)|通过此组件注册监听器|

### 组件修改器
组件修改器和访问器方法名一一对应。例如，交互器管理器 `InteractorManager` 的访问器是 `InteractorManager getInteractorManager()`，修改器则是 `void setInteractorManager(InteractorManager interactorManager)`。

如果需要修改注册型组件的管理器，记得把已注册的组件都挪过来。例如修改交互器管理器 `InteractorManager`：
```java
package cn.chuanwise.xiaoming.example;

import cn.chuanwise.xiaoming.interactor.InteractorManager;
import cn.chuanwise.xiaoming.plugin.JavaPlugin;

/** 示范小明插件主类 */
public class ExamplePlugin extends JavaPlugin {
    public static final ExamplePlugin INSTANCE = new ExamplePlugin();

    private ExamplePlugin() {
        // ...
    }
    
    /** 自定义的交互器管理器 */
    public class CustomInteractorManager implements InteractorManager {
        // ...
    }

    @Override
    public void onEnable() {
        // ...

        final InteractorManager elderInteractorManager = getXiaomingBot().getInteractorManager();
        final InteractorManager newInteractorManager = new CustomInteractorManager();

        newInteractorManager.setXiaomingBot(xiaomingBot);
        
        // 把之前其他组件注册的东西都移过来
        elderInteractorManager.getInteractors().forEach(newInteractorManager::registerInteractor);
        elderInteractorManager.getParameterParsers().forEach(newInteractorManager::registerParameterParser);
        elderInteractorManager.getThrowableCaughters().forEach(newInteractorManager::registerThrowableCaughter);

        // 用自己的交互器管理器替换核心的交互器管理器
        getXiaomingBot().setInteractorManager(newInteractorManager);

        // ...
    }
}
```
如果不移过来，可能导致其他组件之前注册的失效。

**小明对象** `XiaomingObject` 接口中有关于 `getXiaomingBot()` 等方法。小明组件都是小明对象，所以几乎在任何地方，你都可以用 `getXiaomingBot()` 获得小明本体的引用。

## 消息处理
作为一款插件，响应特定的 `QQ` 消息几乎是必备的功能。该功能由 `交互类` 提供。

|关键类|类名|
|---|---|
|交互类 `API`|`cn.chuanwise.xiaoming.interactor.Interactors<T extends Plugin>`|
|交互类内核实现|`cn.chuanwise.xiaoming.interactor.SimpleInteractors<T extends Plugin>`|

这两个模板参数 `T` 都是你的插件主类类型。

只要一个类实现了交互类 `API`，就能被小明作为交互类加载。这个 `API` 只有一个方法：`void onRegister()`，它将在注册此交互类时被调用。

交互类难免经常访问插件类、小明本体引用等对象。你可以继承交互类内核实现 `SimpleInteractors<T extends Plugin>` 来添加这些基础功能。

一个交互类由若干方法组成，这些方法称为 `交互器`。例如：
```java
package cn.chuanwise.xiaoming.example.iterator;

import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.example.ExamplePlugin;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.user.XiaomingUser;

/** 交互器示例 */
public class ExampleInteractors extends SimpleInteractors<ExamplePlugin> {
    @Filter("你会打羽毛球吗")
    public void onCanYouPlayBadminton(XiaomingUser user) {
        user.sendMessage("我会打羽毛球！");
    }
}
```
这个交互器 `onQuery` 响应 `你会打羽毛球吗` 消息，并回复 `我会打羽毛球！`。

交互器是交互类中，至少带一个 `@Filter` 注解的方法。在[下文](#交互器)我们会详细介绍相交互器。

在插件启动时，你需要使用 `getXiaomingBot().getInteractorManager().<T extends Plugin>registerInteractors(Interacters<T>, T plugin)` 注册该交互类的实例，让小明注意到该类中的交互器。
```java
package cn.chuanwise.xiaoming.example;

import cn.chuanwise.xiaoming.example.iterator.ExampleInteractors;
import cn.chuanwise.xiaoming.plugin.JavaPlugin;

/** 示范小明插件主类 */
public class ExamplePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("小明示范插件启动成功，芜湖！");
        getXiaomingBot().getInteractorManager().registerInteractors(new ExampleInteractors(), this);
    }
}
```
这样这个交互器就会真正起作用了。

### 交互器
**交互器**是**交互类**中的，至少有一个 `@Filter` **过滤器**注解的方法。上例中的 `onCanYouPlayBadminton(XiaomingUser)` 就是一个交互器。

过滤器用于判断该方法是否需要响应当前消息。只能用在方法上，有四个参数：

|参数名|类型|含义|默认值|
|---|---|---|---|
|`value`|`String`|由下一个参数而定|**必填项**|
|`pattern`|`FilterPattern`|过滤方式|`FilterPattern.PARAMETER`|
|`enableUsage`|`boolean`|是否需要在「指令格式」列表中显示|`true`|
|`usage`|`String`|指令格式。如果为空串，则默认转换前面的 `value` 值。|`""`|

`FilterPattern` 是过滤方式，是一个枚举类型，其所有可能的取值有：

|取值|触发交互方法的时机|
|---|---|
|`EQUAL`|消息等于 `value` 时|
|`CONTAIN_EQUAL`|消息包含 `value` 时|
|`CONTAIN_MATCH`|消息包含匹配正则表达式 `value` 的段落时|
|`START_EQUAL`|消息以 `value` 开头时|
|`END_EQUAL`|消息以 `value` 结尾时|
|`START_MATCH`|消息开头匹配正则表达式 `value` 时|
|`END_MATCH`|消息结尾匹配正则表达式 `value` 时|
|`EQUAL_IGNORE_CASE`|消息等于 `value`（忽略大小写）时|
|`MATCH`|消息匹配正则表达式 `value` 时|
|`PARAMETER`|消息匹配正则表达式 `value`，并提取其中的参数|

> 小明不喜欢那种无条件触发的交互方法，这可能会让机器人非常吵，所以交互方法至少要有一个 `Filter`。但如果你仍然希望该交互方法被无条件触发，只需要使用 `@Filter(value = "", pattern = FilterPattern.STARTS_WITH)`。

平时使用时，仅需在 `@Filter` 的参数处写上该消息匹配的正则表达式（只是 `{0}` 之类的形式会失效）即可。例如，下面的指令获取数字：
```java
package cn.chuanwise.xiaoming.example.iterator;

import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.example.ExamplePlugin;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.user.XiaomingUser;

/** 交互器示例 */
public class ExampleInteractors extends SimpleInteractors<ExamplePlugin> {
    @Filter("\\d+")
    public void onNumber(XiaomingUser user) {
        user.sendMessage("你输入了一个数字 o(*￣▽￣*)ブ");
    }
}
```

### 提取参数
一些指令需要提取消息中的参数，例如 `设置小明名称  {name}`，其中的 `{name}` 需要被提取出来。这个工作在交互方法内进行有些麻烦，需要讨论参数数组的长度等。小明会帮你做这些操作，只需要在过滤器内使用 `{ref}` 定义变量，并在交互方法的参数中使用 **过滤器参数注解** `@FilterParameter(...)` 引用它们就可以了。

例如：
```java
package cn.chuanwise.xiaoming.example.iterator;

import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.example.ExamplePlugin;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.user.XiaomingUser;

/** 交互器示例 */
public class ExampleInteractors extends SimpleInteractors<ExamplePlugin> {
    @Filter("禁止禁止{what}")
    public void onMultipleProhibited(XiaomingUser user, @FilterParameter("what") String what) {
        user.sendMessage("禁止禁止禁止" + what);
    }
}
```
这个方法的参数除了当前的 `XiaomingUser`，还有一个使用 `@FilterParameter(...)` 注解的参数。

小明会检查当前输入能否匹配 `禁止禁止<what>` 的形式，如果可以，则将参数 `{what}` 对应位置处的值提取到带有 `@FilterParameter("what")` 注解的参数处。

一个方法可以带多个 `@Filter`，其容器是 `@Filters`。在一些情况下，你会使用到默认参数。例如，发送 `禁言  {subject}` 时禁言某用户，单独发送 `禁言` 则禁言某个特定的用户。可以将其写成两个指令，但更棒的办法是使用参数默认值。你可以使用 `@FilterParameter(value = "subject", defaultValue = "1437100907")` 来修饰 `subject` 变量，其作用是当当前 `@Filter` 中的字符串没有出现 `{what}` ，但是这个交互方法的其他过滤器能匹配当前输入时，使用 `"1437100907"` 作为该变量的默认值。`defaultValue` 的默认值为空串 `""`。

例如：
```java
package cn.chuanwise.xiaoming.example.iterator;

import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.annotation.Permission;
import cn.chuanwise.xiaoming.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.contact.contact.MemberContact;
import cn.chuanwise.xiaoming.example.ExamplePlugin;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.user.GroupXiaomingUser;
import cn.chuanwise.xiaoming.user.XiaomingUser;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/** 交互器示例 */
public class ExampleInteractors extends SimpleInteractors<ExamplePlugin> {
    @Filter("禁言")
    @Filter("禁言 {qq}")
    public void onMute(GroupXiaomingUser user, @FilterParameter("qq") long qq) {
        final GroupContact contact = user.getContact();
        final MemberContact member = contact.getMember(qq);

        if (Objects.isNull(member)) {
            user.sendMessage("「{arg.qq}」不在本群哦");
        } else {
            try {
                member.mute(TimeUnit.DAYS.toMillis(1));
                user.sendMessage("成功禁言「{arg.qq}」1 天");
            } catch (Exception exception) {
                user.sendMessage("我好像没有足够的权限呢");
            }
        }
    }
}
```
上面的交互方法实现在群内发送 `禁言` 和 `禁言 <QQ>` 都能达到禁言本群一个用户 `1` 天的效果。如果不带参数，则默认禁言的是小明框架作者 `1437100907`。

我们也可以通过上面的例子得知如果希望一个交互方法仅响应群聊时的做法（用户类型写 `GroupXiaomingUser` 即可）。

### 交互器参数
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
|`InteractorHandler`|当前交互方法的相关信息|包含当前交互方法的所有格式、所需权限等信息。不建议修改其中的值，虽然这可能确实能达到运行时修改指令信息的作用。

由上面的例子，也容易发现参数的类型不一定必须是 `String`。这些使用 `@FilterParameter` 修饰的变量可以自动填充：

|参数类型|`@FilterParameter`中的参数名|自动填充的值|说明|
|---|---|---|---|
|`String`|参数名|当前匹配的过滤器中 `{参数名}` 位置的值|如果匹配当前消息的过滤器中没有 `{参数名}` ，则填充默认值|
|`String[]`|`args` 或 `arguments`|当前消息划分为参数后的数组|默认用空格划分参数。但使用 `""` 作界符的，带有空格的部分会被认为是一个完整的参数，例如 `"argument with spaces"`|
|`long`|`qq` 或 `code`|当前消息 `{qq}` 处的值对应的 `QQ`|如果此处的值非法，会警告`「xxx」不是一个合理的 QQ`。群聊 `@群聊成员` 也会被正确识别|
|`long`|`time` 或 `period`|一段时间长度对应的毫秒数|例如此处的值为 `1秒` 或 `1s` 时，该参数将被填充为 `1000`|
|`PermissionGroup`|参数名|当前匹配的过滤器中 `{参数名}` 位置的值同名的权限组|如果没有找到这个权限组，会警告 `找不到权限组`|


### 智能参数解析
虽然自动提取参数已经省去了手动处理输入的麻烦，但仍然有很多情况需要自行处理特殊参数类型。例如，学生管理系统中经常需要通过学号确定学生，但每次都在交互方法中解析学号格式、查找并反馈学生是否存在是非常麻烦的。

小明支持自定义填充的方式。只需要注册一个 `智能参数解析器` 即可：
```java
package cn.chuanwise.xiaoming.example;

import cn.chuanwise.xiaoming.plugin.JavaPlugin;

import java.util.Optional;

/** 示范小明插件主类 */
public class ExamplePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getXiaomingBot().getInteractorManager().registerParameterParser(Class.class, context -> {
            final String className = context.getInputValue();
            try {
                return Optional.of(Class.forName(className));
            } catch (ClassNotFoundException exception) {
                context.getUser().sendError("小明没有找到类：" + className + " 呢");
                return null;
            }
        }, this);
    }
}
```
这个智能参数解析器将允许用户使用 `@FilterParameter("xxx") Class<?> clazz`，它将被解析为 `xxx` 对应的类。如果找不到类，则会警告 `"小明没有找到类：" + xxx + " 呢"`，免去了不断在交互器中解析类的麻烦。

> 智能参数解析器应该返回一个可选值 `Optional<?>`，是为了区分解析失败和解析结果为 `null` 两种情况而设立的。当它为 `null` 表示解析失败，小明将寻找其他的智能参数解析器。当其值非空时，小明将调用 `Optional.get()` 获取它的值。

事实上，解析参数时小明只会尝试使用内核注册的参数解析器，以及你的插件注册的参数解析器来解析。如果你希望你注册的参数解析器可以被其他插件调用，则注册时将其注册为 `共享参数解析器`，即调用 `getXiaomingBot().getInteractorManager().registerSharedParameterParser(Class<T> clazz, InteractorParameterParser<T> parser, Plugin plugin)` 即可。

### 交互器的注解
小明定义了一些用于交互方法的注解，它们可以影响交互方法的作用域、响应消息的格式等。目前其所有有效的注解有：

注解类型|参数|说明|
---|---|---|
`@Filter`|`value, pattern, usage, enableUsage`|`value` 为相关信息，`pattern` 为匹配方式
`@Required`|所需权限|如果用户没有该权限，将警告 `小明不能帮你做这件事哦，因为你缺少权限：{permission}`。其中可以包含 `@Filter` 的变量，例如 `@Filter("禁言 {qq}")` 和 `@Required("mute.{args.qq}")`
`@WhenExternal`||允许交互方法在任何地方响应
`@WhenQuiet`||允许交互方法在安静模式下仍然响应
`@NonNext`||阻断式响应，即若当前交互方法响应，则不再继续寻找本类中的交互方法。
`@RequireAccountTag("tag")`|账户标记|只有具备该标记的账户才能触发该方法
`@RequireGroupTag("tag")`|群聊标记|只有在具备该标记的群聊中才能触发该方法

需要注意的是，交互方法的返回值一般都是 `void`。默认情况下小明都会认为该方法交互了，会产生一次调用记录。但如果返回 `boolean` 型变量，则只有 `true` 时小明才会认为有了一次交互。

### 处理交互方法抛出的异常
交互方法抛出的异常会被递送至 `异常捕捉器` 中。可以像下面这样注册一个针对 `ClassCastException` 的异常捕捉器：
```java
package cn.chuanwise.xiaoming.example;

import cn.chuanwise.xiaoming.plugin.JavaPlugin;

import java.util.Optional;

/** 示范小明插件主类 */
public class ExamplePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getXiaomingBot().getInteractorManager().registerThrowableCaughter(ClassCastException.class, (context, throwable) -> {
            context.getUser().sendError("出现了一个类型转换异常：" + throwable);
        }, this);
    }
}
```
当异常出现时，小明会逐个调用可用的异常捕捉器，直至有任何一个捕捉器处理了异常。如果没有任何异常捕捉器能捕捉，则小明会警告 `小明遇到了一个意料之外的问题。这个问题已经记录了，期待更好的小明吧`，将其记录并发送错误报告到小明中央服务器（可以在设置中关闭）。

> 你可以在交互器处将抛出必检异常。异常捕捉器将会捕捉到它。

事实上，异常出现时小明只会尝试使用内核注册的异常捕捉器，以及你的插件注册的异常捕捉器来捕捉异常。如果你希望你注册的异常捕捉器可以被其他插件调用，则注册时将其注册为 `共享异常捕捉器`，即调用 `getXiaomingBot().getInteractorManager().registerSharedThrowableCaughter(Class<T> clazz, InteractorThrowableCaughter<T> parser, Plugin plugin)` 即可。

### 运行时加载指令格式
小明支持注册时修改指令格式。前面我们已经知道，交互器 `InteractorHandler` 类型的参数将被填充为交互器信息，此时我们可以修改其值。但这样做很奇怪。

更推荐的办法是使用**交互器自定义器** `Customizer`。交互器自定义器通过交互器名寻找交互器格式，利用该格式调整交互器的权限、格式等信息。

交互器都有自己的名字。默认情况下，方法名就是交互器名。可以通过 `@Customizable(...)` 手动指定交互器名。例如：
```java
package cn.chuanwise.xiaoming.example.iterator;

import cn.chuanwise.xiaoming.annotation.Customizable;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;

/** 交互器示例 */
public class ExampleInteractors extends SimpleInteractors<ExamplePlugin> {
    @Customizable("qwq")
    @Filter("芜湖！")
    public void onHey(XiaomingUser user) {
        user.sendMessage("芜湖！");
    }
}
```
交互器 `onHey` 默认的名称为 `onHey`，但使用注解 `@Customizable` 手动将其名称设置为 `qwq`。

如使用**交互器自定义器**，注册交互类时需要将其作为参数传入。例如：
```java
final Customizer customizer = null;
getXiaomingBot().getInteractorManager().registerInteractors(new ExampleInteractors(), customizer, this);
```
这样在注册时小明就会从交互器自定义器中查找每个交互器的格式，并且自动调整其信息了。

交互器自定义器只是一个函数式接口，定义为：
```java
package cn.chuanwise.xiaoming.interactor.customizer;

import cn.chuanwise.xiaoming.interactor.handler.InteractorHandler;

/** 交互器自定义器 */
@FunctionalInterface
public interface Customizer {
    InteractorHandler forName(String interactorName);
}
```
在注册交互器时，小明会通过该方法获得交互器信息。如果返回 `null`，则不会修改默认的配置。

## 事件监听
监听事件是通过组件**监听器**实现的。

监听器是一个函数化接口：
```java
package cn.chuanwise.xiaoming.listener;

import net.mamoe.mirai.event.Event;

@FunctionalInterface
public interface Listener<T extends Event> {
    void listen(T event);
}
```
监听器可以通过函数注册，也可以通过监听类批量注册监听器。

### 通过函数注册简单监听器
在启动插件时注册该类为监听器，小明就会在该事件发生时调用你的监听器。例如
```java
final ExamplePlugin plugin = ExamplePlugin.INSTANCE;
getXiaomingBot().getListenerManager().registerListener(XiaomingEvent.class, event -> {
    plugin.getLogger().info("芜湖！");
}, plugin);
```
上面的代码注册了一个针对所有 `XiaomingEvent` 事件的监听器。在该事件发生时，将输出一句日志 `芜湖！`。

监听器具有优先级，用枚举类型 `ListenerPriority` 表示，其从高优先级到低优先级，分别是 `HIGHEST` `HIGH` `NORMAL` `LOW` 和 `LOWEST`。默认优先级是 `NORMAL`。注册监听器时，可以手动指定优先级。例如：
```java
getXiaomingBot().getListenerManager().registerListener(XiaomingEvent.class, ListenerPriority.HIGHEST, event -> {
    ExamplePlugin.INSTANCE.getLogger().info("芜湖！");
}, this);
```
这将这个监听器注册为最高级别。将在事件发生时最先调用。

> 高优先级监听器一定在低优先级监听器调用之前调用。同优先级的监听器按其注册先后顺序调用。

实现了 `net.mamoe.mirai.event.CancellableEvent` 接口的事件可以被取消。监听器默认不监听已经被取消的事件。可以在注册监听器时要求监听被取消了的事件。例如：
```java
getXiaomingBot().getListenerManager().registerListener(XiaomingEvent.class, ListenerPriority.HIGHEST, false, event -> {
    ExamplePlugin.INSTANCE.getLogger().info("芜湖！");
}, this);
```
`registerListener` 的第三个参数 `boolean ignoreCancelled` 表示是否忽略被取消的事件。`false` 表示不忽略。

注册简单的监听器的方法 `registerListener` 有 `3` 个重载版本：

|返回类型|泛型参数|原型|异常类型|说明|
|---|---|---|---|---|
|`void`|`T extends Event`|`registerListener(Class<T> clazz, ListenerPriority listenerPriority, boolean replace, Listener<T> listener, Plugin plugin)`|注册一个指定优先级和是否忽略已取消事件的监听器|
|`void`|`T extends Event`|`registerListener(Class<T> clazz, ListenerPriority listenerPriority, Listener listener, Plugin plugin)`|注册一个指定优先级、忽略已取消事件的监听器|
|`void`|`T extends Event`|`registerListener(Class<T> clazz, Listener listener, Plugin plugin)`|注册一个普通优先级 `NORMAL`、指定是否忽略已取消事件的监听器|

### 通过类注册多个监听器
不断的调用 `registerListener` 是很麻烦的。像交互器那样，小明支持将监听器作为类的方法，写在 `监听类` 中，直接注册监听类即可注册这些监听器。

监听类必须实现 `Listeners<T extends Plugin>` 接口。该接口是一个标记接口，只有一个默认方法：
```java
package cn.chuanwise.xiaoming.event;

import cn.chuanwise.xiaoming.plugin.Plugin;

/** 事件监听器 */
public interface Listeners<T extends Plugin> {
    default void onRegister() {}
}
```
其中的 `onRegister()` 将在监听器注册前调用。

实现了 `Listeners<T>` 的类都可以作为监听类。在监听类中，可以使用 `@EventHandler` 标注一个**参数只有一个被监听事件**的监听器方法：
```java
package cn.chuanwise.xiaoming.example.listener;

import cn.chuanwise.xiaoming.annotation.EventHandler;
import cn.chuanwise.xiaoming.event.SimpleListeners;
import cn.chuanwise.xiaoming.example.ExamplePlugin;
import cn.chuanwise.xiaoming.listener.ListenerPriority;
import net.mamoe.mirai.event.events.BotJoinGroupEvent;
import net.mamoe.mirai.event.events.BotMuteEvent;

/** 监听类示例 */
public class ExampleListeners extends SimpleListeners<ExamplePlugin> {
    @EventHandler
    public void onGroupJoin(BotJoinGroupEvent event) {
        getLogger().info("芜湖，小明加入新的群啦");
    }
    
    @EventHandler(priority = ListenerPriority.LOWEST, ignoreCancelled = false)
    public void onMute(BotMuteEvent event) {
        getLogger().info("啊这，小明被禁言了 (；′⌒`)");
    }
}
```
监听器的信息写在 `@EventHandler` 中。这个注解有两个参数：

|参数名|类型|含义|默认值|
|---|---|---|---|
|`priority`|`ListenerPriority`|监听器优先级|`ListenerPriority.NORMAL`|
|`ignoreCancelled`|`boolean`|是否忽略被取消的事件|`true`|

随后只需要注册监听类即可令这两个监听器生效：
```java
getXiaomingBot().getListenerManager().registerListeners(new ExampleListeners(), ExamplePlugin.INSTANCE);
```

和交互类一样，监听类难免经常访问插件本体、日志、小明本体等。小明提供一种监听类的实现 `SimpleListeners<T extends Plugin>`，提供 `plugin` 和 `xiaomingBot` 的引用，方便进行一些操作。



## 调度器
调度器是小明的**核心线程池**。内部是 `ScheduledExecutorService` 实现的。通过 `getXiaomingBot().getScheduler()` 获得调度器的引用。

### 异步执行任务
|返回类型|原型|说明|
|---|---|---|
|`Future<T>`|`run(Callable<T> callable)`|异步执行一个任务|
|`Future<T>`|`run(Runnable runnable, T returnValue)`|异步执行一个任务，返回指定值|
|`void`|`run(Runnable runnable)`|异步执行任务|
|`ScheduledFuture<T>`|`runLater(long delay, Callable<T> callable)`|`delay` 毫秒后执行一个任务|
|`ScheduledFuture<?>`|`runLater(long delay, Runnable runnable)`|`delay` 毫秒后执行一个任务|
|`ScheduledFuture<T>`|`runLater(long delay, Runnable runnable, T returnValue)`|`delay` 毫秒后执行一个任务，并返回指定值|
|`ScheduledFuture<?>`|`runAtFixedRate(long period, Runnable runnable)`|以 `period` 毫秒为周期性地执行异步任务|
|`ScheduledFuture<?>`|`runAtFixedRateLater(long period, long delay, Runnable runnable)`|`delay` 毫秒后以 `period` 毫秒为周期性地执行异步任务|
|`ScheduledFuture<?>`|`runWithFixedDelayLater(long period, long delay, Runnable runnable)`|`delay` 毫秒后以 `period` 毫秒为周期性地执行任务|
|`ScheduledFuture<?>`|`runWithFixedDelay(long period, Runnable runnable)`|以 `period` 毫秒为周期性地执行异步任务|

关于 `AtFixedRate` 和 `WithFixedDelay` 的区别，参照 `ScheduledExecutorService` 的[说明](https://www.jianshu.com/p/aeb391e4edb0)。

### 关闭时执行任务
|返回类型|原型|说明|
|---|---|---|
|`void`|`runFinally(String name, Runnable runnable)`|关闭时执行任务，`name` 是任务名|
|`Runnable`|`cancelFinally(String name)`|取消关闭时要执行的任务，返回被取消的任务|
|`Map<String, Runnable>`|`getFinalTasks()`|获得关闭时要执行的任务|

### 其他方法
|返回类型|原型|异常类型|说明|
|---|---|---|---|
|`ScheduledExecutorService`|`getThreadPool()`||获得线程池本体|
|`void`|`stop()`||关闭调度器|
|`void`|`stopNow()`||立刻关闭调度器|
|`void`|`awaitStop(long l)`|`InterruptedException`|关闭调度器，并等待其结束|
|`boolean`|`isRunning()`||判断调度器是否正在运行|
|`boolean`|`isStopped()`||判断调度器是否已被关闭|

## 文件载入和保存
小明推荐使用 `Json` 作为数据交换语言。默认情况下，数据都以 `Json` 通过 `UTF-8` 编码保存和读取。

需要使用文件保存的类相关的父类有：

|关键类|类名|
|---|---|
|用文件保存 `API`|`cn.chuanwise.toolkit.preservable.Preservable<T>`|
|内核实现|`cn.chuanwise.toolkit.preservable.file.FilePreservableImpl`|

只需要让一个类实现了用文件保存 `API`，就可以使用小明的文件保存器保存和小明的文件载入器载入。小明的核心设置中有有关数据交换语言和编码的设置，使用文件保存器和载入器将免去考虑这些的过程。

### 文件载入
文件载入需要使用 `文件载入器`，通过 `getXiaomingBot().getFileLoader()` 获得。其方法有：

#### 载入相关
|返回类型|原型|异常类型|说明|
|---|---|---|---|
|`T extends Preservable`|`load(Serializer serializer, Class<T> clazz, File file)`|`IOException`|使用某序列化器从某文件中载入某类的对象|
|`T extends Preservable`|`load(Class<T> clazz, File file)`|`IOException`|使用默认序列化器从某文件载入某类的对象|
|`T extends Preservable`|`loadOrSupply(Serializer serializer, Class<T> clazz, File file, Supplier<T> supplier)`||使用某序列化器从某文件中载入某类的对象。如果载入失败，使用 `supplier` 构造一个|
|`T extends Preservable`|`loadOrSupply(Class<T> clazz, File file, Supplier supplier)`||使用默认序列化器从某文件中载入某类的对象。如果载入失败，使用 `supplier` 构造一个|
|`Preservable`|`loadOrFail(Serializer serializer, Class<T> clazz, File file)`||使用某序列化器从某文件中载入某类的对象，失败时返回 `null`|
|`Preservable`|`loadOrFail(Class<T> clazz, File file)`||使用默认序列化器从某文件中载入某类的对象。失败时返回 `null`|

#### 其他功能
|返回类型|原型|异常类型|说明|
|---|---|---|---|
|`String`|`getDecoding()`|||
|`Charset`|`getDecodingCharset()`|||
|`void`|`setDecoding(String string)`|||
|`void`|`setDecodingCharset(Charset charset)`|||
|`Serializer`|`getDefaultSerializer()`|||
|`void`|`setDefaultSerializer(Serializer serializer)`|||

**最好不要直接调用编码的修改器**。在小明启动时，小明已经按照配置文件的要求设置了编码等信息。

默认序列化器一般是 `getXiaomingBot().getSerializer()`，不建议修改。

#### 示例
插件配置类：
```java
package cn.chuanwise.xiaoming.example.pluginConfiguration;

import cn.chuanwise.toolkit.preservable.AbstractPreservable;
import lombok.Data;

@Data
public class ExampleConfiguration extends AbstractPreservable {
    boolean chuanwiseHandsome = true;
    boolean xiaomingBotGood = true;
}
```

插件主类中调用 `getXiaomingBot().getFileLoader().loadOrSupply(...)` 从插件数据文件夹 `getDataFolder()` 下读取一个文件 `pluginConfiguration.json`。如果读取失败，则调用其构造函数构造一个默认值。

```java
package cn.chuanwise.xiaoming.example;

import cn.chuanwise.xiaoming.example.pluginConfiguration.ExampleConfiguration;
import cn.chuanwise.xiaoming.plugin.JavaPlugin;

import java.io.File;

/** 示范小明插件主类 */
public class ExamplePlugin extends JavaPlugin {
    public static final ExamplePlugin INSTANCE = new ExamplePlugin();

    @Override
    public void onEnable() {
        getDataFolder().mkdirs();

        final ExampleConfiguration exampleConfiguration = getXiaomingBot().getFileLoader().loadOrSupply(ExampleConfiguration.class, new File(getDataFolder(), "pluginConfiguration.json"), ExampleConfiguration::new);
    }
}
```

在插件启动时，这个文件会被读取并载入该对象中。

需要注意的是，构造默认值后**小明并不会将默认配置信息写入外存**，除非手动调用相关方法。事实上，小明只建议将那些修改后的，非默认配置信息写入外存。

### 文件保存
文件保存需要使用 `文件保存器`，通过 `getXiaomingBot().getFileSaver()` 获得。其方法主要有：

#### 保存相关
|返回类型|原型|说明|
|---|---|---|
|`void`|`save(Preservable preservable)`|`IOException`|立刻保存一个文件|
|`boolean`|`saveOrFail(Preservable preservable)`||立刻保存一个文件，成功时返回 `true`|
|`void`|`save()`||立刻保存所有等待保存的文件|
|`void`|`readyToSave(Preservable preservable)`||提交一个文件保存申请。具体何时保存由小明决定，保证在关闭前保存|
|`void`|`planToSave(Preservable preservable)`||将一个文件保存列入计划，在下一次例行保存，或小明关闭前保存|
|`Map<File, Preservable>`|`getPreservables()`|获得等待保存的文件列表|

推荐使用 `readyToSave(...)`。因为有些文件需要频繁操作，高频 `IO` 会造成不必要的开销。因此小明配置中有一个开关 `saveFileDirectly`，当其为 `true` 时，`readyToSave(...)` 将立刻保存文件。为 `false` 时，则将文件加入待保存队列，等待定时文件保存任务，或关闭前保存。

#### 其他功能
|返回类型|原型|说明|
|---|---|---|
|`String`|`getEncode()`||
|`Charset`|`getEncodeCharset()`||
|`void`|`setEncodeCharset(Charset charset)`||
|`void`|`setEncode(String string)`||
|`long`|`getLastSaveTime()`|获得上一次例行保存的时间|
|`long`|`getLastValidSaveTime()`|获得上次保存至少一个文件的时间|

类似地，和编码相关的修改器都不建议调用。

## 未完待续

(๑•̀ㅂ•́)و✧

<!-- 该类的方法有：

|返回类型|原型|异常类型|说明|
|---|---|---|---|
|`Serializer`|`getSerializer()`||获得序列化器|
|`void`|`setSerializer(Serializer serializer)`||设置序列化器|
|`void`|`save(Serializer serializer, File file, boolean replace)`|`IOException`|使用某序列化器将类内容写入 `file` 中。如果该文件存在且 `replace`，则会替换旧文件|
|`void`|`save(Serializer serializer)`|`IOException`||
|`void`|`save(File file, boolean replace)`|`IOException`||
|`void`|`save(Serializer serializer, File file)`|`IOException`||
|`void`|`save(Serializer serializer, boolean replace)`|`IOException`||
|`void`|`save()`|`IOException`||
|`void`|`save(boolean replace)`|`IOException`||
|`void`|`save(File file)`|`IOException`||
|`boolean`|`saveOrFail()`||将文件保存到默认路径，成功时返回 `true`|
|`void`|`setMedium(File file)`||设置文件保存介质|
|`File`|`getMedium()`||| -->
<!-- 
值得一提的是，这些方法保存时都是以系统默认编码保存的。建议使用文件保存器：`getXiaomingBot().getFileSaver().readyToSave(...)`，该方法确保一定以小明配置中的相关设置保存，如 `UTF-8` 和 `Json`。 -->


> **声明**
> 
> 到此你已经阅读结束小明的开发文档有关**插件开发**的介绍，赶快去写几个插件试试吧！<br>
> **小明及相关插件的技术交流 / 用户 QQ 群**：`1028959718`
>
> 返回[开发文档](http://chuanwise.cn:10074/#/dev/)<br>
> 查看[插件中心](http://chuanwise.cn:10074/#/plugin/)<br>
> 返回[项目首页](https://github.com/Chuanwise/XiaoMingBot/)<br>
> 
> |本文作者|最后更新时间|对应版本号|
> |---|---|---|
> |`Chuanwise`|`2021年6月18日`|`1.0`|
> |`Chuanwise`|`2021年8月19日`|`2.0`|