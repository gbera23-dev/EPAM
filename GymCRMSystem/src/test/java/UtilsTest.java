import Utils.StringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UtilsTest {


    @Test
    public void testGenerateRandomPassword() {
        String generatedPassword = StringUtils.generateRandomPassword();
        String secondPassword = StringUtils.generateRandomPassword();

        //size is equal to 10
        assertEquals(10, generatedPassword.length());

        //generated passwords should be random
        assertNotEquals(generatedPassword, secondPassword);
    }

}
