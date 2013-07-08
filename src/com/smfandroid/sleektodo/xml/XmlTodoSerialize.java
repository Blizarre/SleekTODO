package com.smfandroid.sleektodo.xml;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

import com.smfandroid.sleektodo.TodoItem;

public class XmlTodoSerialize {

	public static class TodoAddException extends Exception {
		private static final long serialVersionUID = 1L;
		public TodoAddException(String msg) {
			super(msg);
		}
	}
	
	protected XmlSerializer mSerializer;
	protected OutputStreamWriter osw;
	
	public XmlTodoSerialize() {
		
	}
	
	/**
	 * Call this function first for an EXPORT. It opens the file and prepare the environment.
	 * 
	 * @param fileName The filename of the output file. It <i>WILL</i> be overwritten
	 *  
	 */
	public void open(String fileName) throws FileNotFoundException, IOException {
	    mSerializer = Xml.newSerializer();

	    FileOutputStream fos = new FileOutputStream(fileName);
	    osw = new OutputStreamWriter(fos);
	    mSerializer.setOutput(osw);

	    mSerializer.startDocument("utf-8", true);
	    mSerializer.startTag("", XmlContract.TAG_ROOT_TODOITEM);
	}
	

	
	/**
	 * Export a new TodoItem to the file previously initialized by "openExportFile"
	 * 
	 * @param todo The todoItem whose values will be exported
	 * @throws IOException 
	 * @throws IllegalStateException 
	 * @throws IllegalArgumentException 
	 * 
	 */
	public void exportTodoItem(TodoItem todo) throws IOException, TodoAddException   {
		try {
			mSerializer.startTag("", XmlContract.TAG_TODOITEM);
				mSerializer.startTag("", XmlContract.TAG_TEXT);
					mSerializer.text(todo.mText);
				mSerializer.endTag("", XmlContract.TAG_TEXT);
				mSerializer.startTag("", XmlContract.TAG_LONGTEXT);
					mSerializer.text(todo.mLongText);
				mSerializer.endTag("", XmlContract.TAG_LONGTEXT);
				mSerializer.startTag("", XmlContract.TAG_DATE);
					mSerializer.text(todo.mDate);
				mSerializer.endTag("", XmlContract.TAG_DATE);			
				mSerializer.startTag("", XmlContract.TAG_FLAG);
					mSerializer.text(""+todo.mFlag);
				mSerializer.endTag("", XmlContract.TAG_FLAG);
				mSerializer.startTag("", XmlContract.TAG_CHECKED);
					mSerializer.text(""+todo.mIsChecked);
				mSerializer.endTag("", XmlContract.TAG_CHECKED);
			mSerializer.endTag("",  XmlContract.TAG_TODOITEM);
		} catch (IllegalArgumentException e) {
			throw new TodoAddException("internal error : " + e.getMessage());
		} catch (IllegalStateException e) {
			throw new TodoAddException("internal error : " + e.getMessage());
		} 		
	}
	
	/**
	 * 
	 * Write the remaining part of the xml data and close the file.
	 *  
	 * @throws IOException
	 */
	public void close() throws IOException, TodoAddException {
		try {
			mSerializer.endTag("", XmlContract.TAG_ROOT_TODOITEM);
			mSerializer.endDocument();
		} catch (IllegalArgumentException e) {
			throw new TodoAddException("internal error : " + e.getMessage());
		} catch (IllegalStateException e) {
			throw new TodoAddException("internal error : " + e.getMessage());
		}
		osw.close();
	}
}
