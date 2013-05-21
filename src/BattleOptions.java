import java.util.ArrayList;
import java.util.List;

public class BattleOptions {

	private int participants = 1;
	private boolean printSRsOnLvl = false;
	private boolean printSRsBoostOnLvl = false;
	private StatModifier mod1;
	private StatModifier mod2;
	private int verbose = BattleOptions.NONE;
	// verbose options
	public static final int NONE = 0;
	public static final int SOME = 1;
	public static final int ALL = 2;
	private List<Integer> sendoutOrder = null;
	private int usedPokemon = -1;
	private List<ComplexBattleFlags> complexFlags = new ArrayList<ComplexBattleFlags>();

	public BattleOptions() {
		setMod1(new StatModifier());
		setMod2(new StatModifier());
	}

	public boolean isPrintSRsBoostOnLvl() {
		return printSRsBoostOnLvl;
	}

	public void setPrintSRsBoostOnLvl(boolean printSRsBoostOnLvl) {
		this.printSRsBoostOnLvl = printSRsBoostOnLvl;
	}

	public int getParticipants() {
		return participants;
	}

	public void setParticipants(int participants) {
		this.participants = participants;
	}

	public boolean isPrintSRsOnLvl() {
		return printSRsOnLvl;
	}

	public void setPrintSRsOnLvl(boolean printSRsOnLvl) {
		this.printSRsOnLvl = printSRsOnLvl;
	}

	public StatModifier getMod1() {
		return mod1;
	}

	public void setMod1(StatModifier mod1) {
		this.mod1 = mod1;
	}

	public StatModifier getMod2() {
		return mod2;
	}

	public void setMod2(StatModifier mod2) {
		this.mod2 = mod2;
	}

	public int getVerbose() {
		return verbose;
	}

	public void setVerbose(int verbose) {
		this.verbose = verbose;
	}

	public void setTrainerSendoutOrder(List<Integer> order) {
		this.sendoutOrder = order;
	}

	public List<Integer> getTrainerSendoutOrder() {
		return this.sendoutOrder;
	}

	public int getUsedPokemon() {
		return this.usedPokemon;
	}

	public void setUsedPokemon(int num) {
		this.usedPokemon = num;
	}

	public ComplexBattleFlags getComplexFlags(int opponentNumber) {
		if (opponentNumber <= complexFlags.size()) {
			return complexFlags.get(opponentNumber - 1);
		}
		return null;
	}

	public void setComplexFlags(List<ComplexBattleFlags> flags) {
		this.complexFlags.addAll(flags);
	}

	public boolean hasComplexFlags() {
		return this.complexFlags.size() > 0;
	}

}
