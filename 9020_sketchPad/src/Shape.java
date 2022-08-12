import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;

public class Shape implements Serializable{
Integer x1,y1,x2,y2;
String shape, color;

ArrayList<Point> points = new ArrayList<Point>();
ArrayList<Point> polyPoints= new ArrayList<Point>();
ArrayList<Integer> polyY= new ArrayList<Integer>();

public Shape() {}
public Shape(int x, int y,int xx ,int yy, String s,  String c) {
	x1 = x;
	y1 = y;
	x2=xx;
	y2 = yy;
	shape = s;
	color = c;
}

public void setPoints(ArrayList<Point> po ) {
	ArrayList<Point> result = new ArrayList<Point>();
	for(Point p :po) {
		result.add(new Point(p.x,p.y));
	}
	points= result;
}
public void setPolyPoints(ArrayList<Point> po ) {
	ArrayList<Point> result = new ArrayList<Point>();
	for(Point p :po) {
		result.add(new Point(p.x,p.y));
	}
	polyPoints= result;
}

public int getDistance(int x, int y) { 
	int shortestD= Integer.MAX_VALUE;
	if(shape.equals("Freehand")) {
		for(Point p : points) {
			shortestD = Math.min((int)p.distance(x, y), shortestD);
		}
	}else if(shape.equals("Polygon")) {
		for(Point p : polyPoints) {
			shortestD = Math.min((int)p.distance(x, y), shortestD);
		}
	}else {
		Point centralP = new Point((x1+x2)/2,(y1+y2)/2);
		shortestD = (int) centralP.distance(x,y);
	}
	return shortestD;
}



public void reLocate(int dx, int dy) {
	if(shape.equals("Freehand")) {
		x1 = dx+x1;
		y1 = dy+y1;
		for(Point p : points) {
			int ox = p.x;
			int oy = p.y;
			p.x = ox+dx;
			p.y = oy+dy;
		}
	}else if(shape.equals("Polygon")){
		x1 = dx+x1;
		y1 = dy+y1;
		for(Point p : polyPoints) {
			int ox = p.x;
			int oy = p.y;
			p.x = ox+dx;
			p.y = oy+dy;
		}
	}else {
		x1 = x1+dx;
		y1 = y1+dy;
		x2 = x2+dx;
		y2 = y2+dy;
	}
}

}
