import java.util.List;

//represents a battle, with planned statmods
public class Battle extends GameAction {
	private Battleable opponent;
	private BattleOptions options;

	public Battle(Battleable b) {
		opponent = b;
		options = new BattleOptions();
	}

	public Battle(Battleable b, BattleOptions options) {
		opponent = b;
		this.options = options;
	}

	public Battleable getOpponent() {
		return opponent;
	}

	public BattleOptions getOptions() {
		return options;
	}

	public StatModifier getMod1() {
		return options.getMod1();
	}

	public StatModifier getMod2() {
		return options.getMod2();
	}

	public int getVerbose() {
		return options.getVerbose();
	}

	public static Battle makeBattle(int offset) {
		return new Battle(Trainer.getTrainer(offset));
	}

	public static Battle makeBattle(int offset, BattleOptions options) {
		return new Battle(Trainer.getTrainer(offset), options);
	}

	public static Battle makeBattle(Pokemon p) {
		return new Battle(p);
	}

	public static Battle makeBattle(Pokemon p, BattleOptions options) {
		return new Battle(p, options);
	}

	@Override
	public void performAction(Party p) {
		doBattle(p); // temp
		Game game = Settings.game;
		// check for special gym leader badges
		if (game == Game.FIRERED || game == Game.LEAFGREEN) {
			if (opponent.equals(Trainer.getTrainer(414))) {
				for (Pokemon pkmn : p) {
					pkmn.setAtkBadge(true); // brock
				}
			} else if (opponent.equals(Trainer.getTrainer(416))) {
				for (Pokemon pkmn : p) {
					pkmn.setSpeBadge(true); // surge
				}
			} else if (opponent.equals(Trainer.getTrainer(418))) {
				for (Pokemon pkmn : p) {
					pkmn.setDefBadge(true); // koga
				}
			} else if (opponent.equals(Trainer.getTrainer(419))) {
				for (Pokemon pkmn : p) {
					pkmn.setSpcBadge(true); // blaine
				}
			}
		} else if (game == Game.RUBY || game == Game.SAPPHIRE
				|| game == Game.EMERALD) {
			if (opponent.equals(Trainer.getTrainer(265))) {
				for (Pokemon pkmn : p) {
					pkmn.setAtkBadge(true); // roxanne
				}
			} else if (opponent.equals(Trainer.getTrainer(267))) {
				for (Pokemon pkmn : p) {
					pkmn.setSpeBadge(true); // wattson
				}
			} else if (opponent.equals(Trainer.getTrainer(269))) {
				for (Pokemon pkmn : p) {
					pkmn.setDefBadge(true); // norman
				}
			} else if (opponent.equals(Trainer.getTrainer(271))) {
				for (Pokemon pkmn : p) {
					pkmn.setSpcBadge(true); // tate&liza
				}
			}
		}
	}

