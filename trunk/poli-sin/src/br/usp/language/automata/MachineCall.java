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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((calledMachineName == null) ? 0 : calledMachineName
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MachineCall other = (MachineCall) obj;
		if (calledMachineName == null) {
			if (other.calledMachineName != null)
				return false;
		} else if (!calledMachineName.equals(other.calledMachineName))
			return false;
		return true;
	}
}
