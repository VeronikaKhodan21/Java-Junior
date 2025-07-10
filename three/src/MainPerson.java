package three.src;

import java.io.IOException;


public class MainPerson {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Person nil = new Person(12, "Nil");
        System.out.println("До селиазизации:\n"+nil.toString());
        String nameFile = "C:\\Users\\veron\\training\\javaskript\\javaJunior\\three\\data\\pers.txt";
        PersonInOutFile.serialize(nil, nameFile);// Сериализуем объект
        Person person = PersonInOutFile.deserialize(nameFile); // Десериализуем объект
        System.out.println("----------");
        System.out.println("После десериализации:\n"+person.toString());
    }

}
