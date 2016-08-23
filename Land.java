import java.util.ArrayList;
/*
 * Land corresponds to the patch of Netlogo model.
 * For a land, it may has some grain which people can collect.
 */
public class Land {
 
    /*Grain-growth-interval decides how fast grain grows. The bigger the value,
     * the faster it grows.
     * Num-grain-grown decides how much grain grows each time.
     * In addition, the value 1 and 4 are the default value of the model.
     */
    public static int Grain_growth_interval=1;
    public static int Num_grain_grown=4;
    //Grain-here means the current amount of grain on this land.
    public double Grain_here;
    //Max-grain-here decides the the maximum amount of grain this land can hold.
    public double Max_grain_here;
    /*In each land, a list is used to record the specific people who
     * are on the land.
     */    
    public ArrayList<People> peopleInLand=new ArrayList<People>();
    //Constructor, can set the Grain-here and Max-grain-here of the land.
    public Land(double a,double b){
        Grain_here=a;
        Max_grain_here=b;
    }
    /*When a person is entering into a land, use this method to insert his
     *record to the land.
     */   
    public void Move_to_patch(People p){
        peopleInLand.add(p);
    }
    /*Each agent harvests the grain on its land.if there are multiple
     *agents on a patch, divide the grain evenly among them.
     */
    public void Harvest(){
        if (peopleInLand.size()>0) {
            for (People people : peopleInLand) { 
                people.Wealth=(int) Math.floor
                        (people.Wealth+1.0*Grain_here/(peopleInLand.size()));
            }
            //After the grain being harvested, the land will have no grain left.
            Grain_here=0;            
        }
    }
    /*
     * The specific method to decide how the grain grows on the land.
     * If a land does not have it's maximum amount of grain,
     * add num-grain-grown to it. Obviously, we need to make sure that
     * the grain-here is no more than max-grain-here.
     */
    public void grow_grain(){
        if (Grain_here<Max_grain_here) {
            if (Grain_here+Num_grain_grown>=Max_grain_here) {
                Grain_here=Max_grain_here;
            }else {
                Grain_here+=Num_grain_grown;
            }
        }
    }
}
