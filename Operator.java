public enum Operator {
    NOT("¬", false),
    AND("∧", true),
    OR("∨", true),
    XOR("⊕", true);

    private final String strRep;
    private final boolean multiArgs;

    Operator(String strRep, boolean multiArgs) {
        this.strRep = strRep;
        this.multiArgs = multiArgs;
    }

    String getStrRep() {return strRep;}
    boolean getMultiArgs() {return multiArgs;}
}
