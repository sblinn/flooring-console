package com.sblinn.flooringmastery;

import com.sblinn.flooringmastery.controller.FlooringController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author sarablinn
 */
public class App {
    

    public static void main(String[] args) {

//        UserIO io = new UserIOConsoleImpl();
//        FlooringView view = new FlooringView(io);
//
//        TaxInfoDao taxInfoDao = new TaxInfoDaoFileImpl();
//        ProductDao productDao = new ProductDaoFileImpl();
//        OrderDao orderDao = new OrderDaoFileImpl(taxInfoDao);
//        FlooringService service
//                = new FlooringServiceImpl(orderDao, productDao, taxInfoDao);
//
//        FlooringController controller = new FlooringController(view, service);
//
//        controller.run();


        ApplicationContext appContext = new ClassPathXmlApplicationContext(
                "classpath:applicationContext.xml");

        FlooringController controller
                = appContext.getBean("controller", FlooringController.class);

        controller.run();  

    }
}
