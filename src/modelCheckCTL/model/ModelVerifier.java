package modelCheckCTL.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelCheckCTL.util.Constants;
import modelCheckCTL.util.ExpressionUtils;
import modelCheckCTL.util.ModelVerifierUtils;

public class ModelVerifier {

	public static boolean firstTime = true;
	public static boolean viablePathway = false;
	private KripkeModel kripkeModel;
	public String expression;
	private Map<String, String> converstionMap;

	public ModelVerifier(String expression, ModelState modelState, KripkeModel kripkeModel) {

		converstionMap = new HashMap<>();
		ModelVerifierUtils.loadConverstionMap(converstionMap);
		this.kripkeModel = kripkeModel;
		this.expression = ModelVerifierUtils.convertToCTLFormula(expression, converstionMap);
	}

	public List<ModelState> sat(String expression) throws Exception {

		List<ModelState> statesList = new ArrayList<>();
		ExpressionUtils ex = new ExpressionUtils(expression);
		String satType = ModelVerifierUtils.getFormulaType(ex, this.kripkeModel);
		expression = ex.expression;
		String leftExpr = ex.leftExpr;
		String rightExpr = ex.rightExpr;
		System.out.println("satType :" + satType);
		switch (satType) {
		case Constants.ALLTRUE:
			statesList.addAll(kripkeModel.stateList);
			break;
		case Constants.ALLFALSE:
			break;
		case Constants.ATOMIC:
			if (!kripkeModel.atomsList.contains(leftExpr))
				throw new Exception("Ivalid atom present in the formula");
			for (ModelState state : kripkeModel.stateList) {
				if (state.atomsList.contains(leftExpr))
					statesList.add(state);
			}
			break;
		case Constants.NOT:
			statesList.addAll(kripkeModel.stateList);
			for (ModelState state : sat(leftExpr)) {
				if (statesList.contains(state))
					statesList.remove(state);
			}
			break;
		case Constants.AND:
			List<ModelState> andf1List = sat(leftExpr);
			List<ModelState> andf2List = sat(rightExpr);
			for (ModelState state : andf1List) {
				if (andf2List.contains(state))
					statesList.add(state);
			}
			break;
		case Constants.OR:
			List<ModelState> orf1List = sat(leftExpr);
			List<ModelState> orf2List = sat(rightExpr);
			statesList = orf1List;
			for (ModelState state : orf2List) {
				if (!statesList.contains(state))
					statesList.add(state);
			}
			break;
		case Constants.IMPLIES:
			String impliesFormula = "!" + leftExpr + "|" + rightExpr;
			statesList = sat(impliesFormula);
			break;
		case Constants.AX:
			String axFormula = "!" + "EX" + "!" + leftExpr;
			statesList = sat(axFormula);
			List<ModelState> tempList = new ArrayList<>();
			for (ModelState state : statesList) {
				for (ModelTransition trans : kripkeModel.transList) {
					if (state.equals(trans.fromState)) {
						tempList.add(state);
						break;
					}
				}
			}
			statesList = tempList;
			break;

		case Constants.EX:
			statesList = satEX(leftExpr);
			break;
		case Constants.AU:
			StringBuilder sb = new StringBuilder();
			String auFormula = sb.append("!(E(!").append(rightExpr).append("U(!").append(leftExpr).append("&!")
					.append(rightExpr).append("))|(EG!").append(rightExpr).append("))").toString();
			statesList = sat(auFormula);
			break;
		case Constants.EU:
			System.out.println("aaaa");
			statesList = satEU(leftExpr, rightExpr);
			break;
		case Constants.EF:
			statesList = sat("E(TU" + leftExpr + ")");
			break;
		case Constants.EG:
			statesList = sat("!AF!" + leftExpr);
			break;
		case Constants.AF:
			statesList = satAF(leftExpr);
			break;
		case Constants.AG:
			statesList = sat("!EF!" + leftExpr);
			break;
		default:
			throw new IllegalArgumentException("Invalid formula ");
		}
		return statesList;
	}

	private List<ModelState> satAF(String expression) throws Exception {
		List<ModelState> tempList = new ArrayList<>();
		List<ModelState> result = new ArrayList<>();
		tempList.addAll(kripkeModel.stateList);
		result = sat(expression);
		while (!(tempList.size() == result.size() && tempList.containsAll(result))) {
			tempList = result;
			List<ModelState> newTemp = new ArrayList<>();
			List<ModelState> preAStates = preA(result);
			newTemp.addAll(result);
			for (ModelState state : preAStates) {
				if (!newTemp.contains(state))
					newTemp.add(state);
			}
			result = newTemp;
		}

		return result;
	}

