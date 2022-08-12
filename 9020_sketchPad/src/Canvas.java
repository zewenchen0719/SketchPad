import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.Serializable;
import java.util.LinkedList;

import javax.swing.JPanel;

public class Canvas extends JPanel implements Serializable {
	public LinkedList<Shape> shapes = new LinkedList<Shape>(); 
	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		for(Shape x:shapes) {
			setCurrColor(g,x);
			draw(g,x);
		}
	}

	private void draw(Graphics g,Shape x) {
		int W = Math.abs(x.x2-x.x1);
		int H = Math.abs(x.y2-x.y1);
		//top left coordinate
		int TLx = Math.min(x.x1,x.x2);
		int TLy = Math.min(x.y1,x.y2);
		String S= x.shape;
		switch(S) {
		case"Freehand":
			Point p0= x.points.get(0);
			for(int i=1;i<x.points.size();i++) {
				Point p1=x.points.get(i);
				g.drawLine(p0.x, p0.y, p1.x, p1.y);
				p0 =p1;
			}
			break;
		case"Line":
			g.drawLine(x.x1,x.y1, x.x2, x.y2);
			break;
		case"Rectangle":
		    g.fillRect(TLx, TLy, W, H);
			break;
		case"Ellipse":
			 g.fillOval(TLx, TLy, W, H);
			break;
		case"Square":
			 g.fillRect(TLx, TLy, W, W);
			break;
		case"Circle":
			 g.fillOval(TLx, TLy, W, W);
			break;	
		case"Polygon":
			if(x.polyPoints.size()>0) {
				Point p00= x.polyPoints.get(0);
				for(int i=1;i<x.polyPoints.size();i++) {
					Point p11=x.polyPoints.get(i);
					g.fillRect(p00.x-2, p00.y-2, 5, 5);
					g.drawLine(p00.x, p00.y, p11.x, p11.y);
					p00 =p11;
				}
			}
			break;		
		}
		
	}	
	public void setCurrColor(Graphics g,Shape x) {
		String color = x.color;
		switch(color) {
			case"Black":
				g.setColor(Color.black);
				break;
			case "Red":
				g.setColor(Color.red);
				break;
			case "Green":
				g.setColor(Color.green);
				break;
			case "Blue":
				g.setColor(Color.blue);
				break;
		}
	}
	
}
