package effective_java.design_pattern.templateMethod_Pattern;


/**
 * 템플릿 메소드 패턴
 * 특정 작업을 처리하는 일부분을 서브 클래스 캡슐화하여 전체적인 구조는 바꾸지 않으면서
 * 특정 단계에서 수행하는 내용을 바꾸는 패턴
 *
 * 두 개 이상의 프로그램이 기본적으로 동일한 골격 하에서 동작할 때 기본 골격에 해당하는 알고리즘은 일괄적으로 관리하면서
 * 각 프로그램마다 달라지는 부분들에 대해서는 따로 만들고 싶을 때 템플릿 메소드 패턴을 사용하면 좋다.
 *
 */



// 추상 클래스 선생님
abstract class Teacher {

    public void start_class() {
        inside();
        attendance();
        teach();
        outside();
    }


    // 공통 메서드
    public void inside() {
        System.out.println("선생님이 강의실로 들어옵니다.");
    }

    public void attendance() {
        System.out.println("선생님이 출석을 부릅니다.");
    }

    public void outside() {
        System.out.println("선생님이 강의실을 나갑니다.");
    }

    // 추상 메서드
    abstract void teach();
}

class Korean_Teacher extends Teacher {

    @Override
    public void teach() {
        System.out.println("선생님이 국어를 수업합니다.");
    }
}

class Math_Teacher extends Teacher {

    @Override
    public void teach() {
        System.out.println("선생님이 수학을 수업합니다.");
    }
}

class English_Teacher extends Teacher {

    @Override
    public void teach() {
        System.out.println("선생님이 영어를 수업합니다.");
    }
}

public class Main {
    public static void main(String[] args) {
        Korean_Teacher kr = new Korean_Teacher();
        Math_Teacher mt = new Math_Teacher();
        English_Teacher en = new English_Teacher();

        kr.start_class();
        System.out.println("-------------------------");
        mt.start_class();
        System.out.println("---------------------------");
        en.start_class();
    }
}