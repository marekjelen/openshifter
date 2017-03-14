package eu.mjelen.warden.injector;

import javax.inject.Provider;
import java.lang.reflect.Type;
import java.util.Arrays;

public class Injectable {

    protected Object[] arguments(Class[] types, Type[] generics, Injector injector) {
        int count = types.length;
        Object[] params = new Object[count];
        for(int index = 0; index < types.length; index++) {
            Class type = types[index];
            if(type == Provider.class) {
                String generic = generics[index].getTypeName();
                generic = generic.substring(generic.indexOf('<') + 1, generic.indexOf('>'));
                try {
                    Class<?> subtype = Class.forName(generic);
                    params[index] = injector.get(Provider.class, subtype);
                    System.out.println(Arrays.toString(types));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                params[index] = injector.get(type);
            }
            index += 1;
        }
        return params;
    }

}
