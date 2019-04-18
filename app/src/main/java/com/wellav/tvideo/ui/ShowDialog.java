package com.wellav.tvideo.ui;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wellav.tvideo.R;

public class ShowDialog {

	// 加载数据对话框
	private static Dialog mLoadingDialog;

	/**
	 * 显示加载对话框
	 * @param context 上下文
	 * @param msg 对话框显示内容
	 * @param cancelable 对话框是否可以取消
	 */
	public static void showDialogForLoading(Activity context, String msg, boolean cancelable) {
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
		TextView loadingText = (TextView) view.findViewById(R.id.id_tv_loading_dialog_text);
		loadingText.setText(msg);

		mLoadingDialog = new Dialog(context, R.style.loading_dialog_style);
		// 设置点击屏幕Dialog不消失
		mLoadingDialog.setCanceledOnTouchOutside(false);
		mLoadingDialog.setCancelable(cancelable);
		mLoadingDialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		mLoadingDialog.show();
	}

	// 关闭加载对话框
	public static void hideDialogForLoading() {
		if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
			mLoadingDialog.cancel();
		}
	}

}
