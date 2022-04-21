# Item 46 스트림에서는 부작용 없는 함수를 사용하라

--------------------------------------------
> 스트림은 그저 또 하나의 API가 아닌, 함수형 프로그랭밍에 기초한 패러다임이다.


스트림 패러다임의 핵심은 계산을 **일련의 변환(transformation)**으로 재구성하는 부분이다. 

이때 각 변환 단계는 가능한 한 이전 단계의 결과를 받아 처리하는 순수 함수여야 한다. 

* 순수함수란?

: 오직 입력만이 결과에 영향을 주는 함수. 

다른 가변 상태를 참조하지 않고, 함수 스스로도 다른 상태를 변경하지 않는다.

이렇게 하려면 스트림 연산에 건네는 함수 객체는 모두 부작용이 없어야 한다. 

<hr>

다음은 텍스트 파일에서 단어별 수를 세어 빈도표로 만드는 일을 한다. 

코드 46-1 스트림 패러다임을 이해하지 못한 채 API만 사용했다 - 따라 하지 말 것!
``` java
Map<String, Long> freq = new HashMap<>();
try(Stream<String> words = new Scanner(file).tokens()) }
    words.forEach(word -> {
        freq.merge(word.toLowerCase(), 1L, Long::sum);
        });
}
```

스트림, 람다, 메서드 참조를 사용했고 결과도 올바르다. 

하지만 절대 스트림 코드라 할 수 없다. 

스트림 API의 이점을 살리지 못하여 같은 기능의 반복적 코드보다 (조금 더) 길고, 읽기 어렵고, 유지보수에도 좋지 않다. 

이 코드의 모든 작업이 종단 연산인 forEach에서 일어나는데, 이때 외부 상태(빈도표)를 수정하는 람다를 실행하면서 문제가 생긴다. 

forEach가 그저 스트림이 수행한 연산 결괄르 보여주는 일 이상을 하는 것을 보니 나쁜 코드일 것 같은 냄새가 난다. 

코드 46-2 스트림을 제대로 활용해 빈도표를 초기화한다. 
``` java
Map<String, Long<  freq;
try (Stream<String> words = new Scanner(file).tokens()) {
    freq = words
        .collect(groupingBy(String::toLowerCase, counting()));
}
```
앞서와 같은 일을 하지만 이번엔 스트림 API를 제대로 사용했다. 그리고 짧고 명확하다. 

forEach 연산은 종단 연산 중 기능이 가장 적고 가장 '덜' 스트림답다. 대놓고 반복적이라서 병렬화할 수도 없다.

> **forEach 연산은 스트림 계산 결과를 보고할 때만 사용하고, 계산하는 데는 쓰지말자.**
<br>(가끔은 스트림 계산 결과를 기존 컬렉션에 추가하는 등의 다른 용도로도 쓸 수 있다.)

이 코드는 Collector를 사용하는데, 스트림을 사용하려면 꼭 배워야 하는 새로운 개념이다. 

java.util.stream.Collectors 클래스는 메서드를 무려 39개나 가지고 있고, 그중에는 타입 매개변수가 5개나 되는 것도 있다. 

다행히 복잡한 세부내용을 잘 몰라도 이 API의 장점을 대부분 활용할 수 있다. 

익숙해지기 전까지는 Collector 인터페이스를 잠시 잊고, 그저 축소(reduction) 전략을 캡슐화한 블랙박스 객체라고 생각하기 바란다. 

*여기서 축소란? → 스트림의 원소들을 객체 하나에 취합한다는 뜻이다. 

Collector가 생성하는 객체는 일반적으로 Collection이며, 그래서 "collector"라고 이름을 쓴다. 

<hr>

Collector를 사용하면 스트림의 원소를 손쉽게 컬렉션으로 모을 수 있다. 

Collctor는 총 3 가지 
1. toList() - 리스트
2. toSet() - 집합
3. toCollection(collectionFactory) - 프로그래머가 지정한 컬렉션 타입

빈도표에서 가장 흔한 단어 10개를 뽑아내는 스트림 파이프라인을 작성해보자 

