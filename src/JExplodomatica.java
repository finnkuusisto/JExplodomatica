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
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class JExplodomatica implements ActionListener, PropertyChangeListener,
		ChangeListener {

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
	private JLabel nLayersLbl;
	private JLabel nLayersValLbl;
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
	private JSlider nLayers;
	private JSlider duration;
	private JSlider preExplosions;
	private JSlider preDelay;
	private JSlider preLPFactor;
	private JSlider preLPCount;
	private JSlider speedFactor;
	private JSlider reverbEarlyRefls;
	private JSlider reverbLateRefls;
	//slider min/max
	private int[] nLayersMinMax = {1,6};
	private float[] durationMinMax = {0.2F,60.0F};
	private int[] preExplosionsMinMax = {0,5};
	private float[] preDelayMinMax = {0.1F,3.0F};
	private float[] preLPFactorMinMax = {0.2F,0.9F};
	private int[] preLPCountMinMax = {0,10};
	private float[] speedFactorMinMax = {0.1F,10.0F};
	private int[] reverbEarlyReflsMinMax = {1,50};
	private int[] reverbLateReflsMinMax = {1,1000};
	//progress bar
	private JProgressBar progressBar;
	//file chooser
	private JFileChooser fileChooser;
	
	//non gui stuff
	private ExplosionDef settings;
	private String inputFileName;
	private Sound currentSound;
	private Clip currentClip;
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
		//init settings
		this.settings = ExplosionDef.explodomaticaDefaults;
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
		this.frame.add(this.nLayersLbl);
		this.frame.add(this.nLayers);
		this.frame.add(this.nLayersValLbl);
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
		this.nLayersLbl = new JLabel("Layers:");
		this.nLayersValLbl = new JLabel("not set");
		this.nLayers = new JSlider(this.nLayersMinMax[0],
				this.nLayersMinMax[1]);
		this.nLayers.setSnapToTicks(true);
		this.nLayers.addChangeListener(this);
		this.nLayers.setValue(this.settings.nLayers);
		
		this.durationLbl = new JLabel("Duration:");
		this.durationValLbl = new JLabel("not set");
		this.duration = new JSlider(0, 1000);
		this.duration.addChangeListener(this);
		double pos = (this.settings.duration - this.durationMinMax[0]) /
			(this.durationMinMax[1] - this.durationMinMax[0]);
		this.duration.setValue((int)(pos * 1000));
		
		this.preExplosionsLbl = new JLabel("Pre Explosions:");
		this.preExplosionsValLbl = new JLabel("not set");
		this.preExplosions = new JSlider(this.preExplosionsMinMax[0],
				this.preExplosionsMinMax[1]);
		this.preExplosions.setSnapToTicks(true);
		this.preExplosions.addChangeListener(this);
		this.preExplosions.setValue(this.settings.preExplosions);
		
		this.preDelayLbl = new JLabel("Pre Delay:");
		this.preDelayValLbl = new JLabel("not set");
		this.preDelay = new JSlider(0, 1000);
		this.preDelay.addChangeListener(this);
		pos = (this.settings.preExplosionDelay - this.preDelayMinMax[0]) /
			(this.preDelayMinMax[1] - this.preDelayMinMax[0]);
		this.preDelay.setValue((int)(pos * 1000));
		
		this.preLPFactorLbl = new JLabel("Pre Low Pass Factor:");
		this.preLPFactorValLbl = new JLabel("not set");
		this.preLPFactor = new JSlider(0, 1000);
		this.preLPFactor.addChangeListener(this);
		pos = 
			(this.settings.preExplosionLPFactor - this.preLPFactorMinMax[0]) /
			(this.preLPFactorMinMax[1] - this.preLPFactorMinMax[0]);
		this.preLPFactor.setValue((int)(pos * 1000));
		
		this.preLPCountLbl = new JLabel("Pre Low Pass Count:");
		this.preLPCountValLbl = new JLabel("not set");
		this.preLPCount = new JSlider(this.preLPCountMinMax[0],
				this.preLPCountMinMax[1]);
		this.preLPCount.setSnapToTicks(true);
		this.preLPCount.addChangeListener(this);
		this.preLPCount.setValue(this.settings.preExplosionLPIters);
		
		this.speedFactorLbl = new JLabel("Speed Factor:");
		this.speedFactorValLbl = new JLabel("not set");
		this.speedFactor = new JSlider(0, 1000);
		this.speedFactor.addChangeListener(this);
		pos = (this.settings.finalSpeedFactor - this.speedFactorMinMax[0]) /
			(this.speedFactorMinMax[1] - this.speedFactorMinMax[0]);
		this.speedFactor.setValue((int)(pos * 1000));
		
		this.reverbEarlyReflsLbl = new JLabel("Reverb Early Reflections:");
		this.reverbEarlyReflsValLbl = new JLabel("not set");
		this.reverbEarlyRefls = new JSlider(this.reverbEarlyReflsMinMax[0],
				this.reverbEarlyReflsMinMax[1]);
		this.reverbEarlyRefls.setSnapToTicks(true);
		this.reverbEarlyRefls.addChangeListener(this);
		this.reverbEarlyRefls.setValue(this.settings.reverbEarlyRefls);
		
		this.reverbLateReflsLbl = new JLabel("Reverb Late Reflections:");
		this.reverbLateReflsValLbl = new JLabel("not set");
		this.reverbLateRefls = new JSlider(this.reverbLateReflsMinMax[0],
				this.reverbLateReflsMinMax[1]);
		this.reverbLateRefls.setSnapToTicks(true);
		this.reverbLateRefls.addChangeListener(this);
		this.reverbLateRefls.setValue(this.settings.reverbLateRefls);
	}
	
	private void setEnabledAll(boolean enabled) {
		//sliders
		this.duration.setEnabled(enabled);
		this.preExplosions.setEnabled(enabled);
		this.preDelay.setEnabled(enabled);
		this.preLPFactor.setEnabled(enabled);
		this.preLPCount.setEnabled(enabled);
		this.speedFactor.setEnabled(enabled);
		this.reverbEarlyRefls.setEnabled(enabled);
		this.reverbLateRefls.setEnabled(enabled);
		//buttons
		this.generate.setEnabled(enabled);
		this.input.setEnabled(enabled);
		this.play.setEnabled(enabled);
		this.save.setEnabled(enabled);
		this.cancel.setEnabled(enabled);
		this.quit.setEnabled(enabled);
		//and checkboxes
		this.reverb.setEnabled(enabled);
		this.useInput.setEnabled(enabled);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		//generate button
		if (e.getActionCommand().equals("generate")) {
			//fix the settings for input filename if needed
			if (this.useInput.isSelected()) {
				this.settings.inputFileName = this.inputFileName;
			}
			else {
				this.settings.inputFileName = "";
			}
			//fix settings for reverb
			this.settings.reverb = this.reverb.isSelected();
			//disable everything except cancel
			this.setEnabledAll(false);
			this.cancel.setEnabled(true);
			//start a thread generating the sound
			this.workerDone = false;
			this.worker = new GenerateWorker();
			this.worker.addPropertyChangeListener(this);
			(new Thread(this.worker)).start();
		}
		//input button
		else if (e.getActionCommand().equals("input")) {
			int result = this.fileChooser.showOpenDialog(this.frame);
			if (result == JFileChooser.APPROVE_OPTION) {
				File tmp = this.fileChooser.getSelectedFile();
				this.inputFileName = tmp.getAbsolutePath();
				this.useInput.setEnabled(true);
			}
		}
		//play button
		else if (e.getActionCommand().equals("play")) {
			//play current sound
			if (this.currentSound != null && this.currentClip != null &&
					this.currentClip.isOpen()) {
				this.currentClip.stop();
				this.currentClip.flush();
				this.currentClip.setFramePosition(0);
				this.currentClip.start();
			}
		}
		//save button
		else if (e.getActionCommand().equals("save")) {
			if (this.currentSound != null) {
				int result = this.fileChooser.showSaveDialog(this.frame);
				if (result == JFileChooser.APPROVE_OPTION) {
					File tmp = this.fileChooser.getSelectedFile();
					String path = tmp.getAbsolutePath();
					LibExplodomatica.explodomaticaSaveFile(path,
							this.currentSound, 1);
				}
			}
		}
		//cancel button
		else if (e.getActionCommand().equals("cancel")) {
			if (this.worker != null && !this.workerDone) {
				this.worker.cancel(false); //cancel but don't interrupt
			}
		}
		//quit button
		else if (e.getActionCommand().equals("quit")) {
			if (this.worker != null) {
				this.worker.cancel(true);
			}
			if (this.currentClip != null) {
				this.currentClip.stop();
				this.currentClip.close();
			}
			this.frame.dispose();
		}
	}
	
	//update settings and labels for sliders on change
	@Override
	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		if (source.equals(this.nLayers)) { //nLayers slider
			int val = this.nLayers.getValue();
			this.nLayersValLbl.setText("" + val);
			this.settings.nLayers = val;
		}
		else if (source.equals(this.duration)) { //duration slider
			double pos = (double)this.duration.getValue() / 
				(double)this.duration.getMaximum();
			float val = (float)(this.durationMinMax[0] + (pos *
					(this.durationMinMax[1] - this.durationMinMax[0])));
			this.durationValLbl.setText(String.format("%.2f", val));
			this.settings.duration = val;
		}
		else if (source.equals(this.preExplosions)) { //preexplosions slider
			int val = this.preExplosions.getValue();
			this.preExplosionsValLbl.setText("" + val);
			this.settings.preExplosions = val;
		}
		else if (source.equals(this.preDelay)) { //predelay slider
			double pos = (double)this.preDelay.getValue() / 
			(double)this.preDelay.getMaximum();
			float val = (float)(this.preDelayMinMax[0] + (pos *
					(this.preDelayMinMax[1] - this.preDelayMinMax[0])));
			this.preDelayValLbl.setText(String.format("%.2f", val));
			this.settings.preExplosionDelay = val;
		}
		else if (source.equals(this.preLPFactor)) { //preLPfactor slider
			double pos = (double)this.preLPFactor.getValue() / 
			(double)this.preLPFactor.getMaximum();
			float val = (float)(this.preLPFactorMinMax[0] + (pos *
					(this.preLPFactorMinMax[1] - this.preLPFactorMinMax[0])));
			this.preLPFactorValLbl.setText(String.format("%.2f", val));
			this.settings.preExplosionLPFactor = val;
		}
		else if (source.equals(this.preLPCount)) { //preLPCount slider
			int val = this.preLPCount.getValue();
			this.preLPCountValLbl.setText("" + val);
			this.settings.preExplosionLPIters = val;
		}
		else if (source.equals(this.speedFactor)) { //speedFactor slider
			double pos = (double)this.speedFactor.getValue() / 
			(double)this.speedFactor.getMaximum();
			float val = (float)(this.speedFactorMinMax[0] + (pos *
					(this.speedFactorMinMax[1] - this.speedFactorMinMax[0])));
			this.speedFactorValLbl.setText(String.format("%.2f", val));
			this.settings.finalSpeedFactor = val;
		}
		else if (source.equals(this.reverbEarlyRefls)) { //reverbEarly slider
			int val = this.reverbEarlyRefls.getValue();
			this.reverbEarlyReflsValLbl.setText("" + val);
			this.settings.reverbEarlyRefls = val;
		}
		else if (source.equals(this.reverbLateRefls)) { //reverbLate slider
			int val = this.reverbLateRefls.getValue();
			this.reverbLateReflsValLbl.setText("" + val);
			this.settings.reverbLateRefls = val;
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
			//this is ugly... I'm fairly new to Java concurrency
			Callable<Sound> libCall = new Callable<Sound>() {
				@Override
				public Sound call() throws Exception {
					LibExplodomatica.cancel = false;
					LibExplodomatica.explodomaticaProgress = 0.001F;
					return LibExplodomatica.explodomatica(settings);
				}
			};
			FutureTask<Sound> task = new FutureTask<Sound>(libCall);
			//get that task going and watch the progress variable
			LibExplodomatica.explodomaticaProgress = 0.0F;
			float prevProg = LibExplodomatica.explodomaticaProgress;
			Thread taskThread = new Thread(task);
			taskThread.start();
			while (!task.isDone() && !this.isCancelled()) {
				if (LibExplodomatica.explodomaticaProgress > prevProg) {
					prevProg = LibExplodomatica.explodomaticaProgress;
					int progress = (int)(prevProg * 100);
					this.setProgress(progress);
				}
				Thread.sleep(20);
			}
			//stop the lib call if this worker was canceled
			if (this.isCancelled()) {
				LibExplodomatica.cancel = true;
				LibExplodomatica.explodomaticaProgress = 0.0F;
			}
			return task.isCancelled() ? null : task.get();
		}
		
		@Override
		protected void done() {
			//this is executed on the event dispatch thread
			//reset gui crap
			setEnabledAll(true);
			cancel.setEnabled(false);
			play.setEnabled(false);
			save.setEnabled(false);
			if (inputFileName == null || inputFileName.length() == 0) {
				useInput.setEnabled(false);
			}
			progressBar.setValue(0);
			//and grab the sound if we got one
			if (!this.isCancelled()) {
				try {
					Sound tmp = this.get();
					if (tmp != null) {
						currentSound = tmp;
						if (currentClip == null) {
							currentClip = AudioSystem.getClip();
						}
						currentClip.close();
						currentClip.open(
								LibExplodomatica.convertToPCMStream(tmp, 1));
						play.setEnabled(true);
						save.setEnabled(true);
					}
				}
				catch (InterruptedException e) { } 
				catch (ExecutionException e) {
					System.err.println("LibExplodomatica crashed");
				} catch (LineUnavailableException e) {
					System.err.println("Failed to open sound clip");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			//only enable playing and saving if we have a sound
			if (currentSound != null) {
				play.setEnabled(true);
				save.setEnabled(true);
			}
			workerDone = true;
			worker = null;
		}
		
	}
	
	//this is how the worker communicates to the gui about progress
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (!workerDone && worker != null) {
			int tmp = this.worker.getProgress();
			this.progressBar.setValue(tmp);
		}
		else {
			this.progressBar.setValue(0);
		}
	}
	
	public static void main(String[] args) {
		LibExplodomatica.gui = true;
		JExplodomatica jexplodomatica = new JExplodomatica();
		jexplodomatica.show();
	}
	
}
