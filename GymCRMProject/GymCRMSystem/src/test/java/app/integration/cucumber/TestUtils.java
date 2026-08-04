package app.integration.cucumber;

import java.time.LocalDate;
import java.util.List;
import java.util.StringTokenizer;

public class TestUtils {

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

    public static List<String> splitUsername(String username) {
        StringTokenizer stringTokenizer = new StringTokenizer(username, ".");
        String firstName = stringTokenizer.nextToken();
        String lastName = stringTokenizer.nextToken();
        lastName = lastName.replaceAll("[0-9]", "");
        return List.of(firstName, lastName);
    }

}
