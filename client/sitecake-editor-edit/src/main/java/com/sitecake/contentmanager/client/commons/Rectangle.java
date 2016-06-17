package com.sitecake.contentmanager.client.commons;

public class Rectangle {
	private Point start;
	private Point end;
	
	public Point getStart() {
		return start;
	}

	public Point getEnd() {
		return end;
	}

	public void setWidth(double width) {
		end.setX(start.getX() + width);
	}
	
	public void setHeight(double height) {
		end.setY(start.getY() + height);
	}

	public void setWidthHeight(double width, double height) {
		setWidth(width);
		setHeight(height);
	}
	
	public void setWidthRight(double width) {
		start.setX(end.getX() - width);
	}
	
	public void setHeightRight(double height) {
		start.setY(end.getY() - height);
	}

	public double getWidth() {
		return Math.abs(start.getX() - end.getX());
	}
	
	public double getHeight() {
		return Math.abs(start.getY() - end.getY());
	}
	
	public void set(Rectangle rectangle) {
		this.start.setX(rectangle.start.getX());
		this.start.setY(rectangle.start.getY());
		this.end.setX(rectangle.end.getX());
		this.end.setY(rectangle.end.getY());
	}
	
	public Rectangle(Point a, Point b) {
		this.start = new Point(0, 0);
		this.end = new Point(0, 0);
		setPoints(a, b);
	}

	public Rectangle(Point start, double width, double height) {
		this.start = start.clone();
		end = start.clone();
		end.add(width, height);
	}
	
	
	public Rectangle clone() {
		Rectangle clone = new Rectangle(start.clone(), end.clone());
		return clone;
	}

	@Override
	public boolean equals(Object obj) {
		if ( obj == null || !(obj instanceof Rectangle) ) {
			return false;
		}
		
		if ( obj == this ) {
			return true;
		}
		
		Rectangle rect = (Rectangle)obj;
		
		return ( start.equals(rect.start) && end.equals(rect.end) );
	}

	public void setPoints(Point a, Point b) {
		// extranct all coordinates to
		// this method can be used by the class itself
		double ax = a.getX();
		double bx = b.getX();
		double ay = a.getY();
		double by = b.getY();
		
		if ( ax <= bx ) {
			start.setX(ax);
			end.setX(bx);
		} else {
			start.setX(bx);
			end.setX(ax);
		}

		if ( ay <= by ) {
			start.setY(ay);
			end.setY(by);
		} else {
			start.setY(by);
			end.setY(ay);
		}
	}
	
	public void outerLimit(Rectangle limit, boolean preserveDimensions) {
		double width = getWidth();
		double height = getHeight();
		
		if ( start.getX() < limit.getStart().getX() ) {
			start.setX(limit.getStart().getX());
			if ( preserveDimensions ) {
				setWidth(width);
			}
		}
		
		if ( end.getX() > limit.getEnd().getX() ) {
			end.setX(limit.getEnd().getX());
			if ( preserveDimensions ) {
				setWidthRight(width);
			}
		}

		if ( start.getY() < limit.getStart().getY() ) {
			start.setY(limit.getStart().getY());
			if ( preserveDimensions ) {
				setHeight(height);
			}
		}
		
		if ( end.getY() > limit.getEnd().getY() ) {
			end.setY(limit.getEnd().getY());
			if ( preserveDimensions ) {
				setHeightRight(height);
			}
		}
	}
	
