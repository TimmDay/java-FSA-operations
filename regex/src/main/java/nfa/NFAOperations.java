package nfa;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;
import dfa.DFA;
import dfa.DFAOperations;
import dfa.DFAState;

import java.util.*;

public class NFAOperations {
    /**
     * Create an NFA that recognizes one character.
     * it has an initial state, and one transition of the arg char, leading to an accepting state
     * @param c The character to recognize.
     * @return An NFA.
     */
    public static NFA character(char c) {
        NFAState startState = new NFAState(false);
        NFAState acceptState = new NFAState(true);
        startState.addTransition(c, acceptState);
        return new NFA(startState);
    }

    /**
     * CONCATENATE
     * Concatenate two automata. The strings in the language of the resulting
     * automaton should be of form <i>xy</i>, where <i>x</i> is in the language
     * of the first automaton and <i>y</i> in the language of the second automaton.
     * @return The concatenation automaton.
     */
    public static NFA concatenate(NFA nfa, NFA nfa2) {
        Preconditions.checkNotNull(nfa);
        Preconditions.checkNotNull(nfa2);
        NFA result = nfa.clone();
        NFA cloneNFA2 = nfa2.clone();

        // all accStates of NFA1 are made non-accepting and point to start state of NFA2
        NFAState startNFA2 = cloneNFA2.getStartState();
        for (NFAState state : result.getAcceptStates()) {
            state.setAccept(false);
            state.addEpsilon(startNFA2);
        }
        return result;
    }

    /**
     * KLEENESTAR
     * Create a repetition of an automaton. This automaton should accept the empty string
     * and all strings that are a repetition of any element of the automaton. E.g.
     * <i>(foo|bar)* = {"", "foo", "bar", "foofoo", "foobar", "barfoo", ...}.
     * @return The repetition automaton.
     */
    public static NFA kleeneStar(NFA nfa) {
        Preconditions.checkNotNull(nfa);
        NFA cloneNFA = nfa.clone();

        NFAState stateK = new NFAState(true); // make a new accepting state k
        stateK.addEpsilon(cloneNFA.getStartState()); // k points to old start state

        for (NFAState state : cloneNFA.getAcceptStates()) { // all accStates (but not k) epsilon transition to k
            state.addEpsilon(stateK);
        }
        return new NFA(stateK); // return new NFA with k as start state
    }

    /**
     * UNION
     * Create the union of two automata - the resulting automaton should accept all
     * strings accepted by the first and the second automaton.
     * @return The union automaton.
     */
    public static NFA union(NFA nfa, NFA nfa2) {
        Preconditions.checkNotNull(nfa);
        Preconditions.checkNotNull(nfa2);
        NFA cloneNFA1 = nfa.clone();
        NFA cloneNFA2 = nfa2.clone();

        NFAState startState = new NFAState(false); // new startState, epsilonT to both existing startStates
        startState.addEpsilon(cloneNFA1.getStartState());
        startState.addEpsilon(cloneNFA2.getStartState());

        NFA result = new NFA(startState);

        NFAState endState = new NFAState(true); // new endState(accepting). All existing accStates point to this
        for (NFAState s : result.getAcceptStates()){
            s.setAccept(false);
            s.addEpsilon(endState);
        }
        return result;
    }



    /**
     * DETERMINIZE
     * take an NFA and return a deterministic version of it (DFA)
     * algorithm uses the subset construction
     * @param nfa
     * @return
     */
    public static DFA determinize(NFA nfa) {
        Preconditions.checkNotNull(nfa);
        Set<NFAState> startSet = ImmutableSet.of(nfa.getStartState()); // a set containing only the startState
        Map<Set<NFAState>, DFAState> stateMapping = new HashMap<>();   //starts empty. maps Set<NFaState> to DFAState
        return new DFA(determinize(startSet, stateMapping));
    }

    // returns the start state of the dfa
    private static DFAState determinize(Set<NFAState> stateSet, Map<Set<NFAState>, DFAState> stateMapping) {

        Queue<Set<NFAState>> memory = new LinkedList<>();
        memory.add(stateSet);
        DFAState curDFAst = new DFAState(false);
        stateMapping.put(stateSet, curDFAst);

        while (!memory.isEmpty()) {

            // remove the current stateSet of nfas (mapped to DFAstate) from the queue for processing
            // and use it to retrieve the current DFAState
            Set<NFAState> curStateSet = memory.poll();
            curDFAst = stateMapping.get(curStateSet);


            // use map to store/collect transitions from each NFASt in the set, to collate into the DFASt transitions
            // as per the subset construction technique
            Map<Character, Set<NFAState>> dfaStTransitionBuild = new HashMap<>();

            // update curDFAState: update accStatus and transitions, by going through each transition of each NFAState
            // in the Set<NFAState> that the DFAState maps to
            for (NFAState ns : curStateSet) {
                if (ns.isAccept()) curDFAst.setAccept(true); //if curStateSet has an accState in it, the dfa is accept

                for (Character x : ns.getTransitions().keySet()){  // go through all char transitions of this state
                    Set<NFAState> accessibleFromX = ns.to(x); // get the destination states (returns non-modifiable set)

                    // add these to the existing state set so far (create one if not) for this char.
                    Set<NFAState> setSoFarForX = dfaStTransitionBuild.get(x); // pull from outer storage, add new entries to it

                    if (setSoFarForX != null){ // we have an existing set for this char, add to it
                        Set<NFAState> updatedSet = new HashSet<>();
                        for (NFAState item : setSoFarForX){
                            updatedSet.add(item);
                        }
                        for (NFAState dest : accessibleFromX) {
                            updatedSet.add(dest); //additional destinations added!
                        }
                        dfaStTransitionBuild.put(x,updatedSet); // put back to transition builder,

                    } else { // nothing exists for this transition char yet, create it
                        dfaStTransitionBuild.put(x,accessibleFromX);
                    }
                }
            } //end for each NFAState in curStateSet


            // we now have the complete DFATransitions.
            // the destination set from each transition char represents both:
            // - a transition for curDFA, and
            // - a new DFA (if we don't already have it on stateMapping)

            for (Character x : dfaStTransitionBuild.keySet()){
                Set<NFAState> destStates = dfaStTransitionBuild.get(x); //get set destination states for this char

                // add new DFA to mapping
                // but only if we haven't already... check that
                // if we have already added it, point curDFAst to the existing state instead of making the new one
                DFAState nextDFAst = new DFAState(false);

                boolean addedAlready = false;
                for (Set<NFAState> key: stateMapping.keySet()){
                    if (destStates.equals(key)){
                        curDFAst.addTransition(x,stateMapping.get(key)); //
                        addedAlready = true;
                    }
                }
                if (!addedAlready) curDFAst.addTransition(x,nextDFAst);

                // finally, add this new dfaState nextDFAst to the mapping. The transitions will be added next
                // iteration when nextDFAst becomes curDFAst.
                if (!stateMapping.containsKey(destStates)) {
                    memory.add(destStates);
                    stateMapping.put(destStates, nextDFAst);
                }

            } //end for each. DFA state Transitions for this Set<NFAState>/DFASt
        } // end while
        return stateMapping.get(stateSet); //returns the first DFAState, which references the rest of DFA
    }




