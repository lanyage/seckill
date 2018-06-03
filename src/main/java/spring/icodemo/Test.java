package spring.icodemo;

/**
 * Created by lanyage on 2018/4/29.
 */
public class Test {
    public static void main(String[] args) {
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");

        Demo demo = (Demo) ac.getBean("test");

        demo.test();

//         new Test().test();
    }

    private void test() {
        System.out.println(this.getClass().getClassLoader().getResource(""));
    }
}
