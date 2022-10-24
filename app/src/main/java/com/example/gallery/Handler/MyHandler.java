package com.example.gallery.Handler;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/*
https://rerethink.tistory.com/entry/%ED%95%B8%EB%93%A4%EB%9F%AChandler-%EB%A9%94%EB%AA%A8%EB%A6%AC-%EB%88%84%EC%88%98%EB%A5%BC-WeakReference-%EC%82%AC%EC%9A%A9-%EC%9D%B4%EC%9C%A0-%EB%B0%8F-%ED%95%B4%EA%B2%B0-%EB%B0%A9%EB%B2%95
https://medium.com/@joongwon/android-memory-leak-%EC%82%AC%EB%A1%80-6565b817a8fe
 */
public class MyHandler extends Handler {
	private final WeakReference<MyHandelerInterface> mFragment;
	public MyHandler(MyHandelerInterface fragment) {
		mFragment = new WeakReference<MyHandelerInterface>(fragment);
	}

	@Override
	public void handleMessage(Message msg) {

		MyHandelerInterface fragment = mFragment.get();
		if (fragment != null) {
			fragment.handleMessage(msg);
		}
	}
}