코드 46-3 빈도표에서 가장 흔한 단어 10개를 뽑아내는 파이프라인
``` java
List<String> topTen = freq.keySet().stream()
    .sorted(comparing(freq::set).reversed())
    .limit(10)
    .collect(toList());
```
√ 마지막 toList는 Collectors의 메서드다. 이처럼 **Collectors의 멤버를 정적 임포트하여 쓰면 스트림 파이프라인 가독성이 좋아져, 흔히들 이렇게 사용한다.**

### Collectors의 나머지 메서드 

* toMap(keyMapper, valueMapper)

보다시피 스트림 원소를 키에 매핑하는 함수와 값에 매핑하는 함수를 인수로 받는다. 

코드 46-4 toMap 수집기를 사용하여 문자열을 열거 타입 상수에 매핑한다. 
``` java
private static final Map<String, Operation> stringToEnum = 
    Stream.of(values()).collect(
        toMap(Object::toString, e -> e));
```
이 간단한 toMap 형태는 스트림의 각 원소가 고유한 키에 매핑되어 있을 때 적합하다. 

스트림 원소 다수가 같은 키를 사용한다면 파이프라인 IllegalStateException을 던지며 종료될 것이다. 


인수 3개를 받는 toMap은 어떤 키와 그 키에 연관된 원소들 중 하나를 골라 연관 짓는 맵을 만들 때 유용하다. 


다음은 다양한 음악가의 앨범들을 담은 스트림을 가지고, 음악가와 그 음악가의 베스트 앨범을 연관 짓고 싶다고 해보자.

``` java
Map<Artist, Album> topHits = albums.collect(
    toMap(Album::artist, a->a, maxBy(comparing(Album::sales))));
```
여기서 비교자로는 BinaryOperator에서 정적 임포트한 maxBy라는 정적 팩터리 메서드를 사용했다. 

maxBy는 Comparator<T>를 입력받아 BinaryOperator<T>를 돌려준다. 


이 경우 비교자 생성 메서드인 comparing이 maxBy에 넘겨줄 비교자를 반환하는데, 자신의 키 추출 함수로는 Album::sales를 받았다. 


말로 풀어보자면 

>"앨범 스트림을 맵으로 바꾸는데, 이 맵은 각 음악가와 그 음악가의 베스트 앨범을 짝지은 것이다"

인수가 3개인 toMap은 충돌이 나면 마지막 값을 취하는(last-write-wins) 수집기를 만들 때도 유용하다. 


코드 46-7 마지막에 쓴 값을 취하는 수집기 
``` java
toMap(keyMapper, valueMapper, (oldVal, newVal) -> newVal)
```
네 번째 인수로 맵 팩터리를 받는다. 이 인수로는 EnumMap이나 TreeMap처럼 원하는 특정 맵 구현체를 직접 지정할 수 있다. 



* groupingBy

이 메서드는 입력으로 분류 함수(classifier)를 받고 출력으로는 원소들을 카테고리별로 모아 놓은 맵을 담은 수집기를 반환한다. 

그리고 이 카테고리가 해당 원소의 맵 키로 쓰인다. 

다중정의된 groupingBy 중 형태가 가장 간단한 것은 분류 함수 하나를 인수로 받아 맵을 반환한다. 

반환된 맵에 담긴 각각의 값은 해당 카테고리에 속하는 원소들을 모든 담은 리스트다. 


* joining

이 메서드는 (문자열 등의) CharSequence 인스턴스의 스트림에만 적용 가능

매개변수가 없는 joining은 단순히 원소들을 연결(concatenate)하는 수집기를 반환한다. 

인수 하나짜리 joining은 CharSequence 타입의 구분문자(delimiter)를 매개변수로 받는다. 




### 핵심 정리
- 스트림 파이프라인 프로그래밍의 핵심은 부작용 없는 함수 객체에 있다. 
- 스트림뿐 아니라 스트림 관련 객체에 건네지는 모든 함수 객체가 부작용이 없어야 한다. 
- 종단 연산 중 forEach는 스트림이 수행한 계산 결과를 보고할 때만 이용해야 한다. 계산 자체에는 이용 X
- 스트림을 올바르게 사용하려면 Collector를 잘 알아둬야 한다. 
- 가장 중요한 수집기 팩터리는 toList, toSet, groupingBy, joining 이다.