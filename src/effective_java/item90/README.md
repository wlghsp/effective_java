# Item 90 직렬화된 인스턴스 대신 직렬화 프록시 사용을 검토하라 

--------------------------------------------




### 핵심 정리
- 제3자가 확장할 수 없는 클래스라면 가능한 한 직렬화 프록시 패턴을 사용하자. 
- 이 패턴이 아마도 중요한 불변식을 안정적으로 직렬화해주는 가장 쉬운 방법일 것이다. 