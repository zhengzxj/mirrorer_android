package com.smart.mirrorer.util.mUtil;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.smart.mirrorer.base.BaseApplication;

/**
 * Log工具，类似android.util.Log。
 * tag自动产生，格式: customTagPrefix:className.methodName(L:lineNumber),
 * customTagPrefix为空时只输出：className.methodName(L:lineNumber)。
 */
@SuppressWarnings("UnusedDeclaration")
public class L {
	private static boolean isTest = true;
	public static String customTagPrefix = "";

	private L() {

	}

	public static boolean allowD = true;
	public static boolean allowE = true;
	public static boolean allowI = true;
	public static boolean allowV = true;
	public static boolean allowW = true;
	public static boolean allowWtf = true;

	private static String generateTag() {
		String tag = "%s.%s(L:%d)";
		StackTraceElement caller = Thread.currentThread().getStackTrace()[4];
		String callerClazzName = caller.getClassName();
//		callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
		tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
		tag = TextUtils.isEmpty(customTagPrefix) ? tag : customTagPrefix + ":" + tag;
		return "miyu:" + tag;
	}

	public static Logger logger;

	public interface Logger {
		void d(String tag, String content);

		void d(String tag, String content, Throwable tr);

		void e(String tag, String content);

		void e(String tag, String content, Throwable tr);

		void i(String tag, String content);

		void i(String tag, String content, Throwable tr);

		void v(String tag, String content);

		void v(String tag, String content, Throwable tr);

		void w(String tag, String content);

		void w(String tag, String content, Throwable tr);

		void w(String tag, Throwable tr);

		void wtf(String tag, String content);

		void wtf(String tag, String content, Throwable tr);

		void wtf(String tag, Throwable tr);
	}

	public static void d(String content) {
		if(TextUtils.isEmpty(content))return;
		if (!allowD) return;
		String tag = generateTag();
		if (logger != null) {
			logger.d(tag, content);
		} else {
			Log.d(tag, content);
		}
	}
	public static void d(int content) {
		if (!allowD) return;
		String tag = generateTag();
		if (logger != null) {
			logger.d(tag, content+"");
		} else {
			Log.d(tag, content+"");
		}
	}
	public static void d(float content) {
		if (!allowD) return;
		String tag = generateTag();
		if (logger != null) {
			logger.d(tag, content+"");
		} else {
			Log.d(tag, content+"");
		}
	}
	public static void d(Object content) {
		if(content==null)return;
		if (!allowD) return;
		String tag = generateTag();
		if (logger != null) {
			logger.d(tag, content.toString());
		} else {
			Log.d(tag, content.toString());
		}
	}

	public static void d(String content, Throwable tr) {
		if (!allowD) return;
		String tag = generateTag();
		if (logger != null) {
			logger.d(tag, content, tr);
		} else {
			Log.d(tag, content, tr);
		}
	}

	public static void e(String content) {
		if (!allowE) return;
		String tag = generateTag();
		if (logger != null) {
			logger.e(tag, content);
		} else {
			Log.e(tag, content);
		}
	}

	public static void e(String content, Throwable tr) {
		if (!allowE) return;
		String tag = generateTag();
		if (logger != null) {
			logger.e(tag, content, tr);
		} else {
			Log.e(tag, content, tr);
		}
	}

	public static void e(Throwable tr) {
		if (!allowE) return;
		String content = tr.getLocalizedMessage();
		String tag = generateTag();
		if (logger != null) {
			logger.e(tag, content, tr);
		} else {
			Log.e(tag, content, tr);
		}
	}

	public static void i(String content) {
		if (!allowI) return;
		String tag = generateTag();
		if (logger != null) {
			logger.i(tag, content);
		} else {
			Log.i(tag, content);
		}
	}

	public static void i(String content, Throwable tr) {
		if (!allowI) return;
		String tag = generateTag();
		if (logger != null) {
			logger.i(tag, content, tr);
		} else {
			Log.i(tag, content, tr);
		}
	}

	public static void v(String content) {
		if (!allowV) return;
		String tag = generateTag();
		if (logger != null) {
			logger.v(tag, content);
		} else {
			Log.v(tag, content);
		}
	}

	public static void v(String content, Throwable tr) {
		if (!allowV) return;
		String tag = generateTag();
		if (logger != null) {
			logger.v(tag, content, tr);
		} else {
			Log.v(tag, content, tr);
		}
	}

	public static void w(String content) {
		if (!allowW) return;
		String tag = generateTag();
		if (logger != null) {
			logger.w(tag, content);
		} else {
			Log.w(tag, content);
		}
	}

	public static void w(String content, Throwable tr) {
		if (!allowW) return;
		String tag = generateTag();
		if (logger != null) {
			logger.w(tag, content, tr);
		} else {
			Log.w(tag, content, tr);
		}
	}

	public static void w(Throwable tr) {
		if (!allowW) return;
		String tag = generateTag();
		if (logger != null) {
			logger.w(tag, tr);
		} else {
			Log.w(tag, tr);
		}
	}

	public static void wtf(String content) {
		if (!allowWtf) return;
		String tag = generateTag();
		if (logger != null) {
			logger.wtf(tag, content);
		} else {
			Log.wtf(tag, content);
		}
	}

	public static void wtf(String content, Throwable tr) {
		if (!allowWtf) return;
		String tag = generateTag();
		if (logger != null) {
			logger.wtf(tag, content, tr);
		} else {
			Log.wtf(tag, content, tr);
		}
	}

	public static void wtf(Throwable tr) {
		if (!allowWtf) return;
		String tag = generateTag();
		if (logger != null) {
			logger.wtf(tag, tr);
		} else {
			Log.wtf(tag, tr);
		}
	}

	
	public static void show(final Context context,final String str){

		if(isTest) {

			if (android.os.Process.myTid() == BaseApplication.getMainThreadId())
			{
				// 如果在主线程中执行
				Toast toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
			else
			{
				// 需要转的主线程执行
				BaseApplication.mMainThreadHandler.post(new Runnable() {
					@Override
					public void run() {
						Toast toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}
				});
			}
		}
	}
}

