package com.himoo.ydsc.aescrypt;

import javax.crypto.Cipher;

import android.os.AsyncTask;
import android.util.Log;

public class EncryptionOrDecryptionTask extends AsyncTask<Void, Void, Boolean> {

	private String mSourceFile = "";
	private String mNewFilePath = "";
	private String mNewFileName = "";
	private String mSeed = "";
	private boolean mIsEncrypt = false;
	private AESHelper mAESHelper;

	public EncryptionOrDecryptionTask(boolean isEncrypt, String sourceFile,
			String newFilePath, String newFileName, String seed) {
		this.mSourceFile = sourceFile;
		this.mNewFilePath = newFilePath;
		this.mNewFileName = newFileName;
		this.mSeed = seed;
		this.mIsEncrypt = isEncrypt;
		mAESHelper = new AESHelper();
	}

	@Override
	protected Boolean doInBackground(Void... params) {

		boolean result = false;
//		String str = AESCrypt.decryptBook();
//		Log.i("msg",str);
		if (mIsEncrypt) {
			result = mAESHelper.AESCipher(Cipher.ENCRYPT_MODE, mSourceFile,
					mNewFilePath + mNewFileName, mSeed);
		} else {
			result = mAESHelper.AESCipher(Cipher.DECRYPT_MODE, mSourceFile,
					mNewFilePath + mNewFileName, mSeed);
		}

		return result;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		String showMessage = "";

		if (mIsEncrypt) {
			showMessage = result ? "加密已完成" : "加密失败!";
		} else {
			showMessage = result ? "解密完成" : "解密失败!";
		}

		android.util.Log.i("msg", showMessage);
	}
}