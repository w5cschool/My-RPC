package github.w5cschool.loadbalance;

import java.util.List;
import java.util.Random;

/**
 * Implementation of random load balancing strategy
 */
public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> serviceAddresses) {
        Random random = new Random();
        return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
    }
}
