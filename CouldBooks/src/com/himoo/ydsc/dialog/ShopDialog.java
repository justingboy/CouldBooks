package com.himoo.ydsc.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.config.BookTheme;

/**
 * 自定义积分商城dialog
 * 
 */
public class ShopDialog extends Dialog {

	public ShopDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	public ShopDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public static class Builder {

		private Context context;
		private String title;
		private View contentView;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setContentView(View view) {
			this.contentView = view;
			return this;
		}

		public ShopDialog creat() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final ShopDialog dialog = new ShopDialog(context,
					R.style.Refresh_Dialog);
			View layout = inflater.inflate(R.layout.dialog_shop, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			TextView title = ((TextView) layout.findViewById(R.id.title));
			layout.setBackgroundColor(BookTheme.THEME_COLOR);
			LinearLayout content = ((LinearLayout) layout
					.findViewById(R.id.content));
			title.setText(this.title);
			content.addView(this.contentView, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			return dialog;
		}
	}

}
