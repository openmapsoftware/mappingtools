package com.openMap1.mapper.query;

public class Pair {
	
	/**
	 * constants defining data source indexes for left and right data sources
	 */
	public static int LEFT = 0;
	public static int RIGHT = 1;
	
	/**
	 * @return record number from the left data source being matched
	 */
	public int leftRec() {return leftRec;}
	private int leftRec;
	
	/**
	 * @return record number from the right data source being matched
	 */
	public int rightRec() {return rightRec;}
	private int rightRec;
	
	/**
	 * @return matching score
	 */
	public double score() {return score;}
	private double score;
	
	/**
	 * @return true if there is a match
	 */
	public boolean pass() {return pass;}
	private boolean pass;
	
	/**
	 * @return key for hashed storage of pairs
	 */
	public String key() {return (leftRec() + "_" + rightRec());}
	
	public Pair(int leftRec, int rightRec, double score, boolean pass)
	{
		this.leftRec = leftRec;
		this.rightRec = rightRec;
		this.score = score;
		this.pass = pass;
	}

}
