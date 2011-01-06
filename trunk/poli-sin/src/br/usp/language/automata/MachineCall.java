package br.usp.language.automata;

public class MachineCall extends Transition {

    /** Action made when a submachine is called */
    private Action actionBefore;

    private String calledMachineName;

    /**
     * Creates a new Machine Call. Condition will be defined as the calledMachine's name.
     * 
     * @param from state from the calling machine
     * @param to return state from the calling machine
     * @param actionBefore action executed when the machine is called
     * @param actionAfter action executed when the machine returns
     * @param calledMachine machine to be called
     * 
     * */
    public MachineCall(State from, State to, Action actionBefore, Action actionAfter, String calledMachineName) {
        super(from, to, calledMachineName, actionAfter);
        this.actionBefore = actionBefore;
        this.calledMachineName = calledMachineName;
    }

    public Action getActionBefore() {
        return actionBefore;
    }
    public void setActionBefore(Action actionBefore) {
    	this.actionBefore = actionBefore;
    }

    public String getCalledMachineName() {
        return calledMachineName;
    }

    @Override
    public boolean isMachineCall() {
        return true;
    }

    public void executeActionBefore() {
        if (this.actionBefore != null)
            this.actionBefore.doAction();
    }
}
