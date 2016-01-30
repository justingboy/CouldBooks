package com.ctsi.rx.model;

public class ObservableModel<T> {

	public static final int CODE_ERROR = 0;

	public static final int CODE_SUCCESS = 1;

	private int code;
	private String message;
	private T model;

	public ObservableModel() {
		// TODO Auto-generated constructor stubObserverableMode.success("123");
	}

	public static <T> ObservableModel<T> success(T model) {

		ObservableModel<T> observerableMode = new ObservableModel<>();
		observerableMode.setCode(CODE_SUCCESS);
		observerableMode.setModel(model);
		return observerableMode;
	}

	public static <T> ObservableModel<T> error(String errorMessage) {

		ObservableModel<T> observerableMode = new ObservableModel<>();
		observerableMode.setCode(CODE_ERROR);
		observerableMode.setMessage(errorMessage);
		return observerableMode;
	}

	public static <T> ObservableModel<T> other(int code, String message, T model) {

		ObservableModel<T> observerableMode = new ObservableModel<>();
		observerableMode.setCode(code);
		observerableMode.setMessage(message);
		observerableMode.setModel(model);
		return observerableMode;
	}

	public static <T> ObservableModel<T> other(int code, String message) {

		ObservableModel<T> observerableMode = new ObservableModel<>();
		observerableMode.setCode(code);
		observerableMode.setMessage(message);
		return observerableMode;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getModel() {
		return model;
	}

	public void setModel(T model) {
		this.model = model;
	}

}
