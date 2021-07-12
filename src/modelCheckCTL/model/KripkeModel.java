package modelCheckCTL.model;

import java.util.ArrayList;
import java.util.List;

import modelCheckCTL.util.Constants;

public class KripkeModel {

	public ArrayList<ModelTransition> transList = new ArrayList<>();
	public ArrayList<ModelState> stateList = new ArrayList<>();
	public ArrayList<String> atomsList = new ArrayList<>();
	public String fileName;

	public KripkeModel(String definition) throws Exception {

		try {

			String[] parsedDefs = definition.replaceAll(Constants.NEW_LINE_CHAR, Constants.EMPTY_CHAR)
					.replaceAll(Constants.TAB_CHAR, Constants.EMPTY_CHAR).split(Constants.STAN_SEPARATER);
			if (parsedDefs.length < 3)
				throw new Exception("Invalid model description.");
			String[] states = parsedDefs[0].replaceAll(Constants.SPACE_CHAR, Constants.EMPTY_CHAR)
					.split(Constants.VALUE_SEPARATER);
			String[] transitions = parsedDefs[1].replaceAll(Constants.SPACE_CHAR, Constants.EMPTY_CHAR)
					.split(Constants.VALUE_SEPARATER);
			String[] atoms = parsedDefs[2].split(Constants.VALUE_SEPARATER);

			loadStates(states);
			loadTransitions(transitions);
			loadAtoms(atoms);

		} catch (Exception ex) {

			throw new Exception(ex.getMessage());
		}

	}
	
	public KripkeModel clone(){
		KripkeModel R = null;
		try {
			R = new KripkeModel(this.kFileGet());
			R.transList = new ArrayList<>();
			for(ModelTransition t : this.transList)
				R.transList.add(t);
			R.stateList = new ArrayList<>();
			for(ModelState t : this.stateList)
				R.stateList.add(t);
			R.atomsList = new ArrayList<>();
			for(String t : this.atomsList)
				R.atomsList.add(t);
		
			
		} catch (Exception e) {
			
			System.out.println("exception" + e);;
		}
		
		//for(ModelState s : R.stateList)
		//	s.stateName += "(clone)";
	
		System.out.println(R.stateList.size());
		
		return R;
	}

	public KripkeModel join(KripkeModel K) {
		for(ModelState s : this.stateList) {
			System.out.println(s.stateName);
			
		}
		for(ModelTransition t : this.transList) {
			System.out.println(t.transitionName);
			
		}
		
		KripkeModel R = this.clone();
		KripkeModel k = K.clone();
		
		for(ModelState ks : k.stateList) {
			for(ModelState s : R.stateList) {
				ModelTransition t = new ModelTransition(s,ks);
				t.transitionName += s.stateName + " -t- " + ks.stateName;
				ModelTransition tR = new ModelTransition(ks,s);
				tR.transitionName += ks.stateName + " -t- " + s.stateName;
				
				R.transList.add(t);
				R.transList.add(tR);
				
			}
			
		}
		for(ModelState ks : k.stateList) {
			R.stateList.add(ks);
			
		}
		System.out.println("------------------------------------------------------------------------------------------------------------");
		
		for(ModelState s : R.stateList) {
			System.out.println(s.stateName);
			
		}
		for(ModelTransition t : R.transList) {
			System.out.println(t.transitionName);
			
		}
		
		
		return R;
		
	}
	
	public String kFileGet() {
		return this.fileName;
	}
	public void kFileSet(String s) {
		this.fileName = s;
	}
	
	private void loadStates(String[] states) throws Exception {

		for (String state : states) {
			ModelState st = new ModelState();

			st.stateName = state.replaceAll("[^a-zA-Z0-9]", "");

			if (!stateList.contains(st)) {
				stateList.add(st);
				// state.
				// System.out.println("State = -"+st.stateName+"-");
			} else
				throw new Exception("State " + state + " repeated");
		}

	}

	private void loadTransitions(String[] transitions) throws Exception {

		for (String transition : transitions) {
			String[] parsedTrans = transition.split(Constants.VALUE_COL_SEPARATER);
			if (parsedTrans == null || parsedTrans.length != 2)
				throw new Exception("Invalid transition definition");

			String transitionName = parsedTrans[0];
			String[] fromToStates = parsedTrans[1].split(Constants.TRANSITION_SEPARATER);
			if (fromToStates.length != 2)
				throw new Exception("Invalid from state and to state description for transition : " + transitionName);
			if(!stateList.contains(new ModelState(fromToStates[0])))
				throw new Exception("Invalid from state : "+ fromToStates[0] + " defined for transition : " + transitionName);
			if(!stateList.contains(new ModelState(fromToStates[1])))
				throw new Exception("Invalid to state : "+ fromToStates[1] + " defined for transition : " + transitionName);

			ModelState fromState = stateList.stream().filter(x -> x.stateName.equals(fromToStates[0])).findFirst()
					.get();
			ModelState toState = stateList.stream().filter(x -> x.stateName.equals(fromToStates[1])).findFirst().get();

			if (fromState == null || toState == null)
				throw new Exception("Invalid transition definition for : " + transitionName);

			ModelTransition trans = new ModelTransition(fromState, toState, transitionName);
			if (!transList.contains(trans))
				transList.add(trans);
			else
				throw new Exception("Duplicate transitions defined for : " + transitionName);

		}

	}

	private void loadAtoms(String[] atoms) throws Exception {

		for (String atom : atoms) {
			String[] parsedAtom = atom.split(Constants.VALUE_COL_SEPARATER);
			if (parsedAtom == null || parsedAtom.length != 2)
				throw new Exception("Invalid atoms definition");

			String stateName = parsedAtom[0].trim();
			String[] atomList = parsedAtom[1].trim().split(Constants.SPACE_CHAR);

			List<String> stateAtoms = new ArrayList<>();

			for (String a : atomList) {
				if (!a.isEmpty()) {
					if (!stateAtoms.contains(a))
						stateAtoms.add(a);
					else
						throw new Exception("Atoms repeated for state : " + stateName);
					if (!atomsList.contains(a))
						atomsList.add(a);
				}
				if(!stateList.contains(new ModelState(stateName)))
					throw new Exception("Invalid state : " + stateName + " in atom labels");

				ModelState state = stateList.stream().filter(x -> x.stateName.equals(stateName)).findFirst().get();
				state.atomsList = stateAtoms;
			}
		}

	}

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("\t\t\t\rKRIPKE MODEL \n");
		sb.append("Atoms :");
		sb.append(this.atomsList);
		sb.append("\n\n");
		sb.append("States with labels: \n");
		this.stateList.forEach(x -> sb.append(x.stateName).append(x.atomsList).append("\n"));
		sb.append("\n\n");
		sb.append("Transitions : \n");
		this.transList.forEach(x -> sb.append(x.transitionName).append("(").append(x.fromState.stateName).append("-->")
				.append(x.toState.stateName).append(")\n"));
		sb.append("\n");

		return sb.toString();
	}

}
