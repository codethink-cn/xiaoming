# 小明

小明是一个插件化框架接口标准，旨在统一不同插件化平台的核心接口，使开发者只需要编写一次核心业务逻辑，即可在多个插件化平台上使用。

## 背景

围绕即时通讯软件设计的机器人功能为许多人带来了便利。它们让程序可以接受和处理 IM 软件中的内容，扩展了和系统交互的方式，受到了广泛好评。

然而，一旦开发者所依赖的框架停运，其功能往往难以迁移使用，这带来了大量的重写和适配工作，严重限制了社区的发展。

幸运的是，包括 [OneBot](https://onebot.dev/)、[Satori](https://github.com/satorijs/satori) 和 [Kritor](https://github.com/KarinJS/kritor) 在内的许多机器人接口标准的产生和推广改善了这一局面。但是，它们只统一了最核心的机器人功能接口，不含在其基础上继续开发新功能通常所必需的权限检查、命令执行、插件间通讯等接口。开发者仍然需要自行实现和封装这些功能，而这往往和具体插件化平台强相关。在插件平台停运后，仍然无法避免迁移和重写部分代码。

## 动机

本项目希望维护一个统一的插件化框架接口标准，进一步提升运行在包含机器人框架在内的各类框架上插件的兼容性和通用性，为用户和开发者带来便利。

## 社群

* [QQ 群](https://jq.qq.com/?_wv=1027&k=sjBXo6xh)：1028959718。

## 许可

```text
Copyright 2023 CodeThink Technologies and contributors.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
