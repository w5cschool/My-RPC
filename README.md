
## 运行项目

### 导入项目

fork 项目到自己的仓库，然后克隆项目到自己的本地：`git clone git@github.com:username/guide-rpc-framework.git`，使用 IDEA 打开，等待项目初始化完成。


### 下载运行 zookeeper

这里使用 Docker 来下载安装。

下载：

```shell
docker pull zookeeper:3.5.8
```

运行：

```shell
docker run -d --name zookeeper -p 2181:2181 zookeeper:3.5.8
```

###   运行 NettyServerMain
###   运行 NettyClientMain
# My-RPC
