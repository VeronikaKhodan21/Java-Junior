package two;

import java.lang.reflect.Method;

public class MetStr {
 public static void main(String[] args) {
    Class<String> stringClass = String.class;
        Method[] methods = stringClass.getMethods(); 
        for (Method method : methods) {
            System.out.println(method.getName());
        }
 }
}