	private void doBattle(Party party) {
		// TODO: automatically determine whether or not to print
		if (opponent instanceof Pokemon) {
			int main = options.getUsedPokemon();
			Pokemon p = (main == -1) ? party.getCurrentMainPokemon() : party
					.getPokemon(main);
			appropriatePrinting(p, (Pokemon) opponent);
			opponent.battle(p, options, 1);
		} else { // is a Trainer
			Trainer t = (Trainer) opponent;
			if (getVerbose() == BattleOptions.ALL
					|| getVerbose() == BattleOptions.SOME)
				Main.appendln(t.toString());
			List<Pokemon> battlePokes = t.getPokemonInBattleOrder(options);
			int main = options.getUsedPokemon();
			Pokemon currentMain = (main == -1) ? party.getCurrentMainPokemon()
					: party.getPokemon(main);
			int opponentPokeNum = 0;
			for (Pokemon opps : battlePokes) {
				// Case for complex flags?
				opponentPokeNum++;
				if (!options.hasComplexFlags()) {
					trainerBattleIndivPokemon(currentMain, opps, true, 1);
				} else {
					ComplexBattleFlags cbf = options
							.getComplexFlags(opponentPokeNum);
					if (cbf.hasShiftedInPokemon()) {
						currentMain = party.getPokemon(cbf
								.getShiftedInPokemon());
						options.setMod1(new StatModifier());
					}
					options.setMod1(options.getMod1().combineWith(
							cbf.getModifiersForLead()));
					if (cbf.hasSwitchedInPokemon()) {
						trainerBattleIndivPokemon(currentMain, opps, true, 2);
						Pokemon switchedTo = party.getPokemon(cbf
								.getSwitchedInPokemon());
						options.setMod1(cbf.getModifiersForBattler());
						trainerBattleIndivPokemon(switchedTo, opps, true, 2);
						currentMain = switchedTo;
					} else if (cbf.hasDeadSwitchPokemon()) {
						trainerBattleIndivPokemon(currentMain, opps, false, 1);
						Pokemon switchedTo = party.getPokemon(cbf
								.getDeadSwitchPokemon());
						options.setMod1(cbf.getModifiersForBattler());
						trainerBattleIndivPokemon(switchedTo, opps, true, 1);
						currentMain = switchedTo;
					} else {
						options.setMod1(options.getMod1().combineWith(
								cbf.getModifiersForBattler()));
						trainerBattleIndivPokemon(currentMain, opps, true, 1);
					}
				}
			}
		}
		// todo fix this
		// if (getVerbose() == BattleOptions.ALL
		// || getVerbose() == BattleOptions.SOME) {
		// Main.appendln(String.format("LVL: %d EXP NEEDED: %d/%d",
		// p.getLevel(), p.expToNextLevel(), p.expForLevel()));
		// }
	}

	private void trainerBattleIndivPokemon(Pokemon pokemon, Pokemon opponent,
			boolean gainXP, int participants) {
		int lastLvl = pokemon.getLevel();
		appropriatePrinting(pokemon, opponent);
		if (gainXP) {
			opponent.battle(pokemon, options,
					Math.max(participants, options.getParticipants()));
			checkLevelPrinting(pokemon, lastLvl);
		}
	}

	private void checkLevelPrinting(Pokemon pokemon, int lastLvl) {
		if (pokemon.getLevel() > lastLvl) {
			if (options.isPrintSRsOnLvl()) {
				Main.appendln(pokemon.statRanges(false));
			}
			if (options.isPrintSRsBoostOnLvl()) {
				Main.appendln(pokemon.statRanges(true));
			}
		}
	}

	public void appropriatePrinting(Pokemon us, Pokemon them) {
		if (getVerbose() == BattleOptions.ALL)
			printBattle(us, them);
		else if (getVerbose() == BattleOptions.SOME)
			printShortBattle(us, them);
	}

	// does not actually do the battle, just prints summary
	public void printBattle(Pokemon us, Pokemon them) {
		Main.appendln(Settings.damageCalc.summary(us, them, options));
	}

	// does not actually do the battle, just prints short summary
	public void printShortBattle(Pokemon us, Pokemon them) {
		Main.appendln(Settings.damageCalc.shortSummary(us, them, options));
	}
}

class Encounter extends Battle {
	Encounter(Species s, int lvl, BattleOptions options) {
		super(new Pokemon(s, lvl), options);
	}

	Encounter(String s, int lvl) {
		this(Species.getSpeciesFromName(s), lvl, new BattleOptions());
	}

	Encounter(String s, int lvl, BattleOptions options) {
		this(Species.getSpeciesFromName(s), lvl, options);
	}
}

class TrainerPoke extends Battle {
	TrainerPoke(Species s, int lvl, BattleOptions options) {
		super(new Pokemon(s, lvl), options);
		((Pokemon) getOpponent()).setWild(true);
	}

	TrainerPoke(String s, int lvl) {
		this(Species.getSpeciesFromName(s), lvl, new BattleOptions());
	}

	TrainerPoke(String s, int lvl, BattleOptions options) {
		this(Species.getSpeciesFromName(s), lvl, options);
	}
}
