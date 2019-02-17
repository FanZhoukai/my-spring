package com.fzk.ioc;

import com.fzk.ioc.beans.def.reader.XmlBeanDefinitionReader;
import com.fzk.ioc.beans.factory.AutowireCapableBeanFactory;
import com.fzk.ioc.beans.def.BeanDefinition;
import com.fzk.ioc.beans.io.ResourceLoader;
import com.fzk.ioc.context.ApplicationContext;
import com.fzk.ioc.context.ClasspathXmlApplicationContext;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * 单元测试类
 * <p>
 * author:fanzhoukai
 * 2019/2/13 21:21
 */
public class UnitTest {

    // bean名称
    private static final String helloServiceBeanName = "helloService";
    private static final String byebyeServiceBeanName = "byebyeService";

    // xml配置文件路径
    private static final String xmlPath = "beans.xml";


    /**
     * 获取bean（手动进行bean属性赋值）
     */
    @Test
    public void getBean() throws Exception {
        // 创建bean工厂
        AutowireCapableBeanFactory beanFactory = new AutowireCapableBeanFactory();

        // 注册bean定义、bean属性赋值
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setClassObject(HelloService.class);
        beanDefinition.getPropertyValues().addPropertyValue("name", "fanzhoukai");
        beanFactory.registerBeanDefinition(helloServiceBeanName, beanDefinition);

        // 获取bean
        HelloService bean = (HelloService) beanFactory.getBean(helloServiceBeanName);
        bean.sayHello();
    }

    /**
     * 获取bean（xml配置读取bean定义的方式，同时测试循环依赖）
     */
    @Test
    public void getBeanXml() throws Exception {
        // 设置xml路径，并解析为bean定义
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(new ResourceLoader());
        reader.loadBeanDefinitions(xmlPath);

        // 创建bean工厂，注册bean定义
        AutowireCapableBeanFactory beanFactory = new AutowireCapableBeanFactory();
        for (Map.Entry<String, BeanDefinition> entry : reader.getBeanDefinitionMap().entrySet()) {
            beanFactory.registerBeanDefinition(entry.getKey(), entry.getValue());
        }

        // 获取bean
        HelloService helloBean = (HelloService) beanFactory.getBean(helloServiceBeanName);
        ByebyeService byebyeBean = (ByebyeService) beanFactory.getBean(byebyeServiceBeanName);
        helloBean.sayHello();
        byebyeBean.sayByebye();
        // 测试循环依赖
        boolean validateCircleReference = helloBean.getByebyeService() == byebyeBean && byebyeBean.getHelloService() == helloBean;
        Assert.assertTrue("循环依赖测试失败", validateCircleReference);
    }

    /**
     * 测试ApplicationContext
     */
    @Test
    public void getBeanApplicationContext() throws Exception {
        ApplicationContext context = new ClasspathXmlApplicationContext(xmlPath);
        HelloService helloBean = (HelloService) context.getBean(helloServiceBeanName);
        helloBean.sayHello();
    }
}
