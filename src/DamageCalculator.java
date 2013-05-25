//calculates damage (durr)
public abstract class DamageCalculator {
    protected static int MIN_RANGE = 85;
    protected static int MAX_RANGE = 100;
    
    protected abstract int damage(Move attack, Pokemon attacker, Pokemon defender,
            StatModifier atkMod, StatModifier defMod, int rangeNum,
            boolean crit, double basePowerMultiplier);

    public int minDamage(Move attack, Pokemon attacker,
            Pokemon defender, StatModifier atkMod, StatModifier defMod,
            int basePowerMultiplier) {
        return damage(attack, attacker, defender, atkMod, defMod, MIN_RANGE,
                false, basePowerMultiplier);
    }

    public int maxDamage(Move attack, Pokemon attacker,
            Pokemon defender, StatModifier atkMod, StatModifier defMod,
            int basePowerMultiplier) {
        return damage(attack, attacker, defender, atkMod, defMod, MAX_RANGE,
                false, basePowerMultiplier);
    }

    public int minCritDamage(Move attack, Pokemon attacker,
            Pokemon defender, StatModifier atkMod, StatModifier defMod,
            int basePowerMultiplier) {
        return damage(attack, attacker, defender, atkMod, defMod, MIN_RANGE,
                true, basePowerMultiplier);
    }

    public int maxCritDamage(Move attack, Pokemon attacker,
            Pokemon defender, StatModifier atkMod, StatModifier defMod,
            int basePowerMultiplier) {
        return damage(attack, attacker, defender, atkMod, defMod, MAX_RANGE,
                true, basePowerMultiplier);
    }

    // printout of move damages between the two pokemon
    // assumes you are p1
    public String summary(Pokemon p1, Pokemon p2, BattleOptions options) {
        StringBuilder sb = new StringBuilder();
        String endl = Constants.endl;
        StatModifier mod1 = options.getMod1();
        StatModifier mod2 = options.getMod2();

        sb.append(p1.levelName() + " vs " + p2.levelName() + endl);
        // sb.append(String.format("EXP to next level: %d EXP gained: %d",
        // p1.expToNextLevel(), p2.expGiven()) + endl);
        sb.append(pokeStatMods(p1, mod1));

        sb.append(summary_help(p1, p2, mod1, mod2));

        sb.append(endl);

        sb.append(pokeStatMods(p2, mod2));
        
        sb.append(summary_help(p2, p1, mod2, mod1));

        return sb.toString();
    }
    
    // used for the less verbose option
    public String shortSummary(Pokemon p1, Pokemon p2,
            BattleOptions options) {
        StringBuilder sb = new StringBuilder();
        String endl = Constants.endl;

        StatModifier mod1 = options.getMod1();
        StatModifier mod2 = options.getMod2();

        sb.append(p1.levelName() + " vs " + p2.levelName() + endl);
        // sb.append(String.format("EXP to next level: %d EXP gained: %d",
        // p1.expToNextLevel(), p2.expGiven()) + endl);
        sb.append(pokeStatMods(p1, mod1));
        
        sb.append(summary_help(p1, p2, mod1, mod2) + endl);

        sb.append(pokeStatMods(p2, mod2));

        sb.append(" " + p2.getMoveset().toString() + endl);
        return sb.toString();
    }
    
    private String pokeStatMods(Pokemon p, StatModifier sm) {
        StringBuilder sb = new StringBuilder();
        String endl = Constants.endl;
        sb.append(p.pokeName()+ " ");
        
        if (sm.hasMods()) {
            sb.append(String.format("(%s) %s -> (%s) ",
                    p.statsStr(), sm.summary(), sm.modStatsStr(p)));
        } else {
            sb.append(String.format("(%s) ", p.statsStr()));
        }
        sb.append("["+p.getSpecies().getType1()+"/"+p.getSpecies().getType2()+"] ");
        sb.append("{" + p.getNature().toString() + "} ");
        sb.append("[" + p.getAbility() + "]");
        if(p.getHoldItem() == null || p.getHoldItem().getName().isEmpty()) {
            sb.append(": " + endl);
        } else {
            sb.append(" <" + p.getHoldItem().toString() + ">: " + endl);
        }
        return sb.toString();
    }

