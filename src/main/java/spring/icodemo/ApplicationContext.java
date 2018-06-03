package spring.icodemo;

import org.dom4j.DocumentException;

/**
 * Created by lanyage on 2018/4/29.
 */
public interface ApplicationContext {
    Object getBean(String beanId);
}
