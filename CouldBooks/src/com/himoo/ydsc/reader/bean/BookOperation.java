package com.himoo.ydsc.reader.bean;

import java.util.ArrayList;
import java.util.List;

import com.himoo.ydsc.bean.BaiduBook;
import com.himoo.ydsc.bean.BaiduBookChapter;

/**
 * 这个类包含书的信息，书名，作者，章节名称。
 * 由于这个类在本工程中仅需要实例化一次，因此将它设为单例。
 * 其这个类的唯一对象软件启动时被初始化，在关闭软件之前一般是不会发生了。
 * 
 * @author MJZ
 * 
 */
public class BookOperation {
	/** 书名 */
	private String bookName;
	/** 作者 */
	private String author;
	/** 章节列表 */
	private ArrayList<BaiduBookChapter> mChapterList = new ArrayList<BaiduBookChapter>();

	private static BookOperation book = null;
	
	private BaiduBook baiduBook;

	private BookOperation() {
	}

	public static BookOperation getInstance() {

		if (book == null) {
			synchronized (BookOperation.class) {
				if (book == null)
					book = new BookOperation();
			}
		}
		return book;
	}

	public String getBookname() {
		return bookName;
	}

	public void setBookname(String bookName) {
		this.bookName = bookName;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public ArrayList<BaiduBookChapter> getChapterList() {
		return mChapterList;
	}

	public void setChapterList(List<BaiduBookChapter> chapterList) {
		mChapterList.clear();
		mChapterList.addAll(chapterList);
	}

	public String getChapterName(int order) {
		return (String) mChapterList.get(order).getChapterName();
	}

	public BaiduBook getBaiduBook() {
		return baiduBook;
	}

	public void setBaiduBook(BaiduBook baiduBook) {
		this.baiduBook = baiduBook;
	}

	
}
