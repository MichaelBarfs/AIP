package startup;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * ToDo: Describe class here
 *
 * @author Michael Barfs <michael.barfs@gmail.com>
 * @version 26.04.2016
 */
public class Startup {
    private static long uniqueId;
    private static Map<Long, Wochenplan> wochenplaene;
    private static Gson gson = new Gson();

    private static long nextID(){
        long id;

        do{
            id = ++uniqueId;
        }while(!isValidID(id));

        return id;
    }

    private static boolean isValidID(long id){
        return (!wochenplaene.containsKey(id)) && id > 0;
    }

    public static void main(String[] args) {
        uniqueId = 0;
        wochenplaene = new HashMap<>();

        get("/plan/:id", (req, res) -> {
            long id = Long.parseLong(req.params("id"));

            if(id > 0 && wochenplaene.containsKey(id)){
                res.status(200);
                res.type("application/json");
                Wochenplan plan = wochenplaene.get(id);
                return plan.toJson();
            }else{
                res.status(405);
                res.type("application/json");
                return gson.toJson("Invalid Input");
            }
        });


    }
}
