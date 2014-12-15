/**
 * Created by Sadruddin on 12/14/2014.
 */
public class Test {


    public static void main(String[] args){
        String input = "add 1 2 3";

        String workWith = input.substring(3, input.length());
        String[] numbers = workWith.split(" ");

        for(String s : numbers){
            System.out.println(s);
        }
    }
}
