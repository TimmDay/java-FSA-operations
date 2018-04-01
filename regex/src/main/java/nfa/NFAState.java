package nfa;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * State in a non-deterministic finite-state automaton.
 */
public class NFAState {
    public NFAState(boolean accept) {
        this.accept = accept;
        transitions = HashMultimap.create();
    }

    /**
     * Add an epsilon transition to the given state. Note that the epsilon is
     * not implemented as an explicit transition. The transitions of the to-state
     * are just copied to this state. Moreover, if the to-state is accepting,
     * this state also becomes accepting. This means that the to-state should be
     * 'frozen' (does not get new transitions later), otherwise changes are not
     * reflected in this state.
     *
     * @param to The state the transition leads to.
     */
    public void addEpsilon(NFAState to) {
        Preconditions.checkNotNull(to);

        if (to.isAccept()) {
            setAccept(true);
        }

        for (Map.Entry<Character, NFAState> toTransition : to.transitions.entries()) {
            transitions.put(toTransition.getKey(), toTransition.getValue());
        }
    }

    /**
     * Add an outgoing transition to this state.
     *
     * @param ch The character on the transition.
     * @param to The state the transition leads to.
     */
    public void addTransition(char ch, NFAState to) {
        transitions.put(ch, to);
    }

    /**
     * Get all transitions that leave this state.
     */
    SetMultimap<Character, NFAState> getTransitions() {
        return transitions;
    }

    public boolean isAccept() {
        return accept;
    }

    public void setAccept(boolean accept) {
        this.accept = accept;
    }


    /**
     * Get the states that are reached by following a transition for the given character.
     * @param c Transition character.
     * @return The reached states, or the empty set if there is no outgoing transition
     * for the character.
     */
    public Set<NFAState> to(char c) {
        Set<NFAState> toStates = transitions.get(c);
        if (toStates == null)
            return new HashSet<>();

        return Collections.unmodifiableSet(toStates);
    }

    // INSTANCE VARIABLES
    private final SetMultimap<Character, NFAState> transitions;
    private boolean accept;
}
