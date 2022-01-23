### 小明机器人框架 XiaoMingBot
# 开发文档
> 你当前的位置：[项目首页](https://github.com/Chuanwise/XiaoMingBot) -> 开发文档

欢迎来到小明机器人框架的开发文档。

阅读开发文档时，我们默认你已经掌握了小明的使用方法。如果你尚不清楚小明组件的构成，请查阅[用户手册](http://chuanwise.cn:10074/#/manual)。

## 小明开发方式
将你编写的代码和小明结合起来，一共有两种方式：小明调用你的代码（开发小明插件）和你调用小明（小明作为你的组件）。它们的区别主要在[启动小明的方式](#启动小明的方式)。

## 小明内部的组件
### 小明本体
小明本体的 `API` 为 `XiaomingBot`。几乎在小明的任何组件处，都能通过 `getXiaomingBot()` 获得该组件所属的机器人。小明本体有许多其他组件的访问器，可由此调用小明的其他组件。

除了上述访问其他组件的访问器，还有这些方法：
|      返回类型       |       方法名       |       说明         |
|--------------------|--------------------|-------------------|
|       `void`       |      `start()`     |      启动小明      |
|       `void`       |       `stop()`     |      关闭小明      |
|       `void`       |     `isStop()`     | 判断小明是否被关闭  |
|       `void`       |    `optimize()`    |    优化小明性能     |

### 各类插件
* 插件开发文档：[PluginDevelopment](http://chuanwise.cn:10074/#/dev/plugin)
* 插件中心：[PluginCenter](http://chuanwise.cn:10074/#/plugin/)

### 其他组件
|组件名|标识符|文档|说明|
|---|---|---|---|
|交互器管理器|`InteractorManager`|[InteractorManager](http://chuanwise.cn:10074/#/InteractorManager.md)|响应 `QQ` 消息的**交互器**的管理器，用于注册、卸载、查看交互器
|接待员管理器|`ReceptionistManager`|[ReceptionistManager](http://chuanwise.cn:10074/#/ReceptionistManager.md)|专职处理用户消息的接待员的管理器，用于查看用户交互状态、以用户身份执行指令等
|调度器|`Scheduler`|[Scheduler](http://chuanwise.cn:10074/#/Scheduler.md)|进行异步任务、定时、周期任务等操作的小明统一的线程池
|权限管理器|`PermissionManager`|[PermissionManager](http://chuanwise.cn:10074/#/PermissionManager.md)|管理用户在小明处的权限等信息
|事件管理器|`EventListenerManager`|[EventListenerManager](http://chuanwise.cn:10074/#/EventListenerManager.md)|监听各类事件的监听器的管理器，用于注册、卸载、查看监听器
|响应群管理器|`ResponseGroupManager`|[ResponseGroupManager](http://chuanwise.cn:10074/#/ResponseGroupManager.md)|用来增加、删除、获取有关响应群等设置
|统计数据|`Statistician`|[Statistician](http://chuanwise.cn:10074/#/Statistician.md)|小明运行状态、调用次数等统计数据的管理器
|基础配置信息|`Configuration`|[Configuration](http://chuanwise.cn:10074/#/Configuration.md)|小明的基础设置，例如最长等待时间、调用限制数据等等，大部分设置修改后重启才能生效
|用户调用限制器|`UserCallLimitManager`|[UserCallLimitManager](http://chuanwise.cn:10074/#/UserCallLimitManager.md)|限制用户高频或多次调用小明的管理器
|小明账户管理器|`AccountManager`|[AccountManager](http://chuanwise.cn:10074/#/AccountManager.md)|查看、管理用户指令记录、相关事件的痕迹等等
|插件管理器|`PluginManager`|[PluginManager](http://chuanwise.cn:10074/#/PluginManager.md)|管理、获取、加载或卸载现有的插件相关功能

## 启动小明的方式
### 开发小明插件
将插件 `jar` 文件放在小明目录的 `plugins` 文件夹中，重启小明即可加载你的插件。

> **声明**
> 
> 到此你已经阅读结束小明的开发文档，赶快去写几个插件试一试吧！<br>
> **小明及相关插件的技术交流 / 用户 QQ 群**：`1028959718`
>
> 返回[开发文档](http://chuanwise.cn:10074/#/dev/)<br>
> 查看[插件中心](http://chuanwise.cn:10074/#/plugin/)<br>
> 查看[插件开发文档](http://chuanwise.cn:10074/#/dev/plugin)<br>
> 返回[项目首页](https://github.com/Chuanwise/XiaoMingBot/)<br>
> 
> |本文作者|最后更新时间|对应版本号|
> |---|---|---|
> |`Chuanwise`|`2021年6月18日`|`1.0`