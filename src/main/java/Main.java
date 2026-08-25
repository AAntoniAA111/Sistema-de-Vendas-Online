import java.io.IOException;
import model.*;

import service.ViacepService;

public class Main {
    public static void main(String[] args) {
        ViacepService viacepService = new ViacepService();
        
        try{
            Endereco endereco = viacepService.getEndereco("01001000");

            System.out.println(endereco);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
