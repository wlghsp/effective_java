package effective_java.design_pattern.decorator_pattern;

public class MilkDecorator extends Decorator{
    public MilkDecorator(Component coffeeComponent) {
        super(coffeeComponent);
    }

    @Override
    public String add() {
        return super.add() + " + 우유";
    }
}
