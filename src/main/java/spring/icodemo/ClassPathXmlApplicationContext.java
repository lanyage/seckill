package spring.icodemo;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * Created by lanyage on 2018/4/29.
 */
public class ClassPathXmlApplicationContext implements ApplicationContext {

    private String fileName;
    public ClassPathXmlApplicationContext(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public Object getBean(String beanId)  {
        //获取当前对象ClassPathXmlApplicationContext的路径,用于下面获取applicationContext.xml配置文件的路径

        //获取xml文件的路径
        String xmlPath = "/Users/lanyage/IdeaProjects/seckill/src/main/resources/applicationContext.xml";
        //创建一个SAXReader对象,用来读取配置文件
        SAXReader reader = new SAXReader();

        //创建一个Document对象,SAXReader读取配置文件后的返回值即为Document对象
        Document document = null;

        //创建一个Object用来返回
        Object object = null;

        try {
            //saxReader读取配置文件
            document = reader.read(xmlPath);

            //使用Document对象找到配置文件中的bean节点.并且转化为Element对象
            Element bean = (Element) document.selectSingleNode("beans/bean[@id='"+beanId+"']");

            //获取bean的class属性,并且获取这个属性的值
            String beanClass = bean.attributeValue("class");

            //通过反射把这个属性的值转化为指定的对象
            object = Class.forName(beanClass).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("").getBean("");
    }
}
