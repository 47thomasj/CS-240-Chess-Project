package menu;

import org.junit.jupiter.api.*;

import menu.MenuOption;
import java.util.function.Supplier;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MenuOptionTests {
    // Single responsibility: Interface for various menu options.
    // Has attributes: Display, function
    // Had methods: execute()

    private Runnable option1;
    private Runnable option2;
    private Runnable option3;

    private int add(int x, int y) {
        return x + y;
    }

    private String getWord(String word) {
        return "The word is: " + word;
    }

    @BeforeAll
    public void setUp() {
        this.option1 = () -> "Option 1 selected";
        this.option2 = () -> add(1, 2);
        this.option3 = () -> getWord("Hello");
    }

    @Test
    @DisplayName("MenuOption as String prints correctly")
    public void testMenuOptionAsStringPrintsCorrectly() {
        MenuOption option = new MenuOption("Option 1", option1);
        Assertions.assertEquals("Option 1", option.toString());
    }

    @Test
    @DisplayName("MenuOption as String prints correctly")
    public void testMenuOptionExecutePrintsCorrectly() {
        MenuOption option = new MenuOption("Option 1", () -> System.out.println("Option 1 selected"), new String[] {"arg1", "arg2"});
        option.execute();
        
    }
}
