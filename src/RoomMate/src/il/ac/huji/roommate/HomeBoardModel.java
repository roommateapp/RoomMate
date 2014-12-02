package il.ac.huji.roommate;

import android.graphics.Bitmap;

public class HomeBoardModel {
	private String noteContent;
	private String writer;
	private boolean isNotification;
	private Bitmap img;
	private String noteId;

	public HomeBoardModel(String noteContent, String writer, boolean isNotification, String noteId, Bitmap img) {
		super();
		this.setNoteContent(noteContent);
		this.setWriter(writer);
		this.setNotification(isNotification);
		this.setNoteId(noteId);
		this.setImg(img);
	}

	public String getNoteContent() {
		return noteContent;
	}

	public void setNoteContent(String noteContent) {
		this.noteContent = noteContent;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public boolean isNotification() {
		return isNotification;
	}

	public void setNotification(boolean isNotification) {
		this.isNotification = isNotification;
	}

	public String getNoteId() {
		return noteId;
	}

	public void setNoteId(String noteId) {
		this.noteId = noteId;
	}

	public Bitmap getImg() {
		return img;
	}

	public void setImg(Bitmap img) {
		this.img = img;
	}

	

}
