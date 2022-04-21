# Item 56 공개된 API 요소에는 항상 문서화 주석을 작성하라

--------------------------------------------

API를 쓸모 있게 하려면 잘 작성된 문서도 곁들여야 한다. 전통적으로 API 문서는 사람이 직접 작성하므로 코드가 변경되면 

매번 함께 수정해줘야 한는데, 자바에서는 자바독(javadoc)이라는 유틸리티가 이 귀찮은 작업을 도와준다. 

<hr>

자바독은 소스코드 파일에서 문서화 주석(doc comment: 자바독 주석)이라는 특수한 형태로 기술된 설명을 추려 API 문서로 변환해준다. 

### 문서화 주석 작성 규칙 

* API를 올바로 문서화하라면 공개된 모든 클래스, 인터페이스, 메서드, 필드 선언에 문서화 주석을 달아야 한다. 
* 메서드용 문서화 주석에는 해당 메서드와 클라이언트 사이의 규약을 명료하게 기술해야 한다. 
* 전제조건과 사후조건뿐만 아니라 부작용도 문서화해야 한다. 

``` java
/**
 * Returns the element at the specified position in this list.
 *
 * <p>This method is <i>not</i> guaranteed to run in constant
 * tim. In some implementations it may run in time proportional
 * to the element position
 *
 * @param index index of element to return; must be
 *        non-negative and less than the size of this list
 * @return the element at the specified position in this list
 * @throws IndexOutOfBoundsException if the index is out of range
 *         ({@code index < 0 || index >= this.size()})
 */
 E get(int index);
```
자바독 유틸리티는 문서화 주석을 HTML로 변환하므로 문서화 주석 안의 HTML 요소들이 최종 HTML 문서에 반영된다.

* 열거 타입을 문서화할 때는 상수들에도 주석을 달아야 한다. 

* 애너테이션 타입을 문서화할 때는 멤버들에도 모두 주석을 달아야 한다. 

* 클래스 혹은 정적 메서드가 스레드 안전하든 그렇지 않든 스데 안전 수준을 반드시 API 설명에 포함해야 한다. 



### 핵심 정리
- 문서화 주석은 여러분 API를 문서화하는 가장 훌륭하고 효과적인 방법이다. 
- 공개 API라면 빠짐없이 설명을 달아야 한다. 
- 표준규약을 일관되게 지키자. 문서화 주석에 임의의 HTML 태그를 사용할 수 있음을 기억하라. 
- 단, HTML 메타문자는 특별하게 취급해야 한다. 


