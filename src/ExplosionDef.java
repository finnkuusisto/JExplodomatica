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
public class ExplosionDef {
	
	public static ExplosionDef explodomaticaDefaults = 
		new ExplosionDef(
			"",
			"",
			null,
			0L,
			4.0,	//duration in seconds (roughly)
			4,		//nlayers
			1,		//preexplosions
			0.25,	//preexplosion delay, 250ms
			0.8,	//preexplosion low pass factor
			1,		//preexplosion low pass iters
			0.45,	//final speed factor
			10,		//final reverb early reflections
			50,		//final reverb late reflections
			true	//reverb wanted?
		);
	
	public ExplosionDef() {}
	
	public ExplosionDef(String saveFileName, String inputFileName,
			double[] inputData, long inputSamples, double duration,
			int nLayers, int preExplosions, double preExplosionDelay,
			double preExplosionLPFactor, int preExplosionLPIters,
			double finalSpeedFactor, int reverbEarlyRefls, int reverbLateRefls,
			boolean reverb) {
		this.saveFileName = saveFileName;
		this.inputFileName = inputFileName;
		this.inputData = inputData;
		this.inputSamples = inputSamples;
		this.duration = duration;
		this.nLayers = nLayers;
		this.preExplosions = preExplosions;
		this.preExplosionDelay = preExplosionDelay;
		this.preExplosionLPFactor = preExplosionLPFactor;
		this.preExplosionLPIters = preExplosionLPIters;
		this.finalSpeedFactor = finalSpeedFactor;
		this.reverbEarlyRefls = reverbEarlyRefls;
		this.reverbLateRefls = reverbLateRefls;
		this.reverb = reverb;
	}

	public String saveFileName;
	public String inputFileName;
	public double[] inputData;
	public long inputSamples;
	public double duration;
	public int nLayers;
	public int preExplosions;
	public double preExplosionDelay;
	public double preExplosionLPFactor;
	public int preExplosionLPIters;
	public double finalSpeedFactor;
	public int reverbEarlyRefls;
	public int reverbLateRefls;
	public boolean reverb;
	
}
