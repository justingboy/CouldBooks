package com.himoo.ydsc.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.himoo.ydsc.R;
import com.himoo.ydsc.activity.HotwordsActivity;
import com.himoo.ydsc.activity.SearchResultActivity;
import com.himoo.ydsc.base.BaseFragment;
import com.himoo.ydsc.base.quickadapter.BaseAdapterHelper;
import com.himoo.ydsc.base.quickadapter.QuickAdapter;
import com.himoo.ydsc.bean.BookKeyWord;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.db.BookDb;
import com.himoo.ydsc.db.bean.BookSearchRecords;
import com.himoo.ydsc.http.HttpConstant;
import com.himoo.ydsc.http.HttpOperator;
import com.himoo.ydsc.listener.NoDoubleClickListener;
import com.himoo.ydsc.manager.PageManager;
import com.himoo.ydsc.speech.JsonParser;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.util.SharedPreferences;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.ios.dialog.AlertDialog;
import com.ios.edittext.SearchEditText;
import com.ios.edittext.SearchEditText.OnEditTextFocuseChangListener;
import com.ios.edittext.SearchEditText.OnSearchClickListener;
import com.ios.tagview.TagGroup;
import com.ios.tagview.TagGroup.OnTagClickListener;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 
 * 小說搜索功能模块
 * 
 */
