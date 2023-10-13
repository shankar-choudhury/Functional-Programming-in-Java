import java.util.EnumMap;
import java.util.Objects;

public class IntegerCode {
    private EnumMap<Operator, Integer> integerCode;

    private IntegerCode (EnumMap<Operator, Integer> integerCode) {
        this.integerCode = new EnumMap<Operator, Integer>(integerCode);
    }

    public static final IntegerCode of(EnumMap<Operator, Integer> integerCode) {
        return new IntegerCode(Objects.requireNonNull(integerCode));
    }

    private EnumMap<Operator, Integer> getIntegerCode() {return integerCode;}
    private void setIntegerCode(EnumMap<Operator, Integer> integerCode) {
        this.integerCode = integerCode;
    }

    public boolean anyPositive() {
        return getIntegerCode().values().stream()
                .anyMatch(x -> x.compareTo(0) > 0);
    }

    public int occurrences (Operator operator) {
        return getIntegerCode().getOrDefault(operator, 0);
    }

    public void reset() {setIntegerCode(new EnumMap<Operator, Integer>(Operator.class));}

    public String isNegation(Operator operator) {
        assert operator != null;
        return operator.getStrRep().equals("¬") ? "¬" : "";
    }

    /**
     * Using a return type "char" allows for equivalent of each operator to be returned. The ~, &, and | represent a
     * negation, conjunction, and disjunction respectively. Returning a logical symbol is also more meaningful than
     * returning an integer.
     */
    public char aka(Operator operator) {
        assert operator != null;
        return switch (operator) {
            case NOT -> '~';
            case AND -> '&';
            case OR -> '|';
            default -> '-';
        };
    }

    /**
     * Building from the last HW question on how to return multiple values, I created a class that can hold both values,
     * and have options to return each value should that be the case. This is preferable to returning a String that holds
     * all the values, since that would obfuscate the actual types of each value. Using a class to hold both values also
     * allows for the option to return each individual value.
     */
    public strRepAndAkaOf description(Operator operator) {
        assert operator != null;
        return new strRepAndAkaOf(operator);
    }

    private class strRepAndAkaOf {
        private final String opStrRep;
        private final char opAka;
        private strRepAndAkaOf(Operator operator) {
            this.opStrRep = operator.getStrRep();
            this.opAka = aka(operator);
        }
        String getOpStrRep() {
            return opStrRep;
        }
        int getOpAka() {
            return opAka;
        }
    }
}
