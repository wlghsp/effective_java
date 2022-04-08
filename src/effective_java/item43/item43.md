# Item 43 람다보다는 메서드 참조를 사용하라

--------------------------------------------

람다가 익명 클래스보다 나은 점 중에서 가장 큰 특징은 **간결함**

함수 객체를 람다 보다 더 간결하게 만드는 방법은 뭘까요??


→ 메서드 참조(method reference)


``` java
map.merge(key, 1, (count, incr) -> count + incr);
```
자바 8 때 Map에 추가된 merge 메서드를 사용했다. 

merge 메서드는 키, 값, 함수를 인수로 받으며, 주어진 키가 맵 안에 아직 없다면 주어진 [키, 값] 쌍을 그대로 저장한다. 

반대로 키가 이미 있다면 (세번째 인수로 받은) 함수를 현재 값과 주어진 값에 적용한 다음, 그 결과로 현재 값을 덮어쓴다.

즉 맵에 [키, 함수의 결과] 쌍을 저장한다. 

<hr>

매개변수인 count와 incr 은 크게 하는 일ㅇ 없이 공간을 꽤 차지한다. 

자바 8이 되면서 Integer클래스(와 모든 기본 타입의 박싱 타입)은 이 람다와 기능이 같은 정적 메서드 sum을 제공하기 시작했다. 

따라서 람다 대신 이 메서드의 참조를 전달하면 똑같은 결과를 더 보기 좋게 얻을 수 있다. 

``` java
map.merge(key, 1, Integer::sum);
```

> 람다로 할 수 없는 일이라면 메서드 참조로도 할 수 없다. 


항상 메서드 참조 사용이 간결하고 이득인 것은 아니다.
다음 코드가 GoshThisClassNameIsHumogous 클래스 안에 있다고 하자.
``` java
service.execute(GoshThisClassNameIsHumogous::action);
```
이를 람다로 대체하면 다음처럼 된다. 
``` java
service.execute(() -> action());
```

메서드 참조 쪽은 더 짧지도 더 명확하지도 않다. 



### 메서드 참조의 5가지 유형
| 메서드 참조 유형  | 예                      | 같은 기능을 하는 람다       |
|------------|------------------------|--------------------|
| 정적         | Integer::parseInt      | str -> Integer.parseInt(str) |
| 한정적(인스턴스)  | Instant.now()::isAfter | Instant then = Instant.now();<br>t-> then.isAAfter(t) |
| 비한정적(인스턴스) | String::toLowerCase    | str-> str.toLowerCase() |
| 클래스 생성자    | TreeMap<K,V>::new      | () -> new TreeMap<K,V>() |
| 배열 생성자     | int[]::new             | len -> new int[len]|

### 핵심정리
- 메서드 참조는 람다의 간단명료한 대안이 될 수 있다. 
- 메서드 참조 쪽이 짧고 명확하다면 메서드 참조를 쓰고, 그렇지 않을 때만 람다를 사용하라. 