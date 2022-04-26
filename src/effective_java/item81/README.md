# Item 81 wait과 notify보다는 동시성 유틸리티를 애용하라

--------------------------------------------

지금은 wait과 notify를 사용해야할 이유가 많이 줄었다. 

자바 5에서 도입된 고수준의 동시성 유틸리티 덕분이다!

> wait과 notify는 올바르게 사용하기가 아주 까다로우니 고수준 동시성 유틸리티를 사용하자

<hr>

java.util.concurrent의 고수준 유틸리티는 세 범주로 나눌 수 있다. 

1. 실행자 프레임워크
2. 동시성 컬렉션(concurrent collection)
3. 동기화 장치(synchronizer)

여기서는 2, 3번을 다시 살펴본다. 

<hr>

### 동시성 컬렉션

→ List, Queue, Map 같은 표준 컬렉션 인터페이스에 동시성을 가미해 구현한 고성능 컬렉션이다.

높은 동시성에 도달하기 위해 동기화를 각자의 내부에서 수행한다. 

따라서 **동시성 컬렉션에서 동시성을 무력화하는 건 불가능하며, 외부에서 락을 추가로 사용하면 오히려 속도가 느려진다.**

<hr>

동시성 컬렉션에서 동시성을 무력화하지 못하므로 여러 메서드를 원자적으로 묶어 호출하는 일 역시 불가능하다. 

그래서 여러 기본 동작을 하나의 원자적 동작으로 묶는 '상태 의존적 수정' 메서드들이 추가되었다. 

이 메서드들은 아주 유용해서 자바 8에서는 일반 컬렉션 인터페이스에도 디폴트 메서드 형태로 추가되었다. 

<hr>

예를 들어 Map의 putIfAbset(key, value) 메서드는 주어진 키에 매핑된 값이 아직 없을때만 새 값을 집어넣는다. 

그리고 기존 값이 있었다면 그 값을 반환하고, 없었다면 null을 반환한다. 

이 메서드 덕에 스레드 안전한 정규화 맵(canonicalizing map)을 쉽게 구현할 수 있다. 

다음은 String.intern의 동작을 흉내 내어 구현한 메서드다. 

코드 81-1 ConcurrentMap으로 구현한 동시성 정규화 맵 - 최적은 아니다
``` java
private static final ConcurrentMap<String, String> map = new ConcurrentMap<>();

public static String intern(String s) {
    String previousValue = map.putIfAbsent(s,s);
    return previousValue == null ? s : previousValue; 
}
```
 아직 개선할 게 남았다. 

ConcurrentHashMap은 get 같은 검색 기능에 최적화되었다. 

따라서 get을 먼저 호출하여 필요할 때만 putIfAbsent를 호출하면 더 빠르다. 

코드 81-2 ConcurrentHashMap으로 구현한 동시성 정규화 맵 - 더 빠르다. 
``` java
public static String intern(String s) {
    String result = map.get(s);
    if ( result == null) {
        result = map.putIfAbsent(s, s);
        if (result == null)
            result = s;
    }
    return result;
}
```
ConcurrentHashMap은 동시성이 뛰어나며 속도도 무척 빠르다. 

동시성 컬렉션은 동기화한 컬렉션을 낡은 유산으로 만들어버렸다.

예) Collections.synchronizedMap 보다는 ConcurrentHashMap을 사용하는 게 훨씬 좋다.

동기화 된 맵을 동시성 맵으로 교체하는 것만으로 동시성 애플리케이션의 성능은 극적으로 개선된다. 

<hr>

컬렉션 인터페이스 중 일부는 작업이 성공적으로 완료될 때까지 기다리도록(즉, 차단되도록) 확장되었다. 

예) Queue를 확장한 BlockingQueue에 추가된 메서드 중 take는 큐의 첫 원소를 꺼낸다. 

이때 만약 큐가 비었다면 새로운 원소가 추가될 때까지 기다린다. 이런 특성 덕에 BlockingQueue는 작업 큐(생산자-소비자 큐)로 쓰기에 적합하다. 

작업 큐는 하나 이상의 생산자(producer)스레드가 작업(work)을 큐에 추가하고, 하나 이상의 소비자(consumer) 스레드가 큐에 있는 작업을 꺼내 처리하는 형태다. 

짐작하다시피 ThreadPoolExecutor를 포함한 대부분의 실행자 서비스 구현체에서 이 BlockingQueue를 사용한다. 

<hr>

동기화 장치는 스레드가 다른 스레드를 기다릴 수 있게 하여, 서로 작업을 조율할 수 있게 해준다. 

가장 자주 쓰이는 동기화 장치는 CountDownLatch와 Semaphore다. CyclicBarrier와 Exchanger는 그보다 덜 쓰인다. 

그리고 가장 강력한 동기화 장치는 바로 Phaser다.


<hr>
카운트다운 래치(latch: 걸쇠)는 일회성 장벽으로, 하나 이상의 스레드가 또 다른 하나 이상의 스레드 작업이 끝날 때까지 기다리게 한다. 

CountDownLatch의 유일한 생성자는 int 값을 받으며, 이 값이 래치의 countDown 메서드를 몇 번 호출해야 대기 중인 

스레드들을 깨우는지를 결정한다. 

<hr>


