package gov.sandia.cf.webapp.mapper;

import org.springframework.stereotype.Component;

import gov.sandia.cf.webapp.model.stub.Notification;

@Component
public class NotificationBuilder {
    
    public static Notification getNew(String type, String message){
        return new Notification(type, message);
    }
}