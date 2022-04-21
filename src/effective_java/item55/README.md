# Item 55 옵셔널 반환은 신중히 하라

--------------------------------------------

* 자바 8 이전: 메서드가 특정 조건에서 값을 반환할 수 없을 때 취할 수 있는 선택지 
  * 예외 던지기
  * (반환타입이 객체 참조라면) null을 반환

* 두 방법의 허점
  * 예외는 진짜 예외적인 상황에서 사용해야 함, 예외를 생성할 때 스택 추적 전체를 캡처하므로 비용도 만만치 않다.
  * null 을 반환할 수 있는 메서드를 호출할 때는, 별도의 null처리코드를 추가해야 한다. 

null 처리를 무시하고 반환된 null 값을 어딘가에 저장해두면 언젠가 NullPointerException이 발생할 수 있다. 

그것도 근본적인 원인, 즉 null 을 반환하게 한 실제 원인과는 전혀 상관없는 코드에서 말이다. 

<hr>

자바 버전이 8으로 올라가면서 또 하나의 선택지 두두등장

그 주인공은 Optional<T>

null이 아닌 T타입 참조를 하나 담거나, 혹은 아무것도 담지 않을 수 있다. 

아무것도 담지 않는 옵셔널은 '비었다'고 말한다. 

반대로 어떤 값을 담은 옵셔널은 '비지 않았다'고 한다. 

옵셔널은 원소를 최대 1개 가질 수 있는 '불변' 컬렉션이다. 

Optional<T>가 Collection<T>를 구현하지는 않았지만, 원칙적으로 그렇다는 말이다. 

<hr>

보통은 T를 반환해야 하지만 특정 조건에서는 아무것도 반환하지 않아야 할 때 T 대신 Optional<T>를 반환하도록 선언하면 된다. 

그러면 유효한 반환값이 없을 때는 빈 결과를 반환하는 메서드가 만들어진다. 

옵셔널은 반환하는 메서드의 장점  
- 예외를 던지는 메서드유연하고 사용하기 쉬우며, null을 반환하는 메서드보다 오류 가능성이 작다. 

코드 55-1 컬렉션에서 최댓값을 구한다(컬렉션이 비었으면 예외를 던진다)
``` java
public static <E extends Comparable<E>> E max(Collection<E> c) {
    if (c.isEmpty())
        throw new IllegalArgumentException("빈 컬렉션");
        
    E result = null;
    for (E e : c)
        if (result == null || e.compareTo(result) > 0)
            result = Objects.requireNonNull(e);
    
    return result;
}
```
이 메서드에 빈 컬렉션을 건네면 IllegalArgumentException을 던진다. 

코드 55-2 컬렉션에서 최댓값을 구해 Optional<E>로 반환한다. 
``` java
public static <E extends Comparable<E>> Optional<E> max(Collection<E> c) {
    if (c.isEmpty())
        return Optional.empty();
        
    E result = null;
    for (E e : c)
        if (result == null || e.compareTo(result) > 0)
            result = Objects.requireNonNull(e);
    
    return Optional.of(result);
}
```
적절한 정적 팩터리를 사용해서 옵셔널을 생성해주기만 하면 된다. 

2 가지 팩터리를 사용함. 

1. Optional.empty() : 빈 옵셔널 만듬
2. Optional.of(value) : 값이 든 옵셔널 생성 (null을 넣으면 NPE을 던지니 주의)

* null 값도 허용하는 옵셔널을 만들려면 Optional.ofNullable(value)를 사용하자. 

> 옵셔널을 반환하는 메서드에서는 절대 null을 반환하지 말자. (옵셔널 도입 취지 완전 무시하는 행위)


<hr>

스트림의 종단 연산 중 상당수가 옵셔널을 반환

위의 max메서드를 스트림 버전으로 다시 작성

코드 55-3 컬렉션에서 최댓값을 구해 Optional<E>로 반환 - 스트림 버전
``` java
public static <E extends Comparable<E>> Optional<E> max(Collection<E> c) {
    return c.stream().max(Comparator.naturalOrder());
}
```

옵셔널 반환 선택의 기준은? 
* 옵셔널은 검사 예외와 취지가 비슷.(아이템 71) 즉, 반환값이 없을 수도 있음을 API 사용자에게 명확히 알려줌. 


메서드가 옵셔널을 반환한다면 클라이언트는 값을 받지 못했을 때 취할 행동을 선택해야 한다. 

1. 기본값 설정 방법

