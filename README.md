
# VolleyHelper

一个Volley辅助库，采用Hook方式，通过反射动态代理，无入侵方式，避免走内存，直走IO磁盘，提高效率。

在原生Volley基础上，新支持的功能：

- GsonRequest :Json格式的Gson解析的请求
- FormRequest :Form表单的请求
- SingleFileRequest：上传文件的请求
- DownloadRequest: 下载文件的请求

**前期准备**

项目Module的build.gradle依赖:
```
compile 'com.xingen:volleyHelper:1.0.0'
```
本项目额外依赖volley和gson库，也需添加:
```
    compile 'com.google.code.gson:gson:2.2.4'
    compile 'com.android.volley:volley:1.1.1'
```

**使用介绍**

博客详细介绍：[Android开发一个VolleyHelper库，Hook Volley方式，无入侵实现(Form表单、JSON、文件上传、文件下载) ](http://blog.csdn.net/hexingen/article/details/81385125)


License
-------

    Copyright 2018 HeXinGen.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.