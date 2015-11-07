package com.himoo.ydsc.bean;
/**
 *自己服务器搜索返回书籍 
 *
 */
public class BookSearch extends BookDetails {
	
	/** 书的更新状态 */
	private int Book_Yellow;

	public int getBook_Yellow() {
		return Book_Yellow;
	}

	public void setBook_Yellow(int book_Yellow) {
		Book_Yellow = book_Yellow;
	}

}