코드 81-3 동시 실행 시간을 재는 간단한 프레임워크
``` java
public static long time(Executor executor, int concurrency, Runnable action) throws InterruptedException {
    CountDownLatch ready = new CountDownLatch(concurrency);
    CountDownLatch start = new CountDownLatch(1);
    CountDownLatch done = new CountDownLatch(concurrency);
    
    for (int i = 0; i < concurrency; i++) {
        executor.execute(() -> {
            // 타이머에게 준비를 마쳤음을 알린다.
            ready.countDown();
            try {
                // 모든 작업자 스레드가 준비될 때까지 기다린다. 
                start.await();
                action.run();
            } catch (InterruptedException e) {
                Thread.currnetThread().interrupt();
            } finally {
                // 타이머에게 작업을 마쳤음을 알린다. 
                done.countDown();
            }
        });
    }
    
    ready.await(); // 모든 작업자가 준비될 때까지 기다린다. 
    long startNanos = System.nanoTime();
    start.countDown(); // 작업자들을 깨운다. 
    done.await(); // 모든 작업자가 일을 끝마치기를 기다린다. 
    return System.nanoTime() - startNanos;
}
```
이 코드는 카운트다운 래치를 3개 사용한다. ready 래치는 작업자 스레드들이 준비가 완료됐음을 타이머 스레드에 

통지할 때 사용한다. 통지를 끝낸 작업자 스레드들은 두 번째 래치인 start가 열리기를 기다린다. 

마지막 작업자 스레드가 ready.countDown을 호출하면 타이머 스레드가 시작 시각을 기록하고 start.countDown을 호출하여 

기다리던 작업자 스레드들을 깨운다. 그 직후 타이머 스레드는 세 번째 래치인 done이 열리기를 기다린다. 

done 래치는 마지막 남은 작업자 스레드가 동작을 마치고 done.countDown을 호출하면 열린다. 타이머 스레드는 done 래치가 열리자마자 깨어나 종료 시각을 기록한다. 

> 시간간격을 잴 때는 항상 System.currentTimeMillis가 아닌 System.nanoTime을 사용하자.

System.nanoTime은 더 정확하고 정밀하며 시스템의 실시간 시계의 시간 보정에 영향받지 않는다. 

> 이 예제의 코드는 작업에 충분한 시간(예: 1초 이상)이 걸리지 않는다면 정확한 시간을 측정할 수 없을 것이다. 

정밀한 시간 측정은 매우 어려운 작업이라, 꼭 해야 한다면 jmh같은 특수 프레임워크를 사용해야 한다. 
<hr>

이번 아이템은 동시성 유틸리티를 맛만 살짝 보여준다. 

예컨대 앞 예에서 사용한 카운트다운 래치 3개는 CyclicBarrie(혹은 Phaser) 인스턴스 하나로 대체할 수 있다. 

이렇게하면 코드가 더 명료해지겠지만 아마도 이해하기는 더 어려울 것이다. 

<hr>

코드 81-4 wait 메서드를 사용하는 표준 방식
``` java
synchronized (obj) {
    while (<조건이 충족되지 않았다>)
        obj.wait(); (락을 놓고, 깨어난면 다시 잡는다.)
    
    ... // 조건이 충족됐을 때의 동작을 수행한다.
}
```
> wait 메서드를 사용할 때는 반드시 대기 반복문(wait loop)관용구를 사용하라. 반복문 밖에서는 절대로 호출하지 말라.

대기 전에 조건을 검사하여 조건이 이미 충족되었다면 wait를 건너뛰게 한 것은 응답 불가 상태를 예방하는 조치다. 

### 조건이 만족되지 않아도 스레드가 깨어날 수 있는 상황

* 스레드가 notify를 호출한 다음 다음 대기 중이던 스레드가 깨어나는 사이에 다른 스레드가 락을 얻어 그 락이 보호하는 상태를 변경한다. 
* 조건이 만족되지 않았음에도 다른 스레드가 실수로 혹은 악의적으로 notify를 호출한다. 공개된 객체를 락으로 사용해 대기하는 클래스는 이런 위험에 노출된다. 외부에 노출된 객체의 동기화된 메서드 안에서 호출하는 wait는 모두 이 문제에 영향을 받는다. 
* 깨우는 스레드는 지나치게 관대해서, 대기 중인 스레드 중 일부만 조건이 충족되어도 notifyAll을 호출해 모든 스레드를 깨울 수도 있다. 
* 대기 중인 스레드가 (드물게) notify 없이도 깨어나는 경우가 있다. 허위 각성(spurious wakeup)이라는 현상이다. 

### 핵심 정리
- wait과 notify를 직접 사용하는 것을 동시성 '어셈블리 언어'로 프로그래밍하는 것에 비유할 수 있다. 
- 반면 java.util.concurrent는 고수준 언어에 비유할 수 있다. 
- 코드를 새로 작성한다면 wait과 notify를 쓸 이유가 거의(어쩌면 전혀) 없다. 
- 이들을 사용하는 레거시 코드를 유지보수해야 한다면 wait는 항상 표준 관용구에 따라 while 문 안에서 호출하도록 하자. 
- 일반적으로 notify보다는 notifyAll을 사용해야 한다. 
- 혹시라도 notify를 사용한다면 응답 불가 상태에 빠지지 않도록 각별히 주의하자. 