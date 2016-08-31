# Android 自定义View笔记

##

###自定义View流程图
![](https://camo.githubusercontent.com/2fff630a00a9a8a64a227601235984c6cb3b0fcb/687474703a2f2f7777342e73696e61696d672e636e2f6c617267652f30303558746469326a7731663633387772657537346a333066633068656161792e6a7067)

![](http://doc.ithao123.cn/uploads/u/94/1a/941ae15c18cd92c1bfcecbb9967bc175.jpg)

View.requestLayout() 请求重新布局,重新调用：onMeasure，onLayout，onDraw；
用途：有 时我们在改变一个view 的内容之后 可能会造成显示出现错误，比如写ListView的时候 重用convertview中的某个TextView 可能因为前后填入的text长度不同而造成显示出错，此时我们可以在改变内容之后调用requestLayout方法加以解决。

View.invalidate()        刷新视图，相当于调用View.onDraw()方法


##

###字体属性及测量
##

![](http://img1.51cto.com/attachment/201205/204735397.png)

![](http://img1.51cto.com/attachment/201205/204802837.png)

![](http://img1.51cto.com/attachment/201205/204842429.png)

![](http://img1.51cto.com/attachment/201205/204858283.png)

![](http://img1.51cto.com/attachment/201205/204956427.png)