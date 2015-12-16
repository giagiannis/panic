package gr.ntua.ece.cslab.panic.core.partitioners;

import java.util.LinkedList;
import java.util.Random;

/**
 * Class used to create random partitions of a deployment space
 * @author Giannis Giannakopoulos
 *
 */
public class RandomPartitioner extends SingleDimensionPartitioner {

	public RandomPartitioner() {
		super();
	}
	
	@Override
	public void configurePartitioner() {
		LinkedList<String> keys = new LinkedList<>();
		keys.addAll(this.ranges.keySet());
		Random random = new Random();
		int index=random.nextInt(keys.size());
		
		this.setDimensionKey(keys.get(index));
		super.configurePartitioner();
	}
}
