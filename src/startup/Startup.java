package startup;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

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

    private static synchronized long nextID(){
        long id;
        do{
            id = ++uniqueId;
        }while(!isValidID(id));
        return id;
    }

    private static synchronized boolean isValidID(long id){
        return (!wochenplaene.containsKey(id)) && id > 0;
    }

    public static void main(String[] args) {
        uniqueId = 0;
        wochenplaene = new HashMap<>();

        get("/plan/:id", (req, res) -> {
            try {
                long id = Long.parseLong(req.params("id"));

                if (wochenplaene.containsKey(id)) {
                    res.status(200);
                    res.type("application/json");
                    Wochenplan plan = wochenplaene.get(id);
                    return plan.toJson();
                } else {
                    res.status(405);
                    return gson.toJson("Invalid Input");
                }
            }catch(NumberFormatException ex){
                res.status(405);
                return gson.toJson("Invalid Input");
            }
        });

        post("/plan", ((req, res) -> {
            try {
                Wochenplan plan = Wochenplan.fromJson(req.body());
                long id = nextID();
                plan.setID(id);
                wochenplaene.put (id, plan);
                res.status(200);
                return gson.toJson("OK, Plan erstellt");
            }
            catch (IllegalArgumentException e)
            {
                res.status(400);
                return gson.toJson("Invalid Input");
            }

        }));

        post("/plan/:id", (req, res) -> {
            try {
                Wochenplan plan = Wochenplan.fromJson(req.body());
                long id = Long.parseLong(req.params("id"));
                if(!wochenplaene.containsKey(id)){
                    res.status(404);
                    return gson.toJson("ID nicht gefunden");
                }else{
                    plan.setID(id);
                    wochenplaene.put(id, plan);
                    res.status(200);
                    return gson.toJson("OK");
                }
            }
            catch (IllegalArgumentException e)
            {
                res.status(405);
                return gson.toJson("Invalid Input");
            }
        });

        delete("/plan/:id", (req, res) -> {
            long id = Long.parseLong(req.params("id"));
            if(!wochenplaene.containsKey(id)){
                res.status(404);
                return gson.toJson("ID nicht gefunden");
            }else{
                wochenplaene.remove(id);
                res.status(200);
                return "OK";
            }
        });

    }
}
