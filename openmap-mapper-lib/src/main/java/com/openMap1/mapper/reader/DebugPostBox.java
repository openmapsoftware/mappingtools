package com.openMap1.mapper.reader;


import org.w3c.dom.Node;

import com.openMap1.mapper.mapping.DebugRow;

/**
 * This class acts as a postbox between the real MDLXOReader, the XO reader that
 * is emulating it for debugging purposes, and the DebugView
 * @author robert
 *
 */

public class DebugPostBox {
		
	public DebugPostBox(Thread readerThread)
	{
		this.readerThread = readerThread;
		completed = false;
		terminated = false;
		halted = false;
	}
	
	public  Thread getReaderThread() {return readerThread;}
	private Thread readerThread;
	
	public boolean getCompleted() {return completed;}
	public void setCompleted(boolean completed) {this.completed = completed;}
	private boolean completed = false;
	
	public boolean getTerminated() {return terminated;}
	public void setTerminated(boolean terminated) {this.terminated = terminated;}
	private boolean terminated = false;
	
	public boolean getHalted() {return halted;}
	public void setHalted(boolean halted) {this.halted = halted;}
	private boolean halted = false;
	
	public boolean getRunOn() {return runOn;}
	public void setRunOn(boolean runOn) {this.runOn = runOn;}
	private boolean runOn = false;
	
	private DebugRow debugRow = null;
	public void setDebugRow(DebugRow debugRow) {this.debugRow = debugRow;}
	public DebugRow getDebugRow() {return debugRow;}
	
	public void setLastResult(String lastResult) {this.lastResult = lastResult;}
	public String getLastResult() {return lastResult;}
	private String lastResult;
	
	public void setDebugNode(Node node) {this.node = node;}
	public Node getDebugNode() {return node;}
	private Node node;
	
}
