package two;

import java.lang.reflect.Method;

public class MetStr {
 public static void main(String[] args) {
    Class<String> stringClass = String.class; //получаем класс String
        Method[] methods = stringClass.getMethods(); // получаем все методы класса
        for (Method method : methods) {
            System.out.println(method.getName()); //печатаем
        }
 }
}
