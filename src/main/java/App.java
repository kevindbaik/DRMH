import learn.mastery.data.*;
import learn.mastery.domain.GuestService;
import learn.mastery.domain.HostService;
import learn.mastery.domain.ReservationService;
import learn.mastery.ui.ConsoleIO;
import learn.mastery.ui.Controller;
import learn.mastery.ui.View;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        Controller controller = context.getBean("controller", Controller.class);
        controller.run();
    }
}

