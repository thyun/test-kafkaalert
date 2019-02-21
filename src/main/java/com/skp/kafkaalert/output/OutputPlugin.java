package com.skp.kafkaalert.output;

public interface OutputPlugin extends Runnable {
	public void init();
	public void start();
	public void stop();
	public OutputQueue getOutputQueue();
}