	public void innerLimit(Rectangle limit, Axis axis) {
		double ratio = getWidth() / getHeight();
		double newWidth, newHeight, newX, newY;
		
		switch ( axis ) {
			case NW:
				if ( start.getX() > limit.getStart().getX() ) {
					start.setX(limit.getStart().getX());
					newWidth = getWidth();
					newHeight = newWidth / ratio;
					newY = end.getY() - newHeight;
					start.setY(newY);
				}
				
				if ( start.getY() > limit.getStart().getY() ) {
					start.setY(limit.getStart().getY());
					newHeight = getHeight();
					newWidth = newHeight * ratio;
					newX = end.getX() - newWidth;
					start.setX(newX);
				}
				break;
				
			case NE:
				if ( end.getX() < limit.getEnd().getX() ) {
					end.setX(limit.getEnd().getX());
					newWidth = getWidth();
					newHeight = newWidth / ratio;
					newY = end.getY() - newHeight;
					start.setY(newY);
				}
				
				if ( start.getY() > limit.getStart().getY() ) {
					start.setY(limit.getStart().getY());
					newHeight = getHeight();
					newWidth = newHeight * ratio;
					newX = start.getX() + newWidth;
					end.setX(newX);
				}
				break;
			
			case SW:
				if ( start.getX() > limit.getStart().getX() ) {
					start.setX(limit.getStart().getX());
					newWidth = getWidth();
					newHeight = newWidth / ratio;
					newY = start.getY() + newHeight;
					end.setY(newY);
				}
				
				if ( end.getY() < limit.getEnd().getY() ) {
					end.setY(limit.getEnd().getY());
					newHeight = getHeight();
					newWidth = newHeight * ratio;
					newX = end.getX() - newWidth;
					start.setX(newX);
				}
				break;

			case SE:
				if ( end.getX() < limit.getEnd().getX() ) {
					end.setX(limit.getEnd().getX());
					newWidth = getWidth();
					newHeight = newWidth / ratio;
					newY = start.getY() + newHeight;
					end.setY(newY);
				}
				
				if ( end.getY() < limit.getEnd().getY() ) {
					end.setY(limit.getEnd().getY());
					newHeight = getHeight();
					newWidth = newHeight * ratio;
					newX = start.getX() + newWidth;
					end.setX(newX);
				}
				break;
				
			case N:
				if ( start.getY() > limit.getStart().getY() ) {
					start.setY(limit.getStart().getY());
				}
				break;
				
			case S:
				if ( end.getY() < limit.getEnd().getY() ) {
					end.setY(limit.getEnd().getY());
				}
				break;
				
			case W:
				if ( start.getX() > limit.getStart().getX() ) {
					start.setX(limit.getStart().getX());
				}
				break;
				
			case E:
				if ( end.getX() < limit.getEnd().getX() ) {
					end.setX(limit.getEnd().getX());
				}
				break;
				
			case PAN:
				double width = getWidth();
				double height = getHeight();
				
				if ( start.getX() > limit.getStart().getX() ) {
					start.setX(limit.getStart().getX());
					end.setX(start.getX() + width);
				} else if ( end.getX() < limit.getEnd().getX() ) {
					end.setX(limit.getEnd().getX());
					start.setX(end.getX() - width);
				}
				
				if ( start.getY() > limit.getStart().getY() ) {
					start.setY(limit.getStart().getY());
					end.setY(start.getY() + height);
				} else if ( end.getY() < limit.getEnd().getY() ) {
					end.setY(limit.getEnd().getY());
					start.setY(end.getY() - height);
				}
				break;
				
			case NONE:
				break;
		}
	}
	
	public void translate(double x, double y) {
		double width = getWidth();
		double height = getHeight();
		start.setXY(0, 0);
		end.setXY(width, height);
	}
	
	public void translate(double dx, double dy, Rectangle outerLimit, Rectangle innerLimit) {
		start.add(dx, dy);
		end.add(dx, dy);
		
		if ( outerLimit != null ) {
			outerLimit(outerLimit, true);
		}
		
		if ( innerLimit != null ) {
			innerLimit(innerLimit, Axis.PAN);
		}
	}

	public void resize(double dx, double dy, Axis axis, boolean swapPoints) {
		
		switch (axis) {
		
		case N:
			start.add(0, dy);
			break;
			
		case S:
			end.add(0, dy);
			break;
			
		case W:
			start.add(dx, 0);
			break;
			
		case E:
			end.add(dx, 0);
			break;
			
		case NW:
			start.add(dx, dy);
			break;
			
		case NE:
			start.add(0, dy);
			end.add(dx, 0);
			break;
			
		case SW:
			start.add(dx, 0);
			end.add(0, dy);
			break;
			
		case SE:
			end.add(dx, dy);
			break;

		case PAN:
		case NONE:
			break;
		}
		
		if ( swapPoints ) {
			setPoints(start, end);
		}
	}
	
	public boolean intersect(Point point) {
		
		return ( 
				( point.getX() >= start.getX() ) &&
				( point.getX() <= end.getX() ) &&
				( point.getY() >= start.getY() ) &&
				( point.getY() <= end.getY() )
				);
	}
}
