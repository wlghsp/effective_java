# Item 37 ordinal 인덱싱 대신 EnumMap을 사용하라

--------------------------------------------

배열이나 리스트에서 원소를 꺼낼 때 ordinal 메서드로 인덱스를 얻는 코드가 있다. 

식물을 간단히 나타낸 다음 클래스를 예로 살펴 보자 

``` java
class Plant {
    enum LifeCycle { ANNUAL, PERENIAL, BAIENIAL }
    
    final String name;
    final LifeCycle = lifeCycle;
    
    Plant(String name, LifeCycle lifeCycle) {
        this.name = name;
        this.lifeCycle = lifeCycle;
    }
    
    @Override public String toString() {
        return name;
    }
}
```

이제 정원에 심은 식물들을 배열 하나로 관리하고, 이들을 생애주기 (한해살이, 여러해 살이, 두해살이) 별로 묶어보자.
이때 어떤 프로그래머는 집합들을 배열 하나에 넣고 생애주기의 ordinal 값을 그 배열의 인덱스로 사용하려 할 것이다. 

* 코드 37-1 ordinal()을 배열 인덱스로 사용 - 따라하지 말 것
``` java
Set<Plant>[] plantsByLifeCycle = (Set<Plant>[]) new Set[Plant.LifeCycle.values().length];
for (int i = 0l i < plantsByLifeCycle.length; i++) 
    plantsByLifeCycle[i] = new HashSet<>();

for (Plant p : garden)
    plantsByLifeCycle[p.lifeCycle.ordinal()].add(p);
    
// 결과 출력 
for (int i = 0; i< plantsByLifeCycle.length; i++) {
    System.out.printf("%s: %s%n", Plant.LifeCycle.values()[i], plantsByLifeCycle[i]);
```
동작은 하지만 문제가 많다.

1. 배열은 제네릭과 호환되지 않으니 비검사 형변환을 수행해야 하고 깔끔히 컴파일 되지 않음. 
2. 배열은 각 인덱스의 의미를 모르니 출력 결과에 직접 레이블을 달아야 함. 
3. 가장 김각한 문제는 정확한 정숫값을 사용한다는 것을 여러분이 직접 보증해야 한다는 점. 

잘못된 값을 사용하면 잘못된 동작을 묵묵히 수행하거나 (운이 좋다면) ArrayIndexOutOfBoundsException을 던질 것이다. 

#### 여기 **해결책** 있사옵나이다. 
배열은 실질적으로 열거 타입 상수를 값으로 매핑하는 일을 한다. 그러니 Map을 사용할 수도 있을 것이다. 

사실 열거 타입을 키로 사용하도록 설계한 아주 빠른 Map 구현체가 존재하는데, 바로 EnumMap이다. 


* 코드 37-2 EnumMap을 사용해 데이터와 열거 타입을 매핑한다. 
``` java
Map<Plant.LifeCycle, Set<Plant>> plantsByLifeCycle = new EnumMap<>(Plant.LifeCycle.class);
for (Plant.LifeCycle lc : Plant.LifeCycle.values())
    plantsByLifeCycle.put(lc, new HashSet<>());
for (Plant p : garden)
    plantsByLifeCycle.get(p.lifeCycle).add(p);
System.out.println(plantsByLifeCycle);
```
안전하지 않은 형변환은 쓰지 않고, 맵의 키인 열거 탕비이 그 자체로 출력용 문자열을 제공하니 출력 결과에 직접 레이블을 달 일도 없다. 

나아가 배열 인덱스를 계산하는 과정에서 오류가 날 가능성도 원천봉쇄된다. 

EnuMap의 성능이 ordinal을 쓴 배열에 비견되는 이유는 그 내부에서 배열을 사용하기 때문이다.

내부 구현 방식을 안으로 숨겨서 Map의 타입 안정성과 배열의 성능을 모두 얻어 낸 것


<br>
스트림을 사용해 맵을 관리하면 코드를 더 줄일 수 있다. 

*코드 37-3 스트림을 사용한 코드 1 - EnumMap을 사용하지 않는다!
``` java
System.out.println(Arrays.stream(garden)
                    .collect(groupingBy(p -> p.lifeCycle)));
```
이 코드는 EnumMap이 아닌 고유한 맵 구현체를 사용했기 때문에 EnumMap을 써서 얻은 공간과 성능이 이점 사라짐. 

*코드 37-4 스트림을 사용한 코드 2 - EnumMap을 이용해 데이터와 열거 타입을 매핑했다.
``` java
System.out.println(Arrays.stream(garden)
                    .collect(groupingBy(p -> p.lifeCycle,
                    () -> new EnumMap<>(LifeCycle.class), toSet())));
```





#### 핵심 정리
- 배열의 인덱스를 얻기 위해 ordinal을 쓰는 것은 일반적으로 좋지 않으니, 대신 EnumMap을 사용하라.
- 다차원 관계는 EnumMap<..., EnumMap<...>>으로 표현하라.
- "애플리케이션 프로그래머는 Enum.ordinal을 (웬만해서는) 사용하지 말아야 한다."는 일반 원칙의 특수한 사례다. 