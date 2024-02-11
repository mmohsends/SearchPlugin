package com.dataserve.se.bean;

public enum BoxType {
	METAL(1), PLASTIC(2), CARTOON(3), NONE(4);
	
	int id;
	
	BoxType(int id) {
		this.id = id;
	}
	
	int getId() {
		return this.id;
	}
	
	public static BoxType getTypeById(int id) {
		switch (id) {
			case 1: return METAL;
			case 2: return PLASTIC;
			case 3: return CARTOON;
			case 4: return NONE;
			default: return null;
		}
	}
}
