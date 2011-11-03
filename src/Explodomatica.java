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
public class Explodomatica {

	private static void usage() {
		System.err.printf("usage:\n");
		System.err.printf("Explodomatica [options] somefile.wav\n");
		System.err.printf("caution: somefile.wav will be overwritten.\n");
		System.err.printf("options:\n");
		System.err.printf("  --duration n    Specifies duration of explosion in secs\n");
		System.err.printf("                  Default value is %f secs\n",
				ExplosionDef.explodomaticaDefaults.duration);
		System.err.printf("  --nlayers n     Specifies number of sound layers to use\n");
		System.err.printf("                  to build up each explosion.  Default is %d\n", ExplosionDef.explodomaticaDefaults.nLayers);
		System.err.printf("  --preexplosions n\n");
		System.err.printf("                  Specifies number of 'pre-explostions' to generate\n");
		System.err.printf("                  Default is %d\n", ExplosionDef.explodomaticaDefaults.preExplosions);
		System.err.printf("  --pre-delay n\n");
		System.err.printf("                  Specifies approximate length of the 'ka' in 'ka-BOOM!'\n");
		System.err.printf("                  (it is somewhat randomized)\n");
		System.err.printf("                  Default is %f secs\n", ExplosionDef.explodomaticaDefaults.preExplosionDelay);
		System.err.printf("  --pre-lp-factor n\n");
		System.err.printf("                  Specifies the impact of the low pass filter used\n");
		System.err.printf("                  on the pre-explosion part of the sound.  values\n");
		System.err.printf("                  closer to zero lower the cutoff frequency\n");
		System.err.printf("                  while values close to one raise it.\n");
		System.err.printf("                  Value should be between 0.2 and 0.9.\n");
		System.err.printf("                  Default is %f\n", ExplosionDef.explodomaticaDefaults.preExplosionLowPassFactor);
		System.err.printf("  --pre-lp-count n\n");
		System.err.printf("                  Specifies the number of times the low pass filter used\n");
		System.err.printf("                  on the pre-explosion part of the sound.  values\n");

		System.err.printf("                  Default is %d\n", ExplosionDef.explodomaticaDefaults.preExplosionLPIters);
		
		System.err.printf("  --speedfactor n\n");
		System.err.printf("                  Amount to speed up (or slow down) the final\n");
		System.err.printf("                  explosion sound. Values greater than 1.0 speed\n");
		System.err.printf("                  the sound up, values less than 1.0 slow it down\n");
		System.err.printf("                  Default is %f\n", ExplosionDef.explodomaticaDefaults.finalSpeedFactor);
		System.err.printf("  --noreverb      Suppress the 'reverb' effect\n");
		System.err.printf("  --input file    Use the given (44100Hz mono) wav file\n" +
				"                  as input instead of generating white noise for input.\n");
		System.exit(1);
	}

	private static void processOptions(String[] args, ExplosionDef e) {
		//parse options
		String[] options = {
				"--duration",
				"--nlayers",
				"--preexplosions",
				"--speedfactor",
				"--pre-delay",
				"--pre-lp-factor",
				"--pre-lp-count",
				"--noreverb",
				"--input"
		};
		//yeah... this is really, really ugly
		int argIndex = 0;
		while (argIndex < args.length - 1) {
			String opt = args[argIndex];
			int optIndex = Explodomatica.indexOf(options, opt);
			switch (optIndex) {
				case 0: //duration
					argIndex++;
					if (argIndex < args.length - 1 &&
							Explodomatica.validDouble(args[argIndex])) {
						e.duration = Double.parseDouble(args[argIndex]);
						argIndex++;
					}
					else {
						Explodomatica.usage();
					}
					break;
				case 1: //nlayers
					argIndex++;
					if (argIndex < args.length - 1 &&
							Explodomatica.validInt(args[argIndex])) {
						e.nLayers = Integer.parseInt(args[argIndex]);
						argIndex++;
					}
					else {
						Explodomatica.usage();
					}
					break;
				case 2: //preexplosions
					argIndex++;
					if (argIndex < args.length - 1 &&
							Explodomatica.validInt(args[argIndex])) {
						e.preExplosions = Integer.parseInt(args[argIndex]);
						argIndex++;
					}
					else {
						Explodomatica.usage();
					}
					break;
				case 3: //speedfactor
					argIndex++;
					if (argIndex < args.length - 1 &&
							Explodomatica.validDouble(args[argIndex])) {
						e.finalSpeedFactor =
							Double.parseDouble(args[argIndex]);
						argIndex++;
					}
					else {
						Explodomatica.usage();	
					}
					break;
				case 4: //pre-delay
					argIndex++;
					if (argIndex < args.length - 1 &&
							Explodomatica.validDouble(args[argIndex])) {
						e.preExplosionDelay =
							Double.parseDouble(args[argIndex]);
						argIndex++;
					}
					else {
						Explodomatica.usage();
					}
					break;
				case 5: //pre-lp-factor
					argIndex++;
					if (argIndex < args.length - 1 &&
							Explodomatica.validDouble(args[argIndex])) {
						e.preExplosionLowPassFactor =
							Double.parseDouble(args[argIndex]);
						argIndex++;
					}
					else {
						Explodomatica.usage();
					}
					break;
				case 6: //pre-lp-count
					argIndex++;
					if (argIndex < args.length - 1 &&
							Explodomatica.validInt(args[argIndex])) {
						e.preExplosionLPIters =
							Integer.parseInt(args[argIndex]);
						argIndex++;
					}
					else {
						Explodomatica.usage();
					}
					break;
				case 7: //noreverb
					e.reverb = false;
					argIndex++;
					break;
				case 8: //input
					argIndex++;
					if (argIndex < args.length - 1) {
						e.inputFileName = args[argIndex];
						argIndex++;
					}
					else {
						Explodomatica.usage();
					}
					break;
				default:
					Explodomatica.usage();
					break;
			}
		}
		//set the output filename
		if (Explodomatica.indexOf(options, args[args.length - 1]) != -1) {
			Explodomatica.usage();
		}
		e.saveFileName = args[args.length - 1];
		System.out.println("Save filename is \'" + e.saveFileName + "\'");
	}
	
	private static boolean validInt(String str) {
		int val = -1;
		try {
			val = Integer.parseInt(str);
		}
		catch (NumberFormatException e) {
			return false;
		}
		return val >= 0;
	}
	
	private static boolean validDouble(String str) {
		double val = -1.0;
		try {
			val = Double.parseDouble(str);
		}
		catch (NumberFormatException e) {
			return false;
		}
		return val >= 0.0;
	}
	
	private static int indexOf(String[] arr, String str) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].equals(str)) {
				return i;
			}
		}
		return -1;
	}

	public static void main(String[] args) {
		ExplosionDef e = ExplosionDef.explodomaticaDefaults;
		if (args.length < 1) {
			Explodomatica.usage();
		}
		Explodomatica.processOptions(args, e);
		LibExplodomatica.explodomatica(e);
	}

}
