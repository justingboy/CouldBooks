package rx.android;

import android.view.View;
import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

final class ViewClickOnSubscribe implements Observable.OnSubscribe<Void> {
	final View view;

	ViewClickOnSubscribe(View view) {
		this.view = view;
	}

	@Override
	public void call(final Subscriber<? super Void> subscriber) {
		Preconditions.checkUiThread();
		View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!subscriber.isUnsubscribed()) {
					subscriber.onNext(null);
				}
			}
		};
		view.setOnClickListener(listener);

		subscriber.add(new MainThreadSubscription() {
			@Override
			protected void onUnsubscribe() {
				view.setOnClickListener(null);
			}
		});
	}
}
