import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Move {
	public enum MoveClass {
		PHYSICAL, SPECIAL, STATUS;
	}

	private String name;
	private Type type;
	private int pp;
	private int power;
	private int accuracy;
	private int indexNum;
	private MoveClass moveClass;
	// TODO: more fields for special cases (enum on special case?)

	private static Move[][] allMoves;
	private static Map[] allMovesHashMap;

	// gen3 ctor
	public Move(String m_name, Type m_type, int m_pp, int m_power,
			int m_accuracy, int m_indexNum) {
		name = m_name;
		type = m_type;
		pp = m_pp;
		power = m_power;
		accuracy = m_accuracy;
		indexNum = m_indexNum;
		moveClass = Type.isPhysicalType(m_type) ? MoveClass.PHYSICAL
				: MoveClass.SPECIAL;
	}

	// gen4/5 ctor
	public Move(String m_name, Type m_type, int m_pp, int m_power,
			int m_accuracy, int m_indexNum, MoveClass m_class) {
		name = m_name;
		type = m_type;
		pp = m_pp;
		power = m_power;
		accuracy = m_accuracy;
		indexNum = m_indexNum;
		moveClass = m_class;
	}

	public Move(Move baseMove, int increaseStage) {
		name = baseMove.name + " " + increaseStage;
		type = baseMove.type;
		pp = baseMove.pp;
		power = baseMove.power;
		accuracy = baseMove.accuracy;
		indexNum = baseMove.indexNum;
		moveClass = baseMove.moveClass;
	}

	public Move(Move baseMove, String newName, int newPower) {
		name = newName;
		type = baseMove.type;
		pp = baseMove.pp;
		power = newPower;
		accuracy = baseMove.accuracy;
		indexNum = baseMove.indexNum;
		moveClass = baseMove.moveClass;
	}

	static {
		// strictly, gen4 has intra-gen move data variance
		// however this is only hypnosis's accuracy AFAIK
		// as accuracy isn't actually used anywhere, we can ignore it
		allMoves = new Move[3][];
		allMovesHashMap = new Map[3];
		for (int g = 0; g < 3; g++) {
			allMoves[g] = new Move[Constants.numMoves[g] + 1];
			Move m;
			BufferedReader in;
			try {
				in = new BufferedReader(new InputStreamReader(System.class
						.getResource("/resources/movedata" + (g + 3) + ".txt")
						.openStream()));
				String text;
				for (int i = 1; i <= Constants.numMoves[g]; i++) {
					text = in.readLine();
					String[] tokens = text.split("\t");
					String m_name = tokens[0];
					int m_accuracy = Integer.parseInt(tokens[1]);
					int m_power = Integer.parseInt(tokens[2]);
					int m_pp = Integer.parseInt(tokens[3]);
					Type m_type = toType(tokens[4]);
					if (g != 0) {
						MoveClass m_class = MoveClass.valueOf(tokens[5]
								.toUpperCase());
						m = new Move(m_name, m_type, m_pp, m_power, m_accuracy,
								i, m_class);
					} else {
						m = new Move(m_name, m_type, m_pp, m_power, m_accuracy,
								i);
					}
					allMoves[g][i] = m;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			allMovesHashMap[g] = new HashMap<String, Move>();
			for (Move m1 : allMoves[g]) {
				if (m1 != null) {
					allMovesHashMap[g]
							.put(Constants.hashName(m1.getName()), m1);
				}
			}
		}
		// TODO: put in special cases
	}

	private static Type toType(String t) {
		try {
			Type type = Type.valueOf(t.toUpperCase());
			if (type == null) {
				type = Type.NONE;
			}
			return type;
		} catch (IllegalArgumentException ex) {
			return Type.NONE;
		}
	}

	// returns the move object corresponding to the move with index i
	public static Move getMove(int i) {
		int g = Settings.game.generationIndex();
		if (i < 0 || i >= allMoves[g].length)
			return null;
		return allMoves[g][i];
	}

	public static Move getMoveByName(String name) {
		name = Constants.hashName(name);
		int g = Settings.game.generationIndex();
		if (!allMovesHashMap[g].containsKey(name))
			return null;
		return (Move) allMovesHashMap[g].get(name);
	}

	public String toString() {
		return String.format("%d %s %s PP: %d Power: %d Acc: %d", indexNum,
				name, type, pp, power, accuracy);
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	public int getPp() {
		return pp;
	}

	public int getPower() {
		return power;
	}

	public int getAccuracy() {
		return accuracy;
	}

	public int getIndexNum() {
		return indexNum;
	}

	// this will be different in gen 4+
	public boolean isPhysicalMove() {
		return moveClass == MoveClass.PHYSICAL;
	}

	// TODO: consider checking more
	public boolean isEqual(Object o) {
		return (o instanceof Move) && ((Move) o).indexNum == this.indexNum;
	}

	public int hashCode() {
		return indexNum;
	}
}
