package org.qik.empire.core;

import org.qik.empire.context.SessionContext;
import org.qik.empire.utils.Lambda;

import java.lang.reflect.Method;
import java.util.*;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableCollection;
import static org.qik.empire.utils.Lambda.asLambda;
import static org.qik.empire.utils.Utils.*;

/**
 * Created by qik on 05.10.2014.
 */
public class ServiceContainer {

    private SessionContainer sessionContainer = new SessionContainer();
    private List<Service> serviceList = new ArrayList<>();
    private Map<String, EntryPoint> entryPointMap = new HashMap<>();
    private Map<Class<?>, Object> injects = new HashMap<>();

    public ServiceContainer() {
        injects.put(ServiceContainer.class, this);
    }

    public void addService(Service service) {
        serviceList.add(service);
        injects.put(service.getClass(), service);

        asLambda(service.getClass().getMethods())
                .filter( method -> method.isAnnotationPresent(Command.class) )
                .map(    method -> buildEntryPoint(service, method))
        .copyToIndex(entryPointMap, ep -> ep.getCommandName() );
    }

    public Collection<String> getCommandList() {
        return unmodifiableCollection(entryPointMap.keySet());
    }

    public EntryPoint getEntryPoint(String commandName) {
        return entryPointMap.get(commandName);
    }

    public SessionContainer sessions() {
        return sessionContainer;
    }


    public void reload() {
        for (Service service: serviceList) {
            refreshInjects(service);
        }
    }

    private void refreshInjects(Service service) {

        Lambda<Method> injectMethods = asLambda(service.getClass().getMethods())
                                        .filter(method -> method.isAnnotationPresent(Inject.class));

        for(Method method: injectMethods) {

            for(Class<?> injectClass: method.getParameterTypes()) {

                if (!injects.containsKey(injectClass)) throw new IllegalArgumentException(format("Unable to find inject %s for injector %s::%s",  injectClass,
                                                                                                                                                  service.getClass().getName(),
                                                                                                                                                  method.getName()));

            }

            Object[] injectsArguments = asLambda(method.getParameterTypes())
                                        .map(this.injects::get)
                                        .toArray(Object.class);

            invokeMethod(method, service, injectsArguments);
        }
    }


    private EntryPoint buildEntryPoint(Service service, Method method) {
        Command commandAnnotation = method.getAnnotation(Command.class);

        EntryPoint ep = new EntryPoint(service, defaultValue(commandAnnotation.value(), method.getName()), // command name
                                                getReturnType(method.getReturnType()),                     // return type
                                                method);                                                   // method



        if(ep.getCommandName().indexOf(' ') != -1) throw new IllegalArgumentException(format("Wrong command name '%s' in  %s::%s ",
                                                                                                                                ep.getCommandName(),
                                                                                                                                service.getClass().getName(),
                                                                                                                                method.getName()));

        if(ep.getReturnType() == null) throw new UnsupportedOperationException(format("Unsupported return type %s for entry point %s::%s",
                                                                                                                                method.getReturnType(),
                                                                                                                                service.getClass().getName(),
                                                                                                                                method.getName()));


        if(entryPointMap.containsKey(ep.getCommandName())) {
            EntryPoint anotherEntryPoint = entryPointMap.get(ep.getCommandName());
            throw new IllegalArgumentException(format("Unable to register service %s, entry point '%s' already exists in another service %s",
                                                                                                                                service.getClass().getName(),
                                                                                                                                ep.getCommandName(),
                                                                                                                                anotherEntryPoint.getService().getClass().getName()));
        }

        return ep;
    }

    private ReturnType getReturnType(Class<?> clazz) {
        if(clazz == String.class) return ReturnType.STRING;
        if(clazz == Void.TYPE)   return ReturnType.VOID;

        return null;
    }

    public enum ReturnType{
        VOID, STRING;
    }

    public static class EntryPoint {
        private final Service target;
        private final Method method;
        private final String commandName;
        private final ReturnType returnType;

        private EntryPoint(Service target, String commandName, ReturnType returnType, Method method) {
            this.commandName = commandName;
            this.returnType = returnType;
            this.target = target;
            this.method = method;
        }

        public String getCommandName() {
            return commandName;
        }

        public ReturnType getReturnType() {
            return returnType;
        }

        public Object getService() {
            return target;
        }

        public Method getMethod() {
            return method;
        }

        public Object invoke(SessionContainer.Session session, String[] args){
            try{
                SessionContext.setCurrentContext(session.getSessionContext());

                return invokeMethod(method, target, (Object[]) args);

            }finally {
                SessionContext.releaseCurrentContext();
            }
        }
    }
}
