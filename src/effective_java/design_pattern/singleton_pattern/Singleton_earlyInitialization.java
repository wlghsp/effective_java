package effective_java.design_pattern.singleton_pattern;


// 이른 초기화
public class Singleton_earlyInitialization {
    private static Singleton_earlyInitialization instance = new Singleton_earlyInitialization();

    private Singleton_earlyInitialization() {} // 생성자를 private으로

    public static Singleton_earlyInitialization getInstance() {
        return instance;
    }
}

/*
이른 초기화는 클래스가 호출 될 때 인스턴스를 생성하는 방법.
다만 인스턴스를 사용하지 않아도 생성하기 때문에 효율성이 떨어진다. (사용하지 않아도 자리를 잡음)

프로그램 실행 시, 전역에서의 싱글톤 클래스의 생성을 알 수 없으므로 해당 단계에서 해당 싱글톤 클래스와 다른 클래스 또는 함수에서 싱글톤 클래스를
참조하고자 하면 문제가 생길 수 있다.
 */