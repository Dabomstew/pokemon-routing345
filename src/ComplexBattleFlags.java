public class ComplexBattleFlags {
	private int shiftedInPokemon = -1;
	private int switchedInPokemon = -1;
	private int deadSwitchPokemon = -1;
	private StatModifier statModForBattler = new StatModifier();
	private StatModifier statModForLead = new StatModifier();

	public ComplexBattleFlags() {

	}

	public int getShiftedInPokemon() {
		return shiftedInPokemon;
	}

	public void setShiftedInPokemon(int shiftedInPokemon) {
		this.shiftedInPokemon = shiftedInPokemon;
	}

	public int getSwitchedInPokemon() {
		return switchedInPokemon;
	}

	public void setSwitchedInPokemon(int switchedInPokemon) {
		this.switchedInPokemon = switchedInPokemon;
	}

	public int getDeadSwitchPokemon() {
		return deadSwitchPokemon;
	}

	public void setDeadSwitchPokemon(int deadSwitchPokemon) {
		this.deadSwitchPokemon = deadSwitchPokemon;
	}

	public boolean hasShiftedInPokemon() {
		return this.shiftedInPokemon > 0;
	}

	public boolean hasSwitchedInPokemon() {
		return this.switchedInPokemon > 0;
	}

	public boolean hasDeadSwitchPokemon() {
		return this.deadSwitchPokemon > 0;
	}

	public void setIntimidate() {
		this.statModForLead.setAtkStage(Math.max(
				this.statModForLead.getAtkStage() - 1, -6));
	}

	public void useXAttack() {
		this.statModForBattler.setAtkStage(Math.min(
				this.statModForBattler.getAtkStage() + 1, 6));
	}

	public void useXDefend() {
		this.statModForBattler.setDefStage(Math.min(
				this.statModForBattler.getDefStage() + 1, 6));
	}

	public void useXSpecial() {
		this.statModForBattler.setSpcAtkStage(Math.min(
				this.statModForBattler.getSpcAtkStage() + 1, 6));
	}

	public void useXSpDef() {
		this.statModForBattler.setSpcDefStage(Math.min(
				this.statModForBattler.getSpcDefStage() + 1, 6));
	}

	public void useXSpeed() {
		this.statModForBattler.setSpeStage(Math.min(
				this.statModForBattler.getSpeStage() + 1, 6));
	}

	public StatModifier getModifiersForLead() {
		return this.statModForLead;
	}

	public StatModifier getModifiersForBattler() {
		return this.statModForBattler;
	}

}
