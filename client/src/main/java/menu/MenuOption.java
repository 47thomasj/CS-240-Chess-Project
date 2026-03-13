package menu;

public class MenuOption {
    
    private final String display;
    private final Runnable function;

    public MenuOption(String display, Runnable function) {
        this.display = display;
        this.function = function;
    }

    public void execute() {
        function.run();
    }

    @Override
    public String toString() {
        return display;
    }
}
