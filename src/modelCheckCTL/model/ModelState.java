package modelCheckCTL.model;

import java.util.ArrayList;
import java.util.List;

public class ModelState {

	public List<String> atomsList = new ArrayList<>();
	public String stateName;
	
	public ModelState() {
		
	}
	
	public ModelState(String stateName) {
		this.stateName = stateName;
	}
	
	@Override
	public boolean equals(Object state) {
		ModelState ms = (ModelState)state;
		return this.stateName.equals(ms.stateName);
	}

}