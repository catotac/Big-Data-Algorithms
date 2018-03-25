public class BloomFilter{
	public static void main(String[] args) {
		DynamicFilter bfRan = new DynamicFilter(4);
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		bfRan.add("apple");
		System.out.println("FilterSize: "+bfRan.filterSize()+" DataSize: "+bfRan.dataSize());
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		bfRan.add("banana");
		System.out.println("FilterSize: "+bfRan.filterSize()+" DataSize: "+bfRan.dataSize());
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		bfRan.add("mango");
		System.out.println("FilterSize: "+bfRan.filterSize()+" DataSize: "+bfRan.dataSize());
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		bfRan.add("pineapple");
		System.out.println("FilterSize: "+bfRan.filterSize()+" DataSize: "+bfRan.dataSize());
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		
		System.out.println(bfRan);
		
		if(bfRan.appears("Guava")){
			System.out.println("Guava present");
		}
		else{
			System.out.println("Guava not present");
		}
		if(bfRan.appears("Apple")){
			System.out.println("Apple present");
		}
		else{
			System.out.println("Apple not present");
		}
		if(bfRan.appears("banana")){
			System.out.println("Banana present");
		}
		else{
			System.out.println("Banana not present");
		}
		if(bfRan.appears("Pineapple")){
			System.out.println("Pineapple present");
		}
		else{
			System.out.println("Pineapple not present");
		}
		if(bfRan.appears("Mango")){
			System.out.println("Mango present");
		}
		else{
			System.out.println("Mango not present");
		}
		if(bfRan.appears("kuch bhi")){
			System.out.println("Kuch bhi present");
		}
		else{
			System.out.println("Kuch bhi not present");
		}
	}

}