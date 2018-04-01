package dfa;


import com.google.common.base.Preconditions;
import nfa.NFA;
import nfa.NFAState;

import java.util.*;

/**
 * Created by timday on 16.12.17.
 */
public class DFAOperations {

    /**
     * REVERSAL ALGO
     * - original start state becomes rev accept state
     * - original accept states merged into a rev start state (which has accept status  if original start state was accept)
     */
    public static NFA reversalDFA(DFA dfa){
        Preconditions.checkNotNull(dfa);
        DFA cloneDFA = dfa.clone();

        Queue<DFAState> oMemory = new LinkedList<>();
        Queue<NFAState> rMemory = new LinkedList<>();
        DFAState oCurState = cloneDFA.getStartState();
        NFAState revStartState = new NFAState(oCurState.isAccept());
        NFAState revAccState = new NFAState(true);

        oMemory.add(oCurState);
        rMemory.add(revAccState);

        Map<DFAState,NFAState> originalToReverseMap = new HashMap<>();
        originalToReverseMap.put(oCurState,revAccState);

        while (!oMemory.isEmpty()){

            oCurState = oMemory.poll();
            NFAState rCurState = rMemory.poll();

            for (Character x : oCurState.getTransitions().keySet()){

                DFAState oTransState = oCurState.to(x);

                boolean alreadyVisited = originalToReverseMap.containsKey(oTransState);

                 if (oTransState.isAccept() && alreadyVisited) { //SPECIAL CASE
                    revStartState.addTransition(x,rCurState);

                } else if (oTransState.isAccept() && !alreadyVisited) {

                    revStartState.addTransition(x,rCurState);

                    oMemory.add(oTransState);
                    rMemory.add(revStartState);
                    originalToReverseMap.put(oTransState,revStartState);

                } else if (!alreadyVisited) {

                    NFAState newRevState = new NFAState(false);
                    newRevState.addTransition(x,rCurState);

                    oMemory.add(oTransState);
                    rMemory.add(newRevState);
                    originalToReverseMap.put(oTransState,newRevState);

                } else if (alreadyVisited){
                    originalToReverseMap.get(oTransState).addTransition(x,rCurState);
                }
            }
        } // end while
        return new NFA(revStartState);
    }
}
