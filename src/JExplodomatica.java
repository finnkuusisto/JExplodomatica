/* 
    (C) Copyright 2011, Finn C. Kuusisto

    This file is part of jexplodomatica, a Java port of explodomatica.

    explodomatica is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    explodomatica is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with explodomatica; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

 */
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.SwingWorker;

public class JExplodomatica implements ActionListener, PropertyChangeListener {

	//frame
	private JFrame frame;
	//buttons
	private JButton generate;
	private JButton input;
	private JButton play;
	private JButton save;
	private JButton cancel;
	private JButton quit;
	//checkboxes
	private JCheckBox reverb;
	private JCheckBox useInput;
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
	//progress bar
	private JProgressBar progressBar;
	//file chooser
	private JFileChooser fileChooser;
	
	//non gui stuff
	private String inputFileName;
	private Sound currentSound;
	private GenerateWorker worker;
	private boolean workerDone;
	
	//icon, screw storing an actual image
	private int[] icon = {
			0,0,0,0,0,0,0,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0,0,1,1,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0,0,1,1,1,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0,0,1,3,2,1,4,0,0,4,4,0,0,0,0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0,0,1,3,3,2,1,4,0,2,2,4,0,0,0,0,0,0,0,4,4,0,0,
			0,0,0,0,0,4,2,4,4,0,4,1,3,4,3,2,1,4,2,2,4,0,0,0,0,4,4,1,1,0,0,0,
			0,0,0,0,0,0,2,2,2,2,2,2,3,4,4,3,2,2,2,2,2,0,0,4,1,2,3,1,0,0,0,0,
			4,4,4,0,0,0,0,2,2,2,2,3,3,3,4,3,3,2,2,2,2,2,2,3,3,3,1,4,0,0,0,0,
			0,1,1,1,1,1,4,4,2,2,2,3,3,3,4,4,3,3,2,2,2,3,3,4,3,3,1,0,0,0,0,0,
			0,0,4,1,1,3,3,3,3,3,3,3,3,3,4,4,3,3,3,3,3,4,4,3,3,2,2,2,2,0,0,0,
			0,0,0,0,4,2,3,3,3,3,3,3,3,3,4,4,4,3,3,4,4,4,3,3,3,2,2,2,0,0,0,0,
			0,0,0,0,0,4,3,3,3,4,4,4,4,4,4,4,4,4,4,4,4,3,3,3,3,2,2,4,0,0,0,0,
			0,0,0,0,0,0,2,2,3,3,3,3,4,4,4,4,4,4,4,4,3,3,3,3,3,3,2,1,4,0,0,0,
			0,0,0,0,0,2,2,2,2,3,3,3,4,4,4,4,4,4,4,4,4,4,4,4,4,3,3,3,3,1,1,4,
			0,0,0,0,2,2,2,2,2,3,3,4,4,4,4,4,4,4,4,4,4,3,3,3,3,3,3,3,1,1,1,4,
			0,0,0,4,2,2,2,2,2,3,4,4,4,4,4,4,4,3,4,4,4,3,3,3,3,3,2,1,1,4,0,0,
			0,0,0,0,0,0,4,1,3,4,4,4,3,4,4,4,3,3,3,4,4,3,3,3,3,2,2,2,0,0,0,0,
			0,0,0,0,0,4,1,3,4,4,3,3,3,3,4,4,3,3,3,3,4,4,3,3,2,2,2,2,4,0,0,0,
			0,0,0,0,4,1,3,3,3,3,3,3,3,3,4,4,3,3,2,3,3,4,3,3,2,0,0,0,0,0,0,0,
			0,0,0,4,1,1,2,1,1,2,2,2,2,3,4,4,3,2,2,2,2,3,3,3,3,1,0,0,0,0,0,0,
			0,0,4,1,1,1,1,4,2,2,2,2,2,3,4,3,3,2,2,2,2,2,3,3,3,1,0,0,0,0,0,0,
			0,4,1,4,0,0,0,0,2,2,2,2,1,3,4,3,2,2,2,2,2,2,1,2,3,1,4,0,0,0,0,0,
			0,0,0,0,0,0,0,0,2,2,4,1,1,3,4,3,1,1,1,2,2,2,0,1,1,1,1,0,0,0,0,0,
			0,0,0,0,0,0,0,4,2,0,0,1,1,3,3,1,1,1,4,0,2,2,0,0,1,1,1,4,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0,0,1,1,3,2,1,1,4,0,0,0,4,0,0,0,4,1,4,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0,0,4,1,2,1,1,4,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0,0,4,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0,0,4,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0,0,4,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0,0,4,1,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0,0,4,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
	};
	private int[] iconColors = {
			1442047,-6614784,-1356800,-143587,-1644
	};
	
	
	public JExplodomatica() {
		//init frame
		this.frame = new JFrame("JExplodomatica");
		this.frame.setIconImage(this.getIconImage());
		this.frame.setLocation(100, 100);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//init others
		this.initButtons();
		this.initSliders();
		this.progressBar = new JProgressBar(0,100);
		this.fileChooser = new JFileChooser();
		//layout components
		this.layoutComponents();
		//finish frame stuff
		this.frame.pack();
		this.frame.setResizable(false);
	}
	
