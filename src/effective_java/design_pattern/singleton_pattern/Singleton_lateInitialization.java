package effective_java.design_pattern.singleton_pattern;

// 늦은 초기화
public class Singleton_lateInitialization {

    private static Singleton_lateInitialization instance;

    private Singleton_lateInitialization () {} // 생성자를 private로

    public static Singleton_lateInitialization getInstance() {
        if (instance == null) {
            instance = new Singleton_lateInitialization();
        }
        return instance;
    }
}

/*
늦은 초기화는 인스턴스를 실제로 사용할 시점에 생성하는 방법입니다.
인스턴스를 실제로 생성하지 않으면 생성하지 않기에 이른 초기화보다 효율성이 좋긴 합니다만 두 스레드가 동시에
싱글톤 인스턴스에 접근하고 생성이 안된 것을 확인하여 생성한다면 중복으로 생성할 수 있다는 문제가 있을 수 있다.


 */