    // String summary of all of p1's moves used on p2
    // (would be faster if i didn't return intermediate strings)
    private String summary_help(Pokemon p1, Pokemon p2,
            StatModifier mod1, StatModifier mod2) {
        StringBuilder sb = new StringBuilder();
        String endl = Constants.endl;

        int enemyHP = p2.getHP();

        for (Move m : p1.getMoveset()) {
            //rollout, fury cutter
            if (m.getIndexNum() == 205 || m.getIndexNum() == 210) {
                for (int i = 1; i <= 5; i++) {
                    Move m2 = new Move(m, i);
                    printMoveDamage(sb, m2, p1, p2, mod1, mod2, endl, enemyHP,
                            1 << (i - 1));
                }
            } else if (m.getIndexNum() == 99) { //rage
                for (int i = 1; i <= 8; i++) {
                    Move m2 = new Move(m, i);
                    printMoveDamage(sb, m2, p1, p2, mod1, mod2, endl, enemyHP,
                            i);
                }
            } else if (m.getType() == Type.WATER && m.getPower() > 0 && p1.getAbility().equalsIgnoreCase("TORRENT")) {
                printMoveDamage(sb, m, p1, p2, mod1, mod2, endl, enemyHP, 1);
                Move m2 = new Move(m, "(w/ Torrent)", m.getPower() * 3 /2);
                printMoveDamage(sb, m2, p1, p2, mod1, mod2, endl, enemyHP, 1);
            } else if (m.getType() == Type.FIRE && m.getPower() > 0 && p1.getAbility().equalsIgnoreCase("BLAZE")) {
                printMoveDamage(sb, m, p1, p2, mod1, mod2, endl, enemyHP, 1);
                Move m2 = new Move(m, "(w/ Blaze)", m.getPower() * 3 /2);
                printMoveDamage(sb, m2, p1, p2, mod1, mod2, endl, enemyHP, 1);
            } else if (m.getType() == Type.GRASS && m.getPower() > 0 && p1.getAbility().equalsIgnoreCase("OVERGROW")) {
                printMoveDamage(sb, m, p1, p2, mod1, mod2, endl, enemyHP, 1);
                Move m2 = new Move(m, "(w/ Overgrow)", m.getPower() * 3 /2);
                printMoveDamage(sb, m2, p1, p2, mod1, mod2, endl, enemyHP, 1);
            } else if (m.getType() == Type.BUG && m.getPower() > 0 && p1.getAbility().equalsIgnoreCase("SWARM")) {
                printMoveDamage(sb, m, p1, p2, mod1, mod2, endl, enemyHP, 1);
                Move m2 = new Move(m, "(w/ Swarm)", m.getPower() * 3 /2);
                printMoveDamage(sb, m2, p1, p2, mod1, mod2, endl, enemyHP, 1);
            }
            else {
                printMoveDamage(sb, m, p1, p2, mod1, mod2, endl, enemyHP, 1);
            }

        }

        return sb.toString();
    }

    public void printMoveDamage(StringBuilder sb, Move m, Pokemon p1,
            Pokemon p2, StatModifier mod1, StatModifier mod2, String endl,
            int enemyHP, int basePowerMultiplier) {
        sb.append(m.getName() + "\t");
        // calculate damage of this move, and its percentages on opposing
        // pokemon
        int minDmg = minDamage(m, p1, p2, mod1, mod2, basePowerMultiplier);
        int maxDmg = maxDamage(m, p1, p2, mod1, mod2, basePowerMultiplier);

        // don't spam if the move doesn't do damage
        // TODO: better test of damaging move, to be done when fixes are made
        if (maxDmg == 0) {
            sb.append(endl);
            return;
        }
        double minPct = 100.0 * minDmg / enemyHP;
        double maxPct = 100.0 * maxDmg / enemyHP;
        sb.append(String.format("%d-%d %.02f-%.02f", minDmg, maxDmg, minPct,
                maxPct));
        sb.append("%\t(crit: ");
        // do it again, for crits
        int critMinDmg = minCritDamage(m, p1, p2, mod1, mod2, basePowerMultiplier);
        int critMaxDmg = maxCritDamage(m, p1, p2, mod1, mod2, basePowerMultiplier);

        double critMinPct = 100.0 * critMinDmg / enemyHP;
        double critMaxPct = 100.0 * critMaxDmg / enemyHP;
        sb.append(String.format("%d-%d %.02f-%.02f", critMinDmg, critMaxDmg,
                critMinPct, critMaxPct));
        sb.append("%)" + endl);

        int oppHP = p2.getHP();
        // test if noncrits can kill in 1shot
        if (maxDmg >= oppHP && minDmg < oppHP) {
            double oneShotPct = oneShotPercentage(m, p1, p2, mod1, mod2, false,
                    basePowerMultiplier);
            sb.append(String.format("\t(One shot prob.: %.02f%%)", oneShotPct)
                    + endl);
        }
        // test if crits can kill in 1shot
        if (critMaxDmg >= oppHP && critMinDmg < oppHP) {
            double oneShotPct = oneShotPercentage(m, p1, p2, mod1, mod2, true,
                    basePowerMultiplier);
            sb.append(String.format("\t(Crit one shot prob.: %.02f%%)",
                    oneShotPct) + endl);
        }
    }

    private double oneShotPercentage(Move attack, Pokemon attacker,
            Pokemon defender, StatModifier atkMod, StatModifier defMod,
            boolean crit, int basePowerMultiplier) {
        // iterate until damage is big enough
        int rangeNum = MIN_RANGE;
        while (damage(attack, attacker, defender, atkMod, defMod, rangeNum,
                crit, basePowerMultiplier) < defender.getHP()) {
            rangeNum++;
        }
        return 100.0 * (MAX_RANGE - rangeNum + 1) / (MAX_RANGE - MIN_RANGE + 1);
    }
    
    public static DamageCalculator dcFor(Game g) {
    	if(g.generationIndex() == 0) {
    		return new DamageCalculatorG3();
    	}
    	else if(g.generationIndex() == 1) {
    		return new DamageCalculatorG3();
    	}
    	else {
    		return new DamageCalculatorG3();
    	}
    }
}
