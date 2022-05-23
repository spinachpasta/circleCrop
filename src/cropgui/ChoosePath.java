package cropgui;

import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import java.util.prefs.Preferences;

public class ChoosePath extends JPanel {
	public static String imageRoot;
	public static String labelPath;
	public static boolean isRelative;
	public static Preferences prefs;
	public static TextField imageRootTf;
	public static TextField labelTf;
	public static JCheckBox isRelativeCheckbox;

	public static void updateTexts() {
		System.out.println("updateTexts");
		imageRoot = imageRootTf.getText();
		System.out.println(imageRoot);
		prefs.put("imageRoot", imageRoot);
		labelPath = labelTf.getText();
		System.out.println(labelPath);
		prefs.put("labelPath", labelPath);
		isRelative=isRelativeCheckbox.isSelected();
		System.out.println(isRelative);
		System.out.println(isRelative+"");
		prefs.put("isRelative", isRelative+"");
	}

	public ChoosePath(ActionListener al) {
		prefs = Preferences.userRoot().node(this.getClass().getName());
		imageRoot = prefs.get("imageRoot", System.getProperty("user.home") + "/images/");
		labelPath = prefs.get("labelPath", System.getProperty("user.home") + "/labelled.txt");
		JPanel p = new JPanel();

		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		imageRootTf = new TextField(20);
		imageRootTf.setText(imageRoot);

		labelTf = new TextField(20);
		labelTf.setText(labelPath);

		Label imageRootLabel = new Label("image root directory (absolute path):");
		Label labelLabel = new Label("output label text file location (absolute path):");

        isRelativeCheckbox = new JCheckBox("use relative path");
        System.out.println(prefs.get("isRelative","false").equals("true"));
        isRelativeCheckbox.setSelected(prefs.get("isRelative","false").equals("true"));


		p.add(imageRootLabel);
		p.add(imageRootTf);
		p.add(labelLabel);
		p.add(labelTf);
		p.add(isRelativeCheckbox);

		this.add(p);

		JButton confirm = new JButton("confirm");
		Label emptylabel = new Label("");// spacing
		p.add(emptylabel);
		p.add(confirm);

		confirm.addActionListener(al);

	}
}
