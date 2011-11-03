import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;

public class JExplodomatica implements ActionListener {

	private JFrame frame;
	//buttons
	private JButton generate;
	private JButton input;
	private JButton play;
	private JButton save;
	private JButton cancel;
	private JButton quit;
	//labels
	private JLabel durationLbl;
	private JLabel durationValLbl;
	private JLabel preExplosionsLbl;
	private JLabel preExplosionsValLbl;
	private JLabel preDelayLbl;
	private JLabel preDelayValLbl;
	private JLabel preLPFactorLbl;
	private JLabel preLPFactorValLbl;
	private JLabel preLPCountLbl;
	private JLabel preLPCountValLbl;
	private JLabel speedFactorLbl;
	private JLabel speedFactorValLbl;
	private JLabel reverbEarlyReflsLbl;
	private JLabel reverbEarlyReflsValLbl;
	private JLabel reverbLateReflsLbl;
	private JLabel reverbLateReflsValLbl;
	//sliders
	private JSlider duration;
	private JSlider preExplosions;
	private JSlider preDelay;
	private JSlider preLPFactor;
	private JSlider preLPCount;
	private JSlider speedFactor;
	private JSlider reverbEarlyRefls;
	private JSlider reverbLateRefls;
	//toggles
	
	//icon, screw storing an actual image
	private int[] icon = {
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,3,4,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,3,3,4,4,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,4,4,3,3,3,4,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,4,3,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,1,0,0,4,3,3,3,3,3,3,4,1,0,0,0,0,0,0,0,0,0,0,0,
		0,3,4,3,1,0,0,0,2,3,3,3,3,3,3,3,3,2,3,3,3,4,4,4,4,3,4,5,5,0,0,0,
		1,4,1,4,4,4,4,4,4,2,2,3,3,3,3,3,2,2,2,3,3,3,3,4,4,4,3,3,4,0,0,0,
		0,4,3,3,3,2,3,4,3,3,3,3,4,3,3,1,1,2,2,2,3,3,3,3,3,4,3,3,3,0,0,0,
		0,0,4,3,3,3,3,3,3,3,2,2,3,3,1,1,1,1,1,1,2,3,3,2,3,3,3,3,4,0,0,0,
		0,0,4,4,3,3,3,3,2,3,3,2,1,1,1,1,1,1,1,2,2,2,3,3,2,3,4,3,4,0,0,0,
		0,0,0,4,3,3,3,2,1,1,1,1,1,1,1,3,3,1,1,3,4,1,2,2,3,3,3,4,4,0,0,0,
		0,0,0,4,3,3,2,1,1,4,2,1,1,1,1,4,2,1,1,1,2,1,1,2,3,3,4,2,0,0,0,0,
		0,0,0,4,4,3,2,1,1,1,1,1,1,2,1,1,5,1,5,5,1,1,1,2,3,3,2,4,0,0,0,0,
		0,0,0,4,3,3,1,1,3,4,1,1,2,2,5,5,5,5,5,5,1,1,1,1,3,3,2,4,0,0,0,0,
		0,0,0,4,3,3,2,1,2,2,1,1,1,5,5,5,5,5,5,1,1,1,1,1,1,3,3,2,4,0,0,0,
		0,0,0,4,3,3,1,1,1,1,1,2,1,5,5,5,5,5,2,5,1,1,1,1,1,2,3,3,3,4,0,0,
		0,0,0,4,3,3,1,1,1,1,1,1,5,5,2,5,5,5,5,1,1,1,3,3,1,1,2,3,3,4,4,0,
		0,0,4,4,3,3,1,1,1,2,1,1,1,1,1,1,5,5,1,1,1,1,2,2,1,2,2,3,3,3,4,0,
		0,0,4,3,3,3,1,1,1,2,1,1,1,1,1,1,1,1,1,1,2,2,1,1,3,3,2,2,4,4,4,2,
		0,0,4,3,3,2,2,1,1,2,5,5,1,1,1,1,1,1,1,1,5,5,3,2,3,3,3,4,4,4,0,0,
		0,0,4,3,3,2,5,5,3,3,3,3,3,2,1,1,2,2,1,5,5,5,5,3,3,3,4,4,0,0,0,0,
		0,0,4,3,3,2,3,3,3,1,4,3,3,3,3,1,2,2,1,1,5,5,2,3,3,4,4,0,0,0,0,0,
		0,0,4,3,4,4,3,3,4,4,4,4,4,3,3,3,2,1,1,2,3,3,3,3,3,4,4,0,0,0,0,0,
		0,0,4,3,3,3,3,4,4,0,0,4,4,4,3,3,2,1,2,3,2,2,3,4,4,4,3,0,0,0,0,0,
		0,0,4,3,3,3,4,4,0,0,0,0,4,4,4,3,3,3,2,3,2,4,4,3,0,0,0,0,0,0,0,0,
		0,0,4,3,2,2,4,3,0,0,0,0,0,0,4,4,2,3,3,3,3,4,3,0,0,0,0,0,0,0,0,0,
		0,0,4,3,3,4,3,0,0,0,0,0,0,0,0,4,4,3,3,3,3,4,3,0,0,0,0,0,0,0,0,0,
		0,0,4,4,4,3,0,0,0,0,0,0,0,0,0,0,4,4,4,3,3,3,4,4,0,0,0,0,0,0,0,0,
		0,0,4,3,3,0,0,0,0,0,0,0,0,0,0,0,0,4,4,4,4,4,4,2,0,0,0,0,0,0,0,0,
		0,0,5,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,4,4,4,4,3,0,0,0,0,0,0,0
	};
	
	
	public JExplodomatica() {
		//init frame
		this.frame = new JFrame("JExplodomatica");
		this.frame.setLocation(100, 100);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setLayout(new GridLayout(0,3));
		//init buttons
		this.generate = new JButton("Generate");
		this.generate.setActionCommand("generate");
		this.generate.addActionListener(this);
		this.input = new JButton("Input");
		this.input.setActionCommand("input");
		this.input.addActionListener(this);
		this.play = new JButton("Play");
		this.play.setActionCommand("play");
		this.play.addActionListener(this);
		this.save = new JButton("Save");
		this.save.setActionCommand("save");
		this.save.addActionListener(this);
		this.cancel = new JButton("Cancel");
		this.cancel.setActionCommand("cancel");
		this.cancel.addActionListener(this);
		this.quit = new JButton("Quit");
		this.quit.setActionCommand("quit");
		this.quit.addActionListener(this);
		//init sliders and labels
		this.durationLbl = new JLabel("Duration");
		this.durationValLbl = new JLabel("42");
		this.duration = new JSlider(0, 10);
		this.preExplosionsLbl = new JLabel("Pre Explosions");
		this.preExplosionsValLbl = new JLabel("42");
		this.preExplosions = new JSlider(0, 10);
		this.preDelayLbl = new JLabel("Pre Delay");
		this.preDelayValLbl = new JLabel("42");
		this.preDelay = new JSlider(0, 10);
		this.preLPFactorLbl = new JLabel("Pre Low Pass Factor");
		this.preLPFactorValLbl = new JLabel("42");
		this.preLPFactor = new JSlider(0, 10);
		this.preLPCountLbl = new JLabel("Pre Low Pass Count");
		this.preLPCountValLbl = new JLabel("42");
		this.preLPCount = new JSlider(0, 10);
		this.speedFactorLbl = new JLabel("Speed Factor");
		this.speedFactorValLbl = new JLabel("42");
		this.speedFactor = new JSlider(0, 10);
		this.reverbEarlyReflsLbl = new JLabel("Reverb Early Reflections");
		this.reverbEarlyReflsValLbl = new JLabel("42");
		this.reverbEarlyRefls = new JSlider(0, 10);
		this.reverbLateReflsLbl = new JLabel("Reverb Late Reflections");
		this.reverbLateReflsValLbl = new JLabel("42");
		this.reverbLateRefls = new JSlider(0, 10);
		//add components to frame
		this.frame.add(this.durationLbl);
		this.frame.add(this.duration);
		this.frame.add(this.durationValLbl);
		this.frame.add(this.preExplosionsLbl);
		this.frame.add(this.preExplosions);
		this.frame.add(this.preExplosionsValLbl);
		this.frame.add(this.preDelayLbl);
		this.frame.add(this.preDelay);
		this.frame.add(this.preDelayValLbl);
		this.frame.add(this.preLPFactorLbl);
		this.frame.add(this.preLPFactor);
		this.frame.add(this.preLPFactorValLbl);
		this.frame.add(this.preLPCountLbl);
		this.frame.add(this.preLPCount);
		this.frame.add(this.preLPCountValLbl);
		this.frame.add(this.speedFactorLbl);
		this.frame.add(this.speedFactor);
		this.frame.add(this.speedFactorValLbl);
		this.frame.add(this.reverbEarlyReflsLbl);
		this.frame.add(this.reverbEarlyRefls);
		this.frame.add(this.reverbEarlyReflsValLbl);
		this.frame.add(this.reverbLateReflsLbl);
		this.frame.add(this.reverbLateRefls);
		this.frame.add(this.reverbLateReflsValLbl);
		this.frame.add(this.generate);
		this.frame.add(this.input);
		this.frame.add(this.play);
		this.frame.add(this.save);
		this.frame.add(this.cancel);
		this.frame.add(this.quit);
		//finish frame stuff
		this.frame.pack();
		this.frame.setResizable(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		//generate button
		if (e.getActionCommand().equals("generate")) {
			System.out.println("clicked generate");
		}
		//input button
		if (e.getActionCommand().equals("input")) {
			System.out.println("clicked input");
		}
		//play button
		if (e.getActionCommand().equals("play")) {
			System.out.println("clicked play");
		}
		//save button
		if (e.getActionCommand().equals("save")) {
			System.out.println("clicked save");
		}
		//cancel button
		if (e.getActionCommand().equals("cancel")) {
			System.out.println("clicked cancel");
		}
		//quit button
		if (e.getActionCommand().equals("quit")) {
			System.out.println("clicked quit");
		}
	}
	
	public void show() {
		this.frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		JExplodomatica jexplodomatica = new JExplodomatica();
		jexplodomatica.show();
	}
	
}
