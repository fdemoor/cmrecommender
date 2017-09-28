package cmrecommender.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class HashFunction {
  
  private static final Logger log = LoggerFactory.getLogger(HashFunction.class);
  
  private long bigPrime = 9223372036854775783L;
  
  private static long[] randomParamA = {
    3855483969251045550L, 2274948550203382259L, 8086346051022946365L, 3742599363140121417L,
    8127506216481886778L, 8992508990183509581L, 6824159666599056129L, 1515839073414842035L,
    3528950968555460028L, 3742666563140121417L, 8086685051022946685L, 8926282854497893686L,
    7549048658687782964L, 2254963801332704518L, 4662970043116588076L, 1164415423486499490L,
    8596154617854201450L, 35867659355547039L, 7491907009982470321L, 353950969553360028L,
    3742666563140908304L};
                                
  private static long[] randomParamB = {
    8708688407112891618L, 7360455785926943020L, 8825528156902469898L, 7810309941732699355L,
    2097246509310169464L, 238488925168692289L, 260263731934835712L, 688270280519291225L,
    2179209161542291414L, 7816665941732699355L, 6855528156906859898L, 4390665439843151325L,
    8145312472828294149L, 2847806146481835937L, 3995608470880119485L, 947734926901625025L,
    4850723540680713533L, 35867659355547039L, 7491907009982470321L, 2079208161542291414L,
    9083045941732699355L};
   
  private final long a;
  private final long b;
  private final int w;
  
  HashFunction(int iteration, int size) {
    
    a = randomParamA[iteration];
    b = randomParamB[iteration];
    w = size;
    
  }
  
  int hash(long k) {
    int result = (int) ( ((a * k + b) % bigPrime) % w );
    if (result < 0) { result += w; }
    return result;
  }
  
  
}
