import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;

/*
 * This class is also the main class. It can initialize the "world" of the 
 * model and let it change in each time tick. Actually, it can be used as
 * the Setup and Go buttons of the Netlogo model.
 */
public class Setup {
    
    //Set the initial time tick.
    public int Tick=0;
   //Set the default values of the land. 
    /*
     * Max_grain means the maximum grain a land can have.
     * The parameter is 50 because it is used in Netlogo model to appropriate
     * values.
     */
    public static int Max_grain=50;
    public static double diffusePercentage=0.25;
    public static int Percent_best_land=10;
    //Set the size of the "world".
    public static  Land[][] Location=new Land[51][51];
   //Set the default values of the person.
    public static int Num_people=250;
    public static int Maxvision=5;
    public static int Metabolism_max=15;
    public static int Life_expectancy_min=1;
    public static int Life_expectancy_max=83;
    public static double gini_index;
    /*There are lots of people so an array is needed to
     * represent the people set.
     */
    public static People[] p=new People[Num_people];
    /*the following three variables are used to count the number of
     * poor, middle and rich people.
     */   
    public static int poor;
    public static int middle;
    public static int rich;
   
    public static void main(String[] args) {
        /*
         * Like the "setup" function of Netlogo model, the following 
         * steps is need to initialize the environment of the "world".
         */
        Setup setup=new Setup();
        if (args.length>0) {
            /*We can set all parameters when run the model.
             * If you don't want to change them,
             * all value will be set the same as in Netlogo model.
             */
            Setup.Num_people=(Integer.parseInt(args[0]));
            Setup.p=new People[Setup.Num_people];
            Setup.Maxvision=Integer.parseInt(args[1]);
            Setup.Metabolism_max=Integer.parseInt(args[2]);
            Setup.Life_expectancy_min=Integer.parseInt(args[3]);
            Setup.Life_expectancy_max=Integer.parseInt(args[4]);
            Setup.Percent_best_land=(Integer.parseInt(args[5]));
            Land.Grain_growth_interval=Integer.parseInt(args[6]);
            Land.Num_grain_grown=Integer.parseInt(args[7]);
        }
        
        setup.Setup_land();
        setup.Setup_people();
        setup.Update_gini();
      
        try {            
            /*output the data which we need to a txt file.
             * By inputing the txt file into the Matlab, we can analyze
             * the data conveniently.   
             */
            PrintStream data=new PrintStream
                    /*the following address should be changed for different
                     * computers.
                     */                    
                  ("/Users/Venn/Desktop/modelling-experiments/data.txt");   
            System.setOut(data);   
        } catch (FileNotFoundException e) {   
          e.printStackTrace();   
        }
        
        //The following data shows the situation after setup.
        System.out.println
        (poor+" "+middle+" "+rich+" "+Setup.gini_index);
        //The following data shows the situation for each time tick.
        while (setup.Tick<1000) {
        //Calling the Go function and the world will start to opetate.
        setup.Go();
        System.out.println
        (poor+" "+middle+" "+rich+" "+Setup.gini_index);
        }
          
        
      
    }
    
    /*By observing the Netlogo model, we can see that
     * the world has no frontier. It means if a person is on the upperest
     * land and wants to continue to go up, he will appear at the lowest
     * land. Therefore, here we define the mod function which can be used
     * to maintain a world like this.
     * For example, if a person's location is (50,20) and wants to go down, the
     * new location (51,20) will be substituted by (1,20). For an nonnegative
     * number, the model symbol "%" can be used directly. However, for negative
     * number, such -1%50, the answer is wrong for us. What we really need is
     * 49 here.
     * Therefore, the void is wrote as follows to solve the problem.
     */
    public static int mod(int a,int m){
        int b=0;
        if (a>=0) {
            b=a%m;
        }else {
            b=m+a;
        }
        return b;        
    }
    
    /*
     * This method is similar to "setup-patches" of Netlogo model.
     */
    public void Setup_land(){
        for (int i = 0; i < Location.length; i++) {
            for (int j = 0; j < Location[i].length; j++) {
                /*By using Java's Random class, we can use a function to
                 * produce a random integer.
                 */               
                Random random=new Random();
                //set Max_grain_here and Grain_here to 0
                Location[i][j]=new Land(0, 0);
                /*
                 * give some patches the highest amount of grain possible
                 * these patches are the "best land"
                 */
                if (random.nextInt(100)<=Percent_best_land) {
                    Location[i][j].Max_grain_here=Max_grain;
                    Location[i][j].Grain_here=Location[i][j].Max_grain_here;
                }
            }
        }
        /*spread that grain around the window a little and put a little back
         * into the patches that are the "best land" found above.
         */
        for (int k = 0; k < 5; k++) {
            for (int i = 0; i < Location.length; i++) {
                for (int j = 0; j < Location[i].length; j++) {
                    if (Location[i][j].Max_grain_here!=0) {
                        Location[i][j].Grain_here=
                                Location[i][j].Max_grain_here;
                        diffuse(i, j, diffusePercentage);
                    }
                }
            }
        }
        //spread the grain around some more
        for (int k = 0; k < 10; k++) {
            for (int i = 0; i < Location.length; i++) {
                for (int j = 0; j < Location[i].length; j++) {
                    diffuse(i, j, diffusePercentage);
                }
            }
        }
        
        for (int i = 0; i < Location.length; i++) {
            for (int j = 0; j < Location[i].length; j++) {
                //round grain levels to whole numbers
                Location[i][j].Grain_here=
                        Math.floor(Location[i][j].Grain_here);
                //initial grain level is also maximum
                Location[i][j].Max_grain_here=
                        Location[i][j].Grain_here;
            }
        }  
    }
    
