package nmt;

/**
 *    This exception is usually generated by incorrect user input.
 *    It is thrown when a field is out of range
 */
public class DataRangeException extends Exception{
    
    public DataRangeException(){
        super();
    }
    
    public DataRangeException(String s){
        super(s);
    }
}