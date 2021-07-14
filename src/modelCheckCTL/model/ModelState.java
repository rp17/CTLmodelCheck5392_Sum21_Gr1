package modelCheckCTL.model;

import java.util.ArrayList;
import java.util.List;

public class ModelState {

	public List<String> atomsList = new ArrayList<>();
	public String stateName;
	public boolean visitable;
	public ArrayList<ModelState> Children = new ArrayList<>();
	public ArrayList<ModelState> Parents = new ArrayList<>();
	
	public ModelState() {
		
	}
	
	public ModelState(String stateName) {
		this.stateName = stateName;
		visitable = false;
		Children = new ArrayList<>();
		Parents = new ArrayList<>();
	}
	
	@Override
	public boolean equals(Object state) {
		ModelState ms = (ModelState)state;
		return this.stateName.equals(ms.stateName);
	}

}