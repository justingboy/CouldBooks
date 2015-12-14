package com.himoo.ydsc.speech;

import android.content.Context;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;

public class SpeechReader {

	private SpeechSynthesizer mTts;

	private static SpeechReader mInstacne = null;

	private SpeechReader() {

	}

	public static SpeechReader getInstance() {
		if (mInstacne == null)
			synchronized (SpeechReader.class) {
				if (mInstacne == null)

					mInstacne = new SpeechReader();

			}

		return mInstacne;

	}

	public void initSpeech(Context context) {
		mTts = SpeechSynthesizer.createSynthesizer(context, null);
		set_mTts();

	}

	private void set_mTts() {
		// 设置发音人
		mTts.setParameter(SpeechConstant.VOICE_NAME, "yufeng");

		// 设置语速
		mTts.setParameter(SpeechConstant.SPEED, "30");

		// 设置音调
		mTts.setParameter(SpeechConstant.PITCH, "50");

		// 设置音量0-100
		mTts.setParameter(SpeechConstant.VOLUME, "80");

		// 设置播放器音频流类型
		mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
		// 如果不需要保存保存合成音频，请注释下行代码
		// mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH,
		// "./sdcard/iflytek.pcm");

	}

	/**
	 * 语音朗读
	 * 
	 * @param chapter
	 */
	public void speechChapter(String chapter) {

		mTts.startSpeaking(chapter, null);
	}

	/**
	 * 释放资源
	 */
	public void clear() {
		mTts.stopSpeaking();
		mTts.destroy();// 退出时释放连接
	}

}
