package nfa;

import com.google.common.base.Preconditions;
import com.google.common.collect.SetMultimap;

import javax.swing.plaf.nimbus.State;
import java.util.*;

/**
 * A non-deterministic finite-state automaton.
 */
public class NFA {
    public NFA(NFAState startState) {
        this.startState = startState;
    }

    /**
     * Clone the automaton. This creates a copy of all states and their transitions.
     * @return The cloned automaton.
     */
    public NFA clone() {
        Map<NFAState, NFAState> cloneMapping = new HashMap<>();
        Set<NFAState> states = getStates();

        for (NFAState state : states) {
            NFAState cloneState = new NFAState(state.isAccept());
            cloneMapping.put(state, cloneState);
        }

        for (Map.Entry<NFAState, NFAState> stateCloneState : cloneMapping.entrySet()) {
            NFAState state = stateCloneState.getKey();
            NFAState cloneState = stateCloneState.getValue();

            for (Map.Entry<Character, NFAState> transition : state.getTransitions().entries()) {
                cloneState.addTransition(transition.getKey(), cloneMapping.get(transition.getValue()));
            }
        }
        return new NFA(cloneMapping.get(getStartState()));
    }

    public NFAState getStartState() {
        return startState;
    }

    public Set<NFAState> getAcceptStates() {
        Set<NFAState> acceptStates = new HashSet<>();

        for (NFAState state : getStates()) {
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
    public Set<NFAState> getStates() {
        Set<NFAState> states = new HashSet<>();

        Queue<NFAState> stateQueue = new LinkedList<>();
        stateQueue.add(startState);
        while (!stateQueue.isEmpty()) {
            NFAState state = stateQueue.poll();
            states.add(state);

            for (NFAState toState : state.getTransitions().values()) {
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
    private Map<NFAState, Integer> numberStates() {
        Map<NFAState, Integer> numbers = new HashMap<>();

        int i = 0;
        Queue<NFAState> stateQueue = new LinkedList<>();
        stateQueue.add(startState);
        while (!stateQueue.isEmpty()) {
            NFAState state = stateQueue.poll();
            if (numbers.containsKey(state)) {
                continue;
            }

            numbers.put(state, i++);

            for (NFAState toState : state.getTransitions().values()) {
                stateQueue.add(toState);
            }
        }
        return numbers;
    }


    /**
     * RECOGNIZE
     * ...if a string is valid according to this NFA.
     * uses a BFS approach to traverse NFA
     * @param string The string to be recognize.
     * @return {@code true} if the string could be recognized, {@code false} otherwise.
     */
    public boolean recognize(String string) {
        Preconditions.checkNotNull(string);

        int index = 0; // for tracking our position in the string
        Queue<StatePair> memory = new LinkedList<>(); // store states still to visit
        memory.add(new StatePair(index, this.startState)); // add the first state to the visit list

        while (!memory.isEmpty()) {
            StatePair sp = memory.poll();
            NFAState curState = sp.getState();
            index = sp.getIndex();
            if (index == string.length() && curState.isAccept()) return true; //we are at end of string and an accState

            if (index < string.length()) {
                Set<NFAState> transitions = curState.to(string.charAt(index));
                for (NFAState s : transitions) {
                    memory.add(new StatePair(index + 1, s));
                }
            }
        }
        return false;
    }


    /**
     * Output the automaton in Graphviz dot format.
     */
    public String toDot() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("digraph G {\nrankdir=LR;\n");

        Map<NFAState, Integer> stateNumbers = numberStates();

        // We want to traverse states a fixed order, so that the output is predictable. We
        // could also store the numbered states in a TreeMap, but State doesn't implement
        // Comparable, and I wouldn't even know what that would mean ;).
        NFAState[] states = new NFAState[stateNumbers.size()];

        for (Map.Entry<NFAState, Integer> numberedState : stateNumbers.entrySet())
            states[numberedState.getValue()] = numberedState.getKey();

        for (int stateNumber = 0; stateNumber < states.length; ++stateNumber) {
            NFAState s = states[stateNumber];

            if (s.isAccept())
                stringBuilder.append(String.format("%d [peripheries=2];\n", stateNumber));

            for (Map.Entry<Character, NFAState> trans : s.getTransitions().entries())
                stringBuilder.append(String.format("%d -> %d [label=\"%c\"];\n", stateNumber,
                        stateNumbers.get(trans.getValue()), trans.getKey()));
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    /**
     * State-string index pair. Used for keeping track of search states when recognizing a
     * string using the automaton.
     */
    private class StatePair {
        private final int index;
        private final NFAState state;

        public StatePair(int index, NFAState state) {
            this.index = index;
            this.state = state;
        }

        public int getIndex() {
            return index;
        }

        public NFAState getState() {
            return state;
        }
    }

    //CLASS INSTANCE VARIABLES
    private final NFAState startState;
}
