# Item 65 리플렉션보다는 인터페이스를 사용하라

--------------------------------------------
리플렉션 기능(java.lang.reflect)을 이용하면 프로그램에서 임의의 클래스에 접근할 수 있다. 

class 객체가 주어지면 그 클래스의 생성자, 메서드, 필드에 해당하는 Constructor, Method, Field 인스턴스를 가져올 수 있고,

이어서 이 인스턴스들로는 그 클래스의 멤버 이름, 필드 타입, 메서드 시그치처 등을 가져 올 수 있다. 

<hr>

Constructor, Method, Field 인스턴스를 이용해 각각에 연결된 실제 생성자, 메서드, 필드를 조작할 수도 있다. 

이 인스턴스들을 통해 해당 클래스의 인스턴스를 생성하거나, 메서드를 호출하거나, 필드에 접근할 수 있다는 뜻이다. 

예를 들어, Method.invoke는 어떤 클래스의 어떤 객체가 가진 어떤 메서드라도 호출할 수 있게 해준다. 

리플렉션을 이용하면 컴파일 당시에 존재하지 않던 클래스도 이용할 수 있다. 

### 단점도 있다.

* 컴파일타임 타입 검사가 주는 이점을 하나도 누릴 수 없다. 예외 검사도 마찬가지다. 프로그램이 리플렉션 기능을 써서 존재하지 않는 혹은 접근할 수 없는 메서드를 호출하려 시도하면 런타임 오류가 발생한다. 
* 리플렉션을 이용하면 코드가 지저분하고 장황해진다. 지루한 일이고, 읽기도 어렵다. 
* 성능이 떨어진다. 리플렉션을 통한 메서드 호출은 일반 메서드 호출보다 훨씬 느리다. 

<hr> 
코드 분석 도구나 의존관계 주입 프레임워크 처럼 리플렉션을 써야 하는 복잡한 애플리케이션도 리플렉션 사용을 점차 줄이고 있다. 

단점이 명백하기 때문이다. 

<hr>
* 리플렉션은 아주 제한된 형태로만 사용해야 그 단점을 피하고 이점만 취할 수 있다. 

``` java
public static void main(String[] args) {
        // 클래스 이름을 Class 객체로 변환
        Class<? extends Set<String>> cl = null;
        try {
            cl = (Class<? extends Set<String>>) Class.forName(args[0]); // 비검사 형변환!

        } catch (ClassNotFoundException e) {
            fatalError("클래스를 찾을 수 없습니다.");
        }

        // 생성자를 얻는다.
        Constructor<? extends Set<String>> cons = null;
        try {
            cons = cl.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            fatalError("매개변수 없는 생성자를 찾을 수 없습니다.");
        }

        // 집합의 인스턴스를 만든다.
        Set<String> s = null;
        try {
            s = cons.newInstance();
        } catch (InvocationTargetException e) {
            fatalError("생성자에 접근할 수 없습니다.");
        } catch (InstantiationException e) {
            fatalError("클래스를 인스턴스화할 수 없습니다.");
        } catch (ClassCastException e) {
            fatalError("Set을 구현하지 않은 클래스입니다.");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        // 생성한 집합을 사용한다.
        s.addAll(Arrays.asList(args).subList(1, args.length));
        System.out.println(s);
    }

    private static void fatalError(String msg) {
        System.err.println(msg);
        System.exit(1);
    }
```




### 핵심 정리
- 리플렉션은 복잡한 특수 시스템을 개발할 때 필요한 강력한 기능이지만, 단점도 많다. 
- 컴파일타임에는 알 수 없는 클래스를 사용하는 프로그램을 작성한다면 리플렉션을 사용해야 할 것이다. 
- 단, 되도록 객체 생성에만 사용하고, 생성한 객체를 이용할 때는 적절한 인터페이스나 컴파일타임에 알 수 있는 상위 클래스로 형변환해 사용해야 한다.
