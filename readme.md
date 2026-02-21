本模组为原生`fabric`模组，使用`yarn mapping`。目前已经适配原生`forge`和`neoforge`

服务端无需安装，装了也不会有效果。
# 下载
[GitHub release](https://github.com/2894638479/fabric-blue-archive-halo/releases)

[modrinth](https://modrinth.com/mod/blue-archive-halo/versions)

[curseforge](https://www.curseforge.com/minecraft/mc-mods/blue-archive-halo/files/all?page=1&pageSize=20&showAlphaFiles=show)

## 依赖（2.0+）
[kotlinmcui](https://github.com/2894638479/KotlinMCUI)提供UI框架

## 光影（2.0+）
如果开启光影后看不到环：
- 调大`special setting`-`Extra far plane`
- 关闭光影自带的边界雾（border fog）
- 调大光环的不透明度（alpha）

## 客户端缓存（2.0+）
默认开启`special setting`-`clientCache`。是为了视野距离外依然能被渲染。

## 信标合并（2.0+）
开启`ClientCache`后开启`combineBeacon`。只会合并高度相同且光柱结构一样的信标。调节`combineRadius`更改触发合并的距离。