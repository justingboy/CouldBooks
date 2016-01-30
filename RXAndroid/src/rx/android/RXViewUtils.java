package rx.android;

import java.util.concurrent.TimeUnit;

import android.view.View;
import android.view.View.OnClickListener;
import rx.Observable;
import rx.functions.Action1;

public class RXViewUtils {
	public static void clicks(final View view, final OnClickListener onClickListener) {

		Observable.create(new ViewClickOnSubscribe(view)).throttleFirst(500, TimeUnit.MILLISECONDS)
				.subscribe(new Action1<Void>() {

					@Override
					public void call(Void arg0) {
						// TODO Auto-generated method stub
						if (onClickListener != null)
							onClickListener.onClick(view);
					}
				});
	}

}
