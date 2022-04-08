package effective_java.design_pattern.decorator_pattern;

public class Decorator implements Component{

    private Component coffeeComponent;

    public Decorator(Component coffeeComponent) {
        this.coffeeComponent = coffeeComponent;
    }

    public String add() {
        return coffeeComponent.add();
    }
}
