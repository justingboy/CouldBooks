package com.himoo.ydsc.base;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.himoo.ydsc.R;
import com.himoo.ydsc.dialog.RefreshDialog;
import com.himoo.ydsc.mvc.BaseModel;
import com.himoo.ydsc.mvc.ServiceListener;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.ui.view.TitleBar;
import com.himoo.ydsc.util.AppUtils;
import com.himoo.ydsc.util.MyLogger;
import com.himoo.ydsc.util.SharedPreferences;
import com.lidroid.xutils.ViewUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * 该类加入以下功能： 1."再按一次退出程序"提示(只需要调用addFirstToast()方法把activity添加到退出通知提示的集合即可)
 * 2.无需为view设置监听操作，只需要声明一个int[]类型的ids变量，把所有要监听的控件id放到该数组中即可，同时需要继承该类
 * 3.对AsyncTask做了内存优化。 4.带有自定义标题栏 5.带有MVC设计思想 6.在Android framework下，
 * 建议使用优化过的数据容器比如：SparseArray,SparseBooleanArray,LongSparseArray。
 * 通用的HashMap实现的内存使用率非常的低，因为他需要为每一个mapping创建一个分离的entry
 * object。另外，SparseArray类避免了系统对有些key的自动装箱，因而带来了更高的效率。
 */