    /*Implementing the diffuse function which is existent on Netlogo model.
     *Diffuse means put some of the grain into the surrounding eight lands.
     *By calling the mod void, we do not have to worry about boundary problem:)
     */
    public static void diffuse(int i,int j,Double percentage){       
        for (int k = j-1; k <= j+1; k++) {
            for (int k2 = i-1; k2 <=i+1; k2++) {
                if (k==j&&k2==i) {
                    Location[k2][k].Grain_here=Location[i][j].Grain_here;
                }else {
                    Location[mod(k2,Location.length)]
                    [mod(k,Location.length)].Grain_here+=
                    Location[i][j].Grain_here*percentage/8;   
                    }                           
                }
            }                    
            Location[i][j].Grain_here=
            Location[i][j].Grain_here*(1-percentage);       
    }
    
    /*
     * This method is similar to "setup-turtles" of Netlogo model.
     */
    public void Setup_people(){
        Random random=new Random();
        //初始化turtle的位置       
        for (int i = 0; i <Num_people; i++) {
            p[i]=new People();
            Set_initial_turtle_vars(p[i]);
            p[i].Age=random.nextInt(p[i].Life_expectancy);           
            p[i].row=random.nextInt(Location.length);
            p[i].column=random.nextInt(Location.length);
            //将people加入对应位置的land的动态数组中
            Location[p[i].row][p[i].column].Move_to_patch(p[i]);        
        }        
        //determine the class of turtle
        recolor_turtles();  
    }
    
    //Producing the values of all parameters for a person randomly.
    public static void Set_initial_turtle_vars(People people){ 
        Random random=new Random();
        people.Age=0;
        people.Life_expectancy = 
                random.nextInt(Life_expectancy_max-Life_expectancy_min+1)
                +Life_expectancy_min;
        people.Metabolism =random.nextInt(Metabolism_max)+1;
        people.Wealth = random.nextInt(50)+people.Metabolism;
        people.Vision = random.nextInt(Maxvision)+1;
        //determine the direction of people's face
        int faceNum = random.nextInt(4);
        if (faceNum==0) {
            people.face='U';
        }else if (faceNum==1) {
            people.face='D';
        }else if (faceNum==2) {
            people.face='L';
        }else if (faceNum==3) {
            people.face='R';
        }       
    }
    
    /*Reporting the total amount of grain within the person's vision.
     * Apparently, it depends on the direction of the person's face.
     */
    public int grain_ahead(People p){
            if (p.face=='U') {
                return grain_Up(p);
            }
            if (p.face=='D') {
                return grain_Down(p);
             }
            if (p.face=='L') {
                return grain_Left(p);
            }
            else {
                return grain_Right(p);
            }           
    }
    
    /*
     * The following four methods tell us how to calculate the total amount
     * specifically. 
     */
    public int grain_Up(People p){
        int total=0;
        int how_far=1;
        for (int i = 0; i < p.Vision; i++) {              
            total+=Location
                    [mod(p.row-how_far, Location.length)][p.column].Grain_here;
            how_far+=1;
        }
        return total;
    }
    
    public int grain_Down(People p){
        int total=0;
        int how_far=1;
        for (int i = 0; i < p.Vision; i++) {
            total+=Location
                    [mod(p.row+how_far, Location.length)][p.column].Grain_here;               
                }           
            how_far+=1;
        return total;
    }
    
    public int grain_Left(People p){
        int total=0;
        int how_far=1;
        for (int i = 0; i < p.Vision; i++) {
            total+=Location
                    [p.row][mod(p.column-how_far,Location.length)].Grain_here;            
            how_far+=1;
        }
        return total;
    }
    
    public int grain_Right(People p){
        int total=0;
        int how_far=1;
        for (int i = 0; i < p.Vision; i++) {
            total+=Location
                    [p.row][mod(p.column+how_far,Location.length)].Grain_here;      
            how_far+=1;
        }
        return total;
    }
    
    /*determine the direction which is most profitable for each turtle in
     *the surrounding patches within the turtles' vision.
     */ 
    public void Turn_towards_grain(People p){       
        int best_amount=grain_ahead(p);        
        if (grain_Up(p)>best_amount) {
            best_amount=grain_Up(p);
            p.face='U';
        }
        if (grain_Down(p)>best_amount) {
            best_amount=grain_Down(p);
            p.face='D';
        }
        if (grain_Left(p)>best_amount) {
            best_amount=grain_Left(p);
            p.face='L';
        }
        if (grain_Right(p)>best_amount) {
            best_amount=grain_Right(p);
            p.face='R';
        }       
    }   
    
