package model;

import java.util.Date;

public class CustomMessage {
	
	private User author;
	private String message;
	private User reciever;
	private Date date;
	private String subject;
	
	public CustomMessage(User author, String message, User reciever, Date date, String subject) {
		super();
		this.author = author;
		this.message = message;
		this.reciever = reciever;
		this.date = date;
		this.subject = subject;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public CustomMessage() {
		super();
	}
	public CustomMessage(User author, String message, User reciever) {
		super();
		this.author = author;
		this.message = message;
		this.reciever=reciever;
	}
	public User getAuthor() {
		return author;
	}
	public void setAuthor(User author) {
		this.author = author;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public User getReciever() {
		return reciever;
	}
	public void setReciever(User reciever) {
		this.reciever = reciever;
	}
	
	

}
