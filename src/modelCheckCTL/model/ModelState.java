package modelCheckCTL.model;

import java.util.ArrayList;
import java.util.List;

public class ModelState {

	public List<String> atomsList = new ArrayList<>();
	public String stateName;
	public boolean visitable;
	public ArrayList<ModelState> Children = new ArrayList<>();
	public ArrayList<ModelState> Parents = new ArrayList<>();
	public int originalK;
	public KripkeModel originalModel;
	
	public boolean visited;
	
	public ModelState() {
		
	}		
	
	
	public ModelState(String stateName) {
		this.stateName = stateName;
		visitable = false;
		visited = false;
	}
	
	@Override
	public boolean equals(Object state) {
		ModelState ms = (ModelState)state;
		return this.stateName.equals(ms.stateName);
	}

}