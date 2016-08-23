/*
 * People can wander around the lands and gather as much grain as they can.
 * Each person means an agent of the model. In addition, this class corresponds
 * to the turtles of Netlogo model.
 */
public class People {  
    public int Age;
    //Life expectancy means the maximum age that an agent can reach.
    public int Life_expectancy;
    //The amount of grain a person has
    public  int Wealth;
    //How much grain a person eats each time
    public  int Metabolism;
    //How many lands ahead a person can see
    public  int Vision;
    //The level of a person and it depends on his wealth.
    public char level;
    //Row and Column represent the location of a person.
    public int row;
    public int column;
    //Face here means the specific direction of the person's face.
    public char face;

}
