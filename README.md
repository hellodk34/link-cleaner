# UrlParamsRemover
A simple UrlParamsRemover, auto remove your clipboard's url params and write the new url to your system clipboard.

## 下载 jar file

从 [releases](https://github.com/hellodk34/UrlParamsRemover/releases/latest/download/UrlParamsRemover.jar) 下载最新 jar file.

在 jdk11 下开发和测试，不知道 java8 是否兼容。

## 编写 batch file

```
@ECHO OFF
start java -jar <your_jar_file_path>.jar
```

## 编写 powershell profile 脚本

此处可参考我的文章: [为 PowerShell 设置 alias](https://hellodk.cn/post/935)

编辑文件 `xxx\WindowsPowerShell\Microsoft.PowerShell_profile.ps1`，如果没有请生成，请参考上文操作。

```
function url {d:\UrlParamsRemover.bat}
```

此处 url 就是 alias.

---

后记：此程序有点 low，希望有更好的实现，欢迎反馈。:)
