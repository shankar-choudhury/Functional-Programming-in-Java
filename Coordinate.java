import java.math.BigDecimal;
import java.util.Objects;

public record Coordinate(BigDecimal horizontal, BigDecimal vertical) {
    public static final Coordinate ORIGIN = new Coordinate(BigDecimal.ZERO, BigDecimal.ZERO);
    public Coordinate {
        Objects.requireNonNull(horizontal);
        Objects.requireNonNull(vertical);
    }
}
