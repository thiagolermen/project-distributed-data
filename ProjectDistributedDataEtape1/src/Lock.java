enum Lock {
	RL, // Read lock state
	WL, // Write lock state
    NL, // No local lock
    RLC, // Read lock cached (not taken)
    WLC, // Write lock cached
    RLT, // Read lock taken
    WLT, // Write lock taken
    RLT_WLC // Read lock taken and write lock cached
}