	private void layoutComponents() {
		//layout
		this.frame.setLayout(new GridLayout(0,3));
		//add sliders and slider labels
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
		//add toggles
		this.frame.add(this.reverb);
		this.frame.add(this.useInput);
		//progress bar
		this.frame.add(this.progressBar);
		//and regular buttons
		this.frame.add(this.generate);
		this.frame.add(this.input);
		this.frame.add(this.play);
		this.frame.add(this.save);
		this.frame.add(this.cancel);
		this.frame.add(this.quit);
	}
	
	private void initButtons() {
		//regular buttons
		this.generate = new JButton("Generate");
		this.generate.setActionCommand("generate");
		this.generate.addActionListener(this);
		
		this.input = new JButton("Input");
		this.input.setActionCommand("input");
		this.input.addActionListener(this);
		
		this.play = new JButton("Play");
		this.play.setActionCommand("play");
		this.play.addActionListener(this);
		this.play.setEnabled(false);
		
		this.save = new JButton("Save");
		this.save.setActionCommand("save");
		this.save.addActionListener(this);
		this.save.setEnabled(false);
		
		this.cancel = new JButton("Cancel");
		this.cancel.setActionCommand("cancel");
		this.cancel.addActionListener(this);
		this.cancel.setEnabled(false);
		
		this.quit = new JButton("Quit");
		this.quit.setActionCommand("quit");
		this.quit.addActionListener(this);
		//and toggles
		this.reverb = new JCheckBox("Reverb");
		this.useInput = new JCheckBox("Use Input Sound");
		this.useInput.setEnabled(false);
	}
	
	private void initSliders() {
		this.durationLbl = new JLabel("Duration");
		this.durationValLbl = new JLabel("42");
		this.duration = new JSlider(0, 1000);
		
		this.preExplosionsLbl = new JLabel("Pre Explosions");
		this.preExplosionsValLbl = new JLabel("42");
		this.preExplosions = new JSlider(0, 1000);
		
		this.preDelayLbl = new JLabel("Pre Delay");
		this.preDelayValLbl = new JLabel("42");
		this.preDelay = new JSlider(0, 1000);
		
		this.preLPFactorLbl = new JLabel("Pre Low Pass Factor");
		this.preLPFactorValLbl = new JLabel("42");
		this.preLPFactor = new JSlider(0, 1000);
		
		this.preLPCountLbl = new JLabel("Pre Low Pass Count");
		this.preLPCountValLbl = new JLabel("42");
		this.preLPCount = new JSlider(0, 1000);
		
		this.speedFactorLbl = new JLabel("Speed Factor");
		this.speedFactorValLbl = new JLabel("42");
		this.speedFactor = new JSlider(0, 1000);
		
		this.reverbEarlyReflsLbl = new JLabel("Reverb Early Reflections");
		this.reverbEarlyReflsValLbl = new JLabel("42");
		this.reverbEarlyRefls = new JSlider(0, 1000);
		
		this.reverbLateReflsLbl = new JLabel("Reverb Late Reflections");
		this.reverbLateReflsValLbl = new JLabel("42");
		this.reverbLateRefls = new JSlider(0, 1000);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		//generate button
		if (e.getActionCommand().equals("generate")) {
			//clear the current sound
			this.play.setEnabled(false);
			this.save.setEnabled(false);
			this.currentSound = null;
			//TODO gather settings
			//TODO get a thread running the generation
			//TODO store sound in current sound
			this.play.setEnabled(true);
			this.save.setEnabled(true);
		}
		//input button
		if (e.getActionCommand().equals("input")) {
			int result = this.fileChooser.showOpenDialog(this.frame);
			if (result == JFileChooser.APPROVE_OPTION) {
				File tmp = this.fileChooser.getSelectedFile();
				this.inputFileName = tmp.getAbsolutePath();
				this.useInput.setEnabled(true);
			}
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
			this.frame.dispose();
		}
	}
	
	public BufferedImage getIconImage() {
		BufferedImage img = new BufferedImage(32, 32,
				BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < this.icon.length; i++) {
			int r = i / 32;
			int c = i % 32;
			img.setRGB(c, r, this.iconColors[this.icon[i]]);
		}
		return img;
	}
	
	public void show() {
		this.frame.setVisible(true);
	}
	
	//this worker will do the actual generation of the sound on a thread other
	//than the event dispatch thread so as not to hang the GUI
	//this unfortunately means users need to have Java 1.6
	public class GenerateWorker extends SwingWorker<Sound,Void> {

		@Override
		protected Sound doInBackground() throws Exception {
			// TODO do the generation
			
			//TODO use this.setProgress(derp) for progressbar
			return null;
		}
		
		@Override
		protected void done() {
			//TODO this is executed on the event dispatch thread
			//finish gui stuff and store the result from get()
		}
		
	}
	
	//this is how the worker communicates to the gui about progress
	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		if (!workerDone) {
			this.progressBar.setValue(this.worker.getProgress());
		}
	}
	
	public static void main(String[] args) {
		JExplodomatica jexplodomatica = new JExplodomatica();
		jexplodomatica.show();
	}
	
}
