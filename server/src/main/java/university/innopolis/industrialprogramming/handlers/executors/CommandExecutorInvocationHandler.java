package university.innopolis.industrialprogramming.handlers.executors;

import university.innopolis.industrialprogramming.commands.CommandExecutor;
import university.innopolis.industrialprogramming.loaders.CustomLoader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

public class CommandExecutorInvocationHandler implements InvocationHandler {
    private final String className;
    private final Map<String, String> args;

    CommandExecutorInvocationHandler(String className, Map<String, String> args) {
        // ToDo: Package should not be specified in the code
        this.className = "university.innopolis.industrialprogramming." + className;
        this.args = args;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] arguments) {
        CustomLoader customLoader = (CustomLoader) Thread.currentThread().getContextClassLoader();

        try {
            Class<CommandExecutor>  tempClass = (Class<CommandExecutor>) customLoader.loadClass(className);
            Constructor<CommandExecutor> constructor = tempClass.getDeclaredConstructor(Map.class);
            Object commandExecutor = constructor.newInstance(args);
            return method.invoke(commandExecutor, arguments);
        } catch (Throwable e) {
            return null;
        }
    }
}
