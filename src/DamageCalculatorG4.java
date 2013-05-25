import java.util.List;

import org.ini4j.jdk14.edu.emory.mathcs.backport.java.util.Arrays;

public class DamageCalculatorG4 extends DamageCalculator {

	// rangeNum should range from 85 to 100
	// crit indicates if there is a crit or not
	protected int damage(Move attack, Pokemon attacker, Pokemon defender,
			StatModifier atkMod, StatModifier defMod, int rangeNum,
			boolean crit, double basePowerMultiplier) {
		if (rangeNum < MIN_RANGE) {
			rangeNum = MIN_RANGE;
		}
		if (rangeNum > MAX_RANGE) {
			rangeNum = MAX_RANGE;
		}

		int power = attack.getPower();
		int basePower = power;
		if (power <= 0) {
			// TODO: special cases
			return 0;
		}
		power *= basePowerMultiplier;

		// stat modifiers
		// TODO: is this how it works in gen 3+?
		int atk_phy = attacker.getAtk();
		int def_phy = defender.getDef();
		int atk_spc = attacker.getSpcAtk();
		int def_spc = defender.getSpcDef();

		// type-based hold items
		String itemName = (attacker.getHoldItem() == null ? "" : Constants
				.hashName(attacker.getHoldItem().getName()));
		String defenderItemName = (defender.getHoldItem() == null ? ""
				: Constants.hashName(defender.getHoldItem().getName()));
		String attackerAbility = Constants.hashName(attacker.getAbility());
		String defenderAbility = Constants.hashName(defender.getAbility());
		String attackerSpecies = Constants.hashName(attacker.getSpecies()
				.getName());
		String defenderSpecies = Constants.hashName(defender.getSpecies()
				.getName());
		if (itemName.equals("MUSCLEBAND") && attack.isPhysicalMove()) {
			power = power * 11 / 10;
		}
		if (itemName.equals("WISEGLASSES") && !attack.isPhysicalMove()) {
			power = power * 11 / 10;
		}
		// elemental items
		String elementalItem = "NONE";
		switch (attack.getType()) {
		case NORMAL:
			elementalItem = "SILKSCARF";
			break;
		case FIGHTING:
			elementalItem = "BLACKBELT";
			break;
		case FLYING:
			elementalItem = "SHARPBEAK";
			break;
		case BUG:
			elementalItem = "SILVERPOWDER";
			break;
		case DARK:
			elementalItem = "BLACKGLASSES";
			break;
		case DRAGON:
			elementalItem = "DRAGONFANG";
			break;
		case ELECTRIC:
			elementalItem = "MAGNET";
			break;
		case FIRE:
			elementalItem = "CHARCOAL";
			break;
		case GHOST:
			elementalItem = "SPELLTAG";
			break;
		case GRASS:
			elementalItem = "MIRACLESEED";
			break;
		case GROUND:
			elementalItem = "SOFTSAND";
			break;
		case ICE:
			elementalItem = "NEVERMELTICE";
			break;
		case POISON:
			elementalItem = "POISONBARB";
			break;
		case PSYCHIC:
			elementalItem = "TWISTEDSPOON";
			break;
		case ROCK:
			elementalItem = "HARDSTONE";
			break;
		case STEEL:
			elementalItem = "METALCOAT";
			break;
		case WATER:
			elementalItem = "MYSTICWATER";
			break;
		}
		if (elementalItem.equalsIgnoreCase(itemName)) {
			power = power * 12 / 10;
		}

		if (attackerSpecies.equalsIgnoreCase("DIALGA")
				&& itemName.equalsIgnoreCase("ADAMANTORB")
				&& (attack.getType() == Type.DRAGON || attack.getType() == Type.STEEL)) {
			power = power * 12 / 10;
		}

		if (attackerSpecies.equalsIgnoreCase("PALKIA")
				&& itemName.equalsIgnoreCase("LUSTROUSORB")
				&& (attack.getType() == Type.DRAGON || attack.getType() == Type.WATER)) {
			power = power * 12 / 10;
		}

		if (attackerSpecies.equalsIgnoreCase("GIRATINA")
				&& itemName.equalsIgnoreCase("GRISEOUSORB")
				&& (attack.getType() == Type.DRAGON || attack.getType() == Type.GHOST)) {
			power = power * 12 / 10;
		}
		// TODO charge (do like blaze?)
		// TODO rivalry (do we even keep track of gender?)
		// TODO reckless (we don't differentiate recoil yet)
		// iron fist
		List<String> ironFistMoves = Arrays.asList(new String[] {
				"BULLETPUNCH", "COMETPUNCH", "DIZZYPUNCH", "DRAINPUNCH",
				"DYNAMICPUNCH", "FIREPUNCH", "FOCUSPUNCH", "HAMMERARM",
				"ICEPUNCH", "MACHPUNCH", "MEGAPUNCH", "METEORMASH",
				"SHADOWPUNCH", "SKYUPPERCUT", "THUNDERPUNCH" });
		if (ironFistMoves.contains(itemName)) {
			power = power * 12 / 10;
		}
		if (attackerAbility.equalsIgnoreCase("TECHNICIAN") && basePower <= 60) {
			power = power * 3 / 2;
		}
		// blaze, torrent, overgrow, swarm are handled by other places
		if (defenderAbility.equalsIgnoreCase("THICKFAT")
				&& (attack.getType() == Type.FIRE || attack.getType() == Type.ICE)) {
			power /= 2;
		}

		if (defenderAbility.equalsIgnoreCase("FIREPROOF")
				&& attack.getType() == Type.FIRE) {
			power /= 2;
		}
		if (defenderAbility.equalsIgnoreCase("ROUGHSKIN")
				&& attack.getType() == Type.FIRE) {
			power = power * 125 / 100;
		}
		// TODO mudsport/watersport (handle like this Blaze etc)?

		// apply stages, choosing physical/special side
		// TODO handle simple & unaware & psychocut/psystrike properly
		int effective_atk = 0, effective_def = 0;
		if (attack.isPhysicalMove()) {
			effective_atk = (!crit || atkMod.getAtkStage() >= 0) ? atkMod
					.modAtk(atk_phy) : atk_phy;
			effective_def = (!crit || defMod.getDefStage() <= 0) ? defMod
					.modDef(def_phy) : def_phy;

		} else {
			effective_atk = (!crit || atkMod.getSpcAtkStage() >= 0) ? atkMod
					.modSpcAtk(atk_spc) : atk_spc;
			effective_def = (!crit || defMod.getSpcDefStage() <= 0) ? defMod
					.modSpcDef(def_spc) : def_spc;
		}
		// attack/spatk mods
		// huge/pure power
		if ((attackerAbility.equals("PUREPOWER") || attackerAbility
				.equals("HUGEPOWER")) && attack.isPhysicalMove()) {
			effective_atk *= 2;
		}
		// TODO flower gift (somehow?)
		// TODO Guts like Blaze etc
		if (attackerAbility.equals("HUSTLE") && attack.isPhysicalMove()) {
			effective_atk = effective_atk * 3 / 2;
		}
		// TODO slow start like Blaze etc
		// TODO minus/plus/solar power
		if (itemName.equals("CHOICEBAND") && attack.isPhysicalMove()) {
			effective_atk = effective_atk * 3 / 2;
		}
		if (itemName.equals("LIGHTBALL")
				&& attackerSpecies.equalsIgnoreCase("PIKACHU")) {
			effective_atk *= 2;
		}

		if ((attackerSpecies.equals("CUBONE") || attackerSpecies
				.equals("MAROWAK"))
				&& itemName.equals("THICKCLUB")
				&& attack.isPhysicalMove()) {
			effective_atk = effective_atk * 3 / 2;
		}

		if (itemName.equals("CHOICESPECS") && !attack.isPhysicalMove()) {
			effective_atk = effective_atk * 3 / 2;
		}

		if ((attackerSpecies.equals("LATIAS") || attackerSpecies
				.equals("LATIOS"))
				&& itemName.equals("SOULDEW")
				&& !attack.isPhysicalMove()) {
			effective_atk = effective_atk * 3 / 2;
		}

		if (attackerSpecies.equals("CLAMPERL")
				&& itemName.equals("DEEPSEATOOTH") && !attack.isPhysicalMove()) {
			effective_atk *= 2;
		}

		// defense mods
		if (attack.getName().equalsIgnoreCase("EXPLOSION")
				|| attack.getName().equalsIgnoreCase("SELFDESTRUCT")) {
			effective_def = Math.max(effective_def / 2, 1);
		}

		// TODO marvel scale?
		// TODO flower gift?
		if (defenderItemName.equals("METALPOWDER")
				&& defenderSpecies.equals("DITTO") && attack.isPhysicalMove()) {
			effective_def = effective_def * 3 / 2;
		}

		if ((defenderSpecies.equals("LATIAS") || defenderSpecies
				.equals("LATIOS"))
				&& defenderItemName.equals("SOULDEW")
				&& !attack.isPhysicalMove()) {
			effective_def = effective_def * 3 / 2;
		}

		if (attackerSpecies.equals("CLAMPERL")
				&& itemName.equals("DEEPSEASCALE") && !attack.isPhysicalMove()) {
			effective_def *= 2;
		}

		// TODO sandstorm?

		int damage = (2 * attacker.getLevel() / 5 + 2) * effective_atk * power
				/ effective_def / 50;

		// this doesn't happen in gen4 apparently
		// if (attack.isPhysicalMove()) {
		// damage = Math.max(damage, 1);
		// }
		damage += 2;
		damage *= crit ? 2 : 1;

		boolean STAB = attack.getType() == attacker.getSpecies().getType1()
				|| attack.getType() == attacker.getSpecies().getType2();
		double effectiveMult = Type.effectiveness(attack.getType(), defender
				.getSpecies().getType1(), defender.getSpecies().getType2());
		if (effectiveMult == 0) {
			return 0;
		}

		if (STAB) {
			damage = damage * 3 / 2;
		}
		damage *= effectiveMult;
		damage = damage * rangeNum / 100;
		return Math.max(damage, 1);
	}
}
