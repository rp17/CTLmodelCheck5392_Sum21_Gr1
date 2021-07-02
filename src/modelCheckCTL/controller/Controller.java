package modelCheckCTL.controller;

import modelCheckCTL.view.View;

public class Controller {

	private View view;

	public Controller() {
		view = new View(this);
	}

	public static void main(String[] args) {
		Controller control = new Controller();

	}
}