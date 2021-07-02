package modelCheckCTL.util;

import java.util.Map;
import java.util.Map.Entry;

import modelCheckCTL.model.KripkeModel;

public class ModelVerifierUtils {

	public static String getFormulaType(ExpressionUtils ex, KripkeModel kripkeModel) {

		String expression = ex.expression;

		expression = formatBrackets(expression);
		ex.expression = expression;

		if (expression.contains(">") && isBinaryOperater(ex, ">"))
			return Constants.IMPLIES;
		if (expression.contains("&") && isBinaryOperater(ex, "&"))
			return Constants.AND;
		if (expression.contains("|") && isBinaryOperater(ex, "|"))
			return Constants.OR;
		if (expression.startsWith("A(")) {
			ex.expression = ex.expression.substring(2, ex.expression.length() - 1);
			if (isBinaryOperater(ex, "U"))
				return Constants.AU;
		}
		if (expression.startsWith("E(")) {

			ex.expression = ex.expression.substring(2, ex.expression.length() - 1);
			if (isBinaryOperater(ex, "U"))
				return Constants.EU;

		}
		if (expression.equals("T")) {
			ex.leftExpr = expression;
			return Constants.ALLTRUE;
		}
		if (expression.equals("F")) {
			ex.leftExpr = expression;
			return Constants.ALLFALSE;
		}
		if (isAtomicFormula(expression, kripkeModel)) {
			ex.leftExpr = expression;
			return Constants.ATOMIC;
		}
		if (expression.startsWith("!")) {
			ex.leftExpr = expression.substring(1, expression.length());
			return Constants.NOT;
		}
		if (expression.startsWith("AX")) {
			ex.leftExpr = expression.substring(2, expression.length());
			return Constants.AX;
		}
		if (expression.startsWith("EX")) {
			ex.leftExpr = expression.substring(2, expression.length());
			return Constants.EX;
		}
		if (expression.startsWith("EF")) {
			ex.leftExpr = expression.substring(2, expression.length());
			return Constants.EF;
		}
		if (expression.startsWith("EG")) {
			ex.leftExpr = expression.substring(2, expression.length());
			return Constants.EG;
		}
		if (expression.startsWith("AF")) {
			ex.leftExpr = expression.substring(2, expression.length());
			return Constants.AF;
		}
		if (expression.startsWith("AG")) {
			ex.leftExpr = expression.substring(2, expression.length());
			return Constants.AG;
		}
		return "";
	}

	private static boolean isBinaryOperater(ExpressionUtils ex, String symbol) {

		boolean isBinary = false;
		if (ex.expression.contains(symbol)) {
			int openParen = 0;
			int closeParen = 0;

			for (int i = 0; i < ex.expression.length(); i++) {
				String currChar = ex.expression.substring(i, i + 1);
				if (currChar.equals(symbol) && openParen == closeParen) {
					ex.leftExpr = ex.expression.substring(0, i);
					ex.rightExpr = ex.expression.substring(i + 1, ex.expression.length());
					isBinary = true;
					break;
				} else if (currChar.equals("("))
					openParen++;
				else if (currChar.equals(")"))
					closeParen++;
			}
		}

		return isBinary;
	}

	private static boolean isAtomicFormula(String expression, KripkeModel kripkeModel) {
		if (kripkeModel.atomsList.contains(expression))
			return true;
		return false;
	}

	private static String formatBrackets(String expression) {
		String resultExpr = expression;
		int openParen = 0;
		int closedParen = 0;
		if (expression.startsWith("(") && expression.endsWith(")")) {
			for (int i = 0; i < expression.length() - 1; i++) {
				char charExp = expression.charAt(i);
				if (charExp == '(')
					openParen++;
				if (charExp == ')')
					closedParen++;
			}

			if (openParen - 1 == closedParen)
				resultExpr = expression.substring(1, expression.length() - 1);
		}
		return resultExpr;
	}

	public static void loadConverstionMap(Map<String, String> converstionMap) {
		converstionMap.put("and", "&");
		converstionMap.put("or", "|");
		converstionMap.put("->", ">");
		converstionMap.put("not", "!");
		converstionMap.put(" ", "");
	}

	public static String convertToCTLFormula(String expression, Map<String, String> converstionMap) {

		for (Entry<String, String> entry : converstionMap.entrySet()) {
			expression = expression.replace(entry.getKey(), entry.getValue());
		}

		return expression;
	}
}
