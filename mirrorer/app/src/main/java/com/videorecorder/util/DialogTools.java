package com.videorecorder.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smart.mirrorer.R;


public class DialogTools {
	private static final String TAG = "DialogTools";

	/* --------------发帖代码 ---------------------end wangdehua */

	public static void showCameraExceptionDialog(final Activity instance,
			String msg, final DialogClickEventListener listener) {
		final AlertDialog alertDialog = new AlertDialog.Builder(instance)
			.setMessage(msg)
			.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						listener.onClick(which + "");
					}

				})
			.setCancelable(false)
			.create();
		alertDialog.show();

	}

	public interface DialogClickEventListener {
		void onClick(String tag);
	}

	/**
	 * 通用的底部弹出框
	 * 
	 * @param instance
	 * @param items
	 *            ：弹出框条目，不包括取消
	 * @param listener
	 *            ：条目监听事件
	 * @param isNeedLogin
	 *            ：是否需要登录
	 */
	public static void showCommonDialog(final Activity instance,
			String items[], final DialogClickEventListener listener,
			boolean isNeedLogin) {
		final Dialog dlg = new Dialog(instance, R.style.Theme_select_dialog);
		OnClickListener onBtnClick = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = (v.getTag() == null) ? "" : v.getTag().toString();
				listener.onClick(text);
				dlg.dismiss();
			}
		};

		LayoutInflater inflater = (LayoutInflater) instance
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.alert_item_latout, null);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		final LinearLayout content_list = (LinearLayout) layout
				.findViewById(R.id.content_list);
		content_list
				.setBackgroundResource(R.drawable.bg_common_circularbutton_state);
		Button btn_cancel = (Button) layout.findViewById(R.id.btn_cancel);
		btn_cancel
				.setBackgroundResource(R.drawable.bg_common_circularbutton_state);
		btn_cancel.findViewById(R.id.btn_cancel).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						dlg.dismiss();
					}
				});
		float density = instance.getResources().getDisplayMetrics().density;
		int height = (int) (density * 45);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT, height);
		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT, 2);
		for (int i = 0; i < items.length; i++) {
			String text = items[i];
			ImageView iv = new ImageView(instance);
			iv.setBackgroundResource(R.drawable.divider_horizontal);
			iv.setLayoutParams(params2);
			TextView btn = new TextView(instance);
			btn.setGravity(Gravity.CENTER);
			btn.setText(text);
			btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			btn.setBackgroundResource(R.drawable.bg_common_circularbutton_state);
			btn.setTextColor(instance.getResources()
					.getColor(R.color.light_gray));
			btn.setTag(text);
			btn.setOnClickListener(onBtnClick);
			btn.setLayoutParams(params);
			if (i == 0) {
				content_list.addView(btn);
			} else {
				content_list.addView(iv);
				content_list.addView(btn);
			}
		}

		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();
	}

}
