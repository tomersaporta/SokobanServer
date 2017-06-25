package model.sokobanSolver.plannable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

import db.Level;
import sokoElements.Box;
import sokoElements.Position;
import sokoElements.Target;
import model.sokobanSolver.searchable.SearchableBoxToTarget;
import search.Action;
import search.BFS;
import search.Solution;
import strips.Clause;
import strips.PlanAction;
import strips.Plannable;
import strips.Predicate;

public class PlannableLevel implements Plannable {

	private Level level;
	private HashMap<String, Box> boxesMap;
	private Random rand;
	private List<Target> listOfTargets;
	private List<Box> listOfBoxes;
	private char[][] unMoveableBoard;
	BFS<Position> search;

	public PlannableLevel(Level level) {
		this.level = level;
		this.boxesMap = new HashMap<>();
		this.rand = new Random();
		this.listOfTargets = level.getListTarget();
		this.listOfBoxes = level.getListBox();
		this.unMoveableBoard = generateUnMoveableBoard();
		this.search = new BFS<Position>();
	}

	@Override
	public Clause getGoal() {
	
		PriorityQueue<Target>pq=heuristicGoal();
		Clause goal = new Clause(null);
		Predicate predicate;
		Target t;
		
		Predicate sortedPred[]=new Predicate[listOfTargets.size()]; 
		int i=listOfTargets.size()-1;
		
		while(!pq.isEmpty()){
			t=pq.poll();
			predicate = new SokPredicate("boxAt", "?", t.getPosition().toString());
			goal.add(predicate);
			sortedPred[i--]=predicate;
		}
		goal.setSortedPred(sortedPred);
		return goal;
		
	}

	@Override
	public Clause getKnowledgebase() {

		Clause knowledgebase = new Clause(null);
		Predicate predicate;

		// add the player to the knowledgebase
		predicate = new SokPredicate("playerAt", "p1", level.getListPlayer().get(0).getPosition().toString());
		knowledgebase.add(predicate);

		for (Target t : listOfTargets) {
			predicate = new SokPredicate("clear", "?", t.getPosition().toString());
			knowledgebase.add(predicate);
		}

		int i = 0;
		for (Box b : listOfBoxes) {
			predicate = new SokPredicate("boxAt", "b" + i, b.getPosition().toString());
			knowledgebase.add(predicate);
			this.boxesMap.put("b" + i, b);
			i++;
		}

		return knowledgebase;
	}

	@Override
	public Set<PlanAction> getsatisfyingActions(Predicate top) {
		return null;
	}

	@Override
	public PlanAction getsatisfyingAction(Predicate top, Clause knowledgeBase) {

		this.search.initBFS();

		// toPosition
		String posValue = top.getValue();
		Position toPosition = generatePosition(posValue);

		int idx;
		String bid;
		Solution sol = null;
		int maxRounds=10;
		int count=0;
		do {
			count++;
			// fromPosition
			idx = rand.nextInt(listOfBoxes.size());
			bid = "b" + idx;

			Box chosenBox = boxesMap.get(bid);
			Position fromPosition = null;

			if (chosenBox != null) {
				fromPosition = chosenBox.getPosition();
				SearchableBoxToTarget searchableboxToTarget = new SearchableBoxToTarget(fromPosition, toPosition,generateBoredByKnowledgeBase(knowledgeBase));
				sol = search.search(searchableboxToTarget);
			}
		} while (sol == null&&count<=maxRounds);

		if (sol != null) {
			boxesMap.remove(bid);
			Move moveAction = new Move("Move", bid, toPosition.toString(), sol.getTheSolution());
			moveAction.setPreconditions(new Clause(new Predicate("clear", "?", toPosition.toString())));

			Position playerPos = getPlayerPos(sol.getTheSolution().get(sol.getTheSolution().size() - 1), toPosition);
			Clause effects = new Clause(new Predicate("boxAt", "b" + idx, toPosition.toString()),
					new Predicate("playerAt", "p1", playerPos.toString()));
			moveAction.setEffects(effects);
			moveAction.setDeleteEffects(new Clause(new Predicate("clear", "?", toPosition.toString())));
			return moveAction;
		}

		return null;
	}

	public char[][] generateBoredByKnowledgeBase(Clause knowledgeBase) {
		char[][] stateBored = copyBoard(unMoveableBoard);
		Position pos;

		for (Predicate p : knowledgeBase.getPredicates()) {
			if (p.getType() == "boxAt" && p.getId() != "?") {
				pos = generatePosition(p.getValue());
				stateBored[pos.getX()][pos.getY()] = '@';
			}
			if (p.getType() == "playerAt") {
				pos = generatePosition(p.getValue());
				stateBored[pos.getX()][pos.getY()] = 'A';
			}
		}
		/*
		 * for (int i = 0; i < stateBored.length; i++) { for (int j = 0; j <
		 * stateBored[0].length; j++) { System.out.print(stateBored[i][j]); }
		 * System.out.println(); }
		 */

		return stateBored;
	}

	public Position generatePosition(String pos) {
		String s = pos.replace("(", "");
		s = s.replace(")", "");
		String[] arr = s.split(",");
		int x = Integer.parseInt(arr[0]);
		int y = Integer.parseInt(arr[1]);

		return new Position(x, y);

	}

	public Position randomBox() {

		return null;

	}

	public char[][] generateUnMoveableBoard() {
		char[][] board = level.getLevelBored();
		char[][] newBoard = new char[board.length][board[0].length];

		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (board[i][j] == ' ' || board[i][j] == 'o' || board[i][j] == '#')
					newBoard[i][j] = board[i][j];
				else
					newBoard[i][j] = ' ';
			}
		}

		/*
		 * for (int i = 0; i < newBoard.length; i++) { for (int j = 0; j <
		 * newBoard[0].length; j++) { System.out.print(newBoard[i][j]); }
		 * System.out.println(); }
		 */
		return newBoard;

	}

	protected char[][] copyBoard(char[][] board) {

		char[][] newBoard = new char[board.length][board[0].length];
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				newBoard[i][j] = board[i][j];
			}
		}
		return newBoard;
	}

	private Position getPlayerPos(Action action, Position currPos) {
		if (action != null) {
			String act = action.getAction();
			if (act.equals("up"))
				return new Position(currPos.getX() + 1, currPos.getY());
			if (act.equals("down"))
				return new Position(currPos.getX() - 1, currPos.getY());
			if (act.equals("left"))
				return new Position(currPos.getX(), currPos.getY() + 1);
			if (act.equals("right"))
				return new Position(currPos.getX(), currPos.getY() - 1);
		}
		return null;
	}
	
	private PriorityQueue<Target> heuristicGoal(){
		
		PriorityQueue<Target>priorityQueue=new PriorityQueue<>(new Comparator<Target>() {

			@Override
			public int compare(Target o1, Target o2) {
				return getManhattanDistance(o1.getPosition())-getManhattanDistance(o2.getPosition());
			}
		});
		
		for(Target t:listOfTargets){
			priorityQueue.add(t);
		}
		
		
		return priorityQueue;
		
	}
	
	private int getManhattanDistance(Position pos){
		Position playerPos=level.getListPlayer().get(0).getPosition();
				
		int x=Math.abs(playerPos.getX()-pos.getX());
		int y=Math.abs(playerPos.getY()-pos.getY());
		//System.out.println("pos: "+playerPos+" posT: "+pos+" dist "+z);

		
		return x+y;
	}
	
}
