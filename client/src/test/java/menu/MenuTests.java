package menu;

import org.junit.jupiter.api.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MenuTests {
    // Single responsibility: Interface for various menu objects.
    // When given a list of options, displays sequentially them for the user to select.

    // Has attributes: List of MenuOptions
    // Had methods: addOption(MenuOption), selectOptions()

    private Menu menu;
    private MenuOption option1;
    private MenuOption option2;

    private int add(int x, int y) {
        return x + y;
    }

    private String getWord(String word) {
        return "The word is: " + word;
    }

    @BeforeAll
    public void setUp() {
        this.option1 = new MenuOption("Add two numbers", () -> add(1, 2));
        this.option2 = new MenuOption("Get a word", () -> getWord("Hello"));
    }

    @BeforeEach
    public void setUpMenu() {
        this.menu = new Menu("Abort", "Help", "Menu");
        menu.addOption(option1);
        menu.addOption(option2);
    }

    @Test
    @DisplayName("Menu prints options correctly")
    public void testMenuPrintsOptionsCorrectly() {
        menu.printMenu();
    }
}
