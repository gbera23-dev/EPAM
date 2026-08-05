package app.integration.cucumber;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicLong;

public class TestUtils {

    public static final String KNOWN_PASSWORD = "test-password";

    private static final AtomicLong CLIENT_ADDRESS_COUNTER = new AtomicLong();

    public static String uniqueClientAddress() {
        long id = CLIENT_ADDRESS_COUNTER.incrementAndGet();
        return "10.%d.%d.%d".formatted((id >> 16) & 0xFF, (id >> 8) & 0xFF, id & 0xFF);
    }

    public static LocalDate toLocalDate(String date) {

        StringTokenizer stringTokenizer = new StringTokenizer(date, "-");

        String year = stringTokenizer.nextToken();
        String month = stringTokenizer.nextToken();
        String day = "1";
        if(stringTokenizer.hasMoreTokens()) {
            day = stringTokenizer.nextToken();
        }

        return LocalDate.of(Integer.parseInt(year), Integer.parseInt(month),
                Integer.parseInt(day));
    }

    public static List<String> splitCommaSeparated(String values) {

        StringTokenizer stringTokenizer = new StringTokenizer(values, ",");

        List<String> tokens = new ArrayList<>();
        while (stringTokenizer.hasMoreTokens()) {
            tokens.add(stringTokenizer.nextToken().trim());
        }

        return tokens;
    }

    public static List<String> splitUsername(String username) {
        StringTokenizer stringTokenizer = new StringTokenizer(username, ".");
        String firstName = stringTokenizer.nextToken();
        String lastName = stringTokenizer.nextToken();
        lastName = lastName.replaceAll("[0-9]", "");
        return List.of(firstName, lastName);
    }

}