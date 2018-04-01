package dfa;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import java.util.*;

/**
 * State in a deterministic finite-state automaton.
 */
public class DFAState {
    public DFAState(boolean accept) {
        this.accept = accept;
        transitions = new HashMap<>();
    }

    /**
     * Add an outgoing transition to this state.
     *
     * @param ch The character on the transition.
     * @param to The state the transition leads to.
     */
    public void addTransition(char ch, DFAState to) {
        Preconditions.checkNotNull(to);
        transitions.put(ch, to);
    }

    /**
     * Get all transitions that leave this state.
     */
    Map<Character, DFAState> getTransitions() {
        return transitions;
    }

    public boolean isAccept() {
        return accept;
    }

    public void setAccept(boolean accept) {
        this.accept = accept;
    }

    /**
     * Get the state that is reached by following a transition for the given character.
     *
     * @param c Transition character.
     * @return The reached state, or <tt>null</tt> if there is no outgoing transition for the character.
     */
    public DFAState to(char c) {
        return transitions.get(c);
    }


    private final Map<Character, DFAState> transitions;
    private boolean accept;
}
