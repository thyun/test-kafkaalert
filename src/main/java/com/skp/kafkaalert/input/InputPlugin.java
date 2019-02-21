package com.skp.kafkaalert.input;

public interface InputPlugin extends Runnable {
	public void init();
	public void start();
	public void stop();
}