public abstract class BaseActivity extends FragmentActivity implements
		ServiceListener {
	/** 相当于HashMap ,但是效率高于HashMap */
	private HashMap<Integer, String> activitys;
	/** 保存键值对 */
	private SharedPreferences sp;
	private AppUtils app_util;
	private FragmentManager manager;
	protected BaseFragment current_Fragment;
	public MyLogger Log;
	public static int flag = 1; // 无实际意义，只是为了使用SparseArray类
	private long exitTime = 0;
	private TitleBar title;

	private boolean slidemenuModel = true;
	/** 展示 刷新Dialog */
	private static final int REFRESH_DIALOG_SHOW = 0;
	/** 关闭 刷新Dialog */
	private static final int REFRESH_DIALOG_DIMISS = 1;

	private RefreshDialog mDialog;

	public Handler refreshHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH_DIALOG_SHOW:
				if (mDialog == null) {
					mDialog = new RefreshDialog(BaseActivity.this);
					mDialog.setCancelable(false);
				}
				mDialog.setMessage(msg.obj.toString());
				if (!mDialog.isShowing()) {
					mDialog.dismiss();
					mDialog.show();
				}
				break;
			case REFRESH_DIALOG_DIMISS:
				if (mDialog != null)
					if (mDialog.isShowing()) {
						mDialog.dismiss();
					}
				break;

			default:
				break;
			}

		};
	};

	/**
	 * 　显示 刷新Dialog
	 * 
	 * @param string
	 */
	protected void showRefreshDialog(String string) {
		Message msg = refreshHandler.obtainMessage();
		msg.what = REFRESH_DIALOG_SHOW;
		msg.obj = string;
		refreshHandler.sendMessage(msg);
	}

	/** 　关闭 刷新Dialog */
	protected void dismissRefreshDialog() {
		Message msg = refreshHandler.obtainMessage();
		msg.what = REFRESH_DIALOG_DIMISS;
		refreshHandler.sendMessage(msg);
	}

	/**
	 * 设置是否兼容SlideMenu组件
	 * 
	 * @param slidemenuModel
	 */
	public void setSlidemenuModel(boolean slidemenuModel) {
		this.slidemenuModel = slidemenuModel;

	}

	/**
	 * 设置不兼容SlideMenu组件，并刷新该activity的视图
	 * 
	 * @param view
	 *            该activity的视图
	 */
	public void setSlidemenuModel(View view) {
		this.slidemenuModel = false;
		setContentView(view);
	}

	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);

		// 修改状态栏颜色，4.4+生效
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus();
		}
		// 设置状态栏背景色
		setStatusBarTintResource(R.color.status_bar_bg);

		Log = MyLogger.kLog();
		sp = SharedPreferences.getInstance();
		manager = getSupportFragmentManager();
		app_util = new AppUtils(getApplicationContext());
		// 将activity添加到自定义界面集合
		BaseApplication.getInstance().addActivity(this);
		activitys = BaseApplication.getInstance().getUIS();
		onCreate(sp, manager, savedInstanceState);
	}

	@TargetApi(19)
	protected void setTranslucentStatus() {
		Window window = getWindow();
		// Translucent status bar
		window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
				WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		// Translucent navigation bar
		window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
				WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
	}

	/**
	 * 设置状态栏的背景
	 */
	protected void setStatusBarTintResource(int color) {
		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(color);// 通知栏所需颜色
	}

	/**
	 * 重写setContentView()方法，添加自定义标题栏
	 */
	@Override
	public void setContentView(int layoutResID) {
		if (!slidemenuModel) {
			title = new TitleBar(this);
			LinearLayout linear = new LinearLayout(this);
			linear.setOrientation(LinearLayout.VERTICAL);
			View view = View
					.inflate(getApplicationContext(), layoutResID, null);
			linear.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT));
			linear.addView(title, 0);
			linear.addView(view, 1);
			super.setContentView(linear);
		} else {
			super.setContentView(layoutResID);
		}

	}

	@Override
	public void setContentView(View view) {
		if (!slidemenuModel) {
			title = new TitleBar(this);
			LinearLayout linear = new LinearLayout(this);
			linear.setOrientation(LinearLayout.VERTICAL);
			linear.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT));
			linear.addView(title, 0);
			linear.addView(view, 1);
			super.setContentView(linear);
		} else {
			super.setContentView(view);
		}

	}

	protected abstract void initEvent();

	public void setCurrentFragment(BaseFragment frag) {
		current_Fragment = frag;
	}

	/**
	 * 获取标题栏
	 * 
	 * @return
	 */
	public TitleBar getTitleBar() {
		return title;
	}

	/**
	 * 将Activity添加到退出通知
	 * 
	 * @param activityName
	 *            activity的类名
	 */
	public void addFirstToast(String activityName) {
		activitys.put(flag, activityName);
		flag++;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000
					&& activitys.containsValue(getActivityName())) {
				Toast.showShort(this, "再按一次退出程序");
				exitTime = System.currentTimeMillis();
			} else if (activitys.containsValue(getActivityName())) {
				BaseApplication.getInstance().exit();
			}
		}
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& activitys.containsValue(getActivityName())) {
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			BaseApplication.getInstance().delActivity(this);
			return true;
		}
		return onKeyDownMethod(keyCode, event);
	}

	/**
	 * 无需重写onKeyDown()方法,需要时应该重写该方法 已经屏蔽了返回键，如需处理返回键，请重写onKeyDown()方法
	 * 
	 * @param keyCode
	 * @param event
	 * @return
	 */
	protected boolean onKeyDownMethod(int keyCode, KeyEvent event) {
		// 如果启用了BasePage类，下面两行代码需要复制在activity的onKeyDown()方法中
		// BaseFragment frag = (BaseFragment) manager.findFragmentByTag("");
		// return frag.getPager().onKeyDownMethod(keyCode,event);
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 如果启动了BasePager，请将如下代码迁移到BasePager所依附的Activity中
	 * 但需在此之前调用setCurrentFragment()方法才会有效
	 */
	// public boolean onKeyDownMethod(int keyCode, KeyEvent event){
	// return current_Fragment.getCurrentPage().onKeyDownMethod(keyCode, event);
	// }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BaseApplication.getInstance().clearAsyncTask();
	}

	/**
	 * 获取当前正在运行的Activity
	 * 
	 * @return
	 */
	protected String getActivityName() {
		return app_util.getActivityName();
	}

	/**
	 * 传递数据到指定的Fragment中，会调用对应的Fragment的setArguments()方法传递数据。Fragment需要重写该方法接收数据
	 * 
	 * @param tag
	 *            Fragment的标识
	 * @param data
	 *            要传递的数据
	 */
	public void addToFragmentData(String tag, Bundle data) {
		BaseFragment frag = getFragment(tag);
		addToFragmentData(frag, data);
	}

	public void addToFragmentData(BaseFragment frag, Bundle data) {
		frag.setArguments(data);
	}

	/**
	 * 通过Tag找Fragment
	 * 
	 * @param tag
	 * @return
	 */
	public BaseFragment getFragment(String tag) {
		return (BaseFragment) manager.findFragmentByTag(tag);
	}

	/**
	 * 启动一个Activity
	 * 
	 * @param clz
	 *            Activity实例的Class对象
	 */
	public void startActivity(Class<? extends BaseActivity> clz) {
		Intent intent = new Intent(getApplicationContext(), clz);
		startActivity(intent);
	}

	/**
	 * 启动一个Activity，带有启动动画
	 * 
	 * @param clz
	 *            Activity实例的Class对象
	 */
	public void startActivity(Class<? extends BaseActivity> clz, int enterAnim,
			int exitAnim) {
		Intent intent = new Intent(getApplicationContext(), clz);
		startActivity(intent, enterAnim, exitAnim);
	}

	/**
	 * 启动一个Activity，带有启动动画
	 * 
	 * @param action
	 *            Activity实例的action动作
	 */

	public void startActivity(String action) {
		Intent intent = new Intent(action);
		startActivity(intent);
	}

	/**
	 * activity之间的切换，默认渐入渐出动画
	 */
	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		// overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
		// //缩放动画
	}

	/**
	 * 优化过后的获取Context对象的方法
	 */
	@Override
	public Context getApplicationContext() {
		return new SoftReference<Context>(super.getApplicationContext()).get();
	}

	/**
	 * 自定义activity之间的切换动画
	 * 
	 * @param intent
	 * @param enterAnim
	 *            进入时动画
	 * @param exitAnim
	 *            出去时动画
	 */
	public void startActivity(Intent intent, int enterAnim, int exitAnim) {
		super.startActivity(intent);
		overridePendingTransition(enterAnim, exitAnim);
	}

	/**
	 * Activity的生命周期onCreate()方法
	 * 
	 * @param sp
	 *            SharedPreferences文件对象
	 * @param manager
	 *            管理Fragment的对象，如果使用Fragment可以跟本框架做到无缝的结合
	 * @param savedInstanceState
	 *            Activity的状态保存
	 */
	public abstract void onCreate(SharedPreferences sp,
			FragmentManager manager, Bundle savedInstanceState);

	/**
	 * Fragment之间的切换方法，加载Fragment方法.<br />
	 * 重写该方法时要注意的问题：
	 * <ol>
	 * <li>该方法尽量放在activity中。避免Fragment的嵌套出现id资源找不到的问题,最好Fragment里面不要在嵌套Fragment.
	 * </li>
	 * <li>在加载之前需判断是否已经加载了，如果是 请先移除，在加载。避免报该Fragment已经加载了的问题.</li>
	 * <li>该方法不能在activity的生命周期中使用。避免回退栈的问题.<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	 * (原因：addToBackStack()方法不能在activity的生命周期中使用)</li>
	 * </ol>
	 * 
	 * @param frag
	 *            中间容器显示的Fragment
	 * @param tag
	 *            中间容器的标识
	 */
	public void jumpFragment(BaseFragment frag, String tag) {
	}

	/**
	 * Fragment之间的切换方法，加载Fragment方法
	 * 
	 * @param frag
	 * @param tag
	 * @param isHiddenNav
	 *            是否隐藏导航栏，至于如何实现隐藏需开发者自己考虑
	 */
	public void jumpFragment(BaseFragment frag, String tag, boolean isHiddenNav) {
	}

	@Override
	public void handlerIntent(BaseModel model, int dataInstruction) {
	}

	@Override
	public void handlerIntent(List<? extends BaseModel> models,
			int dataInstruction) {
	}

	@Override
	public void handlerIntent(SparseArray<? extends BaseModel> models,
			int dataInstruction) {
	}

	@Override
	public void handlerIntent(File file, int dataInstruction) {
	}
}