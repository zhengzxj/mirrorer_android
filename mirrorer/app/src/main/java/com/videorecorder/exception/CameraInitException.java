package com.videorecorder.exception;

public class CameraInitException extends Exception{

	private static final long serialVersionUID = 1354374881010639851L;

	public CameraInitException() {
	}
	
	public CameraInitException(String message) {
		super(message);
	}
}
