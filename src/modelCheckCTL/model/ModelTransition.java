package modelCheckCTL.model;

public class ModelTransition {

	String transitionName;
	ModelState fromState;
	ModelState toState;

	public ModelTransition(ModelState fromState, ModelState toState, String transitionName) {
		this.transitionName = transitionName;
		this.fromState = fromState;
		this.toState = toState;
	}
	
	public ModelTransition(ModelState fromState, ModelState toState) {
		this.transitionName = "";
		this.fromState = fromState;
		this.toState = toState;
	}

	@Override
	public boolean equals(Object o) {
		ModelTransition transition = (ModelTransition)o;
		return this.transitionName.equals(transition.transitionName)
				|| (this.fromState.equals(transition.fromState) && this.toState.equals(transition.toState));
	}
}