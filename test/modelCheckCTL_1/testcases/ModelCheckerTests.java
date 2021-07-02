package modelCheckCTL_1.testcases;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Assert;
import org.junit.Test;

import modelCheckCTL.model.Model;
import modelCheckCTL.util.Constants;

public class ModelCheckerTests {

	private static final String BASE_PATH = "test-files/";

	@Test
	public void testModel1() throws IOException, Exception {

		String fileContent = getFileContent(BASE_PATH + "Model 1.txt");

		boolean allCasesCheck = testAllFormulas(fileContent, BASE_PATH + "Model 1 - Test Formulas.txt");
		
		Assert.assertTrue(allCasesCheck);

	}
	
	@Test
	public void testModel2() throws IOException, Exception {

		String fileContent = getFileContent(BASE_PATH + "Model 2.txt");

		boolean allCasesCheck = testAllFormulas(fileContent, BASE_PATH + "Model 2 - Test Formulas.txt");
		
		Assert.assertTrue(allCasesCheck);

	}
	
	@Test
	public void testModel3() throws IOException, Exception {

		String fileContent = getFileContent(BASE_PATH + "Model 3.txt");

		boolean allCasesCheck = testAllFormulas(fileContent, BASE_PATH + "Model 3 - Test Formulas.txt");
		
		Assert.assertTrue(allCasesCheck);

	}
	
	@Test
	public void testModel4() throws IOException, Exception {

		String fileContent = getFileContent(BASE_PATH + "Model 4.txt");

		boolean allCasesCheck = testAllFormulas(fileContent, BASE_PATH + "Model 4 - Test Formulas.txt");
		
		Assert.assertTrue(allCasesCheck);

	}
	
	@Test
	public void testModel5() throws IOException, Exception {

		String fileContent = getFileContent(BASE_PATH + "Model 5.txt");

		boolean allCasesCheck = testAllFormulas(fileContent, BASE_PATH + "Model 5 - Test Formulas.txt");
		
		Assert.assertTrue(allCasesCheck);

	}
	
	@Test
	public void testModel6() throws IOException, Exception {

		String fileContent = getFileContent(BASE_PATH + "Model 6.txt");

		boolean allCasesCheck = testAllFormulas(fileContent, BASE_PATH + "Model 6 - Test Formulas.txt");
		
		Assert.assertTrue(allCasesCheck);

	}
	
	@Test
	public void testModel7() throws IOException, Exception {

		String fileContent = getFileContent(BASE_PATH + "Model 7.txt");

		boolean allCasesCheck = testAllFormulas(fileContent, BASE_PATH + "Model 7 - Test Formulas.txt");
		
		Assert.assertTrue(allCasesCheck);

	}
	
	private boolean testAllFormulas(String fileContent, String fileName) throws IOException, Exception {

		FileInputStream fstream = new FileInputStream(fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		boolean allFormulaCheck = true;

		String strLine;

		while ((strLine = br.readLine()) != null && allFormulaCheck) {
			String[] values = strLine.trim().replaceAll(Constants.SPECIAL_CHAR, Constants.EMPTY_CHAR).split(";");
			String checkedState = values[0];
			String expression = values[1];
			Model model = new Model(fileContent);
			model.setExpression(expression);
			model.setState(checkedState);
			allFormulaCheck = (model.verifyFormula()== Boolean.parseBoolean(values[2].toLowerCase()));

		}

		fstream.close();
		return allFormulaCheck;
	}

	public String getFileContent(String file) {

		StringBuffer sb = new StringBuffer();
		try (FileInputStream fstream = new FileInputStream(file)) {
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream, "UTF-8"));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				sb.append(strLine);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

}
