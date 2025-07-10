package three.src;

import java.io.*;

public class PersonInOutFile {
    public static void serialize(Person person, String fileName) throws IOException {
        try(FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)){
            objectOutputStream.writeObject(person);
        }
    }
    public static Person deserialize(String fileName) throws IOException, ClassNotFoundException {
        try(FileInputStream fileInputStream = new FileInputStream(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)){
            return (Person)objectInputStream.readObject();
        }
    }
}
