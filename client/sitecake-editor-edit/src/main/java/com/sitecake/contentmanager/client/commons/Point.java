package com.sitecake.contentmanager.client.commons;

public class Point {
	private double x;
	private double y;
	
	public double getX() {
		return x;
	}


	public void setX(double x) {
		this.x = x;
	}


	public double getY() {
		return y;
	}


	public void setY(double y) {
		this.y = y;
	}

	public Point(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}
	
	public Point clone() {
		return new Point(x, y);
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( obj == null || !(obj instanceof Point) ) {
			return false;
		}
		
		if ( obj == this ) {
			return true;
		}
		
		Point point = (Point)obj;
		
		return ( x == point.x && y == point.y );
	}


	public void setXY(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Point add(double dx, double dy) {
		x += dx;
		y += dy;
		return this;
	}
	
}
