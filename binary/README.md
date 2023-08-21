# 如何构建本地类库

构建本项目前，请在Release中下载WKHtmlToPDF的动态链接库，并将
它们解压到dependencies目录内，否则构建将会失败。

目前本项目使用CMake，您可以使用CMake在Windows或者MacOS构建此项目，
通常来说，如果您已经安装了CMake并且把它配置在系统的环境变量——Path中，
那么通过以下指令就能够构建此项目：

```bash
mkdir ./Build
cd Build
cmake ..
# 接下来要做的事情取决于你使用的构建系统。
# 如果生成的是Makefile，那么只需要执行此命令即可
make
```

Windows系统需要在MSYS2进行构建，MacOS系统需要您安装XCode，至少是XCode的构建工具。
