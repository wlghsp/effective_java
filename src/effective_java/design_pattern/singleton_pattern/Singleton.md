
# 싱글톤 패턴(Singleton Pattern)이란?

싱글톤 패턴은 객체를 딱 하나만 생성하여 생성된 객체를 프로그램 어디에서나 접근하여 사용할 수 있도록 하는 패턴을 말한다. 

----------------------------

### 싱글톤 패턴의 장점
1. 메모리 낭비를 방지할 수 있다. 
2. 싱글톤으로 만들어진 클래스와 다른 클래스의 인스턴스들의 데이터 공유가 쉽다. 
3. 인스턴스가 절대적으로 한 개만 존재하는 것을 보증하기에 개발 시 실수를 줄일 수 있다. 
4. 싱글톤 객체를 사용하지 않는 경우 인스턴스를 생성하지 않는다. 
5. 싱글톤을 상속시킬 수 있다. 
<hr>

### 싱글톤 패턴의 단점
1. 전역변수보다 사용하기가 불편하다.
2. 싱글톤의 역할이 커질수록 결합도가 높아져 객체 지향 설계 원칙에 어긋날 수 있다. 
3. 멀티쓰레드 환경에서 컨트롤이 어렵다. 
4. 객체의 파괴 시점을 컨트롤하기 어려울 수 있다. 
<hr>

### 이른 초기화 
``` java
public class Singleton_earlyInitialization {
    private static Singleton_earlyInitialization instance = new Singleton_earlyInitialization();

    private Singleton_earlyInitialization() {} // 생성자를 private으로

    public static Singleton_earlyInitialization getInstance() {
        return instance;
    }
}

```


이른 초기화는 클래스가 호출 될 때 인스턴스를 생성하는 방법.
다만 인스턴스를 사용하지 않아도 생성하기 때문에 효율성이 떨어진다. (사용하지 않아도 자리를 잡음)

프로그램 실행 시, 전역에서의 싱글톤 클래스의 생성을 알 수 없으므로 해당 단계에서 해당 싱글톤 클래스와 다른 클래스 또는 함수에서 싱글톤 클래스를
참조하고자 하면 문제가 생길 수 있다.
<hr>

### 늦은 초기화 
``` java
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
```

늦은 초기화는 인스턴스를 실제로 사용할 시점에 생성하는 방법입니다.
인스턴스를 실제로 생성하지 않으면 생성하지 않기에 이른 초기화보다 효율성이 좋긴 합니다만 두 스레드가 동시에
싱글톤 인스턴스에 접근하고 생성이 안된 것을 확인하여 생성한다면 중복으로 생성할 수 있다는 문제가 있을 수 있다.

<hr>

``` java
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
```

### 멀티 스레드 환경에서의 늦은 초기화
멀티 스레드 환경에서 늦은 초기화가 중복으로 생성할 수 있는 문제는 synchronized를 사용하여 여러 쓰레드가 getInstance() 메서드에
동시에 접근하는 것을 방지할 수 있다. 하지만 수 많은 Thread 들이 getInstance() 메서드에 접근하게 되면 정체 현상이 일어나 성능 저하를 야기한다.

이를 해결하기 위하여 위의 예제처럼 Double-Checked Locking 기법을 사용하여 synchronized 영역을 줄일 수 있다.

첫번째 if문에서 인스턴스의 존재 여부를 검사하여 인스턴스가 생성되어 있지 않다면 두 번째 if문에서 다시 한 번 검사할 때 synchronized로 동기화 시키는 방법이다.
이후에 호출될 때는 인스턴스가 이미 생성되어 있기 때문에 synchronized 블록에 접근하지 않는다.
