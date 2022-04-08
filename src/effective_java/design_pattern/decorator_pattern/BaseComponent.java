package effective_java.design_pattern.decorator_pattern;

public class BaseComponent implements Component{

    @Override
    public String add() {
        return "에스프레소";
    }
}
