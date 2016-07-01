# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/lzm/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}
#指定代码的压缩级别
-optimizationpasses 5
#包明不混合大小写
-dontusemixedcaseclassnames
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
#优化 不优化输入的类文件
-dontoptimize
#预校验
-dontpreverify
#混淆时是否记录日志
-verbose
#混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-dontnote com.android.vending.licensing.ILicensingService

-keep class com.iflytek.**{*;}
-dontwarn com.justalk.*
-keep class com.justalk.**{*;}
#-keep class com.justalk.cloud.** { *; }
-keep interface com.justalk.cloud.** { *; }
#-keep class com.justalk.ui.MtcNotify { *; }

-keep class com.qiniu.**{*;}
-keep class com.qiniu.**{public <init>();}
-ignorewarnings

-keep public class com.smart.mirrorer.R$*{
    public static final int *;
}
-keepattributes InnerClasses
#不混淆R类里及其所有内部static类中的所有static变量字段
-keep public class **.R$*{
    pulic *;
}

-dontoptimize
-dontpreverify

-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
#==================gson==========================
-keep class com.google.gson.** {*;}

#==================protobuf======================
-dontwarn com.google.**
-keep class com.google.protobuf.** {*;}


#保护注解
-keepattributes Annotation
#EventBus keep
-keepclassmembers class ** {
    public void onEvent*(**);
}
#所有监听的方法都要列在这里
-keepclassmembers class ** {
    public void FinishActivityEvent(**);
    public void LogOutEvent(**);
    public void CallConnectEvent(**);
    public void LogInOkEvent(**);
    public void PushQuestionMsgEvent(**);
    public void PushTutorInfoMsgEvent(**);
    public void UpdateQuestionEvent(**);
    public void DeleteQuestionEvent(**);
    public void ReLoadQuestionEvent(**);
    public void CallConnectEvent(**);
    public void OffLineEvent(**);
}

#保持哪些类不被混淆
-keeppackagenames com.smart.mirrorer
-keeppackagenames com.bdj.video.build
-keeppackagenames com.videorecorder.bean
-keeppackagenames com.videorecorder.exception
-keeppackagenames com.videorecorder.handler
-keeppackagenames com.videorecorder.listener
-keeppackagenames com.videorecorder.util
-keeppackagenames com.videorecorder.widget

-keeppackagenames com.androidex.sharesdk

-keep class com.androidex.sharesdk.** {
     *;
}

#保留一个完整的包
-keep class com.bdj.video.build.** {
    *;
}

-keep class com.videorecorder.** {
    *;
}

-keep class com.smart.mirrorer.base.** {
    *;
}
-keep class com.smart.mirrorer.event.** {
    *;
}
-keep class org.greenrobot.eventbus.** {
    *;
}

-keep class com.tencent.mm.sdk.** {
   *;
}
-keep public class app.xiaoshuyuan.me.wxapi.WXEntryActivity{*;}
-keep public class app.xiaoshuyuan.me.wxapi.WXPayEntryActivity{*;}

#-libraryjars libs/alipaySDK-20150602.jar
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}

-keep class android.support.v4.** { *;}

-keep class com.smart.mirrorer.bean.**{
    *;
}

-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

#保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {   # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {# 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity { # 保持自定义控件类不被混淆
    public void *(android.view.View);
}
-keepclassmembers enum * {     # 保持枚举 enum 类不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable { # 保持 Parcelable 不被混淆
    public static final android.os.Parcelable$Creator *;
}

#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable

-keep public class android.webkit.WebView {*;}
-keep public class android.webkit.WebViewClient {*;}

# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

-keep class sun.misc.Unsafe { *; }
-keep class com.android.volley.** {*;}
-keep class com.android.volley.toolbox.** {*;}
-keep class com.android.volley.Response$* { *; }
-keep class com.android.volley.Request$* { *; }
-keep class com.android.volley.RequestQueue$* { *; }
-keep class com.android.volley.toolbox.HurlStack$* { *; }
-keep class com.android.volley.toolbox.ImageLoader$* { *; }

#如果有引用v4包可以添加下面这行
-keep public class * extends android.support.v4.app.Fragment
-dontwarn android.support.v4.**
-keep class android.support.v4.** {*;}
-keep interface android.support.v4.app.** {*;}

#忽略警告
-ignorewarning
#记录生成的日志数据,gradle build时在本项目根目录输出
#apk 包内所有 class 的内部结构
-dump class_files.txt
#未混淆的类和成员
#-printseeds seeds.txt
#列出从 apk 中删除的代码
#-printusage unused.txt
#混淆前后的映射
-printmapping mapping.txt