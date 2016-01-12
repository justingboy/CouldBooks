package com.himoo.ydsc.activity.more;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.activity.HomeActivity;
import com.himoo.ydsc.bean.KeyboardEnum;
import com.himoo.ydsc.bean.KeyboardEnum.ActionEnum;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.ui.swipebacklayout.SwipeBackActivity;
import com.himoo.ydsc.ui.utils.UIHelper;
import com.himoo.ydsc.util.SharedPreferences;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class PasswordSettingActivity extends SwipeBackActivity {

	@ViewInject(R.id.password_title_line)
	private View password_title_line;

	@ViewInject(R.id.password_text_input_toast)
	private TextView password_text_input_toast;

	@ViewInject(R.id.password_error_toast)
	private TextView password_error_toast;

	@ViewInject(R.id.pay_keyboard_del)
	private ImageView del;
	@ViewInject(R.id.pay_keyboard_zero)
	private ImageView zero;
	@ViewInject(R.id.pay_keyboard_one)
	private ImageView one;
	@ViewInject(R.id.pay_keyboard_two)
	private ImageView two;
	@ViewInject(R.id.pay_keyboard_three)
	private ImageView three;
	@ViewInject(R.id.pay_keyboard_four)
	private ImageView four;
	@ViewInject(R.id.pay_keyboard_five)
	private ImageView five;
	@ViewInject(R.id.pay_keyboard_sex)
	private ImageView sex;
	@ViewInject(R.id.pay_keyboard_seven)
	private ImageView seven;
	@ViewInject(R.id.pay_keyboard_eight)
	private ImageView eight;
	@ViewInject(R.id.pay_keyboard_nine)
	private ImageView nine;
	@ViewInject(R.id.password_key_1)
	private TextView box1;
	@ViewInject(R.id.password_key_2)
	private TextView box2;
	@ViewInject(R.id.password_key_3)
	private TextView box3;
	@ViewInject(R.id.password_key_4)
	private TextView box4;

	private ArrayList<String> mList = new ArrayList<String>();
	private Drawable drawable_Oval, drawable_Line;

	private String firstKey = null;
	private String secondKey = null;
	/** 判断是关闭密码,还是设置密码 */
	private boolean isClosePassword = false;
	/** 判断是修改密码 */
	private boolean isChangePassword = false;
	/** 输入密码错误的次数 */
	private int inputCount = 4;

	private String value;

	private boolean isUnlock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password_setting);
		isUnlock = getIntent().getBooleanExtra("unlock", false);
		if (isUnlock)
			setSwipeBackEnable(false);
		drawable_Oval = getResources().getDrawable(
				R.drawable.shape_password_oval);
		drawable_Line = getResources().getDrawable(
				R.drawable.shape_password_line);
	}

	@Override
	protected void initTitleBar() {
		// TODO Auto-generated method stub
		LinearLayout password_layout = (LinearLayout) this
				.findViewById(R.id.password_layout);
		password_layout.setBackgroundColor(BookTheme.THEME_COLOR);
		mTitleBar.setLeftTitle("更多");
		mTitleBar.setRightLogoGone();
		value = getIntent().getStringExtra("key");
		if (value != null && value.equals("SplashActivity")) {
			mTitleBar.setVisibility(View.GONE);
			password_title_line.setVisibility(View.GONE);
		} else {

			if (getIntent().getBooleanExtra("fixPw", false)) {
				mTitleBar.setTitle("修改数字密码");
				isClosePassword = true;
				isChangePassword = true;
			} else {
				String password = SharedPreferences.getInstance().getString(
						"password", null);
				if (password == null) {
					mTitleBar.setTitle("设置数字密码");
					isClosePassword = false;
				} else {
					isClosePassword = true;
					mTitleBar.setTitle("关闭启动密码");
				}
			}
		}
	}

	@OnClick({ R.id.pay_keyboard_del, R.id.pay_keyboard_zero,
			R.id.pay_keyboard_one, R.id.pay_keyboard_two,
			R.id.pay_keyboard_three, R.id.pay_keyboard_four,
			R.id.pay_keyboard_five, R.id.pay_keyboard_sex,
			R.id.pay_keyboard_seven, R.id.pay_keyboard_eight,
			R.id.pay_keyboard_nine })
	private void onClick(View v) {
		if (v == zero) {
			parseActionType(KeyboardEnum.zero);
		} else if (v == one) {
			parseActionType(KeyboardEnum.one);
		} else if (v == two) {
			parseActionType(KeyboardEnum.two);
		} else if (v == three) {
			parseActionType(KeyboardEnum.three);
		} else if (v == four) {
			parseActionType(KeyboardEnum.four);
		} else if (v == five) {
			parseActionType(KeyboardEnum.five);
		} else if (v == sex) {
			parseActionType(KeyboardEnum.sex);
		} else if (v == seven) {
			parseActionType(KeyboardEnum.seven);
		} else if (v == eight) {
			parseActionType(KeyboardEnum.eight);
		} else if (v == nine) {
			parseActionType(KeyboardEnum.nine);
		} else if (v == del) {
			parseActionType(KeyboardEnum.del);
		}
	}

	private void parseActionType(KeyboardEnum type) {
		// TODO Auto-generated method stub
		if (type.getType() == ActionEnum.add) {
			if (mList.size() < 4) {
				mList.add(type.getValue());
				updateUi();
			}
			if (mList.size() == 4) {
				String password = SharedPreferences.getInstance().getString(
						"password", null);
				if (value != null && value.equals("SplashActivity")) {
					if (password.equals(getPasswordFormList(mList))) {
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub

								UIHelper.startToActivity(
										PasswordSettingActivity.this,
										HomeActivity.class);
								finish();
							}
						}, 100);
					} else {
						inputCount--;
						if (inputCount == 0) {
							com.himoo.ydsc.ui.utils.Toast.showLong(this,
									"密码输入不正确，应用程序退出");
							finish();
						}
						password_error_toast.setBackgroundColor(Color.RED);
						password_error_toast.setVisibility(View.VISIBLE);
						password_error_toast.setText("密码错啦,还可输入" + inputCount
								+ "次");
						mList.clear();
						updateUi();
					}

				} else {

					if (isClosePassword) {
						if (password.equals(getPasswordFormList(mList))) {
							if (!isChangePassword) {
								SharedPreferences.getInstance().remove(
										"password");
								setResult(Activity.RESULT_OK);
								finish();
							} else {
								// 修改密码
								password_text_input_toast.setText("输入您的新密码");
								password_error_toast
										.setVisibility(View.INVISIBLE);
								inputCount = 4;
								isClosePassword = false;
								mList.clear();
								updateUi();
							}

						} else {
							inputCount--;
							if (inputCount == 0) {
								com.himoo.ydsc.ui.utils.Toast.showLong(this,
										"密码输入不正确，请稍后再输入");
								finish();
							}
							password_error_toast.setBackgroundColor(Color.RED);
							password_error_toast.setVisibility(View.VISIBLE);
							password_error_toast.setText("密码错啦,还可输入"
									+ inputCount + "次");
							mList.clear();
							updateUi();
						}

					} else {

						if (firstKey == null && secondKey == null)
							firstKey = getPasswordFormList(mList);
						else if (firstKey != null && secondKey == null) {
							secondKey = getPasswordFormList(mList);
						}

						if (firstKey != null && secondKey != null) {
							if (firstKey.equals(secondKey)) {
								SharedPreferences.getInstance().putString(
										"password", secondKey);
								delayedColse();
							} else {
								password_error_toast
										.setBackgroundColor(Color.RED);
								password_error_toast
										.setVisibility(View.VISIBLE);
								password_error_toast.setText("密码不匹配,再试一次。");
								secondKey = null;
							}

						}
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub

								mList.clear();
								if (!isChangePassword)
									password_text_input_toast
											.setText("再次输入您的密码");
								else {
									password_text_input_toast
											.setText("重复您的新密码");
								}
								updateUi();
							}
						}, 200);
					}
				}
			}
		} else if (type.getType() == ActionEnum.delete) {
			if (mList.size() > 0) {
				mList.remove(mList.get(mList.size() - 1));
				updateUi();
			}
		}

	}

	/**
	 * 更新密码UI
	 */
	private void updateUi() {

		if (mList.size() == 0) {
			box1.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					drawable_Line);
			box2.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					drawable_Line);
			box3.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					drawable_Line);
			box4.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					drawable_Line);
		} else if (mList.size() == 1) {
			box1.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					drawable_Oval);
			box2.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					drawable_Line);
			box3.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					drawable_Line);
			box4.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					drawable_Line);
		} else if (mList.size() == 2) {
			box1.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					drawable_Oval);
			box2.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					drawable_Oval);
			box3.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					drawable_Line);
			box4.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					drawable_Line);
		} else if (mList.size() == 3) {
			box1.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					drawable_Oval);
			box2.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					drawable_Oval);
			box3.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					drawable_Oval);
			box4.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					drawable_Line);
		} else if (mList.size() == 4) {
			box1.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					drawable_Oval);
			box2.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					drawable_Oval);
			box3.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					drawable_Oval);
			box4.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					drawable_Oval);
		}
	}

	/**
	 * 获取密码
	 * 
	 * @param list
	 */
	private String getPasswordFormList(List<String> list) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i));
		}
		return sb.toString();
	}

	/**
	 * 延迟关闭Activity
	 */
	private void delayedColse() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				com.himoo.ydsc.ui.utils.Toast.showShort(
						PasswordSettingActivity.this,
						isChangePassword ? "密码修改成功" : "密码设置成功");
				setResult(Activity.RESULT_OK);
				finish();
			}
		}, 250);
	}

}
