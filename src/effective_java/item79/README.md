# Item 79 과도한 동기화는 피하라

--------------------------------------------

아이템 78: 충분하지 못한 동기화의 피해 

이번 아이템: 반대의 상황


과도한 동기화는 성능을 떨어뜨리고, 교착상태에 빠뜨리고, 심지어 예측할 수 없는 동작을 낳기도 한다. 

> 응답 불가와 안전 실패를 피하려면 동기화 메서드나 동기화 블록 안에서는 제어를 절대로 클라이언트에 양도하면 안 된다. 

예) 동기화된 영역 안에서는 재정의할 수 있는 메서드는 호출하면 안 되며, 클라이언트가 넘겨준 함수 객체를 호출해서도 안 된다. 

동기화된 영역을 포함한 클래스 관점에서는 이런 메서드는 모두 바깥 세상에서 온 외계인이다. 

그 메서드가 무슨 일을 할지 알지 못하며 통제할 수도 없다는 뜻이다. 

외계인 메서드(alien method)가 하는 일에 따라 동기화된 영역은 예외를 일으키거나, 교착상태에 빠지거나, 데이터를 훼손할 수도 있다. 

<hr>

구체적인 예. 다음은 어떤 집합(set)을 감싼 래퍼 클래스이고, 이 클래스의 클라이언트는 집합에 원소가 추가되면 알림을 받을 수 있다. 

바로 관찰자 패턴이다. 

코드 79-1 잘못된 코드. 동기화 블록 안에서 외계인 메서드를 호출한다. 
``` java
public class Observable<E> extends ForwardingSet<E> {
    public ObservableSet(Set<E> set) { super(set); }
    
    private final List<SetObserver<E>> observers = new ArrayList<>();
    
    public void addObserver(SetObserver<E> observer) {
        synchronized(observers) {
            observers.add(observer);
        }
    }
    
    public boolean removeObserver(SetObserver<E> observer) {
        synchronized(observers) {
            return observers.remove(obeserver);
        }
    }
    
    private void notifyElementAdded(E element) {
        synchronized(observers) {
            for (SetObserver<E> observer : observers)
                observer.added(this, element);
        }
    }
    
    @Override public boolean add(E element) {
        boolean added = super.add(element);
        if (added)
            notifyElementAdded(element);
        return added;
    }
    
    @Override public boolean addAll(Collection<? extends E> c) {
        boolean result = false;
        for (E element : c)
            resutl |= add(element); //notifyElementAdded를 호출한다.
        return added;
    }
}
```

관찰자들은 addObserver와 removeObserver 메서드를 호출해 구독을 신청하거나 해지한다. 

두 경우 모두 다음 콜백 인터페이스의 인스턴스를 메서드에 건넨다. 

``` java
@FunctionalInterface public interface SetObserver<E> {
    //ObservableSet에 원소가 더해지면 호출된다.
    void added(ObservableSet<E> set, E element);
}
```
이 인터페이스는 구조적으로 BiConsumer<ObservableSet<E>, E>와 똑같다. 

그럼에도 커스텀 함수형 인터페이스를 정의한 이유는 이름이 더 직관적이고 다중 콜백을 지원하도록 확장할 수 있어서다. 

하지만 BiConsumer를 그대로 사용했더라도 별 무리는 없었을 것이다. 

눈으로 보기에 ObservableSet은 잘 동작할 것 같다. 

다음 프로그램은 0부터 99까지를 출력한다. 

``` java
public static void main(String[] args) {
    ObservableSet<Integer> set = new ObservableSet<>(new HashSet<>());
    
    set.addObserver((s,e) -> System.out.println(e));
    
    for (int i = 0; i < 100; i++)]
        set.add(i);
}
```

그 값이 23이면 자기 자신을 제거(구독해지)하는 관찰자를 추가해보자. 

``` java
set.addObserver(new SetObserver<>() {
    public void added(ObservableSet<Integer> s, Integer e) {
        System.out.println(e);
        if (e == 23)
            s.removeObserver(this);
    }
});
```
* 람다를 사용한 이전 코드와 달리 익명 클래스를 사용했다. s.removeObserver메서드에 함수 객체 자신을 넘겨야 하기 때문이다. 람다는 자신을 참조할 수단이 없다. 

0부터 23까지 출력한 후 관찰자 자신을 구독해지한 다음 조용히 종료할 것이다. 

그런데 실제로 실행해 보면 그렇게 진행되지 않는다! 

이 프로그램은 23까지 출력한 다음 ConcurrentModificationException을 던진다. 

관찰자의 added 메서드 호출이 일어난 시점이 notifyElementAdded가 관찰자들의 리스트를 순회하는 도중이기 때문이다. 

added메서드는 observableSet의 removeObserver 메서드를 호출하고, 이 메서드는 다시 observers.remove 메서드를 호출한다. 

여기에서 문제가 발생한다. 

리스트에서 원소를 제거하려 하는데, 마침 지금은 이 리스트를 순회하는 도중이다.  즉 허용되지 않은 동작이다. 

notifyElementAdded 메서드에서 수행하는 순회는 동기화 블록 안에 있으므로 동시 수정이 일어나지 않도록 보장하지만, 정작 자신이 콜백을 거렻 되돌아와 수정하는 것까지 막지는 못한다. 


<hr>
구독해지를 하는 관찰자를 작성하는데, removeObserver를 직접 호출하지 않고 실행자 서비스(ExecutorService)를 사용해 다른 스레드한테 부탁할 것이다. 

코드 79-2 쓸데없이 백그라운드 스레드를 사용하는 관찰자
``` java
set.addObserver(new SetObserver<>() {
    public void added(ObservableSet<Integer> s, Integer e) {
        System.out.println(e);
        if (e == 23) {
            ExecutorService exec = Executors.newSingleThreadExecutor();
            try {
                exec.sumbit(() -> s.removeObserver(this)).get();
            } catch (ExecutionException | InterruptedException ex) {
                throw new AssertionError(ex);
            } finally {
                exec.shtdown();
            }
            s.removeObserver(this);
        }
    }
});
```
* 이 프로그램은 catch 구문 하나에서 두 가지 예외를 잡고 있다. 다중 캐치(multi-catch)라고도 하는 이 기능은 자바 7부터 지원한다. 
* 이 기법은 똑같이 처리해야 하는 예외가 여러 개일 때 프로그램 크기를 줄이고 코드 가독성을 크게 개선해준다. 

이 프로그램을 실행하면 예외는 나지 않지만 교착상태에 빠진다. 백그라운드 스레드가 s.removeObserver를 호출하면 관찰자를 잠그려 시도하지만 락을 얻을 수 없다.

메인 스레드가 이미 락을 쥐고 있기 때문이다. 그와 동시에 메인 스레드는 백그라운드 스레드가 관찰자를 제거하기만을 기다리는 중디ㅏ. 바로 교착상태다!



## 이해할 수 없어 이후 내용은 언젠가 다시 작성하겠다. 
<중략>


### 핵심 정리
- 교착상태와 데이터 훼손을 피하려면 동기화 영역 안에서 외계인 메서드를 절대 호출하지 말자. 
- 일반화해 이야기하면, 동기화 영역 안에서의 작업은 최소한으로 줄이자. 
- 가변 클래스를 설계할 때는 스스로 동기화해야 할지 고민하자. 
- 멀티코어 세상인 지금은 과도한 동기화를 피하는 게 과거 어느 때보다 중요하다. 
- 합당한 이유가 있을 때만 내부에서 동기화하고, 동기화했는지 여부를 문서에 명확히 밝히자.