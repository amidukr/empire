    package org.qik.empire.core;

    import org.qik.empire.utils.ReflectionUtils;

    import java.lang.reflect.Method;
    import java.util.*;

    import static java.lang.String.format;
    import static java.util.Collections.unmodifiableCollection;
    import static org.qik.empire.utils.Lambda.asLambda;
    import static org.qik.empire.utils.Utils.defaultValue;

    /**
     * Created by qik on 05.10.2014.
     */
    public class EntryPointsContainer {

        private List<Service> serviceList = new ArrayList<>();
        private Map<String, EntryPoint> entryPointMap = new HashMap<>();
        private Map<Class<?>, Object> injects = new HashMap<>();

        public EntryPointsContainer() {
            injects.put(EntryPointsContainer.class, this);
        }

        public void addService(Service service) {
            serviceList.add(service);
            injects.put(service.getClass(), service);

            asLambda(service.getClass().getMethods())
                    .filter(method -> method.isAnnotationPresent(Command.class))
                    .map(method -> buildEntryPoint(service, method))
            .copyToIndex(entryPointMap, ep -> ep.getCommandName());
        }


        public Collection<String> getCommandList() {
            return unmodifiableCollection(entryPointMap.keySet());
        }

        public EntryPoint getEntryPoint(String commandName) {
            return entryPointMap.get(commandName);
        }

        public void reload() {
            for (Service service: serviceList) {
                refreshMethodInjects(service);
                refreshFieldInjects(service);
            }
        }

        public Object resolveInjectByClass(Class<?> injectClass, Object injectTarget){
            if (!injects.containsKey(injectClass)) throw new IllegalArgumentException(format("Unable to find inject %s for injector %s",  injectClass, injectTarget));

            return injects.get(injectClass);
        }

        private void refreshFieldInjects(Service service) {
            asLambda(ReflectionUtils.getXFields(service))
                .filter(field -> field.isAnnotationPresent(Inject.class))
                .forEach(field -> field.setFieldValue(resolveInjectByClass(field.getType(), field)));
        }

        private void refreshMethodInjects(Service service) {

            asLambda(
               ReflectionUtils.getXMethods(service))
                  .filter(method -> method.isAnnotationPresent(Inject.class))
                  .forEach(method -> {

                      Object[] injectsArguments = asLambda(method.getParameterTypes())
                              .map(type -> resolveInjectByClass(type, method))
                              .toArray(Object.class);

                      method.invoke(injectsArguments);
                  });
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

            public Object invoke(String[] args){
                return ReflectionUtils.invokeMethod(method, target, (Object[]) args);
            }
        }
    }
