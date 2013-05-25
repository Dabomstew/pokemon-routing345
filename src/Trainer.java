import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.ini4j.jdk14.edu.emory.mathcs.backport.java.util.Arrays;

//a trainer has a class name and some pokemon, corresponding to some location in memory
public class Trainer implements Battleable, Iterable<Pokemon> {
	private String name;
	private ArrayList<Pokemon> pokes;
	private int trainerNumber;
	private int nameValue;
	private boolean isFemale;
	private int battleType;

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Trainer)) {
			return false;
		} else {
			Trainer t = (Trainer) o;
			return t.name.equals(name) && t.trainerNumber == trainerNumber;
		}
	}

	@Override
	public void battle(Pokemon p, BattleOptions options, int participants) {
		List<Integer> order = options.getTrainerSendoutOrder();
		if (order == null) {
			for (Pokemon tp : pokes) {
				tp.battle(p, options, participants);
			}
		} else {
			for (Integer current : order) {
				pokes.get(current - 1).battle(p, options, participants);
			}
		}
	}

	@Override
	public Iterator<Pokemon> iterator() {
		return pokes.iterator();
	}

	public String toString() {
		return String.format("%s (%d: %s)", name, trainerNumber, allPokes());
	}

	public String allPokes() {
		StringBuilder sb = new StringBuilder();
		for (Pokemon p : pokes) {
			sb.append(p.levelName() + ", ");
		}
		return sb.toString();
	}

	private static HashMap<Integer, Trainer> allTrainers;

	public static Trainer getTrainer(int trainerNum) {
		if (!allTrainers.containsKey(trainerNum))
			return null;
		else
			return allTrainers.get(trainerNum);
	}

	// private static HashMap<String, Trainer> trainersByName;
	//
	// public static Trainer getTrainer(String name) {
	// return trainersByName.get(name);
	// }

	// must be called before any other calls are made
	public static void initTrainers() {
		allTrainers = new HashMap<Integer, Trainer>();
		// trainersByName = new HashMap<String, Trainer>();

		List<Trainer> trainerList = null;
		if (Settings.game == Game.DIAMOND || Settings.game == Game.PEARL)
			trainerList = getDataGen45("trainer_data_dp.txt");
		else if (Settings.game == Game.PLATINUM)
			trainerList = getDataGen45("trainer_data_pt.txt");
		else if (Settings.game == Game.HEARTGOLD
				|| Settings.game == Game.SOULSILVER)
			trainerList = getDataGen45("trainer_data_hgss.txt");
		else if (Settings.game == Game.FIRERED
				|| Settings.game == Game.LEAFGREEN)
			trainerList = getDataGen3("trainer_data_frlg.txt");
		else if (Settings.game == Game.RUBY || Settings.game == Game.SAPPHIRE)
			trainerList = getDataGen3("trainer_data_rs.txt");
		else
			trainerList = getDataGen3("trainer_data_e.txt");

		for (Trainer t : trainerList) {
			allTrainers.put(new Integer(t.trainerNumber), t);
			// if (t.name.equals("GRUNT") == false
			// && t.name.equals("EXECUTIVE") == false
			// && t.name.equals("?") == false
			// && trainersByName.containsKey(t.name) == false) {
			// trainersByName.put(t.name, t);
			// }
		}
	}

	// reads trainer_data.txt for gen4/5 to get trainer data
	private static List<Trainer> getDataGen45(String filename) {
		ArrayList<Trainer> trainers = new ArrayList<Trainer>();
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(System.class
					.getResource("/resources/" + filename).openStream()));

			Trainer t;
			while (in.ready()) {
				String text = in.readLine();
				if (text.trim().isEmpty()) {
					continue;
				}
				// Trainer line:
				// num|name|trainerClass|pokeDataType|#pokes|battleType
				t = new Trainer();
				String[] parts = text.split("\\|");
				t.trainerNumber = Integer.parseInt(parts[0]);
				t.name = parts[1];
				int trainerClass = Integer.parseInt(parts[2]);
				t.isFemale = determineIfFemale(trainerClass); // TODO enum per
																// trainer class
				int pokeDataType = Integer.parseInt(parts[3]);
				int numPokes = Integer.parseInt(parts[4]);
				t.battleType = Integer.parseInt(parts[5]);
				t.pokes = new ArrayList<Pokemon>();

				// the next few lines are pokemon lines
				for (int i = 0; i < numPokes; i++) {
					String pokeText = in.readLine();
					String[] pokeParts = pokeText.split("\\|");
					int level = Integer.parseInt(pokeParts[0]);
					Species s = Species.getSpeciesFromName(pokeParts[1]);
					int AI = Integer.parseInt(pokeParts[2]);
					// TODO: apply these overrides
					int overrides = Integer.parseInt(pokeParts[3]);
					int ivVal = 31 * AI / 255;
					IVs ivs = new IVs(ivVal);
					long PID = (calcPIDUpper(t.trainerNumber, AI, level,
							s.getPokedexNum(), trainerClass) << 8)
							+ (t.isFemale ? 0x78 : 0x88);
					Pokemon pk = null;
					// moveset not specified
					if (pokeDataType == 0) {
						pk = new Pokemon(s, level, ivs, Nature.getNature(PID));
					}
					// moveset not specified with hold item
					else if (pokeDataType == 2) {
						pk = new Pokemon(s, level, ivs, Nature.getNature(PID));
						Item item = Item
								.getItem(Integer.parseInt(pokeParts[4]));
						pk.setHoldItem(item);
					}
					// moveset specified
					else if (pokeDataType == 1) {
						int move1 = Integer.parseInt(pokeParts[4]);
						int move2 = Integer.parseInt(pokeParts[5]);
						int move3 = Integer.parseInt(pokeParts[6]);
						int move4 = Integer.parseInt(pokeParts[7]);
						Moveset m = new Moveset();
						if (move1 != 0)
							m.addMove(move1);
						if (move2 != 0)
							m.addMove(move2);
						if (move3 != 0)
							m.addMove(move3);
						if (move4 != 0)
							m.addMove(move4);
						pk = new Pokemon(s, level, m, ivs,
								Nature.getNature(PID));
					}
					// hold item, and moveset specified
					else if (pokeDataType == 3) {
						Item item = Item
								.getItem(Integer.parseInt(pokeParts[4]));
						int move1 = Integer.parseInt(pokeParts[5]);
						int move2 = Integer.parseInt(pokeParts[6]);
						int move3 = Integer.parseInt(pokeParts[7]);
						int move4 = Integer.parseInt(pokeParts[8]);
						Moveset m = new Moveset();
						if (move1 != 0)
							m.addMove(move1);
						if (move2 != 0)
							m.addMove(move2);
						if (move3 != 0)
							m.addMove(move3);
						if (move4 != 0)
							m.addMove(move4);
						pk = new Pokemon(s, level, m, ivs,
								Nature.getNature(PID));
						pk.setHoldItem(item);
					}
					t.pokes.add(pk);
					// TODO more cases / actually differentiate between games
					// overrides & 48 == 48 for DW ability in gen5?
					// overrides & 1 == 1 for force male
					// overrides & 2 == 2 for force female
					// also find out what, if anything, this does in DPPt
					if ((overrides & 16) == 16) {
						pk.setAbility(pk.getSpecies().getAbility1());
					} else if ((overrides & 32) == 32) {
						pk.setAbility(pk.getSpecies().getAbility2());
					}
				}
				trainers.add(t);
			}
			in.close();
			return trainers;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private static boolean determineIfFemale(int trainerClass) {
		// questionable: 23 (Young Couple), 31 (Double Team), 47 (Sis & Bro), 70
		// (Belle & Pa), 82 (Interviewers)
		Integer[] femaleClassesDP = new Integer[] { 1, 3, 5, 7, 8, 10, 13, 17,
				18, 21, 22, 25, 26, 30, 33, 35, 40, 43, 45, 50, 54, 56, 61, 66,
				69, 72, 74, 76, 77, 78, 80, 84, 85, 87, 89, 90, 92, 94, 96, };
		Integer[] femaleClassesPt = new Integer[] { 1, 3, 5, 7, 8, 10, 13, 17,
				18, 21, 22, 25, 26, 30, 33, 35, 40, 43, 45, 50, 54, 56, 61, 66,
				69, 72, 74, 76, 77, 78, 80, 84, 85, 87, 89, 90, 92, 94, 96, 98,
				99, 101 };
		// questionable: 122 (Young Couple), 121 (Double Team)
		// also have to find the female Grunt class
		Integer[] femaleClassesHGSS = new Integer[] { 3, 5, 8, 21, 25, 36, 43,
				47, 56, 70, 74, 76, 77, 82, 88, 90, 92, 94, 99, 101, 103, 105,
				106, 107, 114 };
		Integer[] femaleClassesBW1 = new Integer[] {};
		Integer[] femaleClassesBW2 = new Integer[] {};

		Integer[] femaleClasses = null;
		switch (Settings.game) {
		case DIAMOND:
		case PEARL:
			femaleClasses = femaleClassesDP;
			break;
		case PLATINUM:
			femaleClasses = femaleClassesPt;
			break;
		case HEARTGOLD:
		case SOULSILVER:
			femaleClasses = femaleClassesHGSS;
			break;
		case BLACK:
		case WHITE:
			femaleClasses = femaleClassesBW1;
			break;
		case BLACK2:
		case WHITE2:
			femaleClasses = femaleClassesBW2;
			break;
		}

		if (femaleClasses == null) {
			// shouldn't ever happen
			return false;
		}

		return Arrays.asList(femaleClasses).contains(trainerClass);
	}

	private static int calcPIDUpper(int trainerNumber, int AI, int level,
			int pokedexNum, int trainerClass) {
		int nails = trainerNumber + AI + level + pokedexNum;
		if (Settings.game.generationIndex() == 2) {
			// 5th gen RNG
			long constant = 0x5D588B656C078965L;
			int curr = 0;
			long working = nails;
			while (curr < trainerClass) {
				working = working * constant + 0x269EC3;
				curr++;
			}
			return (int) (working >>> 48);
		} else {
			// 4th gen RNG
			int constant = 0x41c64e6d;
			int curr = 0;
			int working = nails;
			while (curr < trainerClass) {
				working = working * constant + 0x6073;
				curr++;
			}
			return working >>> 16;
		}
	}

	// reads trainer_data_(frlg|rs|e).txt to get trainer data
	private static List<Trainer> getDataGen3(String filename) {
		ArrayList<Trainer> trainers = new ArrayList<Trainer>();
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(System.class
					.getResource("/resources/" + filename).openStream()));

			Trainer t;
			while (in.ready()) {
				String text = in.readLine();
				if (text.trim().isEmpty()) {
					continue;
				}
				// Trainer line: num|name|isFemale|pokeDataType|#pokes|nameVal
				t = new Trainer();
				String[] parts = text.split("\\|");
				t.trainerNumber = Integer.parseInt(parts[0]);
				t.name = parts[1];
				t.isFemale = Integer.parseInt(parts[2]) == 1;
				int pokeDataType = Integer.parseInt(parts[3]);
				int numPokes = Integer.parseInt(parts[4]);
				t.nameValue = Integer.parseInt(parts[5]);
				t.pokes = new ArrayList<Pokemon>();

				// the next few lines are pokemon lines
				long incrementedNameValue = 0; // the sum of name values of
												// trainer + pokes
				for (int i = 0; i < numPokes; i++) {
					String pokeText = in.readLine();
					String[] pokeParts = pokeText.split("\\|");
					int level = Integer.parseInt(pokeParts[0].substring(1));
					Species s = Species.getSpeciesFromName(pokeParts[1]);
					int AI = Integer.parseInt(pokeParts[2]);
					int ivVal = 31 * AI / 255;
					IVs ivs = new IVs(ivVal);

					incrementedNameValue += t.nameValue;
					incrementedNameValue += s.getNameValue();
					long PID = (incrementedNameValue << 8)
							+ (t.isFemale ? 0x78 : 0x88);
					// moveset not specified
					if (pokeDataType == 0) {
						Pokemon pk = new Pokemon(s, level, ivs,
								Nature.getNature(PID));
						t.pokes.add(pk);
					}
					// moveset not specified with hold item
					else if (pokeDataType == 2) {
						Pokemon pk = new Pokemon(s, level, ivs,
								Nature.getNature(PID));
						Item item = Item
								.getItem(Integer.parseInt(pokeParts[3]));
						pk.setHoldItem(item);
						t.pokes.add(pk);
					}
					// moveset specified
					else if (pokeDataType == 1) {
						int move1 = Integer.parseInt(pokeParts[3]);
						int move2 = Integer.parseInt(pokeParts[4]);
						int move3 = Integer.parseInt(pokeParts[5]);
						int move4 = Integer.parseInt(pokeParts[6]);
						Moveset m = new Moveset();
						if (move1 != 0)
							m.addMove(move1);
						if (move2 != 0)
							m.addMove(move2);
						if (move3 != 0)
							m.addMove(move3);
						if (move4 != 0)
							m.addMove(move4);
						Pokemon pk = new Pokemon(s, level, m, ivs,
								Nature.getNature(PID));
						t.pokes.add(pk);
					}
					// hold item, and moveset specified
					else if (pokeDataType == 3) {
						Item item = Item
								.getItem(Integer.parseInt(pokeParts[3]));
						int move1 = Integer.parseInt(pokeParts[4]);
						int move2 = Integer.parseInt(pokeParts[5]);
						int move3 = Integer.parseInt(pokeParts[6]);
						int move4 = Integer.parseInt(pokeParts[7]);
						Moveset m = new Moveset();
						if (move1 != 0)
							m.addMove(move1);
						if (move2 != 0)
							m.addMove(move2);
						if (move3 != 0)
							m.addMove(move3);
						if (move4 != 0)
							m.addMove(move4);
						Pokemon pk = new Pokemon(s, level, m, ivs,
								Nature.getNature(PID));
						pk.setHoldItem(item);
						t.pokes.add(pk);
					}
				}
				trainers.add(t);
			}
			in.close();
			return trainers;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public int getNameValue() {
		return nameValue;
	}

	public String getName() {
		return name;
	}

	public int getPokemonCount() {
		return pokes.size();
	}

	public List<Pokemon> getPokemonInBattleOrder(BattleOptions options) {
		List<Pokemon> battleOrder = new ArrayList<Pokemon>();
		List<Integer> order = options.getTrainerSendoutOrder();
		if (order == null) {
			battleOrder.addAll(pokes);
		} else {
			for (Integer current : order) {
				battleOrder.add(pokes.get(current - 1));
			}
		}
		return battleOrder;
	}

	// for later
	public int getBattleType() {
		return battleType;
	}
}
