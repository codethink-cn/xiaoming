### 小明机器人框架 xiaoming-bot
### 开发文档 · **小明组件介绍**
# 交互器管理器
> 你当前的位置：[项目首页](https://github.com/Chuanwise/xiaoming-bot) -> [开发文档](https://github.com/Chuanwise/xiaoming-bot/tree/main/docs/Development.md) -> 交互器管理器

交互器是小明响应群、私聊或临时会话消息的工具。

一般只会在插件启动时使用交互器管理器注册一个交互器，其他时候很少用到该组件。

## 方法
其常用方法有：

### 以某用户身份执行一次输入
该方法用于**在当前线程**使用一个指定的信息匹配所有注册的交互器，并做出响应。

共有 `2` 个重载版本：
|返回类型|原型|异常类型|说明|
|---|---|---|---|
|`boolean`|`onInput(XiaomingUser, Message)`|`Exception`|抛出的异常为交互时可能产生的异常|
|`boolean`|`onInput(XiaomingUser, Message, Class)`|`Exception`|让用户与指定交互器的子类交互|
|`boolean`|`onCommand(XiaomingUser, Message)`|`Exception`|让交互器与 `CommandInteractor` 的子类交互|
|`boolean`|`onMessage(XiaomingUser, Message)`|`Exception`|让交互器与 `MessageInteractor` 的子类交互|

这两个方法可能引发阻塞，因为可能部分交互器涉及上下文，需要等待用户的下一条信息。所以不建议在主线程调用它们。

### 其他相关方法
|返回类型|原型|异常类型|说明|
|---|---|---|---|
|`Set<? extends Interactor>`|`getInteractors(XiaomingPlugin)`||获得一个插件注册了的所有交互器|
|`Map<? extends XiaomingPlugin, Set<? extends Interactor>>`|`getPluginInteractors()`||获得所有插件注册的所有交互器|
|`Set<? extends Interactor>`|`getCoreInteractors()`||获得内核注册的交互器|
|`void`|`register(Interactor, XiaomingPlugin)`||使用某插件的身份注册一个交互器|

> **声明**
> 
> 到此你已经阅读结束小明的开发文档有关**交互器管理器**的介绍，赶快去试一试它的功能吧！
>
> 返回[开发文档](https://github.com/Chuanwise/xiaoming-bot/tree/main/docs/Development.md)<br>
> 查看[插件中心](https://github.com/Chuanwise/xiaoming-bot/tree/main/docs/PluginCenter.md)<br>
> 查看[插件开发文档](https://github.com/Chuanwise/xiaoming-bot/tree/main/docs/PluginDevelopment.md)<br>
> 返回[项目首页](https://github.com/Chuanwise/xiaoming-bot/)<br>
> 
> |本文作者|最后更新时间|对应版本号|
> |---|---|---|
> |`Chuanwise`|`2021年6月18日`|`1.0`