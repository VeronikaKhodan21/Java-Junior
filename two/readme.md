# Домошняя работа №2
_Задание:_
* Используя Reflection API, напишите программу, которая выводит на экран все методы класса String.
## Решение:
В классе [MetStr](https://github.com/VeronikaKhodan21/Java-Junior/blob/main/one/MetStr.java) реализован метод который выводит все методы класса String
```
Class<String> stringClass = String.class; //получаем класс String
Method[] methods = stringClass.getMethods(); // получаем все методы класса
for (Method method : methods) {
    System.out.println(method.getName()); // печатаем
    }
```
