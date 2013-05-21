import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Party implements Iterable<Pokemon> {

	private List<Pokemon> partyPokemon;
	private int currentMain;

	public Party() {
		partyPokemon = new ArrayList<Pokemon>();
		currentMain = 1;
	}

	public void addPartyPokemon(Pokemon pkmn) {
		partyPokemon.add(pkmn);
	}

	public Pokemon getCurrentMainPokemon() {
		return partyPokemon.get(currentMain - 1);
	}

	public void setMainPokemon(int main) {
		if (main <= partyPokemon.size() && main >= 1) {
			currentMain = main;
		}
	}

	public Pokemon getPokemon(int number) {
		return partyPokemon.get(number - 1);
	}

	@Override
	public Iterator<Pokemon> iterator() {
		return partyPokemon.iterator();
	}
}