    /*
     * In each time tick, a person moves to collect grain and need to eat.
     * At the same time, there ages increase and may die if they reach to
     * their life expectancy or if they do not have enough grain to eat.
     */
    public void Move_eat_age_die(People p, Land[][] Location){
    	Location[p.row][p.column].peopleInLand.remove(p);
        if (p.face=='U') {
            /*
             * If a person moved, their coordinates changed too and the 
             * list of old and new land need to do delete and add operation
             * respectively.
             */
            Location
            [mod(p.row-1,Location.length)][p.column].peopleInLand.add(p);
            p.row=mod(p.row-1,Location.length);
            
        }else if (p.face=='D') {
            Location
            [mod(p.row+1,Location.length)][p.column].peopleInLand.add(p);
            p.row=mod(p.row+1,Location.length);
        }else if (p.face=='L') {
            Location
            [p.row][mod(p.column-1,Location.length)].peopleInLand.add(p);
            p.column=mod(p.column-1,Location.length);
        }else {
            Location
            [p.row][mod(p.column+1,Location.length)].peopleInLand.add(p);
            p.column=mod(p.column+1,Location.length);
        }
        p.Wealth-=p.Metabolism;
        p.Age+=1;
        if (p.Wealth<0||p.Age>=p.Life_expectancy) {
            /*According to the rules of the wealth distribution model,
             * when a person die, their will appear to a new descendant in the
             * same land with the age 0. In addition, his all other attributes
             * are producing randomly. 
             */
            Set_initial_turtle_vars(p);
        }
    }
    
    /*
     * It is the main function which controls the agent's action in each time
     * tick. Here people will harvest their current land's grain firstly. After
     * that, they will move into the next location.
     * At the same time, because of the changes of all people's wealth, 
     * the Gini-Index and people's level need to be updated.
     */
    public void Go(){        
        for (int i = 0; i < Location.length; i++) {
            for (int j = 0; j < Location[i].length; j++) {
                Location[i][j].Harvest();
            }
        }
        for (int i = 0; i <Num_people; i++) {
            Turn_towards_grain(p[i]);
            Move_eat_age_die(p[i],Location);
        }       
        recolor_turtles();          
        if (Tick%Land.Grain_growth_interval==0) {
            for (int i = 0; i < Location.length; i++) {
                for (int j = 0; j < Location[i].length; j++) {
                    Location[i][j].grow_grain();
                }
            }
        }
        Update_gini();
        Tick++;
    }
    
    /*
     * This method sets the rules to determine which level a person belongs to.
     *if a turtle has less than a third of the wealth of the richest turtle,
     *he is poor. If it between one and two thirds, he is middle.
     *In addition, in order to unify the name of all methods, I use the word
     *recolor. Although in Java we do not have to show the color of each person,
     *it is convenient for people who know the corresponding Netlogo model to
     *use this Java verson.
     */
    public static void recolor_turtles(){
        int max_wealth=0;
        for (int i = 0; i <Num_people; i++) {
            if (p[i].Wealth>=max_wealth) {
                max_wealth=p[i].Wealth;
            }
        }
        poor=0;
        middle=0;
        rich=0;
        for (int i = 0; i <Num_people; i++) {
            if (p[i].Wealth<=max_wealth/3) {
                p[i].level='P';
                poor++;
            }else if (p[i].Wealth<=max_wealth*2/3) {
                p[i].level='M';
                middle++;
            }else {
                p[i].level='R';
                rich++;
            }
        }
    }
   
    /*
     * By using the gini index, we can measure the wealth distributions easily.
     * Moreover, this method can help us to observe the similarity of Java
     * and Netlogo model. Here we do not choose to calculate the Lorenz curve. 
     * It is due to the situation that lorenz curve changes in each time tick.
     * If we want to record the data of 1000 ticks, 1000 lorenz curve is needed.
     * This is not very practical so we choose to delete the code which can
     * calculate the lorenz curve.
     */
    public void Update_gini(){
        int total_wealth=0;
        int sorted_wealth[]=new int[Num_people];
        int wealth_sum_so_far=0;
        int index=0;
        double gini_index_reserve=0;
        for (int i = 0; i < sorted_wealth.length; i++) {
            sorted_wealth[i]=p[i].Wealth;
            total_wealth+=sorted_wealth[i];
        }
        Arrays.sort(sorted_wealth); 
        for (int j = 0; j < Num_people; j++) {
            wealth_sum_so_far+=sorted_wealth[j];
            index++;
            gini_index_reserve+=
                    (index*1.0/Num_people)-(wealth_sum_so_far*1.0/total_wealth);
        }
        gini_index=(gini_index_reserve/Num_people)*2; 
    }
}
