## SpannableString 用法

###经常在工作中经常会遇到一段文字中为了突出某个特定词语而要将其颜色，大小，粗细等设置成和其他字体不一样的问题。刚开始不知道SpannableString的时候自己能想到的解决办法是将文字分成三个textview在放置，将需要突出的词语专门放入到一个textview中来对其设置一些属性。这种办法不仅笨重，而且布局调节起来也麻烦。

###而SpannableString解决起这个问题就变的非常简单了，它可以直接的指定一段话中的某些关键字变成自己想要的样式。


###用法如下：

	SpannableString spannableString = new SpannableString(text);
	spannableString.setSpan(span, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	textview.setText(spannableString);


* text 为一段文字
* setSpan(Object what, int start, int end, int flags)方法
* what 代表一个行为，改变颜色，字体加粗，设置大小等等
* start，end代表what行为的起始位置
* flag是一个标识，固定的用 Spanned.SPAN_EXCLUSIVE_EXCLUSIVE，其他的参数可以参考


