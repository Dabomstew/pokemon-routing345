
public abstract class GameAction {
    abstract void performAction(Party p);
    
    public static final GameAction eatRareCandy = new GameAction() {
        void performAction(Party p) { p.getCurrentMainPokemon().eatRareCandy(); }
    };
    public static final GameAction eatHPUp = new GameAction() {
        void performAction(Party p) { p.getCurrentMainPokemon().eatHPUp(); }
    };
    public static final GameAction eatIron = new GameAction() {
        void performAction(Party p) { p.getCurrentMainPokemon().eatIron(); }
    };
    public static final GameAction eatProtein = new GameAction() {
        void performAction(Party p) { p.getCurrentMainPokemon().eatProtein(); }
    };
    public static final GameAction eatCalcium = new GameAction() {
        void performAction(Party p) { p.getCurrentMainPokemon().eatCalcium(); }
    };
    public static final GameAction eatZinc = new GameAction() {
        void performAction(Party p) { p.getCurrentMainPokemon().eatZinc(); }
    };
    public static final GameAction eatCarbos = new GameAction() {
        void performAction(Party p) { p.getCurrentMainPokemon().eatCarbos(); }
    };
    
    //badges
    public static final GameAction getAtkBadge = new GameAction() {
        void performAction(Party p) { for(Pokemon pkmn : p){ pkmn.setAtkBadge(true);} }
    };
    public static final GameAction getSpeBadge = new GameAction() {
        void performAction(Party p) {for(Pokemon pkmn : p){ pkmn.setSpeBadge(true);} }
    };
    public static final GameAction getSpcBadge = new GameAction() {
        void performAction(Party p) {for(Pokemon pkmn : p){ pkmn.setSpcBadge(true);} }
    };
    public static final GameAction getDefBadge = new GameAction() {
        void performAction(Party p) {for(Pokemon pkmn : p){ pkmn.setDefBadge(true);} }
    };
    
    
    //not really a game action, but it's a nice hack?
    public static final GameAction printAllStats = new GameAction() {
        void performAction(Party p) { for(Pokemon pkmn : p){Main.appendln(pkmn.statsPrintout(true));} }
    };
    public static final GameAction printAllStatsNoBoost = new GameAction() {
        void performAction(Party p) { for(Pokemon pkmn : p){Main.appendln(pkmn.statsPrintout(false));} }
    };
    public static final GameAction printStatRanges = new GameAction() {
        void performAction(Party p) { for(Pokemon pkmn : p){Main.appendln(pkmn.statRanges(true));} }
    };
    public static final GameAction printStatRangesNoBoost = new GameAction() {
        void performAction(Party p) { for(Pokemon pkmn : p){Main.appendln(pkmn.statRanges(false));} }
    };

}

class LearnMove extends GameAction {
    private Move move;
    LearnMove(Move m) { move = m; }
    LearnMove(String s) { move = Move.getMoveByName(s); }
    public Move getMove() { return move; }
    @Override
    void performAction(Party p) { p.getCurrentMainPokemon().getMoveset().addMove(move); }
}


class UnlearnMove extends GameAction {
    private Move move;
    UnlearnMove(Move m) { move = m; }
    UnlearnMove(String s) { move = Move.getMoveByName(s); }
    public Move getMove() { return move; }
    @Override
    void performAction(Party p) { p.getCurrentMainPokemon().getMoveset().delMove(move); }
}

class Evolve extends GameAction {
    private Species target;
    Evolve(Species s) { target = s; }
    Evolve(String s) { target = Species.getSpeciesFromName(s); }
    @Override
    void performAction(Party p) {
        p.getCurrentMainPokemon().evolve(target);
        p.getCurrentMainPokemon().calculateStats();}
}

class GiveItem extends GameAction {
    private Item item;
    GiveItem(Item i) { item = i; }
    GiveItem(String s) { item = Item.getItemByName(s); }
    @Override
    void performAction(Party p) { p.getCurrentMainPokemon().setHoldItem(item); }
}

class TakeItem extends GameAction {
    @Override
    void performAction(Party p) { p.getCurrentMainPokemon().takeHoldItem(); }
}

class SetAbility extends GameAction {
    private String ability;
    SetAbility(String a) { ability = a; }
    void performAction(Party p) { p.getCurrentMainPokemon().setAbility(ability); }
}

class SetMain extends GameAction {
	private int main;
	SetMain(int num) { main=num; }
	void performAction(Party p) { p.setMainPokemon(main); }
}