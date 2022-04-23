# Item 73 추상화 수준에 맞는 예외를 던져라

--------------------------------------------

> 상위 계층에서는 저수준 예외를 잡아 자신의 추상화 수준에 맞는 예외로 바꿔 던져야 한다. 

이를 예외 번역(exception translation)

코드 73-1 예외 번역
``` java
try {
    ...// 저수준 추상화를 이용한다.
} catch (LowerLevelException e) {
    // 추상화 수준에 맞게 번역한다. 
    throw new HigherLevelException(...);
}
```

다음은 AbstractSequentialList에서 수행하는 예외 번역의 예다. AbstractSequentialList는 List 인터페이스의 골격 구현이다. 

이 예에서 수행한 예외 번역은 List<E> 인터페이스의 get 메서드 명세에 명시된 필수사항임을 기억해두자.

``` java
/**
 * 이 리스트 안의 지정한 위치의 원소를 반환한다.
 * @throws IndexOutOfBoundsException index가 범위 밖이라면,
 *          즉 ({@code index < 0 || index >= size()})이면 발생한다.
 */
 public E get(int index){
    ListIterator<E> i = listIterator(index);
    try {
        return i.next();       
    } catch (NoSuchElementException e) {
      throw new IndexOutOfBoundsException("인덱스: " + index);   
    }
 }
```

예외를 번역할 때, 저수준 예외가 디버깅에 도움이 된다면 예외 연쇄 (exception chaining)를 사용하는게 좋다. 

예외 연쇄란 문제의 근본 원인(cause)인 저수준 예외를 고수준 예외에 실어 보내는 방식이다. 

코드 73-2 예외 연쇄
``` java
try {
      ...// 저수준 추상화를 이용한다. 
} catch (LowerLevelException cause) {
        // 저수준 예외를 고수준 예외에 실어  보낸다. 
        throw new HigherLevelException(cause);
}
```
고수준 예외의 생성자는 (예외 연쇄용으로 설계된) 상위 클래스의 생성자에 이 '원인'을 건네주어, 

최종적으로 Throwable(Throwable)  생성자까지 건네지게 한다. 

코드 73-3 예외 연쇄용 생성자
``` java
class HigherLevelException extends Exception {
    HigherLevelException(Throwable cause) {
        super(cause);
    }
}
```

### 핵심 정리
- 아래 계층의 예외를 예방하거나 스스로 처리할 수 없고, 그 예외를 상위 계층에 그대로 노출하기 곤란하다면 예외 번역을 사용하라. 
- 이때 예외 연쇄를 이용하면 상위 계층에는 맥락에 어울리는 고수준 예외를 던지면서 근본 원인도 함께 알려주어 오류를 분석하기에 좋다. 
