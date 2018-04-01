package dfa;

import com.google.common.base.Preconditions;
import nfa.NFA;
import nfa.NFAState;

import java.util.*;

/**
 * A deterministic finite-state automaton.
 */
public class DFA {
    public DFA(DFAState startState) {
        this.startState = startState;
    }

    /**
     * Clone the automaton. This creates a copy of all states and their transitions.
     *
     * @return The cloned automaton.
     */
    public DFA clone() {
        Map<DFAState, DFAState> cloneMapping = new HashMap<DFAState, DFAState>();
        Set<DFAState> states = getStates();

        for (DFAState state : states) {
            DFAState cloneState = new DFAState(state.isAccept());
            cloneMapping.put(state, cloneState);
        }

        for (Map.Entry<DFAState, DFAState> stateCloneState : cloneMapping.entrySet()) {
            DFAState state = stateCloneState.getKey();
            DFAState cloneState = stateCloneState.getValue();

            for (Map.Entry<Character, DFAState> transition : state.getTransitions().entrySet()) {
                cloneState.addTransition(transition.getKey(), cloneMapping.get(transition.getValue()));
            }
        }

        return new DFA(cloneMapping.get(getStartState()));
    }

    public DFAState getStartState() {
        return startState;
    }

    public Set<DFAState> getAcceptStates() {
        Set<DFAState> acceptStates = new HashSet<DFAState>();

        for (DFAState state : getStates()) {
            if (state.isAccept()) {
                acceptStates.add(state);
            }
        }

        return acceptStates;
    }

    /**
     * Get the states in the automaton (all states that are accessible through the start state).
     *
     * @return The automaton's states.
     */
    public Set<DFAState> getStates() {
        Set<DFAState> states = new HashSet<DFAState>();

        Queue<DFAState> stateQueue = new LinkedList<DFAState>();
        stateQueue.add(startState);
        while (!stateQueue.isEmpty()) {
            DFAState state = stateQueue.poll();
            states.add(state);

            for (DFAState toState : state.getTransitions().values()) {
                if (!states.contains(toState)) {
                    stateQueue.add(toState);
                }
            }
        }

        return states;
    }

    /**
     * Number states. Used by {@link #toDot()} to obtain state numbers. The traversal is
     * breadth-first, so that deeper states tend to get higher numbers.
     */
    private Map<DFAState, Integer> numberStates() {
        Map<DFAState, Integer> numbers = new HashMap<DFAState, Integer>();

        int i = 0;
        Queue<DFAState> stateQueue = new LinkedList<DFAState>();
        stateQueue.add(startState);
        while (!stateQueue.isEmpty()) {
            DFAState state = stateQueue.poll();
            if (numbers.containsKey(state)) {
                continue;
            }

            numbers.put(state, i++);

            for (DFAState toState : state.getTransitions().values()) {
                stateQueue.add(toState);
            }
        }

        return numbers;
    }

    /**
     * Output the automaton in Graphviz dot format.
     */
    public String toDot() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("digraph G {\nrankdir=LR;\n");

        Map<DFAState, Integer> stateNumbers = numberStates();

        // We want to traverse states a fixed order, so that the output is predictable. We
        // could also store the numbered states in a TreeMap, but State doesn't implement
        // Comparable, and I wouldn't even know what that would mean ;).
        DFAState[] states = new DFAState[stateNumbers.size()];

        for (Map.Entry<DFAState, Integer> numberedState : stateNumbers.entrySet())
            states[numberedState.getValue()] = numberedState.getKey();

        for (int stateNumber = 0; stateNumber < states.length; ++stateNumber) {
            DFAState s = states[stateNumber];

            if (s.isAccept())
                stringBuilder.append(String.format("%d [peripheries=2];\n", stateNumber));

            for (Map.Entry<Character, DFAState> trans : s.getTransitions().entrySet())
                stringBuilder.append(String.format("%d -> %d [label=\"%c\"];\n", stateNumber,
                        stateNumbers.get(trans.getValue()), trans.getKey()));
        }

        stringBuilder.append("}");
        return stringBuilder.toString();
    }


    /**
     * RECOGNIZE if a string is valid according to this NFA.
     * uses BFS approach
     * @param string The string to be recognize.
     * @return {@code true} if the string could be recognized, {@code false} otherwise.
     */
    public boolean recognize(String string) {
        Preconditions.checkNotNull(string);

        int index = 0; // for tracking our position in the string
        Queue<DFA.StatePair> memory = new LinkedList<>(); // store states still to visit
        memory.add(new DFA.StatePair(index, this.startState)); // add the first state to the visit list

        while (!memory.isEmpty()) {
            DFA.StatePair sp = memory.poll();
            DFAState curState = sp.getState();
            index = sp.getIndex();
            if (index == string.length() && curState.isAccept()) return true;

            if (index < string.length()) {
                DFAState transition = curState.to(string.charAt(index));

                if (transition != null) memory.add(new DFA.StatePair(index + 1, transition));

            }
        }
        return false;
    }

    /**
     * State-string index pair. Used for keeping track of search states when recognizing a
     * string using the automaton.
     */
    private class StatePair {
        private final int index;
        private final DFAState state;

        public StatePair(int index, DFAState state) {
            this.index = index;
            this.state = state;
        }

        public int getIndex() {
            return index;
        }

        public DFAState getState() {return state;
        }
    }


    private final DFAState startState;
}