public class SearchFragment2 extends BaseFragment implements
		OnSearchClickListener, OnEditTextFocuseChangListener,
		OnTagClickListener {

	@ViewInject(R.id.layout_search_bg)
	private LinearLayout layout_search_bg;

	/** 搜索 EditText */
	@ViewInject(R.id.more_book_search)
	private SearchEditText bookSearch;

	@ViewInject(R.id.listview_history)
	private ListView listview_history;

	/** 语音输入功能按钮 */
	@ViewInject(R.id.search_speech)
	private ImageView search_speech;

	/** 语音输入功能按钮 */
	@ViewInject(R.id.bookTagView)
	private TagGroup bookTagView;

	/** 语音输入功 */
	@ViewInject(R.id.tv_hotwords)
	private TextView tv_hotwords;

	/** 删除历史记录 */
	@ViewInject(R.id.tv_delete)
	private TextView tv_delete;

	/** 　默认的关键字 */
	public static String[] keywords = { "玄界之门", "盗墓笔记", "完美世界", "绝世唐门", "武极天下",
			"大道独行", "唐七公子", "斗罗大陆", "剑道独神" };

	/** XUtils http 请求 */
	private HttpUtils http;
	/** 数据库 */
	private BookDb bookDb;

	/** 语音识别Dialog */
	private RecognizerDialog mIatDialog;

	/** 语音识别 */
	private SpeechRecognizer mIat;

	/** 用HashMap存储听写结果 */
	private HashMap<String, String> mIatResultsMap = new LinkedHashMap<String, String>();

	/** 是否可以搜索了 */
	private boolean isKeyEnterDown = false;

	/** 是否要重新刷新 */
	private boolean isAfresh = false;

	private HistoryAdapter adapter;

	private int mCurrentPosition = -1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			SharedPreferences sp, Bundle savedInstanceState,
			PageManager pageManager) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_search2, container,
				false);
		return view;
	}

	@Override
	public void initData() {
		// getKeyWordRequest((int) (Math.random() * 30), 9);
		getKeyWordRequest(1, 9);
		initSpeech(getActivity());

		loadHistoryData();
		layout_search_bg.setBackgroundColor(BookTheme.THEME_COLOR);
		bookSearch.setTextColor(BookTheme.THEME_COLOR);
		bookSearch.setOnSearchClickListener(this);
		bookSearch.setOnFocuesChangeListener(this);
		search_speech.setOnClickListener(this);
		tv_delete.setOnClickListener(this);

	}

	/**
	 * 初始化KeywordsFlow
	 */
	private void initKeyWordFlow(String[] keys) {
		bookTagView.setTagThemColor(BookTheme.THEME_COLOR);
		bookTagView.setTags(keys == null ? keywords : keys);
		bookTagView.setOnTagClickListener(this);
		tv_hotwords.setOnClickListener(this);
		tv_hotwords.setOnClickListener(new NoDoubleClickListener() {

			@Override
			public void onNoDoubleClick(View v) {
				// TODO Auto-generated method stub
				isAfresh = true;
				Intent intent = new Intent(getActivity(),
						HotwordsActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onSearchClick(View view) {
		// TODO Auto-generated method stub
		if (isKeyEnterDown) {
			if (!TextUtils.isEmpty(bookSearch.getText())) {
				listview_history.setFocusable(false);
				save(bookSearch.getText().toString());
				startToActivity(bookSearch.getText().toString());
			}
			isKeyEnterDown = false;
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);

		switch (v.getId()) {
		// case R.id.tv_hotwords:
		// isAfresh = true;
		// Intent intent = new Intent(getActivity(), HotwordsActivity.class);
		// startActivity(intent);
		//
		// break;
		case R.id.tv_delete:

			new AlertDialog(getActivity()).builder().setTitle("删除")
					.setMsg("您确定要删除历史记录吗?")
					.setNegativeButton("取消", new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

						}
					}).setPositiveButton("确定", new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							bookDb.deleteRecords(BookSearchRecords.class);
							if (adapter != null) {
								adapter.clear();
								adapter.notifyDataSetChanged();
							}
						}

					}).show();

			break;
		case R.id.search_speech:
			startSpeech();

			break;

		default:
			break;
		}

	}

	/**
	 * 开启语音功能
	 */
	private void startSpeech() {
		mIatDialog.setListener(mRecognizerDialogListener);
		mIatDialog.show();
	}

	/**
	 * 请求关键字
	 * 
	 * @param page
	 * @param size
	 */
	private void getKeyWordRequest(int page, int size) {
		if (http == null) {
			http = new HttpUtils();
			http.configTimeout(3000);
			http.configSoTimeout(3000);
		}
		showRefreshDialog(" 加载热词中 ");
		String url = HttpConstant.BASE_URL_KEYWORD
				+ HttpOperator.getKeyWordRequestHeard(page, size);
		http.send(HttpMethod.GET, url, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				try {

					dismissRefreshDialog();
					Gson gson = new Gson();
					ArrayList<BookKeyWord> list = gson.fromJson(
							responseInfo.result,
							new TypeToken<ArrayList<BookKeyWord>>() {
							}.getType());
					if (list != null && !list.isEmpty()) {
						for (int i = 0; i < list.size(); i++) {
							keywords[i] = list.get(i).getKeyword();
						}
						initKeyWordFlow(keywords);

					} else {
						initKeyWordFlow(null);
					}
				} catch (Exception e) {
					initKeyWordFlow(null);
				}

			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
				dismissRefreshDialog();
				if (getActivity() != null) {
					Log.e(error);
				}
				initKeyWordFlow(null);
			}

		});
	}

	@Override
	public void onFocuseChange() {
		isKeyEnterDown = true;
	}

	/**
	 * 保存关键字到数据库
	 * 
	 * @param keyword
	 */
	private void save(String keyword) {
		BookSearchRecords record = new BookSearchRecords();
		record.setRecord(keyword);
		bookSearch.setText(keyword);
		bookSearch.setSelection(keyword.length());
		bookSearch.postInvalidate();
		bookDb.saveBookSearch(record);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/**
	 * 初始化语言功能
	 * 
	 * @param context
	 */
	private void initSpeech(Context context) {

		mIat = SpeechRecognizer.createRecognizer(context, mInitListener);
		// 2.设置听写参数，详见《科大讯飞MSC API手册(Android)》SpeechConstant类
		setSpeechParam();
		// 3.开始听写 mIat.startListening(mRecoListener);//无界面的说话
		mIatDialog = new RecognizerDialog(context, mInitListener);

	}

	/**
	 * 初始化监听器。
	 */
	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			if (code != ErrorCode.SUCCESS) {
				Toast.showShort(getActivity(), "初始化失败，错误码：" + code);
			}
		}
	};

	/**
	 * 听写UI监听器
	 */
	private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
		public void onResult(RecognizerResult results, boolean isLast) {
			Log.d(parseResult(results));
			if (isLast) {

				String result = parseResult(results);
				bookSearch.setText(result);
				bookSearch.setSelection(result.length());
				bookSearch.postInvalidate();
				save(result);
				startToActivity(result);
			}
		}

		/**
		 * 识别回调错误.
		 */
		public void onError(SpeechError error) {
			Toast.showShort(getActivity(), error.getPlainDescription(true));
		}

	};

	/**
	 * 参数设置
	 * 
	 * @param param
	 * @return
	 */
	private void setSpeechParam() {
		// 清空参数
		mIat.setParameter(SpeechConstant.PARAMS, null);
		// 设置听写引擎
		mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		// 设置返回结果格式
		mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

		mIat.setParameter(SpeechConstant.DOMAIN, "iat");
		// 设置语言
		mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		// 设置语言区域
		mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");

		// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
		mIat.setParameter(SpeechConstant.VAD_BOS, "5000");

		// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
		mIat.setParameter(SpeechConstant.VAD_EOS, "1500");
		// 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
		mIat.setParameter(SpeechConstant.ASR_PTT, "0");

		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
		mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");

		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH,
				Environment.getExternalStorageDirectory() + "/msc/iat.wav");

		// 设置听写结果是否结果动态修正，为“1”则在听写过程中动态递增地返回结果，否则只在听写结束之后返回最终结果
		// 注：该参数暂时只对在线听写有效
		mIat.setParameter(SpeechConstant.ASR_DWA, "0");
	}

	/**
	 * 解析语言返回的json字符串
	 * 
	 * @param results
	 * @return String
	 */
	private String parseResult(RecognizerResult results) {
		String text = JsonParser.parseIatResult(results.getResultString());

		String sn = null;
		// 读取json结果中的sn字段
		try {
			JSONObject resultJson = new JSONObject(results.getResultString());
			sn = resultJson.optString("sn");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		mIatResultsMap.put(sn, text);

		StringBuffer resultBuffer = new StringBuffer();
		for (String key : mIatResultsMap.keySet()) {
			resultBuffer.append(mIatResultsMap.get(key));
		}

		return resultBuffer.toString();
	}

	/**
	 * 跳转到搜索结果的界面
	 * 
	 * @param keyWord
	 */
	protected void startToActivity(String keyWord) {
		isAfresh = true;
		Intent intent = new Intent(getActivity(), SearchResultActivity.class);
		intent.putExtra("keyWord", keyWord);
		getActivity().startActivity(intent);
		getActivity().overridePendingTransition(R.anim.activity_zoom_in, 0);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mCurrentPosition = -1;
		if (BookTheme.isThemeChange) {
			layout_search_bg.setBackgroundColor(BookTheme.THEME_COLOR);
			bookSearch.setTextColor(BookTheme.THEME_COLOR);
			initKeyWordFlow(null);
		}
		if (isAfresh) {
			loadHistoryData();
			isAfresh = false;
		}

	}

	@Override
	public void onTagClick(String tag) {
		save(tag);
		startToActivity(tag);
		isAfresh = true;
	}

	/**
	 * 通知数据改变
	 */
	public void loadHistoryData() {
		bookDb = BookDb.getInstance(getActivity(), "Book");
		ArrayList<BookSearchRecords> records = bookDb.querryAll();

		if (records != null) {
			Collections.reverse(records);
			adapter = new HistoryAdapter(getActivity(),
					R.layout.popup_listview_item, records);
			listview_history.setAdapter(adapter);
			listview_history.setOnItemClickListener(this);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (mCurrentPosition != -1)
			return;
		mCurrentPosition = position;
		String keyWord = ((BookSearchRecords) parent
				.getItemAtPosition(position)).getRecord();
		save(keyWord);
		startToActivity(keyWord);
	}

	public class HistoryAdapter extends QuickAdapter<BookSearchRecords> {

		public HistoryAdapter(Context context, int layoutResId,
				List<BookSearchRecords> data) {
			super(context, layoutResId, data);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void convert(BaseAdapterHelper helper, BookSearchRecords item) {
			// TODO Auto-generated method stub
			helper.setText(R.id.tv_history, item.getRecord());
		}

	}

}
