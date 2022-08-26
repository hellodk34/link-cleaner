# link-cleaner

link cleaner, removing the track params of urls.

![downloads](https://img.shields.io/github/downloads/hellodk34/link-cleaner/total) ![language](https://img.shields.io/badge/language-Java-green) ![MIT](https://img.shields.io/github/license/hellodk34/link-cleaner)

**互联网上为了保护隐私我们能做些什么？看看我的想法和我带来的 link-cleaner**

前两天 v2ex 有个帖子 [网上留联系方式的隐私保护尝试](https://www.v2ex.com/t/874281) , 引起了我的思考。确实现在的大环境就是在收集隐私，前些年各大 App 还很流行年末搞出各种总结，近些年似乎有些收敛。这就是互联网企业的产品在利用各种手段搜集、统计我们日常使用手机的方方面面的行为，最明显的就是 A 用户分享，B 用户点击了 A 用户分享的内容，然后由此产生了一些微妙的联系，实际上最终这个联系非常的庞大，足以支撑起各大 App 产出各种各样的年度报告。

如今互联网如此渗透进我们的生活了，为了保护我们的隐私，我们能做些什么？我先抛砖引玉说几点

1. 注册非大陆地区的 手机号，在敏感网站上注册使用
2. 尽量使用邮箱注册各个网站，虽然大陆地区这样的网站已经几乎绝迹了
3. 尽量减少使用国内 App，包括桌面端和移动端的程序，桌面端的有些程序不得不用时可以塞进虚拟机打开，比如之前的 WPS 事件，完全可以跟 V 友开车上 Office 365
4. 下载软件只在官网、GitHub 等平台寻找下载链接，国内的下崽器就千万别碰了
5. 手机号千万千万保护好，不轻易泄露
6. 常用软件的设置，多看看，以 **保护自己的隐私** 作为原则设置各个选项。其实这里讲了很多点，如果你嫌麻烦或者不在乎隐私也就无所谓了
7. 多多使用 Google 和 Bing，baidu 只作为一些场景下的候选，比如 `site:tieba.baidu.com xxxx` 搜你的关键词在贴吧中大家的讨论，肯定能搜出来很多
8. **这一点是我很想提的**，**尽量减少使用应用内的分享功能！**，因为他们基本上会带有相应的 track params，也就是追踪参数。平台给你生成了带了个尾巴的链接，你分享了，别人点进去一定会有更多数据生成，追踪了分享人和点击人的行为
9. 上面那个帖子有 V 友提到，在网上留联系方式时，在 V 站大家习惯 base64，微信号直接 base64 出来的字符串是固定的，很容易被搜索引擎爬取到，可能会被人搜到你在其他地方的活动。但是加上一些其他字符串，一起 encode 结果就会变化，这真是一个非常 nice，非常省时省力省心的好方法。比如
    ```
    root@debian11:~# echo "my weixin id is: 12345678, shared on v2ex.com at 2022-08-25" |base64
    bXkgd2VpeGluIGlkIGlzOiAxMjM0NTY3OCwgc2hhcmVkIG9uIHYyZXguY29tIGF0IDIwMjItMDgt
    MjUK
    root@debian11:~# echo "bXkgd2VpeGluIGlkIGlzOiAxMjM0NTY3OCwgc2hhcmVkIG9uIHYyZXguY29tIGF0IDIwMjItMDgt
    MjUK" |base64 -d
    my weixin id is: 12345678, shared on v2ex.com at 2022-08-25
	```

我已经养成了习惯，给别人分享东西时，基本上是分享标题 + 链接，如果是链接要看它干不干净。

为此我用 Java 写了一个简单的应用，打包成了 jar，要使用的话安装 [OpenJDK11](https://www.injdk.cn/) 。

## 用法

复制从平台复制的链接，执行 **java -jar link-cleaner-1.0.0.jar**，**新的链接将会自动写入剪贴板，再粘贴即可**。我处理了 pdd、taobao、jd、bilibili 四个平台的链接。下面是一些例子。

pdd，别人通过应用内分享给你的商品，你点击后会直接给你展示是谁分享的。如果是你分享的链接，被很多人通过微信转发了，会不会社死？所以隐私很重要。

![拼多多极简白键帽分享带链接追踪功能-处理压缩后](https://image.940304.xyz/i/2022/08/25/630776655ec97.jpg)

## pdd

```
$ java -jar link-cleaner-1.0.0.jar
your original clipboard text is: https://mobile.yangkeduo.com/goods.html?_x_org=2&refer_share_uin=KSFDGHKGUQXWTDO6DFD4JEM4II_GEXDA&_x_query=%E6%BA%90%E5%B7%A5%E4%B8%9A%E9%94%AE%E5%B8%BD&share_uin=KSFDGHKGUQXWTDO6DFD4JEM4II_GEXDA&page_from=23&_wv=41729&refer_share_channel=copy_link&refer_share_id=LJ8vv4WYAvYeEDzl5BqMr2rbJCLWiX9u&goods_id=28707795xxxx&pxq_secret_key=VGKR5MJG66CF7YSADPCIDC4OAA3ZUKSORF6SJAVHT3JXSROMIAGA&_wvx=10
your new clipboard text is: https://mobile.yangkeduo.com/goods.html?goods_id=28707795xxxx
```

## taobao

```
$ java -jar link-cleaner-1.0.0.jar
your original clipboard text is: 【淘宝】https://m.tb.cn/h.fAwWH69?tk=XTP02u5xxxx CZ3457 「LuatOS墨水屏开发板（每个ID限购1件,,多拍不发）」
点击链接直接打开
your new clipboard text is:  CZ3457 「LuatOS墨水屏开发板（每个ID限购1件,,多拍不发）」
点击链接直接打开 https://item.taobao.com/item.htm?id=67803651xxxx
```

## jd

```
$ java -jar link-cleaner-1.0.0.jar
your original clipboard text is: https://item.m.jd.com/product/10001058xxxx.html?&utm_source=iosapp&utm_medium=appshare&utm_campaign=t_335139774&utm_term=CopyURL&ad_od=share&gx=RnE3kDMMOmWKmtRN6tUjCHNhknHA
your new clipboard text is: https://item.m.jd.com/product/10001058xxxx.html
```

## bilibili-1

```
$ java -jar link-cleaner-1.0.0.jar
your original clipboard text is: 【让你的耳朵怀孕！《A Moment Apart》极限竞速：地平线4配乐-哔哩哔哩】 https://b23.tv/GUOxxxx
your new clipboard text is: 【让你的耳朵怀孕！《A Moment Apart》极限竞速：地平线4配乐-哔哩哔哩】  https://www.bilibili.com/video/BV1M4411q7W8
```

## bilibili-2

```
$ java -jar link-cleaner-1.0.0.jar
your original clipboard text is: 【【何同学】我做了一个自己打字的键盘...】 https://www.bilibili.com/video/BV1W14y1b7Mq?share_source=copy_web&vd_source=f3e330de995a48b819604c85bc0dxxxx
your new clipboard text is: 【【何同学】我做了一个自己打字的键盘...】  https://www.bilibili.com/video/BV1W14y1b7Mq
```

源码在: https://github.com/hellodk34/link-cleaner 欢迎大家来添砖加瓦

to be continued...
