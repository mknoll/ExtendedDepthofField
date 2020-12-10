package EDF_headless;

import java.io.IOException;

import org.xml.sax.SAXException;

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
	private String outFile = null;

	private ParseParameters pp = null;
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
		//validate xml parameter file
		try {
			ParseParameters.validateXMLSchema(parameterFile);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			System.err.println("Malformed parameter xml file or illegal values!");
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Xml file ("+parameterFile+") not found!");
			e.printStackTrace();
			System.exit(1);
		}
		this.parameterFile = parameterFile;

		//load file
		Opener opener = new Opener();
		imp = opener.openImage(filePath);
		if (imp == null) {
			//File could not be opened
			System.err.println("File " + filePath + " could not be opened!");
			System.exit(1);
		}
		
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
		pp = new ParseParameters(parameterFile);
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
		String prev = (String) out.getProperty("Info");
		if (prev == null) {
			prev = pp.getParamString();
		} else {
			prev = prev + ";" + pp.getParamString();
		}
		out.setProperty("Info", prev); 
		FileSaver fs = new FileSaver(out);
		fs.saveAsTiff(outFile);
	}

	public static void main(String[] args) {	
		if (args.length != 3) {
			System.err.println("Usage: <infile.tiff> <parameter.xml> <outfile.tiff>");
			System.exit(0);
		} 
		
		EDF_runHeadless erh = new EDF_runHeadless(args[0], args[1], args[2]);
		erh.run();
		erh.save();
	}

}
