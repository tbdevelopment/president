package com.example.president;

public class Settings {
	private static Settings instance;
	private Settings(){} 
	public static synchronized Settings getInstance(){
		if (instance == null)
		{
			instance = new Settings();
		}
		return instance;
	}
}
