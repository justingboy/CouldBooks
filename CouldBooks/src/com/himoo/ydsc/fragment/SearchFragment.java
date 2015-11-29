package com.himoo.ydsc.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.himoo.ydsc.R;
import com.himoo.ydsc.activity.SearchResultActivity;
import com.himoo.ydsc.base.BaseFragment;
import com.himoo.ydsc.bean.BookKeyWord;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.db.BookDb;
import com.himoo.ydsc.db.bean.BookSearchRecords;
import com.himoo.ydsc.dialog.BookPopupWindow;
import com.himoo.ydsc.http.HttpConstant;
import com.himoo.ydsc.http.HttpOperator;
import com.himoo.ydsc.manager.PageManager;
import com.himoo.ydsc.speech.JsonParser;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.ui.view.BookTitleBar;
import com.himoo.ydsc.ui.view.KeywordsFlow;
import com.himoo.ydsc.util.SharedPreferences;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.ios.edittext.SearchEditText;
import com.ios.edittext.SearchEditText.OnEditTextFocuseChangListener;
import com.ios.edittext.SearchEditText.OnSearchClickListener;
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
public class SearchFragment extends BaseFragment implements
		OnSearchClickListener, OnEditTextFocuseChangListener {

	/** 搜索 EditText */
	@ViewInject(R.id.more_book_search)
	private SearchEditText bookSearch;

	/** 语音输入功能按钮 */
	@ViewInject(R.id.search_speech)
	private ImageView search_speech;

	/** 飞入飞出效果View ：KeywordsFlow */
	@ViewInject(R.id.search_keywordFlow)
	private KeywordsFlow keywordsFlow;

	/** 　默认的关键字 */
	public static String[] keywords = { "花千骨", "盗墓笔记", "华胥引", "重生", "大主宰",
			"校花的贴身高手", "唐七公子", "凡人修仙传", "魔天记", "换一换" };

	/** 请求关键字的的页数 */
	private int mCurrentPage = 1;

	/** XUtils http 请求 */
	private HttpUtils http;

	/** 用于判断换一换是否可以被点击 ，主要是当网络不好时，点击会被多次调用 */
	private boolean isClickable = true;

	/** 搜索历史记录弹窗 */
	private BookPopupWindow popupWindow;

	/** 数据库 */
	private BookDb bookDb;

	/** 数据库中搜索记录的个数 */
	private int mDbBookCount = 0;

	/** PopupWindow 中的布局 */
	private View popupView;

	/** 语音识别Dialog */
	private RecognizerDialog mIatDialog;

	/** 语音识别 */
	private SpeechRecognizer mIat;

	/** 用HashMap存储听写结果 */
	private HashMap<String, String> mIatResultsMap = new LinkedHashMap<String, String>();

	/** 是否可以搜索了 */
	private boolean isKeyEnterDown = false;

	/** 是否有保存key的操作 */
	private boolean isSaveKey = false;
	
	private BookTitleBar titleBar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			SharedPreferences sp, Bundle savedInstanceState,
			PageManager pageManager) {
		// TODO Auto-generated method stub
		View view = inflater
				.inflate(R.layout.fragment_search, container, false);
		titleBar = (BookTitleBar) view
				.findViewById(R.id.book_titleBar);
		titleBar.setShowSingleTile();
		titleBar.setBackgroundColor(BookTheme.THEME_COLOR);
		titleBar.setTitle(getResources().getString(R.string.main_search));

		popupView = View.inflate(getActivity(), R.layout.popup_book_records,
				null);

		return view;
	}

	@Override
	public void initData() {
		initSpeech(getActivity());
		bookDb = BookDb.getInstance(getActivity(), "Book");
		http = new HttpUtils();
		initKeyWordFlow();
		bookSearch.setTextColor(BookTheme.THEME_COLOR);
		bookSearch.setOnSearchClickListener(this);
		bookSearch.setOnFocuesChangeListener(this);
		search_speech.setOnClickListener(this);

	}

	/**
	 * 初始化KeywordsFlow
	 */
	private void initKeyWordFlow() {
		keywordsFlow.setTextColor(BookTheme.THEME_COLOR);
		keywordsFlow.setDuration(800l);
		keywordsFlow.setOnItemClickListener(this);
		feedKeywordsFlow(keywordsFlow, keywords);
		keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
	}

	/**
	 * 添加关键字
	 * 
	 * @param keywordsFlow
	 * @param keyWordArray
	 */
	private void feedKeywordsFlow(KeywordsFlow keywordsFlow,
			String[] keyWordArray) {
		for (int i = 0; i < keyWordArray.length; i++) {
			keywordsFlow.feedKeyword(keyWordArray[i]);
		}
	}

	@Override
	public void onSearchClick(View view) {
		// TODO Auto-generated method stub
		if (isKeyEnterDown) {
			if (!TextUtils.isEmpty(bookSearch.getText())) {
				save(bookSearch.getText().toString());
				startToActivity(bookSearch.getText().toString());
				// Toast.showShort(getActivity(), "去搜索小说了！");
			}
			isKeyEnterDown = false;
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if (v instanceof TextView) {

			String keyword = ((TextView) v).getText().toString();
			if (keyword.equals("换一换")) {
				if (isClickable) {
					isClickable = false;
					getKeyWordRequest(mCurrentPage, 9);
				}
			} else {
				save(keyword);
				startToActivity(keyword);

			}

		} else if (v instanceof ImageView) {
			startSpeech();
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
	 * 点击（换一换）TextView飞入飞出效果
	 */
	private void changeBook(ArrayList<BookKeyWord> list) {
		int size = list.size();
		for (int i = 0; i < size; i++) {
			keywords[i] = list.get(i).getKeyword();
		}
		keywordsFlow.rubKeywords();
		// keywordsFlow.rubAllViews();
		feedKeywordsFlow(keywordsFlow, keywords);
		Random random = new Random();
		if (random.nextInt(30) % 2 == 0) {
			keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
		} else {
			keywordsFlow.go2Show(KeywordsFlow.ANIMATION_OUT);
		}

		isClickable = true;
	}

	/**
	 * 请求关键字
	 * 
	 * @param page
	 * @param size
	 */
	private void getKeyWordRequest(int page, int size) {
		if (http == null)
			http = new HttpUtils();
		String url = HttpConstant.BASE_URL_KEYWORD
				+ HttpOperator.getKeyWordRequestHeard(page, size);
		http.send(HttpMethod.GET, url, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				Gson gson = new Gson();
				ArrayList<BookKeyWord> list = gson.fromJson(
						responseInfo.result,
						new TypeToken<ArrayList<BookKeyWord>>() {
						}.getType());
				changeBook(list);
				mCurrentPage++;
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
				isClickable = true;
				if (getActivity() != null)
					Toast.showShort(getActivity(), "请求关键字失败 ：" + msg);
			}
		});
	}

	@Override
	public void onFocuseChange() {
		// TODO Auto-generated method stub
		isKeyEnterDown = true;
		if (bookDb.isEmpty())
			return;
		int bookCount = bookDb.querryCount();
		if (bookCount != mDbBookCount || popupWindow == null) {
			popupWindow = new BookPopupWindow(SearchFragment.this, popupView);
			mDbBookCount = bookCount;

		} else if (bookCount == 10 && isSaveKey) {
			popupWindow.initData();
			isSaveKey = false;
		} else {
			if (popupWindow != null && popupWindow.isShowing()) {
				return;
			}
		}
		popupWindow.showAsDropDown(bookSearch);
	}

	/**
	 * 保存关键字到数据库
	 * 
	 * @param keyword
	 */
	private void save(String keyword) {
		isSaveKey = true;
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
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
		}
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
		Intent intent = new Intent(getActivity(), SearchResultActivity.class);
		intent.putExtra("keyWord", keyWord);
		getActivity().startActivity(intent);
		getActivity().overridePendingTransition(R.anim.activity_zoom_in, 0);

	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (BookTheme.isThemeChange)
		{
			titleBar.setBackgroundColor(BookTheme.THEME_COLOR);
			bookSearch.setTextColor(BookTheme.THEME_COLOR);
			initKeyWordFlow();
		}
		
		
		
	}
}
