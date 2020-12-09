import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import EDF_headless.ParseParameters;

public class XMLParameterFile {
	@Test
	void validXMLEasy_1() {
		//assertEquals();
		String parameterFile = ParseParameters.class.getClassLoader().getResource("validEasy_1.xml").getFile();
		assertTrue(ParseParameters.validateXMLSchema(parameterFile));
	}
	
	@Test
	void invalidXMLEasy_1() {
		//assertEquals();
		String parameterFile = ParseParameters.class.getClassLoader().getResource("invalidEasy_1.xml").getFile();
		assertFalse(ParseParameters.validateXMLSchema(parameterFile));
	}
}
