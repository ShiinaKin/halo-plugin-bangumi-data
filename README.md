<p align="center">
    <a target="_blank" rel="noopener noreferrer">
        <img width="100" src="./docs/logo.png" alt="LOGO" />
    </a>
</p>

## plugin-bangumi-data

![Preview Image](./docs/img-1.png)

### 介绍

这是一个用于获取和展示 Bangumi 数据的插件，支持在独立页面中展示个人的 Bangumi 数据。

### 使用

1. 安装后，请在插件配置中绑定你的Bangumi信息
2. 通过`/bangumi`访问插件提供的默认展示页面

> 首次访问时，需要加载数据，可能暂时出现`No Data`字样
>
> 如果网络没有问题，那么数据会在几十秒内加载完成，此后每日0点会自动更新数据
> 
> 可以在 `后台->工具->Bangumi数据源` 中手动更新数据

### 主题开发者

插件提供 `bangumiDataFinder` 以获取数据

数据结构定义可在 [api/model](https://github.com/ShiinaKin/halo-plugin-bangumi-data/tree/main/api/src/main/kotlin/io/sakurasou/halo/bangumi/model) 和 [BangumiUserData](https://github.com/ShiinaKin/halo-plugin-bangumi-data/blob/main/src/main/kotlin/io/sakurasou/halo/bangumi/entity/BangumiUserData.kt) 中查看
