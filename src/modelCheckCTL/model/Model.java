package modelCheckCTL.model;

import java.io.*;
import java.util.*;

public class Model {

	private String expression;
	private ModelState state;
	private KripkeModel kripkeModel;
	
	public Model(String kripkeString) throws Exception {
		kripkeModel = new KripkeModel(kripkeString);
	}
	public Model() throws Exception {
		
	}
	
	public void setKripke(KripkeModel k) throws Exception {
		kripkeModel = k;
	}
	
	public void setState(String stateName) throws Exception {
		state = new ModelState(stateName);
		if(!kripkeModel.stateList.contains(state))
			throw new Exception("Invalid state selected");
	}
	
	public boolean verifyFormula() throws Exception {
		ModelVerifier verifier = new ModelVerifier(expression, state, kripkeModel);
		List<ModelState> states = verifier.sat(verifier.expression);
		System.out.println("states satisfying " + verifier.expression);
		for (ModelState s : states) {
			System.out.println(s.stateName);
			
		}
		return states.contains(state);
	}
	
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public ModelState getState() {
		return state;
	}
	
	public KripkeModel getKripkeModel() {
		return kripkeModel;
	}
	public void setKripkeModel(KripkeModel kripkeModel) {
		this.kripkeModel = kripkeModel;
	}
	

	
	
	public static KripkeModel loadModel(File file) {
		
		KripkeModel model = null;
		try (FileInputStream fstream = new FileInputStream(file)) {

			BufferedReader br = new BufferedReader(new InputStreamReader(fstream, "UTF-8"));

			String strLine;
			StringBuffer sb = new StringBuffer();

			while ((strLine = br.readLine()) != null) {
				sb.append(strLine);
			}
			String modelinput = sb.toString();
			model = new KripkeModel(modelinput);
			//model.getKripkeModel().stateList.forEach(x -> stateSelector.addItem(x.stateName));
			//filecontent.append(model.getKripkeModel().toString());
			//results.setText("");
			fstream.close();
			
		} catch (Exception e) {
			System.out.println(("Exception while reading input file : " + e.getMessage()));
		}
		return model;
		
	}
	
	public static void main(String[] args) throws IOException {
		
		
		BufferedReader f = new BufferedReader(new InputStreamReader(System.in));
		

		//System.out.println("Enter kripke file name");
		//String kripke = f.readLine();
		System.out.println("Enter kripke file name (ctest1)");
		String kripkeC1 = f.readLine();
		System.out.println("Enter kripke file name (ctest2)");
		String kripkeC2 = f.readLine();	
		System.out.println("Enter starting state");
		String starting = f.readLine();
		System.out.println("Enter CTL formula");
		String formula = f.readLine();
		
		//System.out.println(kripke+starting+formula);
		
		try {
			File file1 = new File(kripkeC1);
			File file2 = new File(kripkeC2);
			KripkeModel k1 = loadModel(file1);
			k1.verfiyCP();
			KripkeModel k2 = loadModel(file2);
			k2.verfiyCP();
			Model model = new Model();
			KripkeModel kJoined = k1.join(k2);
			model.setKripke(kJoined);
			//model.kripkeModel.clone();
			model.kripkeModel.join(model.kripkeModel.clone());
			model.setState(starting);
			model.setExpression(formula);
			boolean res = model.verifyFormula();
			System.out.println("formula " + formula + " for state: " + starting + " is " + res);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	
			
		
		
	}
	
	

	
}