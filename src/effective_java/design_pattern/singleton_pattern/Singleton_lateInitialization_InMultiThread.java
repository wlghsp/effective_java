package effective_java.design_pattern.singleton_pattern;

// 늦은 초기화
public class Singleton_lateInitialization_InMultiThread {
    private static Singleton_lateInitialization_InMultiThread instance;
    private Singleton_lateInitialization_InMultiThread() {} // 생성자를 private로

    public static Singleton_lateInitialization_InMultiThread getInstance() {
        if (instance == null) {
            // synchronized를 활용하여 여러 인스턴스를 생성하는 것을 방지
            synchronized (Singleton_lateInitialization_InMultiThread.class) {
                if (instance == null) {
                    new Singleton_lateInitialization_InMultiThread();
                }
            }
        }
        return instance;
    }
}

/*
멀티 스레드 환경에서 늦은 초기화가 중복으로 생성할 수 있는 문제는 synchronized를 사용하여 여러 쓰레드가 getInstance() 메서드에
동시에 접근하는 것을 방지할 수 있다. 하지만 수 많은 Thread 들이 getInstance() 메서드에 접근하게 되면 정체 현상이 일어나 성능 저하를 야기한다.

이를 해결하기 위하여 위의 예제처럼 Double-Checked Locking 기법을 사용하여 synchronized 영역을 줄일 수 있다.

첫번째 if문에서 인스턴스의 존재 여부를 검사하여 인스턴스가 생성되어 있지 않다면 두 번째 if문에서 다시 한 번 검사할 때 synchronized로 동기화 시키는 방법이다.
이후에 호출될 때는 인스턴스가 이미 생성되어 있기 때문에 synchronized 블록에 접근하지 않는다.

 */