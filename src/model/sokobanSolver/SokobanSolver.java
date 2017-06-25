package model.sokobanSolver;

import java.util.ArrayList;
import java.util.List;

import db.Level;
import model.sokobanSolver.plannable.Move;
import model.sokobanSolver.plannable.PlannableLevel;
import search.Action;
import strips.PlanAction;
import strips.Planner;
import strips.Strips;

public class SokobanSolver {
	
	public List<Action> solveLevel(Level level){
		
		PlannableLevel p=new PlannableLevel(level);
		
		//define the max times running the strips
		Strips.count=0;
		Strips.max=10;
		Planner st=new Strips();
		
		List<PlanAction> list=st.plan(p);
		List<Action>result=new ArrayList<>();
		if(list!=null){
			for(PlanAction a:list){
				for(Action act:((Move)a).getSearchResult())
					result.add(act);
			}
		}
		else
			return null;
		
		return result;
	}

}
