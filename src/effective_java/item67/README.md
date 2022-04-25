# Item 67 최적화는 신중히 하라

--------------------------------------------

### 모든 사람이 마음 깊이 새겨야 할 최적화 격언 세 개

* (맹목적인 어리석음을 포함해) 그 어떤 핑계보다 효율성이라는 이름 아래 행해진 컴퓨팅 죄악이 더 많다. (심지어 효율을 높이지도 못하면서) - 윌리엄 울프
* (전체의 97% 정도인) 자그마한 효율성은 모두 잊자. 섣부른 최적화가 만악의 근원이다. - 도널드 크누스
* 최적화를 할 때는 다음 두 규칙을 따르라. - M.A. 잭슨
  1. 하지 마라
  2. (전문가 한정) 아직 하지 마라. 다시 말해, 완전히 명백하고 최적화되지 않은 해법을 찾을 때까지는 하지 마라. 

<hr>

이 격언들은 자바가 탄생하기 20년 전에 나온 것으로, 최적화의 어두운 진실을 이야기해준다. 

최적화는 좋은 결과보다는 해로운 결과로 이어지기 쉽고, 섣불리 진행하면 특히 더 그렇다. 

빠르지도 않고 제대로 동작하지도 않으면서 수정하기는 어려운 소프트웨어를 탄생시키는 것이다. 

<hr>

성능 때문에 견고한 구조를 희생하지 말자. 

> 빠른 프로그램보다는 좋은 프로그램을 작성하라. 

좋은 프로그램은 정보 은닉 원칙을 따르므로 개별 구성요소의 내부를 독립적으로 설계할 수 있다. 

따라서 시스템의 나머지에 영향을 주지 않고도 각 요소를 다시 설계할 수 있다. 

<hr>

* 성능을 제한하는 설계를 피하라
* API를 설계할 때 성능에 주는 영향을 고려하라. 


### 핵심 정리
- 빠른 프로그램을 작성하려 안달하지 말자. 
- 좋은 프로그램을 작성하다 보면 성능은 따라 오게 마련이다. 
- 하지만 시스템을 설계할 때, 특히 API, 네트워크 프로토콜, 영구 저장용 데이터 포맷을 설계할 때는 성능을 염두에 두어야 한다. 
- 시스템 구현을 완료했다면 이제 성능을 측정해보라. 충분히 빠르면 그것으로 끝이다. 
- 그렇지 않다면 프로파일러를 사용해서 문제의 원인이 되는 지점을 찾아 최적화를 수행하라. 
- 가장 먼저 어떤 알고리즘을 사용했는지를 살펴보자. 
- 알고리즘을 잘못 골랐다면 다른 저수준 최적화는 아무리 해봐야 소용이 없다. 
- 만족할 때까지 이 과정을 반복하고, 모든 변경 후에는 성능을 측정하라. 