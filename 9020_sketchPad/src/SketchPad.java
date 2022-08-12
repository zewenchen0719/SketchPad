import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Stack;

import javax.swing.*;
public class SketchPad extends JFrame implements ActionListener {
	private String[] shapeBar = {"Freehand", "Line","Rectangle","Ellipse", "Square", "Circle","Polygon"};
	private String[] colorBar = {"Black","Red","Green","Blue" };
	private String[] modeBar = {"Draw","Select","Delete","Copy","Cut","Paste","Group","UnGroup"};
	private String[] editBar = {"Undo","Redo"};
	private String[] fileBar = {"Save","Load"};
	//identify the selected shape and color, set default
	static private String shape = "Freehand";
	static private String color = "Black";
	static private String mode = "Draw";
	static private JLabel currShapeState = new JLabel("shape: Freehand");
	static private JLabel currColorState = new JLabel("color: Black");
	static private JLabel currModeState = new JLabel("Mode: Draw");
	static private JLabel groupedItem = new JLabel("Group Select: 0");
	static private JPanel state = new JPanel();
	static private Canvas Canvas =  new Canvas();
    private Stack<LinkedList<Shape>> undoStack = new Stack<LinkedList<Shape>>();
    private Stack<LinkedList<Shape>> redoStack = new Stack<LinkedList<Shape>>();
	private int x0,y0;
	private int selectedIndex;
	private Shape temShape;
	private ArrayList<Integer> groupList = new ArrayList<Integer>();
	private  LinkedList<Shape> temShapes = new LinkedList<Shape>();
	//Constructor, initialize frame with user defined size
	public SketchPad(int h,int w) {
		this.setSize(w, h);
		setMenueBar();
		setStateBar();
		Canvas.addMouseListener(new myMouseHandler());
		Canvas.addMouseMotionListener(new myMouseMotionHandler());
		//this.addMouseListener(new myMouseHandler());
		//this.addMouseMotionListener(new myMouseMotionHandler());
		this.add(Canvas,BorderLayout.CENTER);
		this.setBackground(Color.WHITE);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	//helper function for initialize Menu Bar
	private void setMenueBar() {
		MenuBar mb = new MenuBar();
		Menu  shapeMenu = new Menu("Shapes");
		for(String s : shapeBar) {
			MenuItem x=new MenuItem(s);
			x.addActionListener(this);
			shapeMenu.add(x);
		}
		Menu  colorMenu = new Menu("Colors");
		for(String s : colorBar) {
			MenuItem x=new MenuItem(s);
			x.addActionListener(this);
			colorMenu.add(x);
		}
		Menu  functionMenu = new Menu("Functions");
		for(String s : modeBar) {
			MenuItem x=new MenuItem(s);
			x.addActionListener(this);
			functionMenu.add(x);
		}
		Menu  editMenu = new Menu("Edit");
		for(String s : editBar) {
			MenuItem x=new MenuItem(s);
			x.addActionListener(this);
			editMenu.add(x);
		}
		Menu  fileMenu = new Menu("File");
		for(String s : fileBar) {
			MenuItem x=new MenuItem(s);
			x.addActionListener(this);
			fileMenu.add(x);
		}
		mb.add(fileMenu);
		mb.add(shapeMenu);
		mb.add(colorMenu);
		mb.add(functionMenu);
		mb.add(editMenu);
		this.setMenuBar(mb);
	}
	//helper function for initialize state bar
	private void setStateBar() {
		state.setLayout(new BoxLayout(state, BoxLayout.PAGE_AXIS));
		state.add(currShapeState);
		state.add(currColorState);
		state.add(currModeState);
		state.add(groupedItem);
		this.add(state, BorderLayout.SOUTH);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
		if(Arrays.stream(shapeBar).anyMatch(c::equals)) {
			shape=c;
			currShapeState.setText("shape: "+c);
		}
		if(Arrays.stream(colorBar).anyMatch(c::equals)) {
			color=c;
			currColorState.setText("color: "+c);
		}
		if(Arrays.stream(modeBar).anyMatch(c::equals)) {
			mode=c;
			currModeState.setText("Mode: "+c);
		}
		if(c.equals("UnGroup")) {
			groupList.clear();
			temShapes.clear();
			groupedItem.setText("Group Select: "+groupList.size());
			mode = "Draw";
			currModeState.setText("Mode: Draw");
		}
		if(c.equals("Undo")) {
			redoStack.push(copyShapeList(Canvas.shapes));
			Canvas.shapes=copyShapeList(undoStack.pop());
			repaint();
		}
		if(c.equals("Redo")) {
			LinkedList<Shape> tem = redoStack.pop();
			redoStack.push(tem);
			Canvas.shapes=tem;
			repaint();
		}
		if(c.equals("Save")) {
			FileOutputStream fout;
			try {
				fout = new FileOutputStream("/saved/keep.ser");
				ObjectOutputStream oos = new ObjectOutputStream(fout);
				oos.writeObject(Canvas.shapes);
				System.out.println("saved");
				oos.close();
				oos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if(c.equals("Load")){
			try {
				FileInputStream streamIn = new FileInputStream("/saved/keep.ser");
			    ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);
			    Canvas.shapes = (LinkedList<Shape>) objectinputstream.readObject();
			    System.out.println("load Canvas: " + Canvas.shapes);
			    objectinputstream.close();
			 } catch (Exception e1) {
			     e1.printStackTrace();
			 }
			repaint();
		}
	}
//handle mouse event --------------------------------------------------------------------------------------------------
	public class myMouseHandler extends MouseAdapter{
		public void mousePressed(MouseEvent e) {
			x0=e.getX();
			y0=e.getY();
			if(mode.equals("Draw") && !shape.equals("Polygon")) {
				undoStack.push(copyShapeList(Canvas.shapes));
				Shape s = new Shape(x0,y0,x0,y0,shape,color);
				s.points.add(e.getPoint());
				Canvas.shapes.add(s);
			}
			if(mode.equals("Select")) {
				undoStack.push(copyShapeList(Canvas.shapes));
				if(groupList.isEmpty()) {
					selectedIndex = selectIndex(Canvas.shapes,e.getPoint());
					Shape old = Canvas.shapes.get(selectedIndex);
					Shape x = new Shape(old.x1,old.y1,old.x2,old.y2,shape,color);
					undoStack.get(undoStack.size()-1).removeLast();
					undoStack.get(undoStack.size()-1).add(x);
					if(shape.equals("Freehand")) x.setPoints(old.points);
					if(shape.equals("Polygon")) x.setPolyPoints(old.polyPoints);
				}else {
					for(int groupIndex:groupList) {
						groupIndex = selectIndex(Canvas.shapes,e.getPoint());
						Shape old = Canvas.shapes.get(groupIndex);
						Shape x = new Shape(old.x1,old.y1,old.x2,old.y2,shape,color);
						if(shape.equals("Freehand")) x.setPoints(old.points);
						if(shape.equals("Polygon")) x.setPolyPoints(old.polyPoints);
					}
				}
			}
			if(mode.equals("Delete") || mode.equals("Cut")) {
				temShapes.clear();
				undoStack.push(copyShapeList(Canvas.shapes));
				if(groupList.isEmpty()) {
					selectedIndex = selectIndex(Canvas.shapes,e.getPoint());
					if(mode.equals("Cut")) {
						temShape = Canvas.shapes.get(selectedIndex);
					}
					Canvas.shapes.remove(selectedIndex);
				}else {
					Collections.sort(groupList,Collections.reverseOrder());
					for(int groupIndex:groupList) {
						if(mode.equals("Cut")) {
							temShapes.add(Canvas.shapes.get(groupIndex));
						}
						Canvas.shapes.remove(groupIndex);
					}
				}
				repaint();
			}
			if(mode.equals("Copy")) {
				temShapes.clear();
				if(groupList.isEmpty()) {
					selectedIndex = selectIndex(Canvas.shapes,e.getPoint());
					temShape = copyShape(Canvas.shapes.get(selectedIndex));
				}else {
					for(int i :groupList) {
						temShapes.add(copyShape(Canvas.shapes.get(i)));
					}
				}

			}
			if(mode.equals("Paste")) {
				undoStack.push(copyShapeList(Canvas.shapes));
				if(groupList.isEmpty()) {
					int dx = e.getX()-temShape.x1;
					int dy = e.getY()-temShape.y1;
					temShape.reLocate(dx, dy);
					Canvas.shapes.add(temShape);
				}else {
					//group paste goes here
					int dx = e.getX()-temShapes.get(0).x1;
					int dy = e.getY()-temShapes.get(0).y1;
					for(Shape ts: temShapes) {
						ts.reLocate(dx, dy);
						Canvas.shapes.add(ts);
						System.out.println(Canvas.shapes);
					}
				}
				repaint();
			}
			if(mode.equals("Group")) {
				selectedIndex = selectIndex(Canvas.shapes,e.getPoint());
				groupList.add(selectedIndex);
				groupedItem.setText("Group Select: "+groupList.size());
			}
		}
		//-------------------------------------------
		public void mouseReleased(MouseEvent e) {
			if(mode.equals("Select") && !Canvas.shapes.isEmpty()) {
				int dx = e.getX()-x0;
				int dy = e.getY()-y0;
				// need the selection function
				if(groupList.isEmpty()) {
					Canvas.shapes.get(selectedIndex).reLocate(dx, dy);
				}else {
					for(int groupIndex:groupList) {
						Canvas.shapes.get(groupIndex).reLocate(dx, dy);
					}
				}
				repaint();
			}
		}
		//--------------------------------------------
		public void mouseClicked(MouseEvent e) {
			if(shape.equals("Polygon")&& mode.equals("Draw")&& !Canvas.shapes.isEmpty()) {
				Canvas.shapes.getLast().polyPoints.add(e.getPoint());
			}
			repaint();
		}
		//--------------------------------------------
		public void mouseEntered(MouseEvent e){
			if(shape.equals("Polygon") && mode.equals("Draw")) {
				System.out.println("entered");
				Shape s = new Shape(x0,y0,x0,y0,shape,color);
				Canvas.shapes.add(s);
				repaint();
			}
		}
	}
//handle mouse motion event -----------------------------------------------------------------------------------------
	public class myMouseMotionHandler extends MouseMotionAdapter{
		public void mouseDragged(MouseEvent e) {
			if(mode.equals("Draw") && !shape.equals("Polygon")) {
				if(shape.equals("Freehand")) {
					Canvas.shapes.getLast().points.add(e.getPoint());
				}else {
					Canvas.shapes.removeLast();
					Canvas.shapes.add(new Shape(x0,y0,e.getX(),e.getY(),shape,color));
				}
				repaint();
			}
			if(mode.equals("Select")) {}
			
		}
	}
	private int selectIndex( LinkedList<Shape> shapes, Point p ) {
		int index = 0;
		int D = Integer.MAX_VALUE;
		for(int i=0;i<shapes.size();i++) {
			Shape s = shapes.get(i);
			int currD=s.getDistance(p.x, p.y);
			if(currD<D) {
				index = i;
				D = currD;
			}
		}
		return index;
	}
	 
	private Shape copyShape(Shape s) {
		Shape newS = new Shape(s.x1,s.y1,s.x2,s.y2,s.shape,s.color);
		newS.setPoints(s.points);
		newS.setPolyPoints(s.polyPoints);
		return newS;
	}
	
	private LinkedList<Shape> copyShapeList(LinkedList<Shape> x) {
		LinkedList<Shape> tem = new LinkedList<Shape>();
		tem = (LinkedList<Shape>) x.clone();
		return tem;
	}
	
	public static void main(String[] args) {
		 SketchPad sp = new SketchPad(500,500);
	}
}
