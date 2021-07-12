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
	
	public void setState(String stateName) throws Exception {
		state = new ModelState(stateName);
		if(!kripkeModel.stateList.contains(state))
			throw new Exception("Invalid state selected");
	}
	
	public boolean verifyFormula() throws Exception {
		ModelVerifier verifier = new ModelVerifier(expression, state, kripkeModel);
		List<ModelState> states = verifier.sat(verifier.expression);
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
	

	
	
	private static Model loadModel(File file) {
		
		Model model = null;
		try (FileInputStream fstream = new FileInputStream(file)) {

			BufferedReader br = new BufferedReader(new InputStreamReader(fstream, "UTF-8"));

			String strLine;
			StringBuffer sb = new StringBuffer();

			while ((strLine = br.readLine()) != null) {
				sb.append(strLine);
			}
			String modelinput = sb.toString();
			model = new Model(modelinput);
			//model.getKripkeModel().stateList.forEach(x -> stateSelector.addItem(x.stateName));
			//filecontent.append(model.getKripkeModel().toString());
			//results.setText("");
			fstream.close();
			model.kripkeModel.kFileSet(modelinput);
			
		} catch (Exception e) {
			System.out.println(("Exception while reading input file : " + e.getMessage()));
		}
		return model;
		
	}
	
	public static void main(String[] args) throws IOException {
		
		
		BufferedReader f = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter kripke file name");
		String kripke = f.readLine();
		System.out.println("Enter starting state");
		String starting = f.readLine();
		System.out.println("Enter CTL formula");
		String formula = f.readLine();
		
		//System.out.println(kripke+starting+formula);
		
		try {
			File file = new File(kripke);
			Model model = Model.loadModel(file);
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