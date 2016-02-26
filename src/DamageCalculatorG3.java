
public class DamageCalculatorG3 extends DamageCalculator {

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
        if (power <= 0) {
            // TODO: special cases
            return 0;
        }
        power *= basePowerMultiplier;
        
        // stat modifiers
        // TODO: is this how it works in gen 3+?
        //int aa_orig = attacker.getTrueAtk();
        int atk_phy = attacker.getAtk();
        //int dd_orig = defender.getTrueDef();
        int def_phy = defender.getDef();
        //int as_orig = attacker.getTrueSpcAtk();
        int atk_spc = attacker.getSpcAtk();
        //int ds_orig = defender.getTrueSpcDef();
        int def_spc = defender.getSpcDef();

        //type-based hold items
        String itemName = (attacker.getHoldItem() == null ? "" : Constants.hashName(attacker.getHoldItem().getName()));
        if (itemName.equals("SILKSCARF") && attack.getType() == Type.NORMAL) {
            atk_phy = atk_phy * 11 / 10;
        } else if (itemName.equals("BLACKBELT") && attack.getType() == Type.FIGHTING) {
            atk_phy = atk_phy * 11 / 10;
        } else if (itemName.equals("SHARPBEAK") && attack.getType() == Type.FLYING) {
            atk_phy = atk_phy * 11 / 10;
        } else if (itemName.equals("POISONBARB") && attack.getType() == Type.POISON) {
            atk_phy = atk_phy * 11 / 10;
        } else if (itemName.equals("SOFTSAND") && attack.getType() == Type.GROUND) {
            atk_phy = atk_phy * 11 / 10;
        } else if (itemName.equals("HARDSTONE") && attack.getType() == Type.ROCK) {
            atk_phy = atk_phy * 11 / 10;
        } else if (itemName.equals("SILVERPOWDER") && attack.getType() == Type.BUG) {
            atk_phy = atk_phy * 11 / 10;
        } else if (itemName.equals("SPELLTAG") && attack.getType() == Type.GHOST) {
            atk_phy = atk_phy * 11 / 10;
        } else if (itemName.equals("METALCOAT") && attack.getType() == Type.STEEL) {
            atk_phy = atk_phy * 11 / 10;
        } else if (itemName.equals("CHARCOAL") && attack.getType() == Type.FIRE) {
            atk_spc = atk_spc * 11 / 10;
        } else if (itemName.equals("MYSTICWATER") && attack.getType() == Type.WATER) {
            atk_spc = atk_spc * 11 / 10;
        } else if (itemName.equals("MIRACLESEED") && attack.getType() == Type.GRASS) {
            atk_spc = atk_spc * 11 / 10;
        } else if (itemName.equals("MAGNET") && attack.getType() == Type.ELECTRIC) {
            atk_spc = atk_spc * 11 / 10;
        } else if (itemName.equals("TWISTEDSPOON") && attack.getType() == Type.PSYCHIC) {
            atk_spc = atk_spc * 11 / 10;
        } else if (itemName.equals("NEVERMELTICE") && attack.getType() == Type.ICE) {
            atk_spc = atk_spc * 11 / 10;
        } else if (itemName.equals("DRAGONFANG") && attack.getType() == Type.DRAGON) {
            atk_spc = atk_spc * 11 / 10;
        } else if (itemName.equals("BLACKGLASSES") && attack.getType() == Type.DARK) {
            atk_spc = atk_spc * 11 / 10;
        } 
        //TODO: other hold items    
        //TODO: Thick Fat, Hustle, Guts, Marvel Scale
        //TODO: Mud Sport, Water Sport? (Affect base power)
        
        //selfdestruct/explosion halves defense
        if (attack.getName().equalsIgnoreCase("EXPLOSION") || attack.getName().equalsIgnoreCase("SELFDESTRUCT")) {
            def_phy = Math.max(def_phy/2, 1);
        }
        
        //apply stages, choosing physical/special side
        int effective_atk = 0, effective_def = 0;
        if (attack.isPhysicalMove()) {
            effective_atk = (!crit || atkMod.getAtkStage() >= 0) ? atkMod.modAtk(atk_phy) : atk_phy;
            effective_def = (!crit || defMod.getDefStage() <= 0) ? defMod.modDef(def_phy) : def_phy;

        } else {
            effective_atk = (!crit || atkMod.getSpcAtkStage() >= 0) ? atkMod.modSpcAtk(atk_spc) : atk_spc;
            effective_def = (!crit || defMod.getSpcDefStage() <= 0) ? defMod.modSpcDef(def_spc) : def_spc;
        }
        int damage = (attacker.getLevel()*2/5 + 2)*effective_atk*power/50/effective_def;
        
        if(attack.isPhysicalMove()) {
            damage = Math.max(damage, 1);
        }
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