코드 55-4 옵셔널 활용 1 - 기본값을 정해둘 수 있다.
``` java
String lastWordInLexicon = max(words).orElse("단어 없음...");
```

2. 상황에 맞는 예외 던지기

코드 55-5 옵셔널 활용 2 - 원하는 예외를 던질 수 있다.
``` java
Toy myToy = max(toys).orElseThrow(TemperTantrumException::new);
```
3. 항상 값이 채워져 있다고 가정 

코드 55-6 옵셔널 활용 3 - 항상 값이 채워져 있다고 가정한다.
``` java
Element lastNobleGas = max(Elements.NOBLE_GASES).get();
```

기본값을 설정하는 비용이 아주 커서 부담이 될 때가 있다. 

그럴 때는 Supplier<T>를 인수로 받는 orElseGet을 사용하면, 값이 처음 필요할 때 Supplier<T>를 사용해 생성하므로 초기 설정 비용을 낮출 수 있다.

더 특별한 쓰임에 대비한 메서드 → filter, map, flatMap, ifPresent

<hr>
여전히 적합한 메서드를 찾지 못했다면 isPresent 메서드를 살펴보자.

부모 프로세스의 프로세스 ID를 출력하거나, 부모가 없다면 "N/A"를 출력하는 코드다 . 

자바 9에서 소개된 ProcessHandle 클래스를 사용했다. 

``` java
Optional<ProcessHandle> parentProcess = ph.parent();
System.out.println("부모 PID: " + (parentProcess.isPresent() ? String.valueOf(parentProcess.get().pid()) : "N/A"));
```

이 코드는 Optional의 map을 사용하여 다음처럼 다듬을 수 있다. 

``` java
System.out.println("부모 PID: + ph.parent().map(h -> String.valueOf(h.pid())).orElse("N/A"));
```

스트림을 사용하면 옵셔널들을 Stream<Optional<T>>로 받아서, 그중 채워진 옵셔널들에서 값을 뽑아 Stream<T>에 건네 담아 처리하는 경우가 드물지 않다. 

자바 8에서는 다음과 같이 구현한다.
``` java
streamOfOptionals.filter(Option::isPresent)
                 .map(Optional::get)
```
보다시피 옵셔널에 값이 있다면(Optional::isPreset) → 그 값을 꺼내(Optional::get)스트림에 매핑

> 자바 9에서는 Optional에 stream()가 추가됨 : Optional을 Stream으로 변환해주는 어댑터.

옵셔널에 값이 있으면 그 값을 원소로 담은 스트림으로, 값이 없다면 빈 스트림으로 변환한다. 

이를 Stream의 flatMap 메서드와 조합하면 앞의 코드를 다음처럼 바꿀 수 있다. 

``` java
streamOfOptionals.flatMap(Optional::stream)
```
> 컬렉션, 스트림, 배열, 옵셔널 같은 컨테이너 타입은 옵셔널로 감싸면 안 된다. 

빈 Optional<List<T>>를 반환하기보다는 빈 List<T>를 반환하는게 좋다. 

빈 컨테이너를 그대로 반환하면 클라이언트에 옵셔널 처리 코드를 넣지 않아도 된다. 


<hr>

### 메서드 반환 타입을 T 대신 Optional<T>로 선언하는 경우

- 결과가 없을 수 있으며, 클라이언트가 이 상황을 특별하게 처리해야 한다면 Optional<T>를 반환


박싱된 기본 타입을 담는 옵셔널은 기본 타입 자체보다 무거울 수밖에 없다.

값을 두 겹이나 감싸기 때문이다. 

그래서  자바 API 설계자는 int, long, double 전용 옵셔널 클래스들을 준비해 놓았음. 

→ OptionalInt, OptionalLong, OptionalDouble

> 이렇게 대체재가 있으니, 박싱된 기본 타입을 담은 옵셔널을 반환하는 일은 없도록 하자.
( Boolean, Byte, Character, Short, Float 은 예외)


> 옵셔널은 컬렉션의 키, 값, 원소나 배열의 원소로 사용하는게 적절한 상황은 거의 없다. 






### 핵심 정리
- 값을 반환하지 못할 가능성이 있고, 호출할 때마다 반환값이 없을 가능성에 염두에 둬야 하는 메서드라면 옵셔널을 반환해야 할 상황일 수 있다. 
- But, 옵셔널 반환에는 성능저하가 뒤따르니, 성능에 민감한 메서드라면 null을 반환하거나 예외를 던지는 편이 나을 수 있다. 
- 옵셔널을 반환값 이외의 용도로 쓰는 경우는 매우 드물다. 