package modelCheckCTL.view;

import modelCheckCTL.controller.Controller;
import modelCheckCTL.model.Model;

public class View {
	private Controller controller;
	private MainGui gui;
	
	public View(Controller c)
	{
		controller = c;
		gui = new MainGui();
	}
}
