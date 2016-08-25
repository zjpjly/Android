## SpannableString 用法

###经常在工作中经常会遇到一段文字中为了突出某个特定词语而要将其颜色，大小，粗细等设置成和其他字体不一样的问题。刚开始不知道SpannableString的时候自己能想到的解决办法是将文字分成三个textview在放置，将需要突出的词语专门放入到一个textview中来对其设置一些属性。这种办法不仅笨重，而且布局调节起来也麻烦。

###而SpannableString解决起这个问题就变的非常简单了，它可以直接的指定一段话中的某些关键字变成自己想要的样式。
##
###用法如下：

	SpannableString spannableString = new SpannableString(text);
	spannableString.setSpan(span, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	textview.setText(spannableString);


* text 为一段文字
* setSpan(Object what, int start, int end, int flags)方法
* what 代表一个行为，改变颜色，字体加粗，设置大小等等
* start，end代表what行为的起始位置
* flag是一个标识，固定的用 Spanned.SPAN_EXCLUSIVE_EXCLUSIVE，其他的参数可以参考


###what的方法在网上可以搜索到很多，列举一下自己经常会用的一些：

* new ForegroundColorSpan(Color.RED) 设置字符颜色
* new BackgroundColorSpan(Color.RED) 设置背景颜色
* new StyleSpan() arg: Typeface.NORMAL,Typeface.BOLD,Typeface.ITALIC,Typeface.BOLD_ITALIC 设置字体粗细
* new UnderlineSpan() 设置下划线
* new URLSpan("sms:10086") 设置发送短信
* new URLSpan("http://www.hao123.com") 设置打开网页
* new URLSpan("tel:4155551212") 设置打电话
* new ClickableSpan 设置点击事件

##
###在项目中使用时所遇到的“坑”

* 首先第一个要说的就是 setMovementMethod(LinkMovementMethod.getInstance())这个设置，因为如果你对文字设置了URLSpan("sms:10086")，URLSpan("http://www.hao123.com") ，URLSpan("tel:4155551212")，ClickableSpan这四个中的任何一个方法的时候就需要对自己的textview设置setMovementMethod(LinkMovementMethod.getInstance())方法，否则这四个方法会失效  


* textview.setText(spannableString)时不能加"\n","\b"等字符，若想换行之类的可以textview.append("\n"),不能textview.setText(spannableString+"\n")


* 一个spannableString同时设置了URLSpan("http://www.hao123.com")，ForegroundColorSpan(Color.RED)属性时，要先设置URL，后设置ForegroundColorSpan。因为URL自带颜色若设置在ForegroundColorSpan后面的时候，则ForegroundColorSpan设置的颜色会失效。
##

###new ClickableSpan 需要重写两个方法 updateDrawState（）和 onClick（）
*updateDrawState（）用来设置文字属性

*onClick（）用来设置文字点击事件

 