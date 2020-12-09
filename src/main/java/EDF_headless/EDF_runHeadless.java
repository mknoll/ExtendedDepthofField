package EDF_headless;

import edf.Tools;
import edfgui.ExtendedDepthOfField_headless;
import edfgui.Parameters;
import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.io.Opener;

public class EDF_runHeadless {
	//Image of interest
	private ImagePlus imp = null;
	//TODO use File 
	private String outFile = null;

	private Parameters parameters = null;
	private String parameterFile = null;

	private ExtendedDepthOfField_headless edf = null;

	/**
	 * Constructor
	 * 
	 * @param filePath Path of image stack file to load
	 * @param parameterPath Path of parameter file for EDF
	 */
	public EDF_runHeadless(String filePath, String parameterFile, String outFile) {
		//load file
		Opener opener = new Opener();
		imp = opener.openImage(filePath);

		//validate xml parameter file
		boolean valid = ParseParameters.validateXMLSchema(parameterFile);
		this.parameterFile = parameterFile;
		System.out.println("Parameter xml valid: " + valid);

		//obtain parameters
		// from xml file
		readParameters();
		// from image
		parameters.color = getColorMode();
		parameters.outputColorMap = getColorMode() ? Parameters.COLOR_RGB : Parameters.GRAYSCALE;
		if (imp.getType() == ImagePlus.COLOR_RGB) {
			parameters.outputColorMap = Parameters.COLOR_RGB;
		}
		int[] nScalesAndSize = Tools.computeScaleAndPowerTwoSize(imp.getWidth(), imp.getHeight()); 
		int nScales = nScalesAndSize[0];
		parameters.maxScales = nScales;
		
		//where to store the result image
		this.outFile = outFile;
	}

	private void readParameters() {
		ParseParameters pp = new ParseParameters(parameterFile);
		parameters = pp.getParameters();
	}

	private boolean getColorMode() {
		//TODO: exception handling
		boolean color = false;
		if (imp.getType() == ImagePlus.COLOR_RGB)
			color = true;
		else if (imp.getType() == ImagePlus.GRAY8 || 
				imp.getType() == ImagePlus.GRAY16 || 
				imp.getType() == ImagePlus.GRAY32) {
			color = false;
		} else {
			IJ.error("Only process 8-bits, 16-bits, 32-bits and RGB images");
		}
		return color;
	}

	public void run() {
		edf = new ExtendedDepthOfField_headless(imp, parameters);
		edf.process(); 
		System.gc();
	}

	public void save() {
		ImagePlus out = edf.getComposite();
		//add metadata from xml
		out.setProperty("Info", "ABC"); //TODO
		
		FileSaver fs = new FileSaver(out);
		fs.saveAsTiff(outFile);
	}

	public static void main(String[] args) {	
		//params: infile, parameter file, outfile		
		EDF_runHeadless erh = new EDF_runHeadless(args[0], args[1], args[2]);
		erh.run();
		erh.save();
	}

}
