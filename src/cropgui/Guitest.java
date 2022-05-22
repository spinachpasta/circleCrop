package cropgui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;

class Guitest extends JFrame {
	static final int WINDOW_WIDTH = 1000, WINDOW_HEIGHT = 700;
	int currentX, currentY, oldX, oldY;
	private ChoosePath choosePath;

	public Guitest() {
//		initComponents();
		pathWindow();
	}

	public void pathWindow() {
		choosePath = new ChoosePath(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println("buttonpressed");
				ChoosePath.updateTexts();
				initComponents();
			}
		});
		this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		// add the component to the frame to see it!
		this.setContentPane(choosePath);
		// be nice to testers..
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	int radius = 10;
	static ArrayList<String> extensions = new ArrayList<String>(Arrays.asList(".png", ".jpeg", ".jpg"));

	public void shuffleFiles() {
		ArrayList<String> imgPaths1 = new ArrayList<String>();
		ArrayList<Integer> parentIndex = new ArrayList<Integer>();
		ArrayList<Integer> parentLength = new ArrayList<Integer>();
		{
			String lastName = "";
			lastName = "";
			parentIndex.add(0);
			for (int i = 0; i < imgPaths.size(); i++) {
				String parentName = new File(imgPaths.get(i)).getParentFile().getAbsolutePath();// file can't be placed at root directory

//				System.out.println(parentName!=lastName);
				if (!parentName.equals(lastName)) {
					parentIndex.add(i);
					lastName = parentName;
				}
			}
		}
//		System.out.println(parentIndex);
		int maximumLength = 0;
//		System.out.println(parentIndex);
		if (parentIndex.size() == 0) {
			parentLength.add(imgPaths.size());
		} else {
			parentLength.add(parentIndex.get(0));
		}
		for (int parent = 1; parent < parentIndex.size() - 1; parent++) {
			int pl = -parentIndex.get(parent - 1) + parentIndex.get(parent);
			parentLength.add(pl);
			Math.max(maximumLength, pl);
		}
		{
			int pl = imgPaths.size() - parentIndex.get(parentIndex.size() - 1);
			parentLength.add(pl);
			maximumLength = Math.max(maximumLength, pl);
		}

		for (int count = 0; count < maximumLength; count++) {
			for (int parent = 0; parent < parentIndex.size(); parent++) {
				if (count >= parentLength.get(parent)) {
					continue;
				}
				int index = parentIndex.get(parent) + count;
				imgPaths1.add(imgPaths.get(index));
			}
		}
		imgPaths = imgPaths1;
	}

	public void fetchFiles(File dir) {

		if (dir.isDirectory()) {
			for (File file1 : dir.listFiles()) {
				fetchFiles(file1);
			}
		} else {
			File textparent=textfile.getParentFile();
			System.out.println("relative:"+textparent.toURI().relativize(new File(dir.getAbsolutePath()).toURI()).getPath());
			String filename = dir.getAbsolutePath();
			if (filename.contains(".")) {
				if (extensions.contains(filename.substring(filename.lastIndexOf(".")))) {
					AddImage(filename);
				}
			}
		}
	}

	ArrayList<String> imgPaths = new ArrayList<String>();

	public void AddImage(String path) {
		if (size == 0) {
			// ((Panel2) jPanel2).setImage(path);
			begin = new Date();
			// index = 0;
		}
		size++;
		if (imgPaths.size() < 100) {
			// System.out.println(path);
		}
		imgPaths.add(path);
	}

	public int index = -1;
	public int size = 0;
	public int clickCount = 0;
	public ArrayList<Circle> circles = new ArrayList<Circle>();
	Date begin;

	public File textfile;
	public ArrayList<String> skips = new ArrayList<String>();

	public boolean containsskip(String s) {
		for (int i = 0; i < skips.size(); i++) {
			if (skips.get(i).contains(s)) {
				return true;
			}
		}
		return false;
	}

	private void initComponents() {
//		System.out.println(System.getProperty("user.home")+"/labelled.txt");
		textfile = new File(ChoosePath.labelPath);
		try {
			if (!textfile.createNewFile()) {
				BufferedReader br = new BufferedReader(new FileReader(textfile));
				int i = 0;
				String line = "";
				int skip = 0;
				while (true) {
					line = br.readLine();
					if (line == null)
						break;
					String[] elem = line.split(" ");
					if (i >= skip) {
						skip = i + Integer.parseInt(elem[0]) + 1;
						// System.out.println("skipped:"+line.substring(line.indexOf(" ", 1)));
						skips.add(line.substring(line.indexOf(" ", 1)));
					}
					i++;
				}
				br.close();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// System.out.println(System.getProperty("user.home"));
		// we want a custom Panel2, not a generic JPanel!
		trySkipImages();

		jPanel2 = new Panel2();
		((Panel2) jPanel2).circles = circles;
		File dir = new File(ChoosePath.imageRoot);
		fetchFiles(dir);
		shuffleFiles();
		index = 0;
		trySkipImages();
		if (imgPaths.size() <= index) {
			System.exit(0);
		}
		updateJpanel2();
		if (index >= imgPaths.size()) {
			System.out.println(index);
			System.out.println(imgPaths.size());
			System.exit(0);
		}
		((Panel2) jPanel2).setImage(imgPaths.get(index));

		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				super.windowClosing(windowEvent);
			}
		});
		jPanel2.setBackground(new java.awt.Color(255, 255, 255));
		jPanel2.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		int timetook = 0;
		MouseAdapter adapter = new MouseAdapter() {
			public void mouseMoved(MouseEvent evt) {
				jPanel2MouseMoved(evt);
				updateJpanel2();
			}

			public void mousePressed(MouseEvent e) {
				jPanel2MouseMoved(e);
				switch (e.getButton()) {
				case 1:// l
				{
					Circle circle = new Circle();
					circle.r = (int) (radius);
					circle.x = (int) (e.getX() / ((Panel2) jPanel2).scale);
					circle.y = (int) (e.getY() / ((Panel2) jPanel2).scale);
					circles.add(circle);
					break;
				}
				case 3:// r
					moveNext(true);
					break;
				}
				updateJpanel2();
			}

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				jPanel2MouseWheel(e);
			}
		};
		jPanel2.addMouseMotionListener(adapter);
		jPanel2.addMouseListener(adapter);
		jPanel2.addMouseWheelListener(adapter);

		((Panel2) jPanel2).radius = radius;
		// add the component to the frame to see it!
		this.setContentPane(jPanel2);
		// be nice to testers..
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
	}// </editor-fold>

	public void moveNext(boolean save) {
		if (save) {
			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new FileWriter(textfile.getAbsoluteFile(), true));
				bw.write(String.format("%d %s", circles.size(), imgPaths.get(index)));
				bw.newLine();
				for (int i = 0; i < circles.size(); i++) {
					Circle c = circles.get(i);
					bw.write(String.format("%d %d %d", c.x, c.y, c.r));
					bw.newLine();
				}
				bw.flush();
			} catch (IOException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			} finally {
				try {
					bw.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		circles.clear();
		if (imgPaths.size() <= 0)
			return;
		index++;
		clickCount++;
		if (imgPaths.size() <= index) {
			System.exit(0);
		}
		trySkipImages();
		((Panel2) jPanel2).setImage(imgPaths.get(index));
	}

	public void trySkipImages() {
		// System.out.println("image:" + imgPaths.get(index));
		// System.out.println(containsskip(imgPaths.get(index)));
		boolean skipped = false;
		if (index < 0)
			return;
		for (;;) {
			if (index >= imgPaths.size()) {
				break;
			}
			if (!containsskip(imgPaths.get(index)))
				break;
			index++;
			skipped = true;
		}
		if (skipped) {
			System.out.println("skipped");
		}
	}

	private void jPanel2MouseMoved(MouseEvent evt) {
		if (index < 0)
			return;
		((Panel2) jPanel2).x =(int)(evt.getX()/ ((Panel2) jPanel2).scale);
		((Panel2) jPanel2).y =(int)(evt.getY()/ ((Panel2) jPanel2).scale);
	}

	private void jPanel2MouseWheel(MouseWheelEvent evt) {
		if (index < 0)
			return;
		radius += evt.getWheelRotation() * 10;
		if (radius < 1) {
			radius = 1;
		}
		((Panel2) jPanel2).radius = radius;
		updateJpanel2();
	}

	private void updateJpanel2() {
		long timetook = (begin.getTime() - new Date().getTime()) / 1000;
		String filename = ";";
		if (imgPaths.size() > 0 && index > 0) {
			filename = imgPaths.get(index);
			((Panel2) jPanel2).label = String.format("%d/%d filename:%s ,total:%ds average:%ds", index, imgPaths.size(),
					filename, timetook, timetook / (clickCount + 1));
		}
		jPanel2.revalidate();
		jPanel2.repaint();
	}

	// set ui visible//

	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Guitest().setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify
	private JPanel jPanel2;
}