	private List<ModelState> preA(List<ModelState> result) {

		List<ModelState> preEYStates = preE(result, "");
		List<ModelState> diffList = new ArrayList<>();
		diffList.addAll(kripkeModel.stateList);
		diffList.removeAll(result);
		List<ModelState> preEDiffList = preE(diffList, "");
		preEYStates.removeAll(preEDiffList);
		return preEYStates;
	}

	private List<ModelState> preE(List<ModelState> result, String CTLF) {
		
		List<ModelState> states = new ArrayList<>();
		
		if(firstTime) {
			for(ModelState s : result) {
				for(ModelState p : s.Parents)
					p.visitable = true;
				s.visited = true;
				states.add(s);
				
			}
			
		}
		
		for (ModelState fromState : kripkeModel.stateList) {
			for (ModelState toState : result) {
				ModelTransition trans = new ModelTransition(fromState, toState);
				//System.out.println(fromState.stateName + " " + toState.stateName + " " + fromState.visitable + " " + (toState.originalK == fromState.originalK));
				if (kripkeModel.transList.contains(trans)) {
					if (!states.contains(fromState)) {
							if(toState.originalK == fromState.originalK && fromState.visitable) {
								states.add(fromState);
								fromState.visited = true;
						}
							else {
								//System.out.println("papdw");
									viablePathway = false;
									DFSparents(toState, 0, CTLF);
									if(viablePathway) {
										states.add(fromState);
										fromState.visited = true;
										viablePathway = false;
									}
								
								}
								
							}
						}	
					
				}
		}
	System.out.println("----S----");
		for (ModelState s : states) {
			System.out.print(s.stateName + " ");
			for (ModelState sp : s.Parents) {
				sp.visitable = true;
			//	System.out.println(sp.stateName);
			}
			
		}
		System.out.println();
		return states;
	}
	
	public void DFSparents(ModelState s, int c, String prop) {
		//System.out.println(s.stateName + " " + s.originalModel.root.stateName);
		if(prop.length() == 0) {
		if(s.equals(s.originalModel.root))
			viablePathway = true;
		}
		else {
			if(s.equals(s.originalModel.root) && s.originalModel.root.atomsList.contains(prop))
				viablePathway = true;
			
		}
		
		if(c > s.originalModel.stateList.size() + 1)
			return;
		if(prop.length() == 0) {
			for(ModelState f : s.Parents) {
				//System.out.println(s.stateName + " " + f.stateName + f.visited);
				if(f.visited)
					DFSparents(f, c+1, prop);
			}
		}
		else {
			for(ModelState f : s.Parents) {
				//System.out.println(s.stateName + " " + f.stateName + f.visited);
				if(f.visited && f.atomsList.contains(prop))
					DFSparents(f, c+1, prop);
			}
			
		}
				
			
		
	}

	private List<ModelState> satEX(String expression) throws Exception {

		List<ModelState> x = new ArrayList<>();
		List<ModelState> y = new ArrayList<>();
		x = sat(expression);
		y = preE(x, "");
		return y;
	}

	private List<ModelState> satEU(String leftExpr, String rightExpr) {

		List<ModelState> w = new ArrayList<>();
		List<ModelState> x = new ArrayList<>();
		List<ModelState> y = new ArrayList<>();

		try {
			w = sat(leftExpr);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			
		}
		x.addAll(kripkeModel.stateList);
		try {
			y = sat(rightExpr);
		} catch (Exception e1) {
			System.out.println("Y!" );
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (!(x.size() == y.size() && x.containsAll(y))) {
			x = y;
			List<ModelState> newY = new ArrayList<>();
			List<ModelState> preEStates = preE(y, leftExpr);
			newY.addAll(y);
			List<ModelState> wAndPreE = new ArrayList<>();
			for (ModelState state : w) {
				if (preEStates.contains(state))
					wAndPreE.add(state);
			}
			for (ModelState state : wAndPreE) {
				if (!newY.contains(state))
					newY.add(state);
			}
			y = newY;
		}
		/*System.out.println("y");
		for(ModelState s : y)
			System.out.println(s.stateName);*/
		return y;
	}


}
