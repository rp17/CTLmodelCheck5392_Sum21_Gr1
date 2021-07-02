package modelCheckCTL.model;

import java.util.List;

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

	
}