    /**
     * REVERSAL NFA
     * returns the input nfa, reversed
     * @return NFA object reversed
     */
    // PSEUDO for future me
    // build a reversed NFA in parallel
    // o - original
    // rev - reversed
    // cur - current
    // oCurState - the current state from the original NFA that we are looking at
    // we will combine the original accStates into one state. revStartState
    // so all transitions in oNFA that point to an accepting state, will be pointed to fromm revStartState

    // build revNFA as we go through oNFA
    // for each subsequent oNode, we want to make a matching revNode (if not visited yet) and reverse the transition
    // by pointing from the next revNode back to current. then adding that node to the queue)

    public static NFA reversalNFA(NFA nfa){
        Preconditions.checkNotNull(nfa);
        NFA cloneNFA = nfa.clone(); // be careful to not modify the original arg

        Queue<NFAState> oMemory = new LinkedList<>();
        Queue<NFAState> rMemory = new LinkedList<>();
        NFAState oCurState = cloneNFA.getStartState(); // for traversing oNFA
        NFAState revStartState = new NFAState(oCurState.isAccept()); //revStart has same acceptStatus as oStart
        NFAState revAccState = new NFAState(true); // always accepting

        oMemory.add(oCurState);
        rMemory.add(revAccState); //we start from the back. mirroring. revAcc mirrors oStart


        //make a memory of NFAStates already visited, and the rState that corresponds. so we can retrieve it when needed
        //and keep track of whether we have visited a state (by using !keyset.contains(key))
        Map<NFAState,NFAState> originalToReverseMap = new HashMap<>(); // key is already visited oNFAState
        originalToReverseMap.put(oCurState,revAccState);

        //the traversal through oNFA
        while (!oMemory.isEmpty()){

            oCurState = oMemory.poll();
            NFAState rCurState = rMemory.poll();

            //each transition of oCurrState
            for (Character x : oCurState.getTransitions().keySet()){
                for (NFAState oTransState : oCurState.to(x)){ //transition points to a set of states (nondeterministic)

                    // this transition destination can be categorized as follows.
                    // 1. accState, already visited.
                    //    - is is oAccept, which mean we are pooling it into revStart, to transition from that
                    // 2. already visited node
                    //    - transition from the revState corresponding to the alreadyvisited oState, to the rCurState
                    // 3. accState, not already visited.
                    //    - the revStartState (the oAccStates) will point to revCur.
                    // 4. not already visited node


                    boolean alreadyVisited = originalToReverseMap.containsKey(oTransState);

                    if (oTransState.isAccept() && alreadyVisited) { // 1.
                        revStartState.addTransition(x,rCurState);

                    } else if (alreadyVisited){ // 2.
                        originalToReverseMap.get(oTransState).addTransition(x,rCurState);

                    } else if (oTransState.isAccept() && !alreadyVisited) { // 3.

                        revStartState.addTransition(x,rCurState);
                        oMemory.add(oTransState);
                        rMemory.add(revStartState);
                        originalToReverseMap.put(oTransState,revStartState);

                    } else if (!alreadyVisited) { // 4.

                        NFAState newRevState = new NFAState(false);
                        newRevState.addTransition(x, rCurState);

                        oMemory.add(oTransState);
                        rMemory.add(newRevState);
                        originalToReverseMap.put(oTransState, newRevState);
                    }
                }
            }
        } // end while
        return new NFA(revStartState);
    }


    /**
     * MINIMIZE
     * uses the Brzozowski algorithm
     * @param nfa
     * @return a DFA, minimized
     */
    public static DFA minimize(NFA nfa) {
        NFA clone = nfa.clone();
        NFA rev_1 = reversalNFA(clone);
        DFA det_2 = determinize(rev_1);
        NFA rev_3 = DFAOperations.reversalDFA(det_2);
        return determinize(rev_3);
    }

}
