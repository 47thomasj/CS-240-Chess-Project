package menu;

import java.util.concurrent.Callable;

public class MenuOption {
    
    private final String display;
    private final Callable<Object> function;

    public MenuOption(String display, Callable<Object> function) {
        this.display = display;
        this.function = function;
    }

    public Object execute() throws Exception {
        return function.call();
    }

    @Override
    public String toString() {
        return display;
    }
}