class Circle {
	public int x, y, r;
}
// End of variables declaration

// This class name is very confusing, since it is also used as the
// name of an attribute!
class Panel2 extends JPanel {
	public int x, y;
	public String label = "";
	public double scale = 1;

	public int radius = 10;
	public BufferedImage img = null;
	public ArrayList<Circle> circles;

	public void setImage(String path) {
		try {
			System.out.println(path);
			File f = new File(path);
			img = ImageIO.read(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(path);
			e.printStackTrace();
		}
	}

	Panel2() {
		// set a preferred size for the custom panel.
		setPreferredSize(new Dimension(Guitest.WINDOW_WIDTH, Guitest.WINDOW_HEIGHT));
	}

	@Override
	public void paintComponent(Graphics g) {
		// super.paintComponent(g);
		g.clearRect(0, 0, this.getWidth(), this.getHeight());

		scale = 1;
		if (img != null) {
			scale = Math.min(scale, (double) Guitest.WINDOW_WIDTH / img.getWidth());
			scale = Math.min(scale, (double) Guitest.WINDOW_HEIGHT / img.getHeight());
		}
		// scale=1;
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.scale(scale, scale);
		int radius = this.radius;
		if (img != null) {
			g2d.drawImage(img, 0, 0, null);
		} else {
			g2d.setColor(Color.red);
			g2d.drawLine(0, 0, this.getWidth(), this.getHeight());
			g2d.drawLine(this.getWidth(), 0, 0, this.getHeight());
		}

		for (int i = 0; i < circles.size(); i++) {
			Circle c = circles.get(i);
			g2d.setColor(new Color(255, 0, 255));
			g2d.drawArc((int) ((double) c.x ) - c.r, (int) ((double) c.y ) - c.r, c.r * 2, c.r * 2, 0,
					360);
			g2d.setColor(new Color(0, 255, 0));
			int r1 = Math.max((int) (c.r * 0.9), c.r - 10);
			g2d.drawArc((int) ((double) c.x ) - r1, (int) ((double) c.y) - r1, r1 * 2, r1 * 2, 0, 360);
		}
		g2d.setColor(new Color(0, 0, 0));
		g2d.drawArc((int) ((double) x) - radius, (int) ((double) y) - radius, radius * 2, radius * 2, 0,
				360);
//		System.out.println(scale);
		int fontSize = (int) (20 / (double) scale);
		g2d.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));
		g2d.drawString(label, 0, fontSize);
		g2d.dispose();
		// g.drawRect(200, 200, 200, 200);
	}
}