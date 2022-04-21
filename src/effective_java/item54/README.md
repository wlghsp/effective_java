# Item 54 null이 아닌, 빈 컬렉션이나 배열을 반환하라

--------------------------------------------

다음은 주변에서 흔히 볼 수 있는 메서드다. 

코드 54-1 컬렉션이 비었으면 null을 반환한다. - 따라 하지 말 것!
``` java
private final List<Cheese> cheeseInStock = ...;

/**
 * @return 매장 안의 모든 치즈 목록을 반환한다. 
 *      단, 재고가 하나도 없다면 null을 반환한다.
 */
public List<Cheese> getCheeses() {
    return cheeseInStock.isEmpty() ? null : new ArrayList<>(cheeseInStock) ;
}
```
사실 재고가 없다고 해서 특별히 취급할 이유는 없다. 그럼에도 이 코드처럼 null을 반환한다면, 

클라이언트는 이 null 상황을 처리하는 코드를 추가로 생성해야 한다. 

``` java
List<Cheese> cheeses = shop.getCheeses();
if (cheeses != null && cheeses.contains(Cheese.STILTON))
    System.out.println("좋았어, 바로 그거야.");
```
컬렉션이나 배열 같은 컨테이너가 비었을 때 null을 반환하는 메서드를 사용할 때면 항시 이와 같은 방어 코드를 넣어줘야 한다. 

클라이언트에서 방어 코드를 빼먹으면 오류가 발생할 수 있다. 실제로 객체가 0개일 가능성이 거의 없는 상황에서는 수년 뒤에야 오류가 발생하기도 한다.

한편, null을 반환하려면 반환하는 쪽에서도 이 상황을 특별히 취급해야줘야 해서 코드가 더 복잡해진다. 

<hr>
### null을 반환하는 쪽이 낫다는 주장이 틀린 점

1. 성능 분석 결과 이 할당이 성능 저하의 주범이라고 확인되지 않는 한 이 정도의 성능 차이는 신경 쓸 수준이 못된다. 

2. 빈 컬렉션과 배열은 굳이 새로 할당하지 않고도 반환할 수 있다. 


코드 54-2 빈 컬렉션을 반환하는 올바른 예 
``` java
public List<Cheese> getCheeses() {
    return new ArrayList<>(cheesesInStock);
}
```
사용에 다라 빈 컬렉션 할당이 성능을 눈에 띄게 떨어뜨릴 수 있어 해법은 아래와 같다. 

> 매번 똑같은 빈'불변'컬렉션을 반환한다. 
리스트: Collections.emptyList

집합: Collections.emptySet

맵: Collections.emptyMap

코드 54-3 최적화 - 빈 컬렉션을 매번 새로 할당하지 않도록 했다.
``` java
public List<Cheese> getCheeses() {
    return cheeseInStock.isEmpty() Collections.emptyList() : new ArrayList<>(cheesesInStock);
}
```

> 배열도 절대 null을 반환하지 말고 길이가 0인 배열을 반환하라

코드 54-4 길이가 0일 수도 있는 배열을 반환하는 올바른 방법
``` java
public List<Cheese> getCheeses() {
    return cheeseInStock.toArray(new Cheese[0]);
}
```
위 방식이 성능을 떨어뜨릴 것 같다면 길이 0짜리 배열을 미리 선언해두고 매번 그 배열을 반환하면 된다. 

길이가 0인 배열은 모두 불변이기 때문이다. 

코드 54-5 최적화 - 빈 배열을 매번 새로 할당하지 않도록 했다. 
``` java
private static final Cheese[] EMPTY_CHEESE_ARRAY = new Cheese[0];

public Cheese[] getCheeses() {
    return cheeseInStock.toArray(EMPTY_CHEESE_ARRAY);
}
```
이 최적화 버전의 getCheese는 항상 EMPTY_CHEESE_ARRAY를 인수로 넘겨 toArray를 호출한다. 

따라서 cheesesInStock이 비었을 때면 언제나 EMPTY_CHEESE_ARRAY를 반환하게 된다. 

코드 54-6 나쁜 예 - 배열을 미리 할당하면 성능이 나빠진다. 
``` java
return cheesesInStock.toArray(new Cheese[cheesesInStock.size()]);
```






### 핵심 정리 
- null 아닌, 빈 배열이나 컬렉션을 반환하라. 
- null을 반환하는 API는 사용하기 어렵고 오류 처리 코드도 늘어난다. 그렇다고 성능이 좋은 것도 아니다.