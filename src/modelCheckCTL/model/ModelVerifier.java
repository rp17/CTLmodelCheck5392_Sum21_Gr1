package modelCheckCTL.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelCheckCTL.util.Constants;
import modelCheckCTL.util.ExpressionUtils;
import modelCheckCTL.util.ModelVerifierUtils;

public class ModelVerifier {

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

		List<ModelState> preEYStates = preE(result);
		List<ModelState> diffList = new ArrayList<>();
		diffList.addAll(kripkeModel.stateList);
		diffList.removeAll(result);
		List<ModelState> preEDiffList = preE(diffList);
		preEYStates.removeAll(preEDiffList);
		return preEYStates;
	}

	private List<ModelState> preE(List<ModelState> result) {

		List<ModelState> states = new ArrayList<>();
		for (ModelState fromState : kripkeModel.stateList) {
			for (ModelState toState : result) {
				ModelTransition trans = new ModelTransition(fromState, toState);
				if (kripkeModel.transList.contains(trans)) {
					if (!states.contains(fromState))
						//if (fromState.visitable) {
							states.add(fromState);
							
					
				}
			}
		}
		/*for (ModelState s : states) {
			for (ModelState sp : s.Parents)
				sp.visitable = true;
			
		}*/
		return states;
	}

	private List<ModelState> satEX(String expression) throws Exception {

		List<ModelState> x = new ArrayList<>();
		List<ModelState> y = new ArrayList<>();
		x = sat(expression);
		y = preE(x);
		return y;
	}

	private List<ModelState> satEU(String leftExpr, String rightExpr) throws Exception {

		List<ModelState> w = new ArrayList<>();
		List<ModelState> x = new ArrayList<>();
		List<ModelState> y = new ArrayList<>();

		w = sat(leftExpr);
		x.addAll(kripkeModel.stateList);
		y = sat(rightExpr);

		while (!(x.size() == y.size() && x.containsAll(y))) {
			x = y;
			List<ModelState> newY = new ArrayList<>();
			List<ModelState> preEStates = preE(y);
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
		return y;
	}


}
