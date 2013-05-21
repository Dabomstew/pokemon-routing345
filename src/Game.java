public enum Game {
	// identifies the game being routed
	RUBY, SAPPHIRE, EMERALD, FIRERED, LEAFGREEN,
	DIAMOND, PEARL, PLATINUM, HEARTGOLD, SOULSILVER,
	BLACK, WHITE, BLACK2, WHITE2;

	public boolean hasBadgeBoosts() {
		return (this == RUBY || this == SAPPHIRE || this == EMERALD
				|| this == FIRERED || this == LEAFGREEN);
	}

	// gen index for internal data storage
	public int generationIndex() {
		if (this == RUBY || this == SAPPHIRE || this == EMERALD
				|| this == FIRERED || this == LEAFGREEN) {
			return 0;
		} else if (this == DIAMOND || this == PEARL || this == PLATINUM
				|| this == HEARTGOLD || this == SOULSILVER) {
			return 1;
		} else {
			return 2;
		}
	}
}
