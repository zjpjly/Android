##如何删除Android手机系统APP


用Android机的同学们大都会有这样一个问题，就是自己的Android机上有很多自带的所谓“系统app”，它们不能删除但又没有别的同类app好用而且还得占内存（反正我是这么觉得。。。）。那么有没有办法删除这些“鸡肋app”呢？当然是有的啦~

首先我们要了解的一点内容：我们平时下载安装到手机上的app是放在手机上的data/data/区目录下的，这个目录下的app我们是可以进行删除的。而那些“系统app”是在一个叫system/app/的目录下存放的，这个目录下的app我们直接手动长按是删除不了的。接下来，我们就来说一说怎么删除这类app。

1.root手机（心疼手机不想root的同学，就木有办法往下继续了0.0）。root手机的软甲网上随便搜就好（我用的是root大师）。root完之后你会发下已经有部分“系统app”可以直接删除了，如下图，但剩余的一部分还是无法删除。。。

![](http://upload-images.jianshu.io/upload_images/2926229-1af084fbe17da3fa.jpeg?imageMogr2/auto-orient/strip)
![](http://upload-images.jianshu.io/upload_images/2926229-b442efbcc091ac27.jpeg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

2.用adb命令来删除剩下的app。命令步骤如下：

（1）打开cmd终端（Windows下）或Terminal（linux下），输入adb shell命令

（2）输入su，改变用户权限为root

（3）输入mount -o remount rw /system命令，使系统分区重新挂载，变为可读写

（4）输入cd system/app/，进入此目录下面

（5）输入ll命令，查看所需删除APK的名字（如下图）

![](http://upload-images.jianshu.io/upload_images/2926229-d1a33597cd596a6d.jpeg?imageMogr2/auto-orient/strip)


（6）输入rm HwAllBackup.apk删除想要删除的APK。（以HwAllBackup.apk为例，此APK也为系统APK，root工具无法删除）

至此一个“系统app”就删除完成了

## 注：每次执行完删除操作后，若想继续删除其他APK，需要重新执行mount -o remount rw /system命令，然后再执行删除操作。 ##