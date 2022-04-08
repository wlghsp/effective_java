package effective_java.design_pattern.decorator_pattern;

public class Main {

    public static void main(String[] args) {
        Component espresso = new BaseComponent();
        System.out.println("에스프레소 : " + espresso.add());

        Component americano = new BaseComponent();
        System.out.println("아메리카노 : " + americano.add());

        Component latte = new BaseComponent();
        System.out.println("라떼 : " + latte.add());
    }
}
