# Item 72 표준 예외를 사용하라

--------------------------------------------

숙련된 프로그래머는 그렇지 못한 프로그래머보다 더 많은 코드를 재사용한다. 

예외도 마찬가지로 재사용하는 것이 좋으며, 자바 라이브러리는 대부분 API에서 쓰기에 충분한 수의 예외를 제공한다. 

<hr>

표준 예외를 재사용하면 얻는 게 많다. 그중 최고는 여러분의 API가 다른 사람이 익히고 사용하기 쉬워진다는 것이다. 


많은 프로그래머에게 이미 익숙해진 규약을 그대로 따르기 때문이다. 

여러분의 API를 사용한 프로그램도 낯선 예외를 사용하지 않게 되어 읽기 쉽게 된다는 장점도 크다. 

마지막으로 예외 클래스 수가 적을수록 메모리 사용량도 줄고 클래스를 적재하는 시간도 적게 걸린다. 

<hr>

가장 많이 재사용되는 예외는 IllegalArgumentException이다. 호출자가 인수로 부적절한 값을 넘길 때 던지는 예외로, 

예를 들어 반복 횟수를 지정하는 매개변수에 음수를 건넬 때 쓸 수 있다. 

<hr>

메서드가 던지는 모든 예외를 잘못된 인수나 상태라고 뭉뚱그릴 수도 있겠지만, 그중 특수한 일부는 따로 구분해 쓰는 게 보통이다. 

null 값을 허용하지 않는 메서드에 null을 거넨면 관례상 IllegalArgumentException이 아닌 NullPointerException을 던진다. 

비슷하게, 어떤 시퀀스의 허용 범위를 넘는 값을 건넬 때도 IllegalArgumentException보다는 IndexOutOfBoundsException을 던진다. 

<hr>

### UnsupportedOperationException

이 예외는 클라이언트가 요청한 동작을 대상 객체가 지원하지 않을 때 던진다. 


> Exception, RuntimeException, Throwable, Error 는 직접 재사용하지 말자. 

다음 표에 지금까지 설명한 널리 재사용되는 예외들을 정리해보았다.

대부분 객체는 자신이 정의한 메서드를 모두 지원하니 흔히 쓰이는 예외는 아니다. 

보통은 구현하려는 인터페이스의 메서드 일부를 구현할 수 없을 때 쓰는데, 예컨대 원소를 넣을 수만 있는 List 구현체에 대고 

누군가 remove 메서드를 호출하면 이 예외를 던질 것이다. 

|예외| 주요 쓰임                                               |
|----|-----------------------------------------------------|
|IllegalArgumentException| 허용하지 않는 값이 인수로 건네졌을 때 (null은 따로 NullPointerException으로 처리)| 
|IllegalStateException| 객체가 메서드를 수행하기에 적절하지 않은 상태일 때                        |
|NullPonterException| null을 허용하지 않는 메서드에 null을 건넸을 때|
|IndexOutOfBoundsException| 인덱스가 범위를 넘어섰을 때|
|ConcurrentModificationException|허용하지 않는 동시 수정이 발견됐을 때|
|UnsupportedOperationException|호출한 메서드를 지원하지 않았을 때|

복소수나 유리수를 다루는 객체를 작성한다면 ArithmeticException이나 NumberFormatException을 재사용할 수 있을 것이다. 

상황에 부합한다면 항상 표준 예외를 재사용하자. 

이때 API 문서를 참고해 그 예외가 어떤 상황에서 던져지는지 꼭 확인해야 한다. 

예외의 이름뿐 아니라 예외가 던져지는 맥락도 부합할 때만 재사용한다.

> 인수 값이 무엇이었든 어차피 실패했을거라면 IllegalStateException을, 그렇지 않으면 IllegalArgumentException을 던지자. 



