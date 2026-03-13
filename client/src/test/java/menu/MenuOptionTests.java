package menu;

import org.junit.jupiter.api.*;

public class MenuOptionTests {
    // Single responsibility: Interface for various menu options.
    // Has attributes: Display, function
    // Had methods: execute()

    private Runnable option1 = () -> { System.out.println("Option 1 selected"); };

    @Test
    @DisplayName("MenuOption as String prints correctly")
    public void testMenuOptionAsStringPrintsCorrectly() {
        MenuOption option = new MenuOption("Option 1", option1);
        Assertions.assertEquals("Option 1", option.toString());
    }
}
