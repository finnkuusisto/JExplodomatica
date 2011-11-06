/* 
    (C) Copyright 2011, Stephen M. Cameron,
                        Finn C. Kuusisto (Java port)

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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class LibExplodomatica {

	public static final int SAMPLERATE = 44100;
	
	//these are only used by the GUI
	//hack... can't have multiple threads running this at the same time
	public static volatile float explodomaticaProgress = 0.0F;
	public static volatile boolean gui = false;
	public static volatile boolean cancel = false;

	public static int secondsToFrames(double seconds) {
		//this is actually seconds to samples, but they're the same for PCM
		return (int)(seconds * SAMPLERATE);
	}

	public static int explodomaticaSaveFile(String filename, Sound s,
			int channels) {
		AudioInputStream audioStream = LibExplodomatica.convertToPCMStream(s,
				channels);
		//write the audio stream as a wav
		File outFile = new File(filename);
		try {
			AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, outFile);
		}
		catch (IOException e) {
			System.err.println("Cannot open " + filename);
			e.printStackTrace();
			return -1;
		}
		System.out.println("Saved output in \'" + filename + "\'");
		return 0;
	}
	
	public static AudioInputStream convertToPCMStream(Sound s,
			int channels) {
		//convert to bytes for 16-bit signed PCM
		byte[] bytes = new byte[s.nSamples * 2];
		for (int i = 0; i < s.nSamples; i++) {
			short val = (short)(s.data[i] * Short.MAX_VALUE);
			bytes[i * 2] = (byte)((val >> 8) & 0xFF); //left byte
			bytes[(i * 2) + 1] = (byte)(val & 0xFF); //right byte
		}
		ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
		//16-bit, 1 channel, big-endian, signed PCM
		AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
				LibExplodomatica.SAMPLERATE, 16, channels, (2 * channels),
				LibExplodomatica.SAMPLERATE, true);
		return new AudioInputStream(byteStream, format, s.nSamples);
	}

	public static Sound addSound(Sound s1, Sound s2) {
		int n = s1.nSamples;
		if (s2.nSamples > n) {
			n = s2.nSamples;
		}

		Sound s = new Sound();
		s.data = new double[n];
		s.nSamples = 0;

		for (int i = 0; i < n; i++) {
			s.data[i] = 0.0;
			if (i < s1.nSamples) {
				s.data[i] += s1.data[i];
			}
			if (i < s2.nSamples) {
				s.data[i] += s2.data[i];
			}
			s.nSamples++;
		}
		return s;
	}

	public static void accumulateSound(Sound acc, Sound inc) {
		Sound t = LibExplodomatica.addSound(acc, inc);
		acc.data = t.data;
		acc.nSamples = t.nSamples;
	}

	public static void amplifyInPlace(Sound s, double gain) {
		for (int i = 0; i < s.nSamples; i++) {
			s.data[i] = s.data[i] * gain;
			if (s.data[i] > 1.0) {
				s.data[i] = 1.0;
			}
			if (s.data[i] < -1.0) {
				s.data[i] = -1.0;
			}
		}
	}

	public static Sound makeNoise(ExplosionDef e, int nSamples) {
		Sound s = new Sound();
		s.data = new double[nSamples];
		//if there is input data, use that rather than generating noise
		if (e.inputData != null) {
			int n = nSamples;
			if (e.inputSamples < n) {
				n = (int)e.inputSamples;
			}
			System.arraycopy(e.inputData, 0, s.data, 0, n);
			s.nSamples = nSamples;
		}
		else { //generate noise
			for (int i = 0; i < nSamples; i++) {
				s.data[i] = (2.0 * Math.random()) - 1.0;
				s.nSamples++;
			}
			LibExplodomatica.amplifyInPlace(s, 0.70);
		}
		return s;
	}

	public static void fadeout(Sound s, int nSamples) {
		for (int i = 0; i < nSamples; i++) {
			s.data[i] *= 1.0 - ((double)i / (double)nSamples);	
		}
	}	

	//algorithm for low pass filter gleaned from wikipedia
	//and adapted for stereo samples
	public static Sound slidingLowPass(Sound s,	double alpha1, double alpha2) {
		Sound o = new Sound();
		o.data = new double[s.nSamples];
		o.data[0] = s.data[0];
		for (int i = 1; i < s.nSamples;) {
			double alpha = (((double)i / (double)s.nSamples) *
					(alpha2 - alpha1)) + alpha1;
			alpha = alpha * alpha;
			o.data[i] = o.data[i - 1] + (alpha * (s.data[i] - o.data[i - 1]));
			i++;
		}
		o.nSamples = s.nSamples;
		return o;
	}

	public static void slidingLowPassInPlace(Sound s, double alpha1,
			double alpha2) {
		Sound o = slidingLowPass(s, alpha1, alpha2);
		s.data = o.data;
		s.nSamples = o.nSamples;
	}

	public static double interpolate(double x, double x1, double y1,
			double x2, double y2) {
		//return corresponding y on line x1,y1,x2,y2 for value x
		//(y2 -y1)/(x2 - x1) = (y - y1) / (x - x1)     by similar triangles.
		//(x -x1) * (y2 -y1)/(x2 -x1) = y - y1         a little algebra...
		//y = (x - x1) * (y2 - y1) / (x2 -x1) + y1;
		if (Math.abs(x2 - x1) < (0.01 / (double)LibExplodomatica.SAMPLERATE)) {
			return (y1 + y2) / 2.0;
		}
		return (x - x1) * (y2 - y1) / (x2 - x1) + y1;
	}

	public static Sound changeSpeed(Sound s, double factor) {
		int nSamples = (int)(s.nSamples / factor);
		Sound o = new Sound();
		o.data = new double[nSamples];
		o.data[0] = s.data[0];
		o.nSamples = 1;
		for (int i = 1; i < nSamples; i++) {
			double samplePoint = ((double)i / (double)nSamples) * s.nSamples;
			int sp1 = (int)samplePoint;
			int sp2 = sp1 + 1;
			if (sp2 >= s.nSamples) {
				sp2 = sp1;
			}
			o.data[i] = interpolate(samplePoint, sp1, s.data[sp1],
					sp2, s.data[sp2]);
			o.nSamples++;
		}
		return o;
	}

	public static void changeSpeedInPlace(Sound s, double factor) {
		Sound o = changeSpeed(s, factor);
		s.data = o.data;
		s.nSamples = o.nSamples;
	}

	public static Sound copySound(Sound s) {
		Sound o = new Sound();
		o.data = new double[s.nSamples];
		System.arraycopy(s.data, 0, o.data, 0, s.nSamples);
		o.nSamples = s.nSamples;
		return o;
	}

	public static void renormalize(Sound s) {
		double max = 0.0;
		for (int i = 0; i < s.nSamples; i++) {
			if (Math.abs(s.data[i]) > max) {
				max = Math.abs(s.data[i]);
			}
		}
		for (int i = 0; i < s.nSamples; i++) {
			s.data[i] = s.data[i] / (1.05 * max);
		}
	}

	public static void delayEffectInPlace(Sound s, int delaySamples) {
		for (int i = s.nSamples - 1; i >= 0; i--) {
			int source = i - delaySamples;
			if (s.nSamples > source) {
				if (source > 0) {
					s.data[i] = s.data[source];
				}
				else {
					s.data[i] = 0.0;
				}
			}
		}
	}

	public static void dot() {
		System.out.print(".");
		System.out.flush();
	}

	public static void updateProgress(float progressInc) {
		if (LibExplodomatica.explodomaticaProgress == 0.0F) {
			return;
		}
		LibExplodomatica.explodomaticaProgress += progressInc;
		if (LibExplodomatica.explodomaticaProgress > 1.05) {
			LibExplodomatica.explodomaticaProgress = 0.0F;
		}
	}

	public static Sound poorMansReverb(Sound s, int earlyRefls,
			int lateRefls) {
		float progressInc = 1.0F / (float)(earlyRefls + lateRefls);
		System.out.print("Calculating poor man's reverb");
		System.out.flush();
		Sound withVerb = new Sound();
		withVerb.data = new double[s.nSamples * 2];
		for (int i = 0; i < s.nSamples; i++) {
			withVerb.data[i] = s.data[i];
		}
		LibExplodomatica.dot();
		withVerb.nSamples = s.nSamples * 2;
		//GUI cancel//
		if (LibExplodomatica.gui && LibExplodomatica.cancel) { 
			return null;
		}
		//////////////
		Sound echo = LibExplodomatica.copySound(withVerb);
		for (int i = 0; i < earlyRefls; i++) {
			//GUI cancel//
			if (LibExplodomatica.gui && LibExplodomatica.cancel) {
				return null;
			}
			//////////////
			LibExplodomatica.dot();
			Sound echo2 = LibExplodomatica.slidingLowPass(echo, 0.5, 0.5);
			double gain = (Math.random() * 0.03) + 0.03;
			LibExplodomatica.amplifyInPlace(echo, gain); 
			//300 ms range
			int delay = 
				(int)(0.3 * LibExplodomatica.SAMPLERATE * Math.random());
			LibExplodomatica.delayEffectInPlace(echo2, delay);
			LibExplodomatica.accumulateSound(withVerb, echo2);
			LibExplodomatica.updateProgress(progressInc);
		}
		for (int i = 0; i < lateRefls; i++) {
			//GUI cancel//
			if (LibExplodomatica.gui && LibExplodomatica.cancel) {
				return null;
			}
			//////////////
			LibExplodomatica.dot();
			Sound echo2 = LibExplodomatica.slidingLowPass(echo, 0.5, 0.2);
			double gain = (Math.random() * 0.01) + 0.03;
			LibExplodomatica.amplifyInPlace(echo, gain); 
			//2000 ms range
			int delay = 
				(int)(2 * LibExplodomatica.SAMPLERATE * Math.random());
			LibExplodomatica.delayEffectInPlace(echo2, delay);
			LibExplodomatica.accumulateSound(withVerb, echo2);
			LibExplodomatica.updateProgress(progressInc);
		}
		System.out.println("done");
		return withVerb;
	}

	public static Sound makeExplosion(ExplosionDef e, double seconds,
			int nLayers) {
		Sound[] s = new Sound[10];
		for (int i = 0; i < nLayers; i++) {
			Sound t = LibExplodomatica.makeNoise(e,
					LibExplodomatica.secondsToFrames(seconds));
			if (i > 0) {
				LibExplodomatica.changeSpeedInPlace(t, i * 2);
			}

			int iters = i + 1;
			if (iters > 3) {
				iters = 3;
			}
			for (int j = 0; j < iters; j++) {
				LibExplodomatica.fadeout(t, t.nSamples);
			}

			double a1 = (double)(i + 1) / (double)nLayers;
			double a2 = (double)i / (double) nLayers;
			iters = 3 - i; 
			if (iters < 0) {
				iters = 1;	
			}
			for (int j = 0; j < iters; j++) {
				LibExplodomatica.slidingLowPassInPlace(t, a1, a2);
				LibExplodomatica.renormalize(t);
			}
			s[i] = t;
		}

		for (int i = 1; i < nLayers; i++) {
			LibExplodomatica.accumulateSound(s[0], s[i]);
			s[i] = null;
		}
		LibExplodomatica.renormalize(s[0]);
		return s[0];
	}

	public static void trimTrailingSilence(Sound s) {
		for (int i = s.nSamples - 1; i >= 0; i--) {
			if (Math.abs(s.data[i]) < 0.001) {
				s.nSamples--;
			}
		}
	}

	public static Sound makePreExplosions(ExplosionDef e) {
		if (e.preExplosions <= 0) {
			return null;
		}
		Sound pe = new Sound();
		pe.data = new double[LibExplodomatica.secondsToFrames(e.duration)];
		pe.nSamples = LibExplodomatica.secondsToFrames(e.duration);
		for (int i = 0; i < e.preExplosions; i++) {
			Sound exp = makeExplosion(e, e.duration / 2, e.nLayers);
			int offset = (int)(Math.random() *
					LibExplodomatica.secondsToFrames(e.preExplosionDelay));
			LibExplodomatica.delayEffectInPlace(exp, offset);
			LibExplodomatica.accumulateSound(pe, exp);
			LibExplodomatica.renormalize(pe);
		}
		for (int i = 0 ; i < e.preExplosionLPIters; i++) {
			LibExplodomatica.slidingLowPassInPlace(pe,
					e.preExplosionLPFactor,
					e.preExplosionLPFactor);
		}
		LibExplodomatica.renormalize(pe);
		return pe;
	}

	public static Sound readInputFile(String filename) {		
		//desired format:
		//16-bit, 1 channel, big-endian, signed PCM
		AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
				LibExplodomatica.SAMPLERATE, 16, 1, 2,
				LibExplodomatica.SAMPLERATE, true);
		//get the input file stream
		File inFile = new File(filename);
		AudioInputStream input = null;
		try {
			input = AudioSystem.getAudioInputStream(inFile);
		}
		catch (UnsupportedAudioFileException e) {
			System.err.println("Unsupported input audio file " + filename);
			return null;
		}
		catch (IOException e) {
			System.err.println("Cannot open input audio file " + filename);
			return null;
		}
		//see if we can convert it to the version we want
		if (!AudioSystem.isConversionSupported(format, input.getFormat())) {
			//with any luck, this will be uncommon
			System.err.println("Cannot convert input audio format\n" + 
					input.getFormat().toString());
			return null;
		}
		//convert if we can, and read in the bytes
		input = AudioSystem.getAudioInputStream(format, input);
		List<Double> vals = new ArrayList<Double>();
		byte[] sample = {0, 0};
		boolean done = false;
		while (!done) {
			int numRead = -1;
			try {
				numRead = input.read(sample);
			}
			catch (IOException e) {
				System.err.println("Error reading input audio stream");
			}
			if (numRead < 2) { //no more samples
				done = true;
			}
			else { //convert the sample
				short val = (short)(sample[0] << 8);
				val |= sample[1];
				double toAdd = (double)val / (double)Short.MAX_VALUE;
				if (toAdd < -1.0) { //could happen
					toAdd = -1.0;
				}
				if (toAdd > 1.0) { //shouldn't happen
					toAdd = 1.0;
				}
				vals.add(toAdd);
			}
		}
		Sound ret = new Sound();
		ret.data = new double[vals.size()];
		ret.nSamples = vals.size();
		for (int i = 0; i < ret.nSamples; i++) {
			ret.data[i] = vals.get(i);
		}
		return ret;
	}

	public static Sound explodomatica(ExplosionDef e) {
		//GUI cancel//
		if (LibExplodomatica.gui && LibExplodomatica.cancel) { 
			return null;
		}
		//////////////
		if (e.inputFileName != null && e.inputFileName.length() != 0) {
			Sound tmp = LibExplodomatica.readInputFile(e.inputFileName);
			if (tmp == null) {
				return null;
			}
			e.inputData = tmp.data;
			e.inputSamples = tmp.nSamples;
		}
		if (!e.reverb && LibExplodomatica.gui) {
			LibExplodomatica.explodomaticaProgress = 0.2F;
		}
		
		//GUI cancel//
		if (LibExplodomatica.gui && LibExplodomatica.cancel) { 
			return null;
		}
		//////////////
		Sound pe = LibExplodomatica.makePreExplosions(e);
		if (!e.reverb && LibExplodomatica.gui) {
			LibExplodomatica.explodomaticaProgress = 0.33F;
		}

		//GUI cancel//
		if (LibExplodomatica.gui && LibExplodomatica.cancel) { 
			return null;
		}
		//////////////
		Sound s = LibExplodomatica.makeExplosion(e, e.duration, e.nLayers);
		if (!e.reverb && LibExplodomatica.gui) {
			LibExplodomatica.explodomaticaProgress = 0.5F;
		}
		
		//GUI cancel//
		if (LibExplodomatica.gui && LibExplodomatica.cancel) { 
			return null;
		}
		//////////////
		if (pe != null) {
			LibExplodomatica.accumulateSound(s, pe);
			LibExplodomatica.renormalize(s);
		}
		if (!e.reverb && LibExplodomatica.gui) {
			LibExplodomatica.explodomaticaProgress = 0.8F;
		}
		
		//GUI cancel//
		if (LibExplodomatica.gui && LibExplodomatica.cancel) { 
			return null;
		}
		//////////////
		LibExplodomatica.changeSpeedInPlace(s, e.finalSpeedFactor);
		LibExplodomatica.trimTrailingSilence(s);
		Sound s2 = null;
		//GUI cancel//
		if (LibExplodomatica.gui && LibExplodomatica.cancel) { 
			return null;
		}
		//////////////
		if (e.reverb) {
			s2 = LibExplodomatica.poorMansReverb(s, e.reverbEarlyRefls,
					e.reverbLateRefls);
			//GUI cancel//
			if (LibExplodomatica.gui && LibExplodomatica.cancel) { 
				return null;
			}
			//////////////
			LibExplodomatica.trimTrailingSilence(s2);
		}
		else {
			s2 = LibExplodomatica.copySound(s);
			if (!e.reverb && LibExplodomatica.gui) {
				LibExplodomatica.explodomaticaProgress = 0.9F;
			}
		}

		//GUI cancel//
		if (LibExplodomatica.gui && LibExplodomatica.cancel) { 
			return null;
		}
		//////////////
		if (e.saveFileName.length() != 0) {
			LibExplodomatica.explodomaticaSaveFile(e.saveFileName, s2, 1);
		}

		if (LibExplodomatica.gui) {
			LibExplodomatica.explodomaticaProgress = 1.0F;
		}
		//GUI cancel//
		if (LibExplodomatica.gui && LibExplodomatica.cancel) { 
			return null;
		}
		//////////////
		return s2;
	}

}
