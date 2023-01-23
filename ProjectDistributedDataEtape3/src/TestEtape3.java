import java.awt.*;
import java.awt.event.*;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;


public class TestEtape3 extends JFrame {

	private static final long serialVersionUID = -4313194228903001868L;
	public TextArea text;
	public TextField data;
	SharedObject sentence;
	static String myName;
	static int flag;

	public static void main(String argv[]) {
		
		if (argv.length != 1) {
			System.out.println("java Irc <name>");
			return;
		}
		myName = argv[0];
	
		// initialize the system
		Client.init();
		
		// look up the IRC object in the name server
		// if not found, create it, and register it in the name server
		SharedObject s = Client.lookup("IRC");
		if (s == null) {
			s = Client.create(new Sentence());
			Client.register("IRC", s);
		}
		// create the graphical part
		new TestEtape3(s);
	}

	public TestEtape3(SharedObject s) {

		setLayout(new FlowLayout());

		text = new TextArea(10, 56);
		text.setEditable(false);
		text.setForeground(Color.red);
		add(text);

		data = new TextField(55);
		add(data);

		JButton start_button = new JButton("start");
		start_button.addActionListener(new StartListener(this, start_button));
		add(start_button);

		setSize(470, 300);
		text.setBackground(Color.black);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		sentence = s;
	}
}

class StartListener implements ActionListener {
	TestEtape3 irc;
	JButton start_button;

	public StartListener(TestEtape3 i, JButton button) {
		irc = i;
		start_button = button;

	}

	public void actionPerformed(ActionEvent e) {
		if (TestEtape3.flag == 0) {
			start_button.setText("Stop");
			new Action(irc).start();
		} else {
			TestEtape3.flag = 0;
			start_button.setText("Start");
		}
	}
}

class Action extends Thread {

	TestEtape3 irc;
	
	static Random rand = new Random();

	public Action(TestEtape3 irc) {
		this.irc = irc;
	}

	@Override
	public void run() {
		TestEtape3.flag = 1;
		int i = 0;
		while (TestEtape3.flag == 1) {
			try {
				sleep(10000 * ((long) Math.random()));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
				
			// Generates a random number -> 0 corresponds a read lock request and 1 a write lock request
			int n = rand.nextInt(2);
			
			if (n == 0) {
				// lock the object in read mode
				irc.sentence.lock_read();
				// invoke the method
				String s = ((Sentence)(irc.sentence.obj)).read();
				// unlock the object
				irc.sentence.unlock();
				// display the read value
				irc.text.append(s + "\n");
			} else {
				// lock the object in write mode
				irc.sentence.lock_write();
				// invoke the method
				((Sentence)(irc.sentence.obj)).write(irc.myName + " wrote "+ i++);
				// unlock the object
				irc.sentence.unlock();
			}
		}
	}
}