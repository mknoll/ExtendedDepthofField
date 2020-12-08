package EDF_headless;

import edf.Tools;
import edfgui.ExtendedDepthOfField_headless;
import edfgui.Parameters;
import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.io.Opener;

//https://github.com/PoburkoLab/ExtendedDepthofField

public class EDF_runHeadless {
	
	public static void main(String[] args) {
		/*
		 * Open file
		 */
		Opener opener = new Opener();  
		String imageFilePath = "/home/mknoll/Downloads/samples/confocal-series.tif";
		ImagePlus imp = opener.openImage(imageFilePath);
		//imp.show();
		
		
		/*
		 * Extract information from image and extract parameters
		 */
		// Color or grayscale image
		boolean color = false;
		if (imp.getType() == ImagePlus.COLOR_RGB)
			color = true;
		else if (imp.getType() == ImagePlus.GRAY8)
			color = false;
		else if (imp.getType() == ImagePlus.GRAY16)
			color = false;
		else if (imp.getType() == ImagePlus.GRAY32)
			color = false;
		else {
			IJ.error("Only process 8-bits, 16-bits, 32-bits and RGB images");
			return;
		}
		//getParameters();
		Parameters parameters = new Parameters();
		parameters.color = color;
		parameters.showTopology = true;
		int[] nScalesAndSize = Tools.computeScaleAndPowerTwoSize(imp.getWidth(), imp.getHeight()); 
		int nScales = nScalesAndSize[0];
		parameters.maxScales = nScales;
		
		parameters.show3dView = true; 
		parameters.outputColorMap = color ? Parameters.COLOR_RGB : Parameters.GRAYSCALE;
		parameters.colorConversionMethod = 0;		
		// value range: 0-4; 
		parameters.setQualitySettings(1);
		parameters.setTopologySettings(1);
		//parameters.showTopology = true; 	
		parameters.showTopology = false; 

		if (imp.getType() == ImagePlus.COLOR_RGB) {
			parameters.outputColorMap = Parameters.COLOR_RGB;
		}
		
		
		//Process
		//ExtendedDepthOfField edf = new ExtendedDepthOfField(imp, parameters);
		//edf.process(); 
		//System.gc();
		
		// headless
		ExtendedDepthOfField_headless edf = new ExtendedDepthOfField_headless(imp, parameters);
		edf.process(); 
		System.gc();
		
		// extract data
		ImagePlus img1 = edf.getComposite();		
		
		//Save as tiff 
		FileSaver fs = new FileSaver(img1);
		fs.saveAsTiff("/tmp/img1.tif");
	}
}
