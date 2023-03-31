# 腾讯云/阿里云 云函数代理OpenAi

代理域名 ` api.openai.com`，需要修改可自行在`yml`配置文件修改

***

> java冷启动很慢，如果是实际使用，最好使用其他语言，或者一直保持云函数热启动（修改云函数配置）

***
# 阿里云配置
在 [https://cn.aliyun.com/](https://cn.aliyun.com/) 注册账号

进入云函数控制台：[https://fcnext.console.aliyun.com/overview](https://fcnext.console.aliyun.com/overview)

！！！先修改地区
![地区](./asset/aliyun/Snipaste_2023-03-30_22-04-36.png)
### 1、左侧选栏选择【服务及函数】，创建服务
![创建服务](./asset/aliyun/Snipaste_2023-03-30_21-57-02.png)
### 2、创建服务后选择【函数管理】，选择【创建函数】
![创建函数](./asset/aliyun/Snipaste_2023-03-30_22-02-11.png)
### 3、函数配置
- 创建函数方式：使用自定义运行时创建
- 基本设置：
    - 函数名称：
    - 请求处理程序类型：处理HTTP请求
- 函数代码
    - 运行环境：Java11
    - 函数代码：上传代码打包后的jar
    - 启动命令：java org.springframework.boot.loader.JarLauncher
    - 监听端口：8080
- 高级配置
    - vCPU：0.1
    - 内存：128M
    - 硬盘大小：512
    - 实例并发度：2
    - 执行超时时间：60
    - 时区：UTC
- 环境变量（不修改）
- 触发器配置（这里可能要创建一个新的触发器）：
    - 请求方法：ANY
    - 禁用公网访问：否
    - 认证方式：无需认证

## 4、获取代理url
创建函数后，查看函数详情，【触发器管理（URL）】中找到公网访问地址
![函数详情](./asset/aliyun/Snipaste_2023-03-30_22-07-09.png)
