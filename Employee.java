public class Employee implements Comparable<Employee>{
    private int savings;

    public Employee(int savings) {
        this.savings = savings;
    }

    @Override
    public int compareTo(Employee o) {
        return savings - o.savings;
    }
}
