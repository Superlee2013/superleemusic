package com.superlee.guessmusic.model;

public class Song {
	//名称
	private String songName;
	//文件名称
	private String songFileName;
	//名称长度
	private int songNameLength;
	
	public char[] getNameCharacters(){
		return songName.toCharArray();
	}
	
	public String getSongName() {
		return songName;
	}
	public void setSongName(String songName) {
		this.songName = songName;
		this.songNameLength=songName.length();
	}
	public String getSongFileName() {
		return songFileName;
	}
	public void setSongFileName(String songFileName) {
		this.songFileName = songFileName;
	}
	public int getSongNameLength() {
		return songNameLength;
	}
	public void setSongNameLength(int songNameLength) {
		this.songNameLength = songNameLength;
	}
